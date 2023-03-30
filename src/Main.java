import manager.ManagerException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Main{
    //private static final TaskManager taskManager = Managers.getDefaultTask();
    private static final TaskManager taskManager = Managers.getFileBackedTasks();

    public static void main(String[] args){
        try{
            testingByTechTask();
        } catch (ManagerException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * <h1>Тестирование работы программы</h1>
     * После написания менеджера истории проверьте его работу:
     * <ul>
     *     <li> создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;</li>
     *     <li> запросите созданные задачи несколько раз в разном порядке;</li>
     *     <li> после каждого запроса выведите историю и убедитесь, что в ней нет повторов;</li>
     *     <li> удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться;</li>
     *     <li> удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.</li>
     * </ul>
     */
    private static void testingByTechTask() {
        List<Integer> taskId = new ArrayList<>();
        List<Integer> subtaskId = new ArrayList<>();
        List<Integer> epicId = new ArrayList<>();

        taskId.add(taskManager.addTask(
                new Task("Task 1", "Description by Task 1")));//0
        taskId.add(taskManager.addTask(
                new Task("Task 2", "Description by Task 2")));//1

        epicId.add(taskManager.addEpic(
                new Epic("Epic 1", "Description by Epic 1")));//0
        subtaskId.add(taskManager.addSubTask(
                new SubTask("SubTask 1", "Description by SubTask 1", epicId.get(0))));
        subtaskId.add(taskManager.addSubTask(
                new SubTask("SubTask 2", "Description by SubTask 2", epicId.get(0))));
        subtaskId.add(taskManager.addSubTask(
                new SubTask("SubTask 3", "Description by SubTask 3", epicId.get(0))));

        epicId.add(taskManager.addEpic(
                new Epic("Epic 2", "Description by Epic 2")));//1

        taskManager.getTask(taskId.get(1));         // Task2
        taskManager.getSubTask(subtaskId.get(1));   // STask2
        taskManager.getTask(taskId.get(0));         // Task1


        taskManager.getSubTask(subtaskId.get(0));   // STask1
        getHistoryOfTasks();
        taskManager.getTask(taskId.get(0));         // Task1   (double)
        getHistoryOfTasks();
        taskManager.getSubTask(subtaskId.get(1));   // STask2  (double)
        getHistoryOfTasks();
        taskManager.getTask(taskId.get(1));         // Task2   (double)
        getHistoryOfTasks();
        taskManager.getSubTask(subtaskId.get(2));   // STask3
        taskManager.getEpic(epicId.get(0));         // Epic1
        taskManager.getSubTask(subtaskId.get(2));   // STask3  (double)
        getHistoryOfTasks();
        taskManager.getEpic(epicId.get(1));         // Epic2
        taskManager.getEpic(epicId.get(0));         // Epic1   (double)
        getHistoryOfTasks();
        taskManager.getSubTask(subtaskId.get(0));   // STask1  (double)
        getHistoryOfTasks();
        taskManager.getEpic(epicId.get(1));         // Epic2   (double)
        getHistoryOfTasks();

        taskManager.deleteTask(taskId.get(0));      // del Task1
        getHistoryOfTasks();

        taskManager.deleteEpic(epicId.get(0));      // del Epic1 (+SubTask[0,1,2])
        getHistoryOfTasks();

        destroyData(); // end
    }

    private static void getHistoryOfTasks() {
        System.out.println("checking taskManager.getHistory ---------------->begin");
        for (Object task : taskManager.getHistory()) {
            System.out.println(task.toString());
        }
        System.out.println("end<-------------------------- taskManager.getHistory");
    }

    private static void checkTasks() {
        System.out.println("checking taskManager.getAllTasks - - - - - - - - - - - - - - -");
        System.out.println(taskManager.getAllTasks());
    }

    private static void checkAllSubTasks() {
        System.out.println("checking taskManager.getAllSubTasks  - - - - - - - - - - - - -");
        System.out.println(taskManager.getAllSubTasks());
    }

    private static void checkAllEpics() {
        System.out.println("checking taskManager.getAllEpics - - - - - - - - - - - - - - -");
        System.out.println(taskManager.getAllEpics());
    }

    private static void destroyData() {
        System.out.println("checking taskManager.deleteAllTasks - - - - - - - - - - - - - -");
        taskManager.deleteAllTasks();
        checkTasks();
        System.out.println("checking taskManager.deleteAllSubTasks  - - - - - - - - - - - -");
        taskManager.deleteAllSubTasks();
        checkAllSubTasks();
        System.out.println("checking taskManager.deleteAllEpic  - - - - - - - - - - - - - -");
        taskManager.deleteAllEpics();
        checkAllEpics();
    }
}
