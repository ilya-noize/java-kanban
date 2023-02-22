package tasks;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Integer> idsSubTask;

    public Epic(String title, String description) {
        super(title, description);
        idsSubTask = new ArrayList<>();
    }

    public Epic() {
        idsSubTask = new ArrayList<>();
    }

    public List<Integer> getIdsSubTask() {
        return idsSubTask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", UIN=" + this.getID() +
                ", idsSubTask=" + this.getIdsSubTask() +
                '}';
    }
}
