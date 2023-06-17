import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.KVServer;
import manager.Managers;
import manager.history.HistoryManager;
import manager.task.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class Main {
    static KVServer kv;
    public static void main(String[] args) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();

            kv = new KVServer();
            kv.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task1 = Demo.TASKS.TASK_1.getTask();
            httpTaskManager.addTask(task1);

            Task task2 = Demo.TASKS.TASK_2.getTask();
            httpTaskManager.addTask(task2);

            Epic epic1 = Demo.EPICS.EPIC_1.getEpic();
            epic1 = httpTaskManager.addEpic(epic1);
            int epic1Id = epic1.getId();

            Epic epic2 = Demo.EPICS.EPIC_2.getEpic();
            httpTaskManager.addEpic(epic2);


            SubTask subTask1 = Demo.SUBTASKS.SUBTASK_1.getSubTask();
            subTask1.setEpicId(epic1Id);
            httpTaskManager.addSubTask(subTask1);

            SubTask subTask2 = Demo.SUBTASKS.SUBTASK_2.getSubTask();
            subTask2.setEpicId(epic1Id);
            httpTaskManager.addSubTask(subTask2);

            SubTask subTask3 = Demo.SUBTASKS.SUBTASK_3.getSubTask();
            subTask3.setEpicId(epic1Id);
            httpTaskManager.addSubTask(subTask3);


            httpTaskManager.getTask(task1.getId());
            httpTaskManager.getEpic(epic1.getId());
            httpTaskManager.getSubTask(subTask1.getId());

            System.out.println("░ getAllTasks    ▲▼▲▼▲▼▲▼");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("░ getAllEpics    ▲▼▲▼▲▼▲▼");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("░ getAllSubTasks ▲▼▲▼▲▼▲▼");
            System.out.println(gson.toJson(httpTaskManager.getAllSubTasks()));
            System.out.println("░ getHistory     ▲▼▲▼▲▼▲▼");
            System.out.println(gson.toJson(httpTaskManager.getHistory()));
            System.out.println("░ getCurrentManager  ▲▼▲▼");
            System.out.println(httpTaskManager);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kv.stop();
        }
    }
}
