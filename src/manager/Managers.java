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

    public static TaskManager getDefaultMemoryTask() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultBackedTasks(){
        return new FileBackedTasksManager();
    }
}
