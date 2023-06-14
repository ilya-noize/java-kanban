package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

import static tasks.TypeTask.SUBTASK;

public class SubTask extends Task{
    protected int epicId;

    /**
     * Full version Constructor
     * @param title       Название задачи
     * @param description Описание задачи
     * @param startTime   Начало выполнения задачи (дата)
     * @param duration    Длительность выполнения по шаблону из Duration.parse {@code PnDTnHnMn.nS}
     * @param epicId      Принадлежность к главной задаче (epic)
     */
    public SubTask(String title, String description, LocalDateTime startTime, String duration, int epicId) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask() {
        super();
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TypeTask getType() {
        return SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;

        return getEpicId() == subTask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return this.getId() + ","
                + this.getType() + ",'"
                + this.getTitle() + "',"
                + this.getStatus() + ",'"
                + this.getDescription() + "',"
                + this.getStartTimeToString() + ","
                + this.getDurationToString() + ","
                + this.getEpicId() + "";
    }
}
