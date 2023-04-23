package manager;

import exception.ManagerException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TypeTask;
import tasks.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String saveFileName = "tasks.csv";
    private static final int VALID_LENGTH_OF_ARRAY_FOR_SUBTASK = 6;

    FileBackedTasksManager() {
        File file = new File(saveFileName);
        createFile(file);
    }


    public void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Ошибка создания файла: " + saveFileName);
            }
        }
    }

    public static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager fileTasks = new FileBackedTasksManager();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFileName))) {
            String line;
            boolean headerIsOver = true;

            while ((line = bufferedReader.readLine()) != null) {
                if (headerIsOver || line.equals("\n")) {
                    fileTasks.addTask(fromString(line));
                }
                line = bufferedReader.readLine();
                historyFromString(line);
                headerIsOver = false;
            }

        } catch (IOException e) {
            throw new ManagerException("Ошибка записи в файл: " + saveFileName);
        }
        return new FileBackedTasksManager();
    }

    /**
     * Создание задачи из строки
     *
     * @param s Строка формата "1,TASK,'Task 1',NEW,'Description by Task 1',";
     * @return (Задача / Эпик)/ПодЗадача.
     */
    public static Task fromString(String s) {
        //s = "1,TASK,'Task 1',NEW,'Description by Task 1',";
        String[] array = s.split(",");
        int id = Integer.parseInt(array[0]);
        int epicId;
        TypeTask typeTask = TypeTask.valueOf(array[1]);
        String title = array[2].substring(1, array[2].length() - 1);
        String description = array[4].substring(1, array[4].length() - 1);
        Status status = Status.valueOf(array[3]);

        if (array.length == VALID_LENGTH_OF_ARRAY_FOR_SUBTASK && typeTask.equals(TypeTask.SUBTASK)) {
            epicId = Integer.parseInt(array[5]);
            return new SubTask(id, title, description, status, epicId);
        } else if (typeTask.equals(TypeTask.TASK)) {
            return new Task(id, title, description, status);
        } else return new Epic(id, title, description, status);
    }

    /**
     * Save tasks in CSV-file
     */
    void save() {

        try (FileWriter save = new FileWriter(saveFileName)) {
            save.write("id,type,name,status,description,epic\n");

            for (Task e : getAllTasks()) {
                save.write(e.toString());
            }
            for (Epic e : getAllEpics()) {
                save.write(e.toString());
            }
            for (SubTask e : getAllSubTasks()) {
                save.write(e.toString());
            }

            save.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerException("Ошибка записи в файл: " + saveFileName);
        }
    }

    public static List<Integer> historyFromString(String string) {
        //2,4,5,9
        List<Integer> history = new ArrayList<>();
        for (String taskId : string.split(",")) {
            history.add(Integer.parseInt(taskId));
        }
        return history;
    }

    /**
     * Для сохранения менеджера истории в CSV
     *
     * @return Строка для сохранения в файл
     */
    public String historyToString(HistoryManager manager) {
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


    static TaskManager taskManager = Managers.getFileBackedTasks();
    public static void main(String[] args) {
        try {
            testingByTechTask();
        } catch (ManagerException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void testingByTechTask() {


        List<Integer> taskId = new ArrayList<>();
        List<Integer> subtaskId = new ArrayList<>();
        List<Integer> epicId = new ArrayList<>();

        taskId.add(taskManager.addTask(
                new Task("Task 1", "Description by Task 1")));//0
        taskId.add(taskManager.addTask(
                new Task("Task 2", "Description by Task 2")));//1

        epicId.add(taskManager.addEpic(
                new Epic("Epic 1", "Description by Epic 1")));//0
        subtaskId.add(taskManager.addSubTask(
                new SubTask("SubTask 1", "Description by SubTask 1", epicId.get(0))));
        subtaskId.add(taskManager.addSubTask(
                new SubTask("SubTask 2", "Description by SubTask 2", epicId.get(0))));
        subtaskId.add(taskManager.addSubTask(
                new SubTask("SubTask 3", "Description by SubTask 3", epicId.get(0))));

        epicId.add(taskManager.addEpic(
                new Epic("Epic 2", "Description by Epic 2")));//1

        taskManager.getTask(taskId.get(1));
        System.out.println("get task " + taskId.get(1));

        taskManager.getSubTask(subtaskId.get(1));
        System.out.println("get subtask " + subtaskId.get(1));

        taskManager.getTask(taskId.get(0));
        System.out.println("get task " + taskId.get(0));

        taskManager.getSubTask(subtaskId.get(0));
        System.out.println("get subtask " + subtaskId.get(0));

        getHistoryOfTasks(taskManager);

        taskManager.getTask(taskId.get(0));
        System.out.println("get task " + taskId.get(0));

        getHistoryOfTasks(taskManager);

        taskManager.getSubTask(subtaskId.get(1));
        System.out.println("get subtask " + subtaskId.get(1));

        getHistoryOfTasks(taskManager);

        taskManager.getTask(taskId.get(1));
        System.out.println("get task " + taskId.get(1));

        getHistoryOfTasks(taskManager);

        taskManager.getSubTask(subtaskId.get(2));
        System.out.println("get subtask " + subtaskId.get(2));

        taskManager.getEpic(epicId.get(0));
        System.out.println("get epic " + epicId.get(0));

        taskManager.getSubTask(subtaskId.get(2));
        System.out.println("get subtask " + subtaskId.get(2));

        getHistoryOfTasks(taskManager);

        taskManager.getEpic(epicId.get(1));
        System.out.println("get epic " + epicId.get(1));
        taskManager.getEpic(epicId.get(0));
        System.out.println("get epic " + epicId.get(0));
        getHistoryOfTasks(taskManager);
        taskManager.getSubTask(subtaskId.get(0));
        System.out.println("get subtask " + subtaskId.get(0));
        getHistoryOfTasks(taskManager);
        taskManager.getEpic(epicId.get(1));
        System.out.println("get epic " + epicId.get(1));
        getHistoryOfTasks(taskManager);

        taskManager = loadFromFile();

        taskManager.deleteTask(taskId.get(0));
        System.out.println("delete task " + taskId.get(0));
        getHistoryOfTasks(taskManager);

        taskManager.deleteEpic(epicId.get(0));
        System.out.println("delete epic " + epicId.get(0));
        getHistoryOfTasks(taskManager);

        //destroyData
        taskManager.deleteTask(taskId.get(1));
        System.out.println("delete task " + taskId.get(1));
        getHistoryOfTasks(taskManager);
        taskManager.deleteTask(epicId.get(1));
        System.out.println("delete " + epicId.get(1));
        getHistoryOfTasks(taskManager);
    }

    private static void getHistoryOfTasks(TaskManager taskManager) {
        System.out.println("checking taskManager.getHistory ...................");
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("checking taskManager.getHistory ................ OK");
    }
}