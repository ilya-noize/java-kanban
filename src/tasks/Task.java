package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static tasks.TypeTask.TASK;

/**
 * Класс Задача. Простейшая сущность системы управления задачами. Включает в себя 4 поля:
 * <ul>
 *     <li>id - Идентификатор задачи</li>
 *     <li>title - заголовок (или название) задачи</li>
 *     <li>description - Подробности (по желанию)</li>
 *     <li>status - статус выполнения задачи</li>
 * </ul>
 */
public class Task {
    private int id;
    protected String title;
    protected String description;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";//31.09.1876 23:45
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    /**
     * Full - Конструктор задачи
     * @param id            Номер задачи
     * @param title         Название задачи
     * @param description   Описание задачи
     * @param status        Статус задачи
     * @param startTime     Начало выполнения задачи (дата)
     * @param duration      Длительность выполнения по шаблону из Duration.parse {@code PnDTnHnMn.nS}
     */
    public Task(int id, String title, String description, Status status, String startTime, String duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.parse(startTime,DATE_TIME);
        this.duration = Duration.parse(duration);
    }

    /**
     * Simple - конструктор задачи
     * @param title         Номер задачи
     * @param description   Описание задачи
     * @param startTime     Начало выполнения задачи (дата)
     * @param duration      Длительность выполнения из {@code Duration.parse} по шаблону {@code PnDTnHnMn.nS}
     */
    public Task(String title, String description, String startTime, String duration) {
        this.title = title;
        this.description = description;
        this.startTime = LocalDateTime.parse(startTime,DATE_TIME);
        this.duration = Duration.parse(duration);
        this.status = Status.NEW;
    }

    public Task() {
        this.title = "";
        this.description = "";
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(0);
        this.startTime = LocalDateTime.now();
    }


    public TypeTask getType(){
        return TASK;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime(){
        return this.startTime.plusMinutes(this.duration.toMinutes());
    }

    public String getStartTime() {
        return this.startTime.format(DATE_TIME);
    }

    public String getDuration() {
        return duration.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        return title.equals(task.title) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

    @Override
    public String toString() {
        return this.getId() + ","
                + this.getType() + ",'"
                + this.getTitle() + "',"
                + this.getStatus() + ",'"
                + this.getDescription() + "',"
                + this.getStartTime() + ","
                + this.getDuration() + ",\n";
    }
}


