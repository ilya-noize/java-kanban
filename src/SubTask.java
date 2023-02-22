public class SubTask extends Task{
    private int referenceToEpic;

    /**
     *
     * @param title Название задачи
     * @param description Описание задачи
     * @param referenceToEpic Принадлежность к главной задаче (epic)
     */
    public SubTask(String title, String description, int referenceToEpic) {
        super(title, description);
        this.referenceToEpic = referenceToEpic;
    }
    public SubTask(){
    }

    public int getReferenceToEpic() {
        return referenceToEpic;
    }

    public void setReferenceToEpic(int referenceToEpic) {
        this.referenceToEpic = referenceToEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", UIN=" + this.getUIN() +
                ", referenceToEpic=" + this.getReferenceToEpic() +
                '}';
    }
}
