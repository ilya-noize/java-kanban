package http;

import manager.Managers;
import manager.TaskManagerTest;
import manager.history.HistoryManager;
import manager.task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            manager = Managers.getDefault(historyManager);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task("Task 1",
                "Description of Task 1",
                LocalDateTime.of(2022, 12, 30, 19, 0, 0),
                "PT15M");
        Task task2 = new Task("Task 2",
                "Description of Task 2",
                LocalDateTime.of(2022, 12, 30, 19, 30, 0),
                "PT30M");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic("Epic 1", "Description by Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description by Epic 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpic(epic1.getId());
        manager.getEpic(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Description by Epic 1");
        epic1 = manager.addEpic(epic1);
        int epicId = epic1.getId();
        SubTask subTask1 = new SubTask("SubTask 1", "Description by SubTask 1",
                LocalDateTime.of(2022, 12, 31, 12, 0, 0),
                "PT20M", epicId);
        SubTask subTask2 = new SubTask("SubTask 2", "Description by SubTask 2",
                LocalDateTime.of(2022, 12, 31, 12, 30, 0),
                "PT120M", epicId);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.getSubTask(subTask1.getId());
        manager.getSubTask(subTask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllSubTasks(), list);
    }
}