package utils;

import manager.HistoryManager;
import tasks.*;

import static tasks.TypeTask.*;

public class CSVUtils {
    private static final String SEPARATOR_CSV = ",";

    public static String toString(Task task) {
        TypeTask typeTask = task.getType();
        if (typeTask.equals(SUBTASK)) {
            return subTaskToString((SubTask) task);
        } else if (typeTask.equals(EPIC)) {
            return epicToString((Epic) task);
        } else
            return taskToString(task);
    }

    public static String subTaskToString(SubTask epic) {
        return epic.getId()
                + SEPARATOR_CSV + SUBTASK
                + SEPARATOR_CSV + "'" + epic.getTitle() + "'"
                + SEPARATOR_CSV + epic.getStatus()
                + SEPARATOR_CSV + "'" + epic.getDescription() + "'"
                + SEPARATOR_CSV + epic.getEpicId() + "\n";
    }

    public static String epicToString(Epic task) {
        return task.getId()
                + SEPARATOR_CSV + EPIC
                + SEPARATOR_CSV + "'" + task.getTitle() + "'"
                + SEPARATOR_CSV + task.getStatus()
                + SEPARATOR_CSV + "'" + task.getDescription() + "'"
                + SEPARATOR_CSV + "\n";
    }

    public static String taskToString(Task subTask) {
        return subTask.getId()
                + SEPARATOR_CSV + TASK
                + SEPARATOR_CSV + "'" + subTask.getTitle() + "'"
                + SEPARATOR_CSV + subTask.getStatus()
                + SEPARATOR_CSV + "'" + subTask.getDescription() + "'"
                + SEPARATOR_CSV + "\n";
    }

    /**
     * Создание задачи из строки
     *
     * @param line Массив строк формата {1,TASK,'Task 1',NEW,'Description by Task 1',};
     * @return (Задача / Эпик)/ПодЗадача.
     */
    public static Task fromString(String line) {
        String[] array = line.split(SEPARATOR_CSV);
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
     *
     * @param stringInQuotes 'исходная строка в кавычках'
     * @return полученная строка без кавычек
     */
    private static String quoteOff(String stringInQuotes) {
        return stringInQuotes.substring(1, stringInQuotes.length() - 1);
    }

    /**
     * Для сохранения истории в CSV
     *
     * @return Строка для сохранения в файл
     */
    public static String historyToString(HistoryManager manager) {
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

    public static String[] historyFromString(String stringWithHistory) {
        return stringWithHistory.split(SEPARATOR_CSV);
    }
}
