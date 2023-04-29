package tasks;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tasks.Status.*;

class EpicTest {
    private static final Integer ID_EPIC_1_FULL = 1;
    private static final Integer ID_SUBTASK_1_IN_EPIC1 = 2;
    private static final Integer ID_SUBTASK_2_IN_EPIC1 = 3;
    private static final Integer ID_SUBTASK_3_IN_EPIC1 = 4;
    private static final Integer ID_EPIC_2_EMPTY = 5;
    private static FileBackedTasksManager manager;
    @BeforeEach
    public void createEpicNSubTasks() {
        manager = new FileBackedTasksManager();
        final int epicId = manager.addEpic(new Epic("Epic 1", "Description by Epic 1", "31.12.2022 12:00", "PT100M"));
        manager.addSubTask(new SubTask("SubTask 1", "Description by SubTask 1", "31.12.2022 12:00", "PT20M", epicId));
        manager.addSubTask(new SubTask("SubTask 2", "Description by SubTask 2", "31.12.2022 12:30", "PT120M", epicId));
        manager.addSubTask(new SubTask("SubTask 3", "Description by SubTask 3", "31.12.2022 14:30", "PT20M", epicId));
        manager.addEpic(new Epic("Epic 2", "Description by Epic 2", "31.12.2022 15:00", "PT60M"));
    }

    @Test
    public void shouldReturnToString(){
        final Epic epic = manager.getEpic(ID_EPIC_1_FULL);
        assertEquals("1,EPIC,'Epic 1',NEW,'Description by Epic 1','31.12.2022 12:00','PT2H50M',\n", epic.toString());
    }

    /**
     * 1. Для расчёта статуса Epic. Граничные условия:<br>
     * + Все подзадачи со статусом NEW.<br>
     * + Подзадачи со статусами NEW и DONE.<br>
     * + Подзадачи со статусом IN_PROGRESS.<br>
     * + Все подзадачи со статусом DONE.<br>
     * + Пустой список подзадач.<br>
     */
    @Test
    public void shouldReturnNewAfterAddSubTacksInEpic() {
        assertEquals(NEW, manager.getEpic(ID_EPIC_1_FULL).getStatus());
        assertEquals(NEW, manager.getSubTask(ID_SUBTASK_1_IN_EPIC1).getStatus());
        assertEquals(NEW, manager.getSubTask(ID_SUBTASK_2_IN_EPIC1).getStatus());
        assertEquals(NEW, manager.getSubTask(ID_SUBTASK_3_IN_EPIC1).getStatus());
    }

    @Test
    public void shouldReturnInProgressAfterSetStatus1SubTacksIsDone() {
        SubTask subTask = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
                subTask.setStatusDone();
        manager.updateEpicStatus(subTask.getEpicId());
        assertEquals(IN_PROGRESS, manager.getEpic(ID_EPIC_1_FULL).getStatus());
    }

    @Test
    public void shouldReturnInProgressAfterSetStatus3SubTacksIsInProgress() {
        SubTask subTask = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        subTask.setStatusInProgress();
        manager.getSubTask(ID_SUBTASK_2_IN_EPIC1).setStatusInProgress();
        manager.getSubTask(ID_SUBTASK_3_IN_EPIC1).setStatusInProgress();
        manager.updateEpicStatus(ID_EPIC_1_FULL);
        assertEquals(IN_PROGRESS, manager.getEpic(ID_EPIC_1_FULL).getStatus());
    }

    @Test
    public void shouldReturnInProgressAfterSetStatus1SubTacksIsDone2SubTacksIsInProgress() {
        SubTask subTask = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        subTask.setStatusDone();
        manager.getSubTask(ID_SUBTASK_2_IN_EPIC1).setStatusInProgress();
        manager.getSubTask(ID_SUBTASK_3_IN_EPIC1).setStatusInProgress();
        manager.updateEpicStatus(subTask.getEpicId());
        assertEquals(IN_PROGRESS, manager.getEpic(ID_EPIC_1_FULL).getStatus());
    }

    @Test
    public void shouldReturnInProgressAfterSetStatus2SubTacksIsDone2SubTacksIsInProgress() {
        SubTask subTask = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        subTask.setStatusDone();
        manager.getSubTask(ID_SUBTASK_2_IN_EPIC1).setStatusDone();
        manager.getSubTask(ID_SUBTASK_3_IN_EPIC1).setStatusInProgress();
        manager.updateEpicStatus(subTask.getEpicId());
        assertEquals(IN_PROGRESS, manager.getEpic(ID_EPIC_1_FULL).getStatus());
    }

    @Test
    public void shouldReturnDoneAfterSetStatus3SubTacksIsDone() {
        SubTask subTask = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        subTask.setStatusDone();
        manager.getSubTask(ID_SUBTASK_2_IN_EPIC1).setStatusDone();
        manager.getSubTask(ID_SUBTASK_3_IN_EPIC1).setStatusDone();
        manager.updateEpicStatus(subTask.getEpicId());
        final Epic epic = manager.getEpic(ID_EPIC_1_FULL);
        assertEquals(DONE, epic.getStatus());
        assertEquals(4,manager.getHistory().size());
        assertEquals(subTask,manager.getHistory().get(0));
        assertEquals(epic,manager.getHistory().get(3));
        assertEquals(4,manager.getHistory().size());
    }

    @Test
    public void shouldReturnNewInEpicWithoutSubTasks() {
        assertEquals(NEW, manager.getEpic(ID_EPIC_2_EMPTY).getStatus());
        assertEquals(manager.getEpic(ID_EPIC_1_FULL).getType(),
                manager.getEpic(ID_EPIC_2_EMPTY).getType(), "Типы задач разные");
        assertNotEquals(manager.getEpic(ID_EPIC_1_FULL),
                manager.getEpic(ID_EPIC_2_EMPTY), "Задачи идентичны");
    }

    // getType

    // removeSubtaskId

    // hashcode

    // equals

    // toString
}