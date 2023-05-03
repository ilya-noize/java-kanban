package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private static final String TASKS_CSV_TEST = "tasks.test.csv";
    private static final LocalDateTime TIME_NOW = LocalDateTime.now();
    public static final Path path = Path.of(TASKS_CSV_TEST);
    File file = new File(String.valueOf(path));

    @DisplayName(value = "Подготовительные работы. " +
            "Создание менеджера для всех тестов.")
    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @DisplayName(value = "Удаление файла после всех тестов.")
    @AfterEach
    public void afterEach() {
        file.delete();
    }

    @DisplayName(value = "Создание и сохранение задач в файл. Загрузка из файла." +
            " Проверка на наличие в списке всех задач.")
    @Test
    public void shouldCorrectlySaveAndLoad() {
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

    @DisplayName(value = "Сохранение пустых hashmap и их загрузка из файла. Все коллекции пусты")
    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        backedTasksManager.save();
        backedTasksManager.loadFromFile();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTasks());
    }

    @DisplayName(value = "Сохранение и загрузка с пустой историей")
    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        backedTasksManager.save();
        backedTasksManager.loadFromFile();
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}