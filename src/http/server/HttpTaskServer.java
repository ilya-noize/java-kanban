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
     * @throws IOException if troubles 'create' , 'bind' throws IOException
     */
    public HttpTaskServer() throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getFileBackedTaskManager(historyManager);

        this.httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler(manager));
        httpServer.createContext("/tasks/subtask/", new SubTaskHandler(manager));
        httpServer.createContext("/tasks/subtask/epic/", new SubTaskByEpicHandler(manager));
        httpServer.createContext("/tasks/subtask/", new EpicHandler(manager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(manager));
        httpServer.createContext("/tasks/", new OwnerHandler(manager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(3);
    }
}