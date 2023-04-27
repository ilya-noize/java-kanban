import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tasks.Status.DONE;
import static tasks.TypeTask.TASK;

public class Main{
    protected LocalDateTime startTime;
    protected Duration duration;
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);


    Main(){
        //"31.12.2022 18:00", "PT360M"
        this.startTime = LocalDateTime.parse("31.12.2022 18:00",DATE_TIME);
        this.duration = Duration.parse("PT360M");
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    private static void dataTimeDurationTest(){
        Main obj = new Main();
        System.out.println("getStartTime: " + obj.getStartTime());
        System.out.println("getDuration: " + obj.getDuration());
    }


    public static void main(String[] args) {
        addTasksTest();
        SubTask subTask = manager.getSubTask(4); //st1
        statusSubTaskIsChangeTo(DONE, subTask);
        manager.getAllEpics().forEach(System.out::print);
    }


    public static TaskManager manager = Managers.getDefaultMemoryTask();
    public static Map<TypeTask, List<Integer>> idTasks = new HashMap<>();

    private static void addTasksTest() {
        idTasks.put(TASK, List.of(//"dd.MM.yyyy hh:mm"
                manager.addTask(new Task(                   //1
                        "Task 1", "Description by Task 1", "30.12.2022 19:00", "PT15M")),
                manager.addTask(new Task(               //2
                        "Task 2", "Description by Task 2", "30.12.2022 19:30", "PT30M"))
        ));
        int epicId = manager.addEpic(new Epic(              //3
                "Epic 1", "Description by Epic 1", "31.12.2022 12:00", "PT100M"));
        idTasks.put(TypeTask.SUBTASK, List.of(
                manager.addSubTask(new SubTask(         //4
                        "SubTask 1", "Description by SubTask 1", "31.12.2022 12:00", "PT20M", epicId)),
                manager.addSubTask(new SubTask(         //5
                        "SubTask 2", "Description by SubTask 2", "31.12.2022 12:30", "PT120M", epicId)),
                manager.addSubTask(new SubTask(         //6
                        "SubTask 3", "Description by SubTask 3", "31.12.2022 14:30", "PT20M", epicId))
        ));
        idTasks.put(TypeTask.EPIC, List.of(
                epicId,
                manager.addEpic(new Epic(                   //7
                        "Epic 2", "Description by Epic 2", "31.12.2022 18:00", "PT360M"))
        ));
    }

    public static void statusSubTaskIsChangeTo(Status status, SubTask subTask) {
        manager.updateSubTask(new SubTask(
                subTask.getId(),
                subTask.getTitle(),
                subTask.getDescription(),
                status,
                subTask.getStartTime(),
                subTask.getDuration(),
                subTask.getEpicId())
        );
    }




}
