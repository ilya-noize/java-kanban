package http.handlers.subtask.epic;

import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SubTaskByEpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final TaskManager taskManager;

    public SubTaskByEpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обработка запроса " + method + " : " + path);

        if ("GET".equals(method)) {
            String query = exchange.getRequestURI().getQuery();
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                statusCode = 200;
                response = gson.toJson(taskManager.getSubTask(id));
            } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                response = "В запросе отсутствует необходимый параметр - id";
            } catch (NumberFormatException e) {
                response = "Неверный формат id";
            }
        } else {
            response = "Некорректный запрос";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + UTF_8);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
