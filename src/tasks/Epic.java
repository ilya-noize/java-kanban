package tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected final List<Integer> subtaskID;

    public Epic(String title, String description) {
        super(title, description);
        subtaskID = new ArrayList<>();
    }

    public List<Integer> getSubtaskID() {
        return subtaskID;
    }

    public void removeSubtaskID(Integer id) {
        this.subtaskID.remove(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;

        return getSubtaskID().equals(epic.getSubtaskID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtaskID());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getID() +
                ", idsSubTask=" + this.getSubtaskID() +
                '}';
    }
}
