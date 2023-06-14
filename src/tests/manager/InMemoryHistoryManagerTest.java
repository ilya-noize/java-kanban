package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private static final LocalDateTime TIME_NOW = LocalDateTime.now();
    private HistoryManager manager;
    private int id = 0;

    public int generateId() {
        return ++id;
    }

    protected Task newTask() {
        return new Task("Task 1", "Description by Task 1", TIME_NOW, "PT0S");
    }

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName(value = "Добавление задач в историю")
    void shouldAddTasks() {
        Task task1 = newTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = newTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = newTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        assertEquals(List.of(task1, task2, task3), manager.getHistory());
    }

    @Test
    @DisplayName(value = "Удаление задач из заполненной истории")
    void shouldRemoveTask() {
        Task task1 = newTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = newTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = newTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task2.getId());
        assertEquals(List.of(task1, task3), manager.getHistory());
    }

    @Test
    @DisplayName(value = "Удаление одной задачи из заполненной истории")
    void shouldRemoveOnlyOneTask() {
        Task task = newTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        manager.add(task);
        manager.remove(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    @DisplayName(value = "Удаление задач которых нет в истории " +
            "вернёт пустую коллекцию")
    void shouldHistoryIsEmpty() {
        Task task1 = newTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = newTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = newTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        manager.remove(task1.getId());
        manager.remove(task2.getId());
        manager.remove(task3.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    @DisplayName(value = "Удаление задачи c неверным id игнорируется")
    void shouldIgnoredRemoveTaskWithInvalidId() {
        Task task = newTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        manager.add(task);
        manager.remove(0);
        assertEquals(List.of(task), manager.getHistory());
    }
}