package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.time.Duration.between;
import static java.time.Duration.ofMinutes;
import static tasks.TypeTask.EPIC;

public class Epic extends Task {
    private final List<Integer> subTaskIds;
    private LocalDateTime endTime;

    /**
     * ДЛЯ РАБОТЫ ТЕСТОВ.
     *
     * @param id          Номер задачи
     * @param title       Название задачи
     * @param description Описание задачи
     * @param status      Статус задачи
     * @param startTime   Начало выполнения задачи (дата)
     * @param duration    Длительность выполнения по шаблону из Duration.parse {@code PnDTnHnMn.nS}
     */
    public Epic(int id, String title, String description, Status status, String startTime, String duration) {
        super(id, title, description, status, startTime, duration);
        this.subTaskIds = new ArrayList<>();
        this.endTime = this.startTime.plusMinutes(this.duration.toMinutes());
    }

    /**
     * ДЛЯ РАБОТЫ ТЕСТОВ.
     *
     * @param title       Номер задачи
     * @param description Описание задачи
     * @param startTime   Начало выполнения задачи (дата)
     * @param duration    Длительность выполнения из {@code Duration.parse} по шаблону {@code PnDTnHnMn.nS}
     */
    public Epic(String title, String description, String startTime, String duration) {
        super(title, description, startTime, duration);
        this.subTaskIds = new ArrayList<>();
        this.endTime = this.startTime.plusMinutes(this.duration.toMinutes());
    }

    /**
     * Simple - конструктор задачи для Epic
     *
     * @param title       Номер задачи
     * @param description Описание задачи
     */
    public Epic(String title, String description) {
        super(title, description);
        this.subTaskIds = new ArrayList<>();
        this.endTime = this.startTime.plusMinutes(this.duration.toMinutes());
    }

    @Override
    public TypeTask getType() {
        return EPIC;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void removeSubtaskId(Integer id) {
        this.subTaskIds.remove(id);
    }

    public void setStartEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.duration = ofMinutes(between(startTime, endTime).toMinutes());
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;

        return getSubTaskIds().equals(epic.getSubTaskIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubTaskIds());
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
