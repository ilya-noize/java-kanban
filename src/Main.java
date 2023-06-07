import http.client.KVTaskClient;
import http.server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        new KVTaskClient("http://localhost:8078/");

        //HttpTaskServer httpTaskServer = new HttpTaskServer(
        //        HttpServer.create(new InetSocketAddress(8080), 0));
        //httpTaskServer.start();
    }
}
