package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static Gson getGson() {// todo Спринт8?
        GsonBuilder gsonBuilder = new GsonBuilder();
        // todo Сделать реализацию class LocalDateTimeAdapter()
        //gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
