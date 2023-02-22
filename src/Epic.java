import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> sequenceTask = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic() {
    }

    public List<Integer> getSequenceTask() {
        return sequenceTask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", UIN=" + this.getUIN() +
                ", sequenceTask=" + this.getSequenceTask() +
                '}';
    }
}
