package tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tasks.TypeTask.EPIC;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public Epic(int id, String title, String description, Status status, String startTime, String duration) {
        super(id, title, description, status, startTime, duration);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, String startTime, String duration) {
        super(title, description, startTime, duration);
        this.subTaskIds = new ArrayList<>();
    }

    @Override
    public TypeTask getType(){
        return EPIC;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void removeSubtaskId(Integer id) {
        this.subTaskIds.remove(id);
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
                + this.getDescription() + "',"
                + this.getStartTime() + ","
                + this.getDuration() + ",\n";
    }
}
