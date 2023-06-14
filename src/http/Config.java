package http;

public class Config {
    public enum PORTS {
        Server(8078),
        HTTP(8080);

        final int port;

        PORTS(int port) {
            this.port = port;
        }

        public int get() {
            return port;
        }
    }
}