package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new ArrayList<>();
    private static final int LIMIT_RECORDS = 10;

    /**
     * Добавление задачи в список просмотренных задач
     *
     * @param task задача
     */
    @Override
    public void addTask(Task task) {
        if (taskHistory.size() > LIMIT_RECORDS - 1) {
            taskHistory.remove(0);
        }
        taskHistory.add(task);
    }

    /**
     * Получение списка истории просмотров задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
