package manager;

import manager.task.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @DisplayName(value = "Подготовительные работы. " +
            "Создание менеджера для всех тестов.")
    void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}