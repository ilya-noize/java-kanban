package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.task.TaskManager;
import tasks.SubTask;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SubTaskHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
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
                    response = gson.toJson(taskManager.getAllSubTasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        SubTask subTask = taskManager.getSubTask(id);
                        if (subTask != null) {
                            response = gson.toJson(subTask);
                        } else {
                            response = "Подзадача с данным id не найдена";
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
                    SubTask subTask = gson.fromJson(bodyRequest, SubTask.class);
                    int id = subTask.getId();
                    if (taskManager.getSubTask(id) != null) {
                        taskManager.updateTask(subTask);
                        statusCode = 200;
                        response = "Подзадача с id=" + id + " обновлена";
                    } else {
                        statusCode = 201;
                        SubTask newSubTask = taskManager.addSubTask(subTask);
                        System.out.println("CREATED SUBTASK: " + newSubTask);
                        response = "Создана подзадача с id=" + newSubTask.getId();
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllSubTasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteSubTask(id);
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
