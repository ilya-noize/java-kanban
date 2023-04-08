package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    final private String saveFileName;

    FileBackedTasksManager(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    FileBackedTasksManager() {
        saveFileName = "tasks.csv";
    }

    /**
     * Save tasks in CSV-file
     */
    void save() {
        try {
            FileWriter saveToFile = new FileWriter(this.saveFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    enum TypeTask {
        TASK,
        SUBTASK,
        EPIC;
    }
}
