package manager;

import tasks.Task;
import java.util.*;

/**
 *
 * Ключом будет id задачи, просмотр которой требуется удалить
 * Значение — место просмотра этой задачи в списке (узел связного списка).
 */
public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer,Node> nodes = new HashMap<>();
    private Node begin;
    private Node end;

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

    private void removeNode(Node node){
        if(node == begin){
            begin = node.next;
            nodes.remove(node.task.getId());
            begin.prev = null;
        } else if (node == end) {
            end = node.prev;
            nodes.remove(node.task.getId());
            end.next = null;
        } else if (begin == end){
            begin = end = null;
            nodes.clear();
        } else {
            Node nodePrev = node.prev;
            nodePrev.next = node.next;

            Node nodeNext = node.next;
            nodeNext.prev = node.prev;

            remove(node.task.getId());

            nodes.remove(node.task.getId());
        }
    }

    /**
     * Получение списка истории просмотров задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node node = begin;
        while (node.next != null) {
            history.add(node.task);
            node = node.next;
        }

        return history;
    }

    /**
     * Добавление задачи в список просмотренных задач
     *
     * @param task задача
     */
    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if(nodes.containsKey(taskId)){
            removeNode(nodes.get(taskId));
            nodes.remove(taskId);
        }
        linkLast(task);
    }


    @Override
    public void remove(int id){
        if(nodes.containsKey(id)) {
            removeNode(nodes.get(id));
        }
    }

    private class Node {
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