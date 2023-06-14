package http.server;

import http.HttpTaskServer;
import http.KVServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

class HttpTaskServerTest {

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
}