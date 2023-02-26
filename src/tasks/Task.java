package tasks;

import java.util.Objects;

/**
 * Класс Задача. Простейшая сущность системы управления задачами. Включает в себя 4 поля:
 * <ul>
 *     <li>id - Идентификатор задачи</li>
 *     <li>title - заголовок (или название) задачи</li>
 *     <li>description - Подробности (по желанию)</li>
 *     <li>status - статус выполнения задачи</li>
 * </ul>
 */
public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected Status status;

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.id = hashCode();
        this.status = Status.NEW;
    }

    public Task() {
        this.title = "";
        this.description = "";
        this.id = hashCode();
        this.status = Status.NEW;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!title.isEmpty())
            this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.isEmpty()) this.description = "";
        else this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getID() +
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

