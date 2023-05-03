package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @DisplayName(value = "Подготовительные работы. " +
            "Создание менеджера для всех тестов.")
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}