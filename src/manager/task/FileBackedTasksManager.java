package manager.task;

import exception.ManagerException;
import manager.history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TypeTask;
import utils.CSVUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static tasks.TypeTask.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;
    private static final String TASKS_CSV = "tasks.csv";
    private static final String HEADER_LINE_TASKS = "id,type,name,status,description,startTime,duration,epicId";
    private static final String SEPARATOR_TASKS_HISTORY = "History";

    public FileBackedTasksManager(HistoryManager historyManager, File file){
        super(historyManager);
        this.file = file;
    }

    public FileBackedTasksManager(HistoryManager historyManager){
        super(historyManager);
        this.file = new File(TASKS_CSV);
    }

    private boolean prepareFile(){
        if (file.exists()) {
            file.delete();
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new ManagerException("Не удалось создать файл ");
        }

    }

    /**
     * Сохранение задач в CSV-файл
     */
    public void save() {
        prepareFile();
        try (FileWriter save = new FileWriter(this.file, StandardCharsets.UTF_8)){
            save.write(HEADER_LINE_TASKS + '\n');
            for (Task e : getAllTasks()) {
                save.write(CSVUtils.toString(e));
            }
            for (Epic e : getAllEpics()) {
                save.write(CSVUtils.toString(e));
            }
            for (SubTask e : getAllSubTasks()) {
                save.write(CSVUtils.toString(e));
            }

            save.write(SEPARATOR_TASKS_HISTORY + '\n' + CSVUtils.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerException("Ошибка записи в файл " + file.getName());
        }
    }

    /**
     * Загрузка задач из CSV-файла
     */
    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean headerIsOver = false;
            boolean historySeparatorIsOver = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (!headerIsOver) {
                    headerIsOver = true;
                } else if (!historySeparatorIsOver && !line.isBlank() && !line.equals(SEPARATOR_TASKS_HISTORY)) {
                    recoveredTasks(line);
                } else if (!historySeparatorIsOver) {
                    historySeparatorIsOver = true;
                } else if (!line.isBlank()){
                    historyFromString(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Ошибка чтения из файла " + file.getName());
        }
    }

    /**
     * Загрузка задач из строки файла в обход override-методов addTask, addSubTask, addEpics
     * @param line строка файла
     */
    private void recoveredTasks(String line) {
        Task task = CSVUtils.fromString(line);
        setGenerateId(task.getId());
        TypeTask typeTask = task.getType();
        if (typeTask.equals(SUBTASK)) {
            subtasks.put(task.getId(), (SubTask) task);
        } else if (typeTask.equals(TASK)) {
            tasks.put(task.getId(), task);
        } else if (typeTask.equals(EPIC)) {
            epics.put(task.getId(), (Epic) task);
        }
    }

    /**
     * Распаковать строку в историю
     *
     * @param stringWithHistory Строка с историей о похождениях пользователя по задачам
     */
    private void historyFromString(String stringWithHistory) {
        int id;
        for (String taskId : CSVUtils.historyFromString(stringWithHistory)) {
            id = Integer.parseInt(taskId);
            historyManager.add(getTaskInMemory(id));
        }
        CSVUtils.historyToString(historyManager);
    }

    /**
     * Поиск задачи по трем hashmap, для распаковки id задачи в задачу.
     * history.add(Task task)
     * Get-методы переопределены для сохранения истории в файл.
     * Их использование уничтожит текущую историю.
     *
     * @param id Номер задачи
     * @return Задача
     */
    private Task getTaskInMemory(int id) {
        /*
        Access denied! private tasks, subtasks, epics;
        */
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return new Task();
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic out = super.addEpic(epic);
        save();
        return out;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask out = super.addSubTask(subTask);
        save();
        return out;
    }

    @Override
    public Task addTask(Task task) {
        Task out = super.addTask(task);
        save();
        return out;
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
}