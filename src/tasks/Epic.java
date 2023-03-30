package tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTaskId;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subTaskId = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description);
        this.subTaskId = new ArrayList<>();
    }

    public List<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void removeSubtaskId(Integer id) {
        this.subTaskId.remove(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;

        return getSubTaskId().equals(epic.getSubTaskId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubTaskId());
    }

    @Override
    public String toString() {

        return this.getId() + "," + TypeTask.EPIC + ",\'" + this.getTitle() + "\',"
                + this.getStatus() + ",\'" + this.getDescription() + "\',\n";
    }
}
