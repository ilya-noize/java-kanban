package tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected final List<Integer> subtaskId;

    public Epic(String title, String description) {
        super(title, description);
        subtaskId = new ArrayList<>();
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void removeSubtaskId(Integer id) {
        this.subtaskId.remove(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;

        return getSubtaskId().equals(epic.getSubtaskId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtaskId());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getId() +
                ", idsSubTask=" + this.getSubtaskId() +
                '}';
    }
}
