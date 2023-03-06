package tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> idsSubTask;

    public Epic(String title, String description) {
        super(title, description);
        idsSubTask = new ArrayList<>();
    }

    public List<Integer> getIdsSubTask() {
        return idsSubTask;
    }

    public void setIdsSubTask(List<Integer> idsSubTask) {
        this.idsSubTask = idsSubTask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getID() +
                ", idsSubTask=" + this.getIdsSubTask() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return getIdsSubTask().equals(epic.getIdsSubTask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdsSubTask());
    }
}
