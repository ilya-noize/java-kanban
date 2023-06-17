package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static http.Config.PORTS.KV;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <h3>Постман: <a href="https://www.getpostman.com/collections/a83b61d9e1c81c10575c">getpostman.com</a></h3>
 */
public class KVServer {
    public static final String PROTOCOL = "http://";
    public static final String HOSTNAME = "localhost";
    public static final int KV_PORT = KV.get();
    public static final String KV_HOST = String.format("%s%s:%d", PROTOCOL, HOSTNAME, KV_PORT);

    public static final String REGISTER_PATH = "/register";
    public static final String SAVE_PATH = "/save";
    public static final String LOAD_PATH = "/load";
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress(HOSTNAME, KV_PORT), 0);
        server.createContext(REGISTER_PATH, this::register);
        server.createContext(SAVE_PATH, this::save);
        server.createContext(LOAD_PATH, this::load);
    }

    private void load(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n" + LOAD_PATH);
            if (!hasAuth(httpExchange)) {
                System.out.println("▓ The query is not authorized, " +
                        "you need the query API_TOKEN parameter with the API-key value");
                httpExchange.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring((LOAD_PATH + "/").length());
                if (key.isEmpty()) {
                    System.out.println("▓ Key to save empty. Key is specified in the path: " + LOAD_PATH + "/{key}");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                if (data.get(key) == null) {
                    System.out.println("▓ Cannot get the data for the '" + key + "' key, no data\t" + KVServer.this);
                    httpExchange.sendResponseHeaders(404, 0);
                    return;
                }
                String response = data.get(key);
                sendText(httpExchange, response);
                System.out.println("▓ The value for the key '" + key + "' has been successfully sent in response to the request!");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println(LOAD_PATH + " waits for the GET-request, but got " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void save(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n" + SAVE_PATH);
            if (!hasAuth(httpExchange)) {
                System.out.println("▒ Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                httpExchange.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring((SAVE_PATH + "/").length());
                if (key.isEmpty()) {
                    System.out.println("▒ Key для сохранения пустой. key указывается в пути: " + SAVE_PATH + "/{key}");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(httpExchange);
                if (value.isEmpty()) {
                    System.out.println("▒ Value для сохранения пустой. value указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("▒ Значение для ключа " + key + " успешно обновлено!");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println(SAVE_PATH + " ждёт POST-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void register(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n" + REGISTER_PATH);
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                System.out.println(REGISTER_PATH + " ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("░ Запускаем сервер на порту " + KV_PORT);
        System.out.println("░ Открой в браузере " + KV_HOST);
        System.out.println("░ API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("▓ На " + KV_PORT + " порту сервер остановлен!");
    }

    private String generateApiToken() {
        return System.currentTimeMillis() + "";
    }

    protected boolean hasAuth(HttpExchange httpExchange) {
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }
}