public enum Status{
    NEW(0),
    IN_PROGRESS(1),
    DONE(2);
    private final int status;

    Status(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                '}';
    }
}