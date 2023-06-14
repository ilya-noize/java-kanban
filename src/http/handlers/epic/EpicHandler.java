package http.handlers.epic;

import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обработка запроса " + method + " : " + path);

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getAllEpics());
                    System.out.println("GET EPICS: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            response = gson.toJson(epic);
                        } else {
                            response = "Главная задача с данным id не найдена";
                        }
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        response = "Неверный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
                try {
                    Epic epic = gson.fromJson(bodyRequest, Epic.class);
                    int id = epic.getId();
                    if (taskManager.getEpic(id) != null) {
                        statusCode = 200;
                        taskManager.updateTask(epic);
                        response = "Эпик с id=" + id + " обновлен";
                    } else {
                        statusCode = 201;
                        Epic newEpic = taskManager.addEpic(epic);
                        System.out.println("CREATED EPIC: " + newEpic);
                        response = "Создан эпик с id=" + newEpic.getId();
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllEpics();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteEpic(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                response = "Некорректный запрос";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + UTF_8);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
