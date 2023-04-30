package manager;

import exception.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.*;

class TaskManagerTest{
    private static final Integer ID_TASK_1 = 1;
    private static final Integer ID_TASK_2 = 2;
    private static final Integer ID_EPIC_1_FULL = 3;
    private static final Integer ID_SUBTASK_1_IN_EPIC1 = 4;
    private static final Integer ID_SUBTASK_2_IN_EPIC1 = 5;
    private static final Integer ID_SUBTASK_3_IN_EPIC1 = 6;
    private static final Integer ID_EPIC_2_EMPTY = 7;

    public final FileBackedTasksManager manager = new FileBackedTasksManager();

    @BeforeEach
    public void prepareData() {
        // 0, 1
        manager.addTask(new Task("Task 1", "Description by Task 1", "30.12.2022 19:00", "PT15M"));
        manager.addTask(new Task("Task 2", "Description by Task 2", "30.12.2022 19:30", "PT30M"));
        // 2
        manager.addEpic(new Epic("Epic 1", "Description by Epic 1"));
        // 3, 4, 5
        manager.addSubTask(new SubTask("SubTask 1", "Description by SubTask 1", "31.12.2022 12:00", "PT20M", ID_EPIC_1_FULL));
        manager.addSubTask(new SubTask("SubTask 2", "Description by SubTask 2", "31.12.2022 12:30", "PT120M", ID_EPIC_1_FULL));
        manager.addSubTask(new SubTask("SubTask 3", "Description by SubTask 3", "31.12.2022 14:30", "PT20M", ID_EPIC_1_FULL));
        // 6
        manager.addEpic(new Epic("Epic 2", "Description by Epic 2"));
    }

    @Test
    void getGenerateId() {
        int count = manager.getAllTasks().size() + manager.getAllEpics().size() + manager.getAllSubTasks().size();
        assertEquals(count + 1, manager.getGenerateId());
        manager.setGenerateId(1);
        assertEquals(2, manager.getGenerateId());
    }

    @Test
    void getHistory() {
        assertEquals(0, manager.getHistory().size());
        Task task = manager.getTask(ID_TASK_1);
        assertEquals(new ArrayList<>(List.of(task)), manager.getHistory());
    }

    @Test
    void getAllTasks() {
        assertEquals(2, manager.getAllTasks().size());
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getTask(ID_EPIC_2_EMPTY));
        assertEquals("Task's not exists: null", exception.getMessage(), "Task is exist.");
    }

    @Test
    void getAllEpics() {
        assertEquals(2, manager.getAllEpics().size());
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getEpic(ID_TASK_1));
        assertEquals("Epic's not exists: null", exception.getMessage(), "Epic is exist.");
    }

    @Test
    void getAllSubTasks() {
        assertEquals(3, manager.getAllSubTasks().size());
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getSubTask(ID_TASK_1));
        assertEquals("SubTask's not exists: null", exception.getMessage(), "SubTask is exist.");
    }

    @Test
    void updateTask() {
        exceptionTitleDescriptionIsBlankThenUpdateTask();
        exceptionStatusIsNullThenUpdateTask();
        //shouldReturnExceptionIfTimeIsBlankThenUpdateTask();
    }
    private void exceptionTitleDescriptionIsBlankThenUpdateTask() {
        Task task = new Task(1, "", "",
                NEW, "30.12.2022 19:00", "PT15M");
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateTask(task));
        assertEquals("Название и описание задачи не могут быть пустыми.", exception.getMessage());
    }
    private void exceptionStatusIsNullThenUpdateTask() {
        Task task = new Task(1, "TitleWrongTask", "DescriptionWrongTask",
                null, "30.12.2022 19:00", "PT15M");
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateTask(task));
        assertEquals("Статус задачи не может быть пустым.", exception.getMessage());
    }

    @Test
    void updateSubTask() {
        exceptionTitleDescriptionIsBlankThenUpdateSubTask();
        exceptionIfStatusIsNullThenUpdateSubTask();
        exceptionNotContainsThenUpdateSubTask();
        //successChangeStatusSubTask();
    }
    @Test
    public void successChangeStatusSubTask(){
        SubTask subTask = manager.getSubTask(ID_SUBTASK_2_IN_EPIC1);
        assertEquals(NEW, subTask.getStatus());
        subTask.setStatusInProgress();
        manager.updateSubTask(subTask);
        assertEquals(IN_PROGRESS, manager.subtasks.get(ID_SUBTASK_2_IN_EPIC1).getStatus());
        //SubTask subTaskUpdate = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
    }

    private void exceptionTitleDescriptionIsBlankThenUpdateSubTask() {
        SubTask subTask = new SubTask(1, "", "",
                NEW, "30.12.2022 19:00", "PT15M", 3);
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateSubTask(subTask));
        assertEquals("Название и описание задачи не могут быть пустыми.", exception.getMessage());
    }
    private void exceptionIfStatusIsNullThenUpdateSubTask() {
        SubTask subTask = new SubTask(1, "TitleWrongTask", "DescriptionWrongTask",
                null, "30.12.2022 19:00", "PT15M", 3);
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateSubTask(subTask));
        assertEquals("Статус задачи не может быть пустым.", exception.getMessage());
    }
    private void exceptionNotContainsThenUpdateSubTask() {
        SubTask subTask = new SubTask(1000, "TitleWrongTask", "DescriptionWrongTask",
                NEW, "30.12.2022 19:00", "PT15M", 1);
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateSubTask(subTask));
        assertEquals("Такой задачи не существует.", exception.getMessage());
    }

    @Test
    void updateEpic() {
        exceptionTitleDescriptionIsBlankThenUpdateEpic();
        exceptionIfStatusIsNullThenUpdateEpic();
        exceptionNotContainsThenUpdateEpic();
    }
    private void exceptionTitleDescriptionIsBlankThenUpdateEpic() {
        Epic epic = new Epic(1, "", "",
                NEW, "30.12.2022 19:00", "PT15M");
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateEpic(epic));
        assertEquals("Название и описание задачи не могут быть пустыми.", exception.getMessage());
    }
    private void exceptionIfStatusIsNullThenUpdateEpic() {
        Epic epic = new Epic(1, "TitleWrongTask", "DescriptionWrongTask",
                null, "30.12.2022 19:00", "PT15M");
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateEpic(epic));
        assertEquals("Статус задачи не может быть пустым.", exception.getMessage());
    }
    private void exceptionNotContainsThenUpdateEpic() {
        Epic epic = new Epic(1000, "TitleWrongTask", "DescriptionWrongTask",
                NEW, "30.12.2022 19:00", "PT15M");
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.updateEpic(epic));
        assertEquals("Такой задачи не существует.", exception.getMessage());
    }


    @Test
    void getTask() {
        getTaskExist();
        getTaskNotExist();
    }
    private void getTaskExist(){
        Task task1 = manager.getTask(ID_TASK_1);
        Task task2 = new Task(ID_TASK_1, "Task 1", "Description by Task 1", NEW,
                "30.12.2022 19:00", "PT15M");
        assertEquals(task2, task1);
    }
    private void getTaskNotExist(){
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getTask(ID_EPIC_1_FULL));
        assertEquals("Task's not exists: null", exception.getMessage());
    }

    @Test
    void getEpic() {
        getEpicNotExist();
        getEpicWithSubtask();
        getEpicWithoutSubtask();
    }
    private void getEpicNotExist(){
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getEpic(ID_TASK_1));
        assertEquals("Epic's not exists: null", exception.getMessage());
    }
    private void getEpicWithoutSubtask(){
        Epic epic1 = manager.getEpic(ID_EPIC_2_EMPTY);
        Epic epic2 = new Epic(ID_EPIC_2_EMPTY, "Epic 2", "Description by Epic 2",
                NEW, "01.01.1970 00:00", "PT0S");
        //in epic3.subtaskIds - isEmpty; epic3 equal epic4;
        assertEquals(epic1, epic2);
    }
    private void getEpicWithSubtask(){
        Epic epic1 = manager.getEpic(ID_EPIC_1_FULL);
        Epic epic2 = new Epic(ID_EPIC_1_FULL, "Epic 1", "Description by Epic 1",
                NEW, "01.01.1970 00:00", "PT0S");
        //in epic2.subtaskIds - isEmpty; epic1 not equal epic2;
        assertNotEquals(epic1, epic2);
    }

    @Test
    void getSubTask() {
        getSubTaskExist();
        getSubTaskNotExist();
    }
    private void getSubTaskNotExist(){
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getSubTask(ID_TASK_1));
        assertEquals("SubTask's not exists: null", exception.getMessage());
    }
    private void getSubTaskExist(){
        SubTask subTask1 = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        SubTask subTask2 = new SubTask(ID_SUBTASK_1_IN_EPIC1,"SubTask 1", "Description by SubTask 1",
                NEW, "31.12.2022 12:00", "PT20M", 3);
        assertEquals(subTask2, subTask1);
    }

    @Test
    void addTask() {
        Task task1 = manager.getTask(ID_TASK_1);
        Task task2 = new Task(ID_TASK_1, "Task 1", "Description by Task 1",
                NEW, "30.12.2022 19:00", "PT15M");
        assertEquals(task1, task2);
    }

    @Test
    void addEpic() {
        Epic epic1 = manager.getEpic(ID_EPIC_2_EMPTY);
        Epic epic2 = new Epic(ID_EPIC_2_EMPTY, "Epic 2", "Description by Epic 2",
                NEW, "01.01.1970 00:00", "PT0S");
        assertEquals(epic1, epic2);
    }

    @Test
    void addSubTask() {
        addSubTaskStandard();
        addSubTaskWithoutEpic();
    }
    private void addSubTaskStandard(){
        SubTask subTask1 = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        SubTask subTask2 = new SubTask(ID_SUBTASK_1_IN_EPIC1, "SubTask 1", "Description by SubTask 1",
                NEW, "31.12.2022 12:00", "PT20M", ID_EPIC_1_FULL);
        assertEquals(subTask1, subTask2);
    }
    private void addSubTaskWithoutEpic(){
        SubTask subTask = new SubTask(8, "SubTask 1", "Description by SubTask 1",
                NEW, "31.12.2022 12:00", "PT20M", 1000);
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> manager.addSubTask(subTask));
        assertEquals("Сначала создайте главную задачу.", exception.getMessage());
    }

    @Test
    void deleteTask() {
        Task task1 = manager.getTask(ID_TASK_1);
        Task task2 = manager.getTask(ID_TASK_2);
        assertEquals(List.of(task1, task2), manager.getAllTasks());
        manager.deleteTask(task2.getId());
        assertEquals(List.of(task1), manager.getAllTasks());
    }

    @Test
    void deleteEpic() {
        Epic epic1 = manager.getEpic(ID_EPIC_1_FULL);
        Epic epic2 = manager.getEpic(ID_EPIC_2_EMPTY);
        assertEquals(List.of(epic1, epic2), manager.getAllEpics());
        manager.deleteEpic(epic1.getId());
        assertEquals(List.of(epic2), manager.getAllEpics());
    }

    @Test
    void deleteSubTask() {
        SubTask subTask1 = manager.getSubTask(ID_SUBTASK_1_IN_EPIC1);
        SubTask subTask2 = manager.getSubTask(ID_SUBTASK_2_IN_EPIC1);
        SubTask subTask3 = manager.getSubTask(ID_SUBTASK_3_IN_EPIC1);
        assertEquals(List.of(subTask1, subTask2,subTask3), manager.getAllSubTasks());
        manager.deleteSubTask(subTask2.getId());
        assertEquals(List.of(subTask1, subTask3), manager.getAllSubTasks());
        manager.deleteSubTask(subTask1.getId());
        assertEquals(List.of(subTask3), manager.getAllSubTasks());
        manager.deleteSubTask(subTask3.getId());
        assertEquals(List.of(), manager.getAllSubTasks());
    }

    @Test
    void getSubTasksByEpic() {

        List<SubTask> listEpic7 = manager.getSubTasksByEpic(ID_EPIC_2_EMPTY);
        assertEquals(0,listEpic7.size());


        List<SubTask> listEpic3 = manager.getSubTasksByEpic(ID_EPIC_1_FULL);
        assertEquals(3,listEpic3.size());
    }

    @Test
    void deleteAllTasks() {
        assertNotEquals(0, manager.getAllTasks().size());
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void deleteAllEpics() {
        assertNotEquals(0, manager.getAllEpics().size());
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void deleteAllSubTasks() {
        assertNotEquals(0, manager.getAllSubTasks().size());
        manager.deleteAllSubTasks();
        assertEquals(0, manager.getAllSubTasks().size());
    }
}