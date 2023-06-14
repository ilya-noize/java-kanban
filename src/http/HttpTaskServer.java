package http;

import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import manager.Managers;
import manager.history.HistoryManager;
import manager.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static http.Config.PORTS.HTTP;


public class HttpTaskServer {
    private static final int PORT = HTTP.get();
    private final HttpServer httpServer;

    /**
     * API должен работать так, чтобы все запросы по пути /tasks/<ресурсы> приходили
     * в интерфейс TaskManager.
     * @throws IOException if troubles 'create' , 'bind' throws IOException
     */
    public HttpTaskServer() throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getFileBackedTaskManager(historyManager);

        this.httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new OwnerHandler(manager));
        httpServer.createContext("/tasks/task/", new TaskHandler(manager));
        httpServer.createContext("/tasks/subtask/", new SubTaskHandler(manager));
        httpServer.createContext("/tasks/subtask/epic/", new SubTaskByEpicHandler(manager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(manager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(manager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(3);
    }
}