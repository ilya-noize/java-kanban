package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static tasks.Status.*;
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
     * Full - Конструктор задачи (TASK, SUBTASK)
     * @param title         Название задачи
     * @param description   Описание задачи
     * @param startTime     Начало выполнения задачи (дата)
     * @param duration      Длительность выполнения по шаблону из Duration.parse {@code PnDTnHnMn.nS}
     */
    public Task(String title, String description, LocalDateTime startTime, String duration) {
        this.title = title;
        this.description = description;
        this.status = NEW;
        this.startTime = startTime;
        this.duration = Duration.parse(duration);
    }

    /**
     * Standard - конструктор задачи (EPIC)
     * @param title         Номер задачи
     * @param description   Описание задачи
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = NEW;
        this.startTime = LocalDateTime.ofEpochSecond(0L,0, ZoneOffset.UTC);
        this.duration = Duration.ofMinutes(0);
    }

    public Task() {
        this.title = "";
        this.description = "";
        this.status = NEW;
        this.startTime = LocalDateTime.ofEpochSecond(0L,0, ZoneOffset.UTC);
        this.duration = Duration.ofMinutes(0);
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime(){
        return this.startTime.plusMinutes(this.duration.toMinutes());
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public String getStartTimeToString() {
        return this.startTime.format(DATE_TIME);
    }

    public String getDurationToString() {
        return duration.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getId() != task.getId()) return false;
        if (!getTitle().equals(task.getTitle())) return false;
        if (!getDescription().equals(task.getDescription())) return false;
        if (getStatus() != task.getStatus()) return false;
        if (!getStartTimeToString().equals(task.getStartTimeToString())) return false;
        return getDurationToString().equals(task.getDurationToString());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + getStartTimeToString().hashCode();
        result = 31 * result + getDurationToString().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.getId() + ","
                + this.getType() + ",'"
                + this.getTitle() + "',"
                + this.getStatus() + ",'"
                + this.getDescription() + "','"
                + this.getStartTimeToString() + "','"
                + this.getDurationToString() + "',\n";
    }
}


