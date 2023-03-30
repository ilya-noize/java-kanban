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
        //loadFromFile(new File("tasks.csv"));

        System.out.println(fromString("").toString());
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasks =
                new FileBackedTasksManager(file.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(
                new FileReader(fileBackedTasks.saveFileName))) {

            String line;
            String[] lineCSV;
            boolean headerIsOver = false;
            while ((line = br.readLine()) != null) {
                if (headerIsOver) {

                    try {
                        lineCSV = line.split(" , ");

                        int id = Integer.parseInt(lineCSV[0]);
                        TypeTask type = TypeTask.valueOf(lineCSV[1]);
                        String title = lineCSV[2];
                        Status status = Status.valueOf(lineCSV[3]);
                        String description = lineCSV[4];

                        final int is6 = lineCSV.length;

                        if (is6 == 6) {
                            try {
                                int epicId = Integer.parseInt(lineCSV[5]);
                                SubTask subTask = new SubTask(id, title, description, status, epicId);
                                fileBackedTasks.addSubTask(subTask);

                            } catch (NumberFormatException nfe) {
                                throw new NumberFormatException("Повреждены данные подзадачи. Ошибка: " + nfe.getMessage());
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
                        throw new NumberFormatException("Повреждены данные подзадачи. Ошибка: " + nfe.getMessage());
                    }
                }
                headerIsOver = true;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
            /*
            try{
                save.write("\n" + saveHistory());
            } catch (NullPointerException e){
                throw new ManagerException(e.getMessage());
            }

             */
        } catch (IOException e) {
            throw new ManagerException(e.getMessage());
        }
    }


    public static Task fromString(String s) {
        /** /
         String t = "1,TASK,'Task 1',NEW,'Description by Task 1',";
         String regex2 = "\\d,\\D*,\\'\\w.*\\',\\D*,\\'\\w.*\\',\\d*";
         //regex2 = "(\\d),(\\D*),\\'(\\w.*)\\',(\\D*),\\'(\\w.*)\\',(\\d*)";
         Pattern p = Pattern.compile(regex2);
         Matcher m = p.matcher(t);
         while (m.find()) {
         System.out.println("strlen = " + t.length());
         System.out.println(m.start() + "<start | end>" +  m.end());
         System.out.println(t.substring(m.start(), m.end()));
         }
         return new Task();
         /**/
        s = "1,TASK,'Task 1',NEW,'Description by Task 1',";
        String[] array = s.split(",");
        int id = Integer.parseInt(array[0]);
        TypeTask typeTask = TypeTask.valueOf(array[1]);
        String title = array[2].substring(1, array[2].length() - 1);
        Status status = Status.valueOf(array[3]);
        String description = array[4].substring(1, array[4].length() - 1);
        int epicId = 0;
        if (array.length == 6 && typeTask.equals(TypeTask.SUBTASK)) {
            epicId = Integer.parseInt(array[5]);
        }
        return new Task(id, title, description, status);/**/
    }

    public List<Integer> historyFromString(String string) {
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
     * @return
     */
    public String historyToString(HistoryManager manager) {

        try {
            StringBuilder out = new StringBuilder();
            for (Task task : manager.getHistory()) {
                out.append(task.getId()).append(",");
            }

            return out.substring(0, out.lastIndexOf(","));

        } catch (NullPointerException e) {
            throw new ManagerException("History is empty: " + e.getMessage());
        }
    }

    /**
     * @param epic объект Большая задача
     * @return Номер новой записи
     */
    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    /**
     * @param subTask Подзадача для Большой задачи
     * @return Номер новой записи
     */
    @Override
    public int addSubTask(SubTask subTask) {
        int id = super.addSubTask(subTask);
        save();
        return id;
    }

    /**
     * @param task объект задача
     * @return Номер новой записи
     */
    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }
}