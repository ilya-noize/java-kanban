package http;

import com.google.gson.*;
import exception.ManagerException;
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
    private static final String TASKS = "tasks";
    private static final String SUBTASKS = "subtasks";
    private static final String EPICS = "epics";
    private static final String HISTORY = "history";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final KVTaskClient client = new KVTaskClient();

    public HttpTaskManager(HistoryManager historyManager) throws IOException, InterruptedException {
        super(historyManager);

        String loadTasks = client.load(TASKS);
        String loadEpics = client.load(EPICS);
        String loadHistory = client.load(HISTORY);

        if (loadTasks.isEmpty()) {
            JsonElement jsonTasks = JsonParser.parseString(loadTasks);
            if (!jsonTasks.isJsonNull()) {
                JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
                for (JsonElement jsonTask : jsonTasksArray) {
                    gson.fromJson(jsonTask, Task.class);
                }
            }
        }

        if (loadEpics.isEmpty()) {
            JsonElement jsonEpics = JsonParser.parseString(loadEpics);
            if (!jsonEpics.isJsonNull()) {
                JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
                for (JsonElement jsonEpic : jsonEpicsArray) {
                    gson.fromJson(jsonEpic, Epic.class);
                }
            }

            JsonElement jsonSubTasks = JsonParser.parseString(client.load(SUBTASKS));
            if (!jsonSubTasks.isJsonNull()) {
                JsonArray jsonSubTasksArray = jsonSubTasks.getAsJsonArray();
                for (JsonElement jsonSubtask : jsonSubTasksArray) {
                    gson.fromJson(jsonSubtask, SubTask.class);
                }
            }
        }

        if (loadHistory.isEmpty()) {
            JsonElement jsonHistoryList = JsonParser.parseString(loadHistory);
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
    }

    @Override
    public void save() {
        try {
            client.put(TASKS, gson.toJson(tasks.values()));
            client.put(SUBTASKS, gson.toJson(subtasks.values()));
            client.put(EPICS, gson.toJson(epics.values()));
            client.put(HISTORY, gson.toJson(getHistory()
                    .stream()
                    .map(Task::getId)
                    .collect(Collectors.toList())));
        } catch (ManagerException e) {
            throw new ManagerException("▓▒░ Критическая ошибка сохранения на сервер", e.getCause());
        }
    }
}