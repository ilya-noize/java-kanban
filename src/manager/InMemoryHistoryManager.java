package manager;

import tasks.Task;

import java.util.*;


/**
 * Ключом будет id задачи, просмотр которой требуется удалить
 * Значение — место просмотра этой задачи в списке (узел связного списка).
 */
public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer,Node> nodes = new HashMap<>();
    private Node begin;
    private Node end;

    /**
     * Элемент реализации двусвязного списка задач.
     * linkLast будет добавлять задачу в конец этого списка.
     * @param task задача
     */
    public void linkLast(Task task){
        if(begin == null){
            begin = new Node(null, task, null);
            end = begin;
        } else {
            Node lastPrev = end;
            end = new Node(lastPrev, task, null);
            lastPrev.next = end;
        }
        nodes.put(task.getId(), end);
    }

    /**
     * Удаление узла двусвязного списка.
     * При повторном просмотре задачи или принудительном удалении.
     * @param node узел двусвязного списка
     */
    private void removeNode(Node node){
        if(node.equals(begin)){
            begin = node.next;
            begin.prev = null;
            nodes.remove(node.task.getId());
        } else if (node.equals(end)) {
            end = node.prev;
            nodes.remove(node.task.getId());
            end.next = null;
        } else if (begin.equals(end)){
            begin = end = null;
            nodes.clear();
        } else {
            Node nodePrev = node.prev;
            nodePrev.next = node.next;

            Node nodeNext = node.next;
            nodeNext.prev = node.prev;

            nodes.remove(node.task.getId());
            remove(node.task.getId());
        }
    }

    /**
     * Элемент реализации двусвязного списка задач.
     * Собирает все задачи из него в обычный ArrayList
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node node = null;
        if (begin != null) {
            node = begin;
        }
        while (node != null) {
            history.add(node.task);
            node = node.next;
        }

        return history;
    }

    /**
     * Добавление задачи в список просмотренных задач для getTask(int id), getSubTask(int id), getEpic(int id)
     *
     * @param task задача
     */
    @Override
    public void add(Task task) {
        int taskId = task.getId();
        remove(taskId);
        linkLast(task);
    }

    /**
     * Удаление задачи из истории:
     * если дублируется при добавлении в историю (add);
     * если задача совсем удаляется (deleteTask/SubTask/Epic).
     *
     * @param id номер задачи
     */
    @Override
    public void remove(int id){
        if(nodes.containsKey(id)) {
            removeNode(nodes.get(id));
            nodes.remove(id);
        }
    }

    private static class Node {
        Node prev;
        Task task;
        Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(prev, node.prev) && task.equals(node.task) && Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prev, task, next);
        }
    }
}