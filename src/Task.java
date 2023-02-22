import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int uniqueIdentificationNumber;
    private int status;

    protected void setUIN(int uniqueIdentificationNumber) {
        this.uniqueIdentificationNumber = uniqueIdentificationNumber;
    }

    protected int getUIN() {
        return uniqueIdentificationNumber;
    }

    /**
     *
     * @param title Название задачи
     * @param description Описание задачи
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.uniqueIdentificationNumber = hashCode();
        this.status = Status.NEW.getStatus();
    }
    public Task(){
        this.title = "";
        this.description = "";
        this.uniqueIdentificationNumber = hashCode();
        this.status = Status.NEW.getStatus();
    }

    public String getTitle() {
        return title;
    }
    public boolean setTitle(String title) {
        if (title == null) {
            return false;//this.title = "";
        }
        this.title = title;
        return true;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        if(status == Status.NEW.getStatus() || status == Status.IN_PROGRESS.getStatus()
                || status == Status.DONE.getStatus())
            this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", UIN=" + this.getUIN() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return title.equals(task.title) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }
}

