package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.time.Duration.between;
import static java.time.Duration.ofMinutes;
import static tasks.TypeTask.EPIC;

public class Epic extends Task {
    private List<Integer> subTaskIds;
    private LocalDateTime endTime;

    /**
     * @param title       Название задачи
     * @param description Описание задачи
     */
    public Epic(String title, String description) {
        super(title, description);
        this.subTaskIds = new ArrayList<>();
        this.startTime = LocalDateTime.ofEpochSecond(0L,0, ZoneOffset.UTC);
        this.duration = Duration.parse("PT0S");
        this.endTime = this.startTime.plusMinutes(this.duration.toMinutes());
    }

    public Epic(){
        super();
    }
    @Override
    public TypeTask getType() {
        return EPIC;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void removeSubtaskId(Integer id) {
        if(!subTaskIds.isEmpty()) {
            this.subTaskIds.remove(id);
        }
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
