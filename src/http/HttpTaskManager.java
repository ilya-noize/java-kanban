package http;

import com.google.gson.*;
import manager.history.HistoryManager;
import manager.task.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTasksManager {
    public static final String TASKS = "tasks";
    public static final String SUBTASKS = "subtasks";
    public static final String EPICS = "epics";
    public static final String HISTORY = "history";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final KVTaskClient client;

    public HttpTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        client = new KVTaskClient(path);

        JsonElement jsonTasks = JsonParser.parseString(client.load(TASKS));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                newTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(EPICS));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                newEpic(task);
            }
        }

        JsonElement jsonSubTasks = JsonParser.parseString(client.load(SUBTASKS));
        if (!jsonSubTasks.isJsonNull()) {
            JsonArray jsonSubTasksArray = jsonSubTasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubTasksArray) {
                SubTask task = gson.fromJson(jsonSubtask, SubTask.class);
                newSubTask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(HISTORY));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int id = jsonTaskId.getAsInt();
                if (epics.containsKey(id)) {
                    getEpic(id);
                } else if (subtasks.containsKey(id)) {
                    getSubTask(id);
                } else if (tasks.containsKey(id)) {
                    getTask(id);
                }
            }
        }
    }

    @Override
    public void save() {
        client.put(TASKS, gson.toJson(tasks.values()));
        client.put(SUBTASKS, gson.toJson(subtasks.values()));
        client.put(EPICS, gson.toJson(epics.values()));
        client.put(HISTORY, gson.toJson(getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }


}
