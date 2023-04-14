package manager;

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

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    final private String saveFileName;

    FileBackedTasksManager() {
        saveFileName = "tasks.csv";
    }

    public FileBackedTasksManager(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public static void main(String[] args) {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager();
        backedTasksManager = loadFromFile(new File(backedTasksManager.getSaveFileName()));
        backedTasksManager.getHistory();

        //System.out.println(fromString(""));
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasks =
                new FileBackedTasksManager(file.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(
                new FileReader(fileBackedTasks.saveFileName)))
        {

            String line;
            String[] lineCSV;
            boolean headerIsOver = true;

            while ((line = br.readLine()) != null) {
                if (headerIsOver || line.equals("\n")) {

                    try {
                        lineCSV = line.split(" , ");

                        int id = Integer.parseInt(lineCSV[0]);
                        TypeTask type = TypeTask.valueOf(lineCSV[1]);
                        String title = lineCSV[2];
                        Status status = Status.valueOf(lineCSV[3]);
                        String description = lineCSV[4];

                        final boolean isSixItemsForSubTask = (lineCSV.length == 6);

                        if (isSixItemsForSubTask) {
                            try {
                                int epicId = Integer.parseInt(lineCSV[5]);
                                SubTask subTask = new SubTask(id, title, description, status, epicId);
                                fileBackedTasks.addSubTask(subTask);

                            } catch (NumberFormatException nfe) {
                                throw new NumberFormatException("Повреждены данные подзадачи. "
                                        + "Ошибка: " + nfe.getMessage());
                            }
                        } else {

                            if (type.equals(TypeTask.TASK)) {

                                Task task = new Task(id, title, description, status);
                                fileBackedTasks.addTask(task);

                            } else if (type.equals(TypeTask.EPIC)) {

                                Epic epic = new Epic(id, title, description, status);
                                fileBackedTasks.addEpic(epic);
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException("Повреждены данные задач. "
                                + "Ошибка: " + nfe.getMessage());
                    }
                }
                line = br.readLine();
                historyFromString(line);
                headerIsOver = false;
            }

        } catch (IOException e) {
            throw new ManagerException(e.getMessage());
        }
        return new FileBackedTasksManager();
    }

    /**
     * Save tasks in CSV-file
     */
    void save() {

        try (FileWriter save = new FileWriter(this.saveFileName)) {
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
            throw new ManagerException(e.getMessage());
        }
    }

    /**
     * Создание задачи из строки
     * @param s Строка формата "1,TASK,'Task 1',NEW,'Description by Task 1',";
     * @return (Задача/Эпик)/ПодЗадача.
     */
    public static Task fromString(String s) {
        //s = "1,TASK,'Task 1',NEW,'Description by Task 1',";
        String[] array = s.split(",");
        int id;
        int epicId;
        TypeTask typeTask;
        String title;
        String description;
        Status status;

        id = Integer.parseInt(array[0]);
        typeTask = TypeTask.valueOf(array[1]);
        title = array[2].substring(1, array[2].length() - 1);
        status = Status.valueOf(array[3]);
        description = array[4].substring(1, array[4].length() - 1);

        if (array.length == 6 && typeTask.equals(TypeTask.SUBTASK)) {
            epicId = Integer.parseInt(array[5]);

            return new SubTask(id, title, description, status, epicId);
        }
        return new Task(id, title, description, status);/**/
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

        try {
            StringBuilder out = new StringBuilder();
            for (Task task : manager.getHistory()) {
                out.append(task.getId()).append(",");
            }

            int end;
            if((end = out.lastIndexOf(",")) != -1)
                return out.substring(0, end);
            return "";

        } catch (NullPointerException e) {
            return "";//throw new ManagerException("History is empty: " + e.getMessage());
        }
    }

    public String getSaveFileName() {
        return saveFileName;
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
}