package http.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import http.KVServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

class HttpTaskServerTest {
    private static final String TASK_URL = "http://localhost:8080/tasks/task/";
    private static final String EPIC_URL = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_URL = "http://localhost:8080/tasks/subtask/";

    private static final Task TASK_1 = new Task("Task 1", "Description of Task 1",
            LocalDateTime.of(2022, 12, 30, 19, 0, 0), "PT15M");

    private static final Epic EPIC_1 = new Epic("Epic 1", "Description by Epic 1");

    private static final SubTask SUBTASK_1 = new SubTask("SubTask 1", "Description by SubTask 1",
            LocalDateTime.of(2022, 12, 31, 12, 0, 0), "PT20M", 0);

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTimeAdapter.class, new LocalDateTimeAdapter())
            .create();

    private static KVServer kvServer;
    private static HttpTaskServer taskServer;

    @BeforeAll
    @DisplayName(value = "Запуск серверов")
    static void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @AfterAll
    @DisplayName(value = "Остановка серверов")
    static void stopServer() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    @DisplayName(value = "Добавить задачи")
    void methodPostByTasks() {

    }
}