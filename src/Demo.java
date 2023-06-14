import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;

public class Demo {
    public enum TASKS {
        TASK_1(new Task("Task 1",
                "Description of Task 1",
                LocalDateTime.of(2022, 12, 30, 19, 0, 0),
                "PT15M")),
        TASK_2(new Task("Task 2",
                "Description of Task 2",
                LocalDateTime.of(2022, 12, 30, 19, 30, 0),
                "PT30M"));

        final Task task;

        TASKS(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

    public enum EPICS {
        EPIC_1(new Epic("Epic 1", "Description by Epic 1")),
        EPIC_2(new Epic("Epic 2", "Description by Epic 2"));

        final Epic epic;

        EPICS(Epic epic) {
            this.epic = epic;
        }

        public Epic getEpic() {
            return epic;
        }
    }

    /**
     * Нужно переопредение поля epicId, если epicId не равен 1;
     */
    public enum SUBTASKS {
        SUBTASK_1(new SubTask("SubTask 1",
                "Description by SubTask 1",
                LocalDateTime.of(2022, 12, 31, 12, 0, 0),
                "PT20M",
                1)),
        SUBTASK_2(new SubTask("SubTask 2",
                "Description by SubTask 2",
                LocalDateTime.of(2022, 12, 31, 12, 30, 0),
                "PT120M",
                1)),
        SUBTASK_3(new SubTask("SubTask 3",
                "Description by SubTask 3",
                LocalDateTime.of(2022, 12, 31, 14, 30, 0),
                "PT20M",
                1));

        final SubTask subTask;

        SUBTASKS(SubTask subTask) {
            this.subTask = subTask;
        }

        public SubTask getSubTask() {
            return subTask;
        }
    }
}
