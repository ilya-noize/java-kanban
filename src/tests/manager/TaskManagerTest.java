package manager;

import exception.ManagerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    private static final LocalDateTime TIME_NOW = LocalDateTime.now();
    private static final int WRONG_ID = 123;
    protected T manager;

    protected Task newTask() {
        return new Task("Task 1", "Description by Task 1", TIME_NOW, "PT0S");
    }

    protected Epic newEpic() {
        return new Epic("Epic 1", "Description by Epic 1");
    }

    protected SubTask newSubTask(Epic epic) {
        return new SubTask("SubTask 1", "Description by SubTask 1", TIME_NOW, "PT20M", epic.getId());
    }

    @Test
    @DisplayName(value = "Создаст задачу со статусом NEW и внутри списка всех эпиков")
    public void shouldAddTask() {
        Task task = newTask();
        manager.addTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }


    @Test
    @DisplayName(value = "Создаст эпик с пустым списком подзадач, статусом NEW" +
            " и внутри списка всех эпиков")
    public void shouldAddEpic() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertEquals(Collections.EMPTY_LIST, epic.getSubTaskIds());
        assertEquals(NEW, epic.getStatus());
        assertEquals(List.of(epic), epics);
    }


    @Test
    @DisplayName(value = "Создав Эпик, создаётся подзадача со статусом NEW," +
            " внутри списка всех подзадач, привязанную к этому эпику через поле epicId")
    public void shouldAddSubTask() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        SubTask subTask = newSubTask(epic);
        manager.addSubTask(subTask);
        List<SubTask> subTasks = manager.getAllSubTasks();
        assertEquals(NEW, subTask.getStatus());
        assertEquals(epic.getId(), subTask.getEpicId());
        assertEquals(List.of(subTask), subTasks);

    }

    @Test
    @DisplayName(value = "Создав подзадачу, без созданного эпика, вернёт исключение")
    public void shouldAddSubTaskWithoutEpic() {
        Epic epic = newEpic();
        SubTask subTask = newSubTask(epic);
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.addSubTask(subTask));
        assertEquals("Сначала создайте главную задачу.", exception.getMessage(), "Task added.");
    }

    @Test
    @DisplayName(value = "Вернёт исключение, если эпик - null")
    void shouldReturnNullWhenAddEpicNull() {
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.addEpic(null));
        assertEquals("Задача не создана", exception.getMessage(), "Task added.");
    }

    @Test
    @DisplayName(value = "Вернёт исключение, если подзадача - null")
    void shouldReturnNullWhenAddSubTaskNull() {
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.addSubTask(null));
        assertEquals("Задача не создана", exception.getMessage(), "Task added.");
    }


    @Test
    @DisplayName(value = "Вернёт исключение с null, если задача - null")
    void shouldReturnNullWhenAddTaskNull() {
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.addTask(null));
        assertEquals("Задача не создана", exception.getMessage(), "Task added.");
    }

    @Test
    @DisplayName(value = "Статус Задачи обновлён до IN_PROGRESS")
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = newTask();
        manager.addTask(task);
        task.setStatus(IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    @DisplayName(value = "Статус Эпика обновлён до IN_PROGRESS")
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        epic.setStatus(IN_PROGRESS);
        manager.updateEpic(epic);
        assertEquals(IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    @DisplayName(value = "Обновление статуса подзадачи на IN_PROGRESS" +
            " повлечёт обновление статуса эпика до такого же статуса")
    public void shouldUpdateSubTaskStatusToInProgress() {
        Epic epic = newEpic();
        Epic epic1 = manager.addEpic(epic);
        SubTask subTask = newSubTask(epic1);
        subTask = manager.addSubTask(subTask);
        subTask.setStatus(IN_PROGRESS);
        manager.updateSubTask(subTask);
        assertEquals(IN_PROGRESS, subTask.getStatus());
        assertEquals(IN_PROGRESS, epic1.getStatus());
    }

    @Test
    @DisplayName(value = "Обновление статуса задачи до DONE")
    public void shouldUpdateTaskStatusToInDone() {
        Task task = newTask();
        manager.addTask(task);
        task.setStatus(DONE);
        manager.updateTask(task);
        assertEquals(DONE, manager.getTask(task.getId()).getStatus());
    }

    @Test
    @DisplayName(value = "Обновление статуса эпика до DONE")
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        epic.setStatus(DONE);
        assertEquals(DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    @DisplayName(value = "Обновление статуса единственной подзадачи на DONE" +
            " повлечёт обновление статуса эпика до такого же статуса")
    public void shouldUpdateSubTaskStatusToInDone() {
        Epic epic = newEpic();
        Epic epic1 = manager.addEpic(epic);
        SubTask subTask = newSubTask(epic);
        SubTask subTask1 = manager.addSubTask(subTask);
        subTask.setStatus(DONE);
        manager.updateSubTask(subTask);
        assertEquals(DONE, subTask1.getStatus());
        assertEquals(DONE, epic1.getStatus());
    }

    @Test
    @DisplayName(value = "Обновление null-задачи игнорируется")
    public void shouldNotUpdateTaskIfNull() {
        Task task = newTask();
        manager.addTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    @DisplayName(value = "Обновление null-эпика игнорируется")
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @Test
    @DisplayName(value = "Обновление null-подзадачи игнорируется")
    public void shouldNotUpdateSubTaskIfNull() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        SubTask subTask = newSubTask(epic);
        manager.addSubTask(subTask);
        manager.updateSubTask(null);
        assertEquals(subTask, manager.getSubTask(subTask.getId()));
    }

    @Test
    @DisplayName(value = "Удаление всех задач приводит к очистке соответствующей hashmap")
    public void shouldDeleteAllTasks() {
        Task task = newTask();
        manager.addTask(task);
        manager.deleteAllTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    @DisplayName(value = "Удаление всех эпиков приводит к очистке соответствующей hashmap" +
            "и удалением всех подзадач в соответствующей hashmap")
    public void shouldDeleteAllEpics() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        manager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
    }

    @Test
    @DisplayName(value = "Удаление всех подзадач приводит к очистке соответствующей hashmap " +
            "и удалением связей 'эпик-подзадача' в поле эпика List<Integer> subTaskIds;")
    public void shouldDeleteAllSubTasks() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        SubTask subTask = newSubTask(epic);
        manager.addSubTask(subTask);
        manager.deleteAllSubTasks();
        assertTrue(epic.getSubTaskIds().isEmpty());
        assertTrue(manager.getAllSubTasks().isEmpty());
    }

    @Test
    @DisplayName(value = "Удаление единственной задачи опустошает соответствующий hashmap")
    public void shouldDeleteTask() {
        Task task = newTask();
        manager.addTask(task);
        manager.deleteTask(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    @DisplayName(value = "Удаление единственного эпика опустошает соответствующий hashmap")
    public void shouldDeleteEpic() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        manager.deleteEpic(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
    }

    @Test
    @DisplayName(value = "Удаление задачи с несуществующим номером не влияет на соответствующий hashmap")
    public void shouldNotDeleteTaskIfInvalidId() {
        Task task = newTask();
        manager.addTask(task);
        manager.deleteTask(WRONG_ID);
        assertEquals(List.of(task), manager.getAllTasks());
    }

    @DisplayName(value = "Удаление эпика с несуществующим номером не влияет на соответствующий hashmap")
    @Test
    public void shouldNotDeleteEpicIfInvalidId() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        manager.deleteEpic(WRONG_ID);
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    @DisplayName(value = "Удаление подзадачи с несуществующим номером не влияет на соответствующий hashmap," +
            "а так же и на поле subTaskIds всех эпиков")
    public void shouldNotDeleteSubTaskIfInvalidId() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        SubTask subTask = newSubTask(epic);
        manager.addSubTask(subTask);
        manager.deleteSubTask(WRONG_ID);
        assertEquals(List.of(subTask), manager.getAllSubTasks());
        assertEquals(List.of(subTask.getId()), manager.getEpic(epic.getId()).getSubTaskIds());
    }

    @Test
    @DisplayName(value = "Удаление задачи с несуществующим номером не влияет на соответствующий hashmap," +
            "предварительно уже очищенного от всех задач")
    public void shouldDoNothingIfTaskHashMapIsEmpty() {
        manager.deleteAllTasks();
        manager.deleteTask(WRONG_ID);
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    @DisplayName(value = "Удаление эпика с несуществующим номером не влияет на соответствующий hashmap," +
            "предварительно уже очищенного от всех эпиков")
    public void shouldDoNothingIfEpicHashMapIsEmpty() {
        manager.deleteAllEpics();
        manager.deleteEpic(WRONG_ID);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    @DisplayName(value = "Удаление подзадачи с несуществующим номером не влияет на соответствующий hashmap," +
            "предварительно уже очищенного от всех подзадач")
    public void shouldDoNothingIfSubTaskHashMapIsEmpty() {
        manager.deleteAllEpics();
        manager.deleteSubTask(WRONG_ID);
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    @DisplayName(value = "Возвращает пустой список номеров подзадач " +
            "вновь созданного эпика без привязки к нему подзадач")
    void shouldReturnEmptyListWhenGetSubTaskByEpicIdIsEmpty() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        List<SubTask> subtasks = manager.getSubTasksByEpic(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    @DisplayName(value = "Вернёт null если задачи с таким Id нет в списке.")
    public void shouldReturnExceptionMessageIfTaskDoesNotExist() {
        assertNull(manager.getTask(WRONG_ID));
    }

    @Test
    @DisplayName(value = "Вернёт null если эпика с таким Id нет в списке.")
    public void shouldReturnExceptionMessageIfEpicDoesNotExist() {
        assertNull(manager.getEpic(WRONG_ID));
    }

    @Test
    @DisplayName(value = "Вернёт null если подзадачи с таким Id нет в списке.")
    public void shouldReturnNullIfSubTaskDoesNotExist() {
        assertNull(manager.getSubTask(WRONG_ID));
    }

    @Test
    @DisplayName(value = "Вернёт пустую коллекцию если история пуста.")
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    @DisplayName(value = "При обращении к задачам с несуществующими id в списке," +
            " история будет пуста")
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTask(WRONG_ID);
        manager.getSubTask(WRONG_ID);
        manager.getEpic(WRONG_ID);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    @DisplayName(value = "Просмотренные существующие задачи будут в истории просмотров")
    public void shouldReturnHistoryWithTasks() {
        Epic epic = newEpic();
        manager.addEpic(epic);
        SubTask subTask = newSubTask(epic);
        manager.addSubTask(subTask);
        manager.getEpic(epic.getId());
        manager.getSubTask(subTask.getId());
        List<Task> tasksHistory = manager.getHistory();
        assertEquals(2, tasksHistory.size());
        assertTrue(tasksHistory.contains(subTask));
        assertTrue(tasksHistory.contains(epic));
    }
}