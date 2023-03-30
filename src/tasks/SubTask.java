package tasks;

import java.util.Objects;

public class SubTask extends Task{
    protected int epicId;

    /**
     *
     * @param title Название задачи
     * @param description Описание задачи
     * @param epicId Принадлежность к главной задаче (epic)
     */
    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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

        return this.getId() + "," + TypeTask.SUBTASK + ",\'" + this.getTitle() + "\',"
                + this.getStatus() + ",\'" + this.getDescription() + "\'," + this.getEpicId() + "\n";
    }
}
