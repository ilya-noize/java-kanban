package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.task.TaskManager;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OwnerHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final TaskManager taskManager;

    public OwnerHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getQuery();

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        if ("GET".equals(method)) {
            response = gson.toJson(taskManager.getPrioritizedTasks());
            writeResponse(response, exchange, 200);
        } else {
            writeResponse("Некорректный запрос", exchange, 405);
        }
    }

    private void writeResponse(String body, HttpExchange exchange, int code) throws IOException {
        byte[] responseBody = body.getBytes(UTF_8);
        exchange.sendResponseHeaders(code, responseBody.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBody);
        } finally {
            exchange.close();
        }
    }
}
