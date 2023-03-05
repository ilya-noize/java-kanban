package manager;

/**
 * Cлужебный класс Managers;
 * Cтатический метод HistoryManager getDefaultHistory().
 * Он должен возвращать объект InMemoryHistoryManager — историю просмотров.
 */
public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }
}
