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
    public SubTask(){
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", UIN=" + this.getID() +
                ", referenceToEpic=" + this.getEpicId() +
                '}';
    }
}
