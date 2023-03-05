package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final List<Task> TASK_HISTORY = new ArrayList<>();
    private final int LIMIT_RECORDS = 10;

    /**
     * Добавление задачи в список просмотренных задач
     *
     * @param task задача
     */
    @Override
    public void addTask(Task task) {
        if (TASK_HISTORY.size() > LIMIT_RECORDS - 1) {
            TASK_HISTORY.remove(0);
        }
        TASK_HISTORY.add(task);
    }

    /**
     * Получение списка истории просмотров задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        return TASK_HISTORY;
    }
}
