package manager;

import exception.ManagerException;
import tasks.*;
import utils.CSVUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tasks.TypeTask.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String TASKS_CSV = "tasks.csv";
    private static final CSVUtils FILE = new CSVUtils(TASKS_CSV);
    private static final String HEADER_LINE_TASKS = "id,type,name,status,description,epic";
    private static final String SEPARATOR_TASKS_HISTORY = "History";
    private static final String SEPARATOR_CSV = ",";
    private static final String EXCEPTION_WRITE = "Ошибка записи из файла ";
    private static final String EXCEPTION_READ = "Ошибка чтения в файл ";

    FileBackedTasksManager() {
    }

    /**
     * Save tasks in CSV-file
     */
    private void save() {
        try (FileWriter save = new FileWriter(FILE.getFile())) {
            save.write(HEADER_LINE_TASKS + '\n');
            for (Task e : getAllTasks()) {
                save.write(e.toString());
            }
            for (Epic e : getAllEpics()) {
                save.write(e.toString());
            }
            for (SubTask e : getAllSubTasks()) {
                save.write(e.toString());
            }

            save.write(SEPARATOR_TASKS_HISTORY + '\n' + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerException(EXCEPTION_WRITE + TASKS_CSV);
        }
    }


    private static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager recoveredTasksManager = new FileBackedTasksManager();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE.getFile()))) {
            String line;
            boolean headerIsOver = false;
            boolean historySeparatorIsOver = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (!headerIsOver) {
                    headerIsOver = true;
                } else if (!historySeparatorIsOver && !line.isBlank() && !line.equals(SEPARATOR_TASKS_HISTORY)) {
                    recoveredTasks(line, recoveredTasksManager);
                } else if (!historySeparatorIsOver){
                    historySeparatorIsOver = true;
                } else if (!line.isBlank()){
                    historyFromString(line, recoveredTasksManager);
                }
            }
        } catch (IOException e) {
            throw new ManagerException(EXCEPTION_READ + TASKS_CSV);
        }
        return recoveredTasksManager;
    }

    /**
     * Загрузка задач из строки файла в обход override-методов addTask, addSubTask, addEpics
     * @param line строка файла
     */
    private static void recoveredTasks(String line, FileBackedTasksManager manager){
        String[] array = line.split(SEPARATOR_CSV);
        Task task = fromString(array);
        manager.setGenerateId(task.getId());
        TypeTask typeTask = TypeTask.valueOf(array[1]);
        if(typeTask.equals(SUBTASK)){
            manager.subtasks.put(Integer.valueOf(array[0]), (SubTask) task);
        } else if (typeTask.equals(TASK)) {
            manager.tasks.put(Integer.valueOf(array[0]), task);
        } else if (typeTask.equals(EPIC)) {
            manager.epics.put(Integer.valueOf(array[0]), (Epic) task);
        }
    }

    /**
     * Создание задачи из строки
     * @param array Массив строк формата {1,TASK,'Task 1',NEW,'Description by Task 1',};
     * @return (Задача / Эпик)/ПодЗадача.
     */
    private static Task fromString(String[] array) {
        int id = Integer.parseInt(array[0]);
        TypeTask typeTask = TypeTask.valueOf(array[1]);
        String title = quoteOff(array[2]);
        String description = quoteOff(array[4]);
        Status status = Status.valueOf(array[3]);

        if (typeTask.equals(SUBTASK)) {
            return new SubTask(id, title, description, status, Integer.parseInt(array[5]));
        } else if (typeTask.equals(TASK)) {
            return new Task(id, title, description, status);
        } else {
            return new Epic(id, title, description, status);
        }
    }

    /**
     * Удаление символа ['] в начале и конце строки
     * @param stringInQuotes 'исходная строка в кавычках'
     * @return полученная строка без кавычек
     */
    private static String quoteOff(String stringInQuotes){
        return stringInQuotes.substring(1, stringInQuotes.length() - 1);
    }

    private static void historyFromString(String stringWithHistory, FileBackedTasksManager manager) {
        //2,4,5,9
        int id;
        for (String taskId : stringWithHistory.split(SEPARATOR_CSV)) {
            id = Integer.parseInt(taskId);
            manager.historyManager.add(getTaskInMemory(id, manager));
        }
        manager.historyToString(manager.historyManager);
    }

    /**
     * Поиск задачи по трем хешмапам.
     * Get-методы переопределены для сохранения истории в файл.
     * Их использование уничтожит текущую историю.
     * @param id      Номер задачи
     * @param manager Текущий менеджер
     * @return Задача
     */
    private static Task getTaskInMemory(int id, FileBackedTasksManager manager){
        if(manager.subtasks.containsKey(id)) {
            return manager.subtasks.get(id);
        }
        if(manager.tasks.containsKey(id)) {
            return manager.tasks.get(id);
        }
        if(manager.epics.containsKey(id)) {
            return manager.epics.get(id);
        }
        return new Task();
    }

    /**
     * Для сохранения истории в CSV
     * @return Строка для сохранения в файл
     */
    private String historyToString(HistoryManager manager) {
        StringBuilder out = new StringBuilder();
        int end;
        for (Task task : manager.getHistory()) {
            out.append(task.getId()).append(",");
        }
        if ((end = out.lastIndexOf(",")) != -1) {
            return out.substring(0, end);
        }
        return "";
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        int id = super.addSubTask(subTask);
        save();
        return id;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    /**
     * <h1>Тестирование работы программы</h1>
     * После написания менеджера истории проверьте его работу:
     * <ul>
     *     <li> создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;</li>
     *     <li> запросите созданные задачи несколько раз в разном порядке;</li>
     *     <li> после каждого запроса выведите историю и убедитесь, что в ней нет повторов;</li>
     *     <li> удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться;</li>
     *     <li> удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.</li>
     * </ul>
     */

    private static final TaskManager backedTasksManager = Managers.getFileBackedTasks();

    public static void main(String[] args) {
        System.out.println("Backed > > > > > > > > > > > > > > > > >\n"+
                "TestingTechTask (Backed): ..............");
        testingTechTaskArrayTest();
        System.out.println("Recovered < < < < < < < < < < < < < < < \n" +
                "TestingTechTask (Recovered): ...........");
        testingTechTaskFileTest(loadFromFile());
    }

    private static void testingTechTaskArrayTest() {
        System.out.println("TestingTechTask: getTaskListTest() .....");
        getTaskListTest(addTasksTest());
        System.out.println("TestingTechTask: getHistory() ..........");
        backedTasksManager.getHistory();
        System.out.println("TestingTechTask: showAllDataTest .......");
        showAllDataTest(backedTasksManager);
    }
    private static void testingTechTaskFileTest(FileBackedTasksManager recoveredTasksManager) {
        System.out.println("TestingTechTask (R): getHistory() ......");
        recoveredTasksManager.getHistory();
        System.out.println("TestingTechTask (R): showAllDataTest ...");
        showAllDataTest(recoveredTasksManager);
    }

    /**
     *     <li> создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;</li>
     * @return хэшмап с id задач для get-метода
     */
    private static Map<TypeTask,List<Integer>> addTasksTest(){
        Map<TypeTask, List<Integer>> typeTaskListId = new HashMap<>();

        typeTaskListId.put(TASK,List.of(
                backedTasksManager.addTask(new Task("Task 1", "Description by Task 1")),
                backedTasksManager.addTask(new Task("Task 2", "Description by Task 2"))
        ));

        int epicId = backedTasksManager.addEpic(new Epic("Epic 1", "Description by Epic 1"));
        typeTaskListId.put(TypeTask.SUBTASK, List.of(
                backedTasksManager.addSubTask(new SubTask("SubTask 1", "Description by SubTask 1", epicId)),
                backedTasksManager.addSubTask(new SubTask("SubTask 2", "Description by SubTask 2", epicId)),
                backedTasksManager.addSubTask(new SubTask("SubTask 3", "Description by SubTask 3", epicId))
        ));

        typeTaskListId.put(TypeTask.EPIC, List.of(
                epicId,
                backedTasksManager.addEpic(new Epic("Epic 2", "Description by Epic 2"))
        ));

        return typeTaskListId;
    }

    /**
     * Тестирование работы менеджера истории просмотров
     * @param typeTaskListId хэшмап с id задач из getTaskIdMapTest или addTasksTest
     */
    private static void getTaskListTest(Map<TypeTask,List<Integer>> typeTaskListId){
        /* Records in history.
         U      unical record. Recording in last place;
         again  contains record. After deleting contains record will unical;
         */
        if (typeTaskListId == null) {
            return;
        }
        getTaskTest(TASK,    typeTaskListId.get(TASK)   .get(1)); //    U   2
        getTaskTest(SUBTASK, typeTaskListId.get(SUBTASK).get(1)); //    U   5 2
        getTaskTest(TASK,    typeTaskListId.get(TASK)   .get(0)); //    U   1 5 2
        getTaskTest(SUBTASK, typeTaskListId.get(SUBTASK).get(0)); //    U   4 1 5 2
        getTaskTest(TASK,    typeTaskListId.get(TASK)   .get(0)); //again   1 4 5 2
        getTaskTest(SUBTASK, typeTaskListId.get(SUBTASK).get(1)); //again   5 1 4 2
        getTaskTest(TASK,    typeTaskListId.get(TASK)   .get(1)); //again   2 5 1 4
        getTaskTest(SUBTASK, typeTaskListId.get(SUBTASK).get(2)); //    U   6 2 5 1 4
        getTaskTest(EPIC,    typeTaskListId.get(EPIC)   .get(0)); //    U   3 6 2 5 1 4
        getTaskTest(SUBTASK, typeTaskListId.get(SUBTASK).get(2)); //again   6 3 2 5 1 4
        getTaskTest(EPIC,    typeTaskListId.get(EPIC)   .get(1)); //    U   7 6 3 2 5 1 4
        getTaskTest(EPIC,    typeTaskListId.get(EPIC)   .get(0)); //again   3 7 6 2 5 1 4
        getTaskTest(SUBTASK, typeTaskListId.get(SUBTASK).get(0)); //again   4 3 7 6 2 5 1
        getTaskTest(EPIC,    typeTaskListId.get(EPIC)   .get(1)); //again   7 4 3 6 2 5 1
    }

    /**
     * Получение задачи
     * @param type тип задачи
     * @param id Номер задачи
     */
    private static void getTaskTest(TypeTask type, Integer id){
        System.out.printf ("get %7s:%d .......................... ",type, id);
        switch(type){
            case TASK:    backedTasksManager.getTask(id);    break;
            case SUBTASK: backedTasksManager.getSubTask(id); break;
            case EPIC:    backedTasksManager.getEpic(id);    break;
            default:
                throw new IllegalStateException("Unexpected TypeTask.value: " + type);
        }
        System.out.println("OK");
        System.out.println("getHistory    .......................... >");
        backedTasksManager.getHistory().forEach(System.out::print);
        System.out.println("........................................ OK");
    }

    private static void showAllDataTest(TaskManager taskManager) {
        taskManager.getAllTasks().forEach(System.out::print);
        taskManager.getAllEpics().forEach(System.out::print);
        taskManager.getAllSubTasks().forEach(System.out::print);
        System.out.println("History ................................");
        taskManager.getHistory().forEach(System.out::print);
    }
}