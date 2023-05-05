package manager;
/**
 * Cлужебный класс Managers;
 * Cтатический метод HistoryManager getDefaultHistory().
 * Он должен возвращать объект InMemoryHistoryManager — историю просмотров.
 */
public class Managers {
    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
