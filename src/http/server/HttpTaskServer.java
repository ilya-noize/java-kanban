package http.server;

import com.sun.net.httpserver.HttpServer;
import http.handlers.OwnerHandler;
import http.handlers.epic.EpicHandler;
import http.handlers.history.HistoryHandler;
import http.handlers.subtask.SubTaskHandler;
import http.handlers.subtask.epic.SubTaskByEpicHandler;
import http.handlers.task.TaskHandler;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static http.Config.PATHS.*;
import static http.Config.PORTS.HTTP;


public class HttpTaskServer {
    private static final int PORT = HTTP.get();
    private final HttpServer httpServer;

    /**
     * API должен работать так, чтобы все запросы по пути /tasks/<ресурсы> приходили
     * в интерфейс TaskManager.
     * Путь для обычных задач — /tasks/task ,
     * для подзадач — /tasks/subtask ,
     * для эпиков — /tasks/epic.
     * Получить все задачи сразу можно по пути /tasks/ ,
     * а получить историю задач по пути /tasks/history .
     * Методы:
     * Для получения данных должны быть GET-запросы.
     * Для создания и изменения — POST-запросы.
     * Для удаления — DELETE-запросы.
     *
     * @param httpServer HTTP-сервер API
     * @throws IOException if troubles 'create' , 'bind' throws IOException
     */
    public HttpTaskServer(HttpServer httpServer) throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getFileBackedTaskManager(historyManager);

        this.httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext(PATH_TASK.get(), new TaskHandler(manager));
        httpServer.createContext(PATH_SUBTASK.get(), new SubTaskHandler(manager));
        httpServer.createContext(PATH_SUBTASK_BY_EPIC.get(), new SubTaskByEpicHandler(manager));
        httpServer.createContext(PATH_EPIC.get(), new EpicHandler(manager));
        httpServer.createContext(PATH_HISTORY.get(), new HistoryHandler(manager));
        httpServer.createContext(ROOT_TASK.get(), new OwnerHandler(manager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(3);
    }
}