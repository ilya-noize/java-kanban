package http;

public class Config {
    public enum PORTS {
        KV(8078),
        HTTP(8080);

        final int port;

        PORTS(int port) {
            this.port = port;
        }

        public int get() {
            return port;
        }
    }

    public enum PATHS {
        ROOT_TASK("/tasks"),
        PATH_TASK(ROOT_TASK + "/task"),
        PATH_SUBTASK(ROOT_TASK + "/subtask"),
        PATH_SUBTASK_BY_EPIC(PATH_SUBTASK + "/epic"),
        PATH_EPIC(ROOT_TASK + "/epic"),
        PATH_HISTORY(ROOT_TASK + "/history");

        final String path;

        PATHS(String path) {
            this.path = path;
        }

        public String get() {
            return path;
        }
    }
}