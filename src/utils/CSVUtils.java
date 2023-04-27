package utils;

import manager.HistoryManager;
import tasks.*;

import static tasks.TypeTask.EPIC;
import static tasks.TypeTask.SUBTASK;

public class CSVUtils {
    private static final String SEPARATOR_CSV = ",";
    private static final int LENGTH_SUBTASK_ARRAY = 8;

    public static String toString(Task task) {
        TypeTask typeTask = task.getType();
        if (typeTask.equals(SUBTASK)) {
            return subTaskToString((SubTask) task);
        } else if (typeTask.equals(EPIC)) {
            return epicToString((Epic) task);
        } else
            return taskToString(task);
    }

    public static String epicToString(Epic epic) {
        return epic.getId() + SEPARATOR_CSV
                + epic.getType() + SEPARATOR_CSV
                + "'" + epic.getTitle() + "'" + SEPARATOR_CSV
                + epic.getStatus() + SEPARATOR_CSV
                + "'" + epic.getDescription() + "'" + SEPARATOR_CSV
                + "'" + epic.getStartTime() + "'" + SEPARATOR_CSV
                + "'" + epic.getDuration() + "'" + SEPARATOR_CSV
                + "\n";
    }

    public static String taskToString(Task task) {
        return task.getId() + SEPARATOR_CSV
                + task.getType() + SEPARATOR_CSV
                + "'" + task.getTitle() + "'" + SEPARATOR_CSV
                + task.getStatus() + SEPARATOR_CSV
                + "'" + task.getDescription() + "'" + SEPARATOR_CSV
                + "'" + task.getStartTime() + "'" + SEPARATOR_CSV
                + "'" + task.getDuration() + "'" + SEPARATOR_CSV
                + "\n";
    }

    public static String subTaskToString(SubTask subTask) {
        return subTask.getId() + SEPARATOR_CSV
                + subTask.getType() + SEPARATOR_CSV
                + "'" + subTask.getTitle() + "'" + SEPARATOR_CSV
                + subTask.getStatus() + SEPARATOR_CSV
                + "'" + subTask.getDescription() + "'" + SEPARATOR_CSV
                + "'" + subTask.getStartTime() + "'" + SEPARATOR_CSV
                + "'" + subTask.getDuration() + "'" + SEPARATOR_CSV
                + subTask.getEpicId() + "\n";
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
        Status status = Status.valueOf(array[3]);
        String description = quoteOff(array[4]);
        String startTime = quoteOff(array[5]);
        String duration = quoteOff(array[6]);

        if (typeTask.equals(SUBTASK) && array.length == LENGTH_SUBTASK_ARRAY) {
            return new SubTask(id, title, description, status, startTime, duration, Integer.parseInt(array[7]));
        } else if (typeTask.equals(EPIC)) {
            return new Epic(id, title, description, status, startTime, duration);
        } else {
            return new Task(id, title, description, status, startTime, duration);
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
            out.append(task.getId()).append(SEPARATOR_CSV);
        }
        if ((end = out.lastIndexOf(SEPARATOR_CSV)) != -1) {
            return out.substring(0, end);
        }
        return "";
    }

    public static String[] historyFromString(String stringWithHistory) {
        return stringWithHistory.split(SEPARATOR_CSV);
    }
}
