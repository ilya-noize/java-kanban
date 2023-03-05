package manager;

import java.util.List;

public interface HistoryManager {

    void addTask(tasks.Task task);

    List<tasks.Task> getHistory();
}