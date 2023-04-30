import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class Main {
    /*
    private static final Integer ID_TASK_1 = 1;
    private static final Integer ID_TASK_2 = 2;
    */
    private static final Integer ID_EPIC_1_FULL = 3;
    /*
    private static final Integer ID_SUBTASK_1_IN_EPIC1 = 4;
    private static final Integer ID_SUBTASK_2_IN_EPIC1 = 5;
    private static final Integer ID_SUBTASK_3_IN_EPIC1 = 6;
    private static final Integer ID_EPIC_2_EMPTY = 7;
     */

    public static TaskManager manager = Managers.getDefaultMemoryTask();

    Main() {
    }


    public static void main(String[] args) {
        addTasksSimple();
        getPrioritizedTasks().forEach(System.out::print);
    }

    public static Set<Task> getPrioritizedTasks(){
        Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
            LocalDateTime time1 = task1.getStartTime();
            LocalDateTime time2 = task2.getStartTime();

            if (time1 == null && time2 == null) {
                return task1.getId() - task2.getId();
            }

            if (time1 == null) {
                return 1;
            }

            if (time2 == null) {
                return -1;
            }

            return time1.compareTo(time2);
        });

        prioritizedTasks.addAll(manager.getAllTasks());
        prioritizedTasks.addAll(manager.getAllSubTasks());
        prioritizedTasks.addAll(manager.getAllEpics());

        return prioritizedTasks;
    }
    private static void addTasksSimple() {
        //1.2
        manager.addTask(new Task("Task 1", "Description by Task 1",
                "30.12.2022 19:00", "PT15M"));
        manager.addTask(new Task("Task 2", "Description by Task 2",
                "30.12.2022 19:30", "PT30M"));
        //3
        manager.addEpic(new Epic("Epic 1", "Description by Epic 1"));
        //4.5.6
        manager.addSubTask(new SubTask("SubTask 1", "Description by SubTask 1",
                "31.12.2022 12:00", "PT20M", ID_EPIC_1_FULL));
        manager.addSubTask(new SubTask("SubTask 2", "Description by SubTask 2",
                "31.12.2022 12:30", "PT120M", ID_EPIC_1_FULL));
        manager.addSubTask(new SubTask("SubTask 3", "Description by SubTask 3",
                "31.12.2022 14:30", "PT20M", ID_EPIC_1_FULL));
        //7
        manager.addEpic(new Epic("Epic 2", "Description by Epic 2"));
    }
}
