package tasks;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TypeTask.SUBTASK;

class SubTaskTest {
    private static final Integer ID_EPIC_1_FULL = 1;
    private static final Integer ID_SUBTASK_1_IN_EPIC1 = 2;
    private static final Integer ID_SUBTASK_2_IN_EPIC1 = 3;
    private static final Integer ID_SUBTASK_3_IN_EPIC1 = 4;
    private static final Integer ID_EPIC_2_EMPTY = 5;
    private static FileBackedTasksManager manager;
    @BeforeEach
    public void createEpicNSubTasks() {
        manager = new FileBackedTasksManager();
        manager.addEpic(new Epic("Epic 1", "Description by Epic 1"));
        manager.addSubTask(new SubTask("SubTask 1", "Description by SubTask 1",
                "31.12.2022 12:00", "PT20M", ID_EPIC_1_FULL));
        manager.addSubTask(new SubTask("SubTask 2", "Description by SubTask 2",
                "31.12.2022 12:30", "PT120M", ID_EPIC_1_FULL));
        manager.addSubTask(new SubTask("SubTask 3", "Description by SubTask 3",
                "31.12.2022 14:30", "PT20M", ID_EPIC_1_FULL));
        manager.addEpic(new Epic("Epic 2", "Description by Epic 2"));
    }

    @Test
    void getEpicId() {
        assertNotEquals(ID_EPIC_2_EMPTY, manager.getSubTask(ID_SUBTASK_1_IN_EPIC1).getEpicId());
    }

    @Test
    void getType() {
        assertEquals(SUBTASK, manager.getSubTask(ID_SUBTASK_1_IN_EPIC1).getType());
    }

    @Test
    void testEquals() {
        SubTask subTask1 = manager.getSubTask(ID_SUBTASK_2_IN_EPIC1);
        SubTask subTask2 = manager.getSubTask(ID_SUBTASK_3_IN_EPIC1);
        assertNotEquals(subTask1, subTask2);
    }

    @Test
    void testHashCode() {
        SubTask subTask = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        assertNotEquals(manager.getSubTask(ID_SUBTASK_2_IN_EPIC1).hashCode(),subTask.hashCode());
    }

    @Test
    void testToString() {
        String str = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1).toString();
        assertEquals("2,SUBTASK,'SubTask 1',NEW,'Description by SubTask 1',31.12.2022 12:00,PT20M,1\n",str);
    }
}