package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private final String TASKS_CSV_TEST = "tasks.test.csv";
    private final File file = new File(TASKS_CSV_TEST);
    private final LocalDateTime TIME_NOW = LocalDateTime.now();

    @BeforeEach
    @DisplayName(value = "Подготовительные работы. " +
            "Создание менеджера для всех тестов.")
    void beforeEach() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    @DisplayName(value = "Удаление файла после всех тестов.")
    void afterEach() {
        file.delete();
    }

    @Test
    @DisplayName(value = "Создание и сохранение задач в файл. Загрузка из файла." +
            " Проверка на наличие в списке всех задач.")
    void shouldCorrectlySaveAndLoad() {
        Task task = new Task("Task 1", "Description by Task 1", TIME_NOW, "PT15M");
        manager.addTask(task);
        Epic epic = new Epic("Epic 1", "Description by Epic 1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask 1", "Description by SubTask 1", TIME_NOW, "PT20M", epic.getId());
        manager.addSubTask(subTask);
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        backedTasksManager.loadFromFile();
        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    @DisplayName(value = "Сохранение пустых hashmap и их загрузка из файла. Все коллекции пусты")
    void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        backedTasksManager.save();
        backedTasksManager.loadFromFile();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTasks());
    }

    @Test
    @DisplayName(value = "Сохранение и загрузка с пустой историей")
    void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        backedTasksManager.save();
        backedTasksManager.loadFromFile();
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}