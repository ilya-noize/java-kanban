package http.handlers.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TaskHandler implements HttpHandler { //todo


    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getAllTasks());
                    System.out.println("GET TASKS: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManager.getTask(id);
                        if (task != null) {
                            response = gson.toJson(task);
                        } else {
                            response = "Задача с id = " + id + " не найдена";
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
                String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    int id = task.getId();
                    if (taskManager.getTask(id) != null) {
                        taskManager.updateTask(task);
                        statusCode = 201;
                        response = "Задача с id=" + id + " обновлена";
                    } else {
                        statusCode = 201;
                        Task newTask = taskManager.addTask(task);
                        System.out.println("CREATED TASK: " + newTask);
                        response = "Создана задача с id=" + newTask.getId();
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllTasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteTask(id);
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

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + UTF_8);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    /*
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            //HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    */
}