package manager;

import http.HTTPTaskManager;
import http.server.KVServer;

import java.io.IOException;

/**
 * Cлужебный класс Managers;
 * Cтатический метод HistoryManager getDefaultHistory().
 * Он должен возвращать объект InMemoryHistoryManager — историю просмотров.
 */
public class Managers {
    public static TaskManager getFileBackedTaskManager(HistoryManager historyManager) {
        return new FileBackedTasksManager(historyManager);
    }

    public static HTTPTaskManager getDefault(HistoryManager historyManager) throws IOException, InterruptedException {
        return new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT);
    }

    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
