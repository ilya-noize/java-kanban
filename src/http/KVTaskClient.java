package http;

import exception.ManagerException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static http.KVServer.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {
    private final String apiToken;

    public KVTaskClient() throws IOException, InterruptedException {
        URI uri = URI.create(KV_HOST + REGISTER_PATH);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, //send throws IOException, InterruptedException
                BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            this.apiToken = response.body();
        } else {
            throw new ManagerException("Не удалось завершить регистрацию клиента.");
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(KV_HOST + SAVE_PATH + "/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest.BodyPublisher body = BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    BodyHandlers.ofString(UTF_8));
            if (response.statusCode() != 200) {
                System.out.println("Не удалось сохранить данные");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerException("Во время POST-запроса по url произошла ошибка.");
        }
    }

    public String load(String key) {
        URI uri = URI.create(KV_HOST + LOAD_PATH + "/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    BodyHandlers.ofString(UTF_8)
            );
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Не удалось загрузить данные");
                return "Не удалось загрузить данные";
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerException("Во время GET-запроса по url произошла ошибка.");
        }
    }
}
