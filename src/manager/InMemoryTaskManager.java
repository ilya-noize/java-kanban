package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранение задач всех типов.
 *
 * @see #getAllTasks() Получение списка всех задач
 * @see #getAllEpics() Получение списка всех главных задач
 * @see #getAllSubTasks() Получение списка всех подзадач
 * @see #deleteAllTasks() Удаление всех задач
 * @see #deleteAllEpics() Удаление всех главных задач
 * @see #deleteAllSubTasks() Удаление всех подзадач
 * @see #getTask(int)  Получение задачи по идентификатору;
 * @see #getEpic(int)  Получение Большой задачи по идентификатору;
 * @see #getSubTask(int)  Получение подзадачи по идентификатору;
 * @see #addTask(Task) Создание задачи
 * @see #addEpic(Epic) Создание Большой задачи
 * @see #addSubTask(SubTask) Создание подзадачи
 * @see #updateTask(Task) Обновление задачи
 * @see #updateEpic(Epic) Обновление Большой задачи
 * @see #updateSubTask(SubTask) Обновление подзадачи
 * @see #deleteTask(int) Удаление задачи по идентификатору
 * @see #deleteEpic(int) Удаление Большой задачи по идентификатору
 * @see #deleteSubTask(int)  Удаление подзадачи по идентификатору
 * @see #getSubTasksByEpic(int) Получение списка всех подзадач определённого эпика.
 * @see #updateEpicStatus(int) Проверить статус Большой задачи
 */
public class InMemoryTaskManager implements TaskManager {

    private int generateId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();


    /**
     * Получение списка истории просмотров задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        List<Task> history = historyManager.getHistory();
        if(history != null) return history;
        return new ArrayList<>();
    }

    /**
     * Получение списка всех задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(tasks.values());
    }

    /**
     * Получение списка всех главных задач
     * @return Список объектов EPIC
     */
    @Override
    public List<Epic> getAllEpics() {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(epics.values());
    }

    /**
     * Получение списка всех подзадач
     * @return Список объектов SUBTASK
     */
    @Override
    public List<SubTask> getAllSubTasks() {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(subtasks.values());
    }

    /**
     * Удаление всех задач
     */
    @Override
    public void deleteAllTasks() {
        for (Task task: tasks.values()) {
            deleteTask(task.getId());
        }
    }

    /**
     * Удаление всех главных задач.
     */
    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            deleteEpic(epic.getId());
        }
    }

    /**
     * Удаление всех подзадач.
     */
    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subtasks.values()) {
            deleteSubTask(subTask.getId());
        }
    }

    /**
     * Обновление задачи
     */
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
        }
    }

    /**
     * Обновление Большой задачи
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
        }
    }

    /**
     * Обновление подзадачи
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.replace(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
        }
    }

    /**
     * Получение задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);

        return task;
    }

    /**
     * Получение Большой задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);

        return epic;
    }

    /**
     * Получение подзадачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subtasks.get(id);
        historyManager.add(subTask);

        return subTask;
    }

    /**
     * Создание задачи
     * @param task объект задача
     */
    @Override
    public int addTask(Task task) {
        task.setId(generateId++);
        tasks.put(task.getId(), task);

        return task.getId();
    }

    /**
     * Создание Большой задачи
     * @param epic объект Большая задача
     * @return Уникальный номер в Карте для связки с подзадачами
     */
    @Override
    public int addEpic(Epic epic) {
        epic.setId(generateId++);
        epics.put(epic.getId(), epic);

        return epic.getId();
    }

    /**
     * <p>Запись новой подзадачи.</p>
     * Если Большая задача с ID, с которой связана текущая подзадача, существует, то:
     * <ul>
     *     <li>Генерим новый ID</li>
     *     <li>Редактируем поле ID объекта подзадачи</li>
     *     <li>Сохраняем подзадачу в Карту</li>
     *     <li>Обращаемся к Большой задаче в Карте для обновления её связей с подзадачей.</li>
     *     <li>Возвращаем ИСТИНУ</li>
     * </ul>
     * Иначе возвращаем ЛОЖЬ.
     * @param subTask Подзадача для Большой задачи
     */
    @Override
    public int addSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        if (epics.containsKey(epicId)) {
            int subtaskId = generateId++;
            subTask.setId(subtaskId);
            subtasks.put(subtaskId, subTask);
            epics.get(epicId).getSubTaskId().add(subtaskId);
            updateEpicStatus(epicId);

            return subTask.getId();
        }

        return (-1);
    }

    /**
     * Удаление задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    /**
     * <p>Удаление Большой задачи по ID</p>
     * <b>ЕСЛИ:</b>
     * <ul>
     *     <li>Большая задача существует в Карте</li>
     *     <li>Список связанных с ней подзадач пуст</li>
     * </ul> то удаление Большой задачи из Карты (вернуть ИСТИНА),
     * иначе вернуть ЛОЖЬ.
     *
     * @param id идентификатор
     */
    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            List<Integer> subtaskIds = epics.get(id).getSubTaskId();
            if (!subtaskIds.isEmpty()) {
                for (int subtaskId: subtaskIds) {
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    /**
     * Удаление подзадачи по ID
     * <p>Если подзадача с таким ID существует, то обращаемся к её Большой задаче для удаления взаимосвязи
     * и последующим удалением подзадачи из Карты.</p>
     *
     * @param id идентификатор
     */
    @Override
    public void deleteSubTask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        if (epics.get(epicId).getSubTaskId().contains(id)) {
            epics.get(epicId).removeSubtaskId(id);
            updateEpicStatus(epicId);
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    /**
     * <p>Получение всех задач определённой Большой задачи</p>
     * @param epicId идентификатор Большой задачи
     * @return Список связанных с Большой задачей мелких подзадач.
     */
    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Integer subTaskId : getEpic(epicId).getSubTaskId()) {
                subTasksByEpic.add(getSubTask(subTaskId));
            }
        }

        return subTasksByEpic;
    }

    /**
     * Проверить статус всех главных задач
     * <ul>
     *     <li>Получаем доступ к объекту Большая задача из Карты</li>
     *     <li>Получаем список ID связанных подзадач</li>
     *     <li>Объявляем счётчик состояний</li>
     *     <li>Ищем все связанные задачи для получения доступа к статусу задачи</li>
     *     <li>Подсчёт всех состояний<sup>*</sup></li>
     * </ul><hr>
     * <sup>*</sup>Управление статусами осуществляется по следующему правилу:
     *      <p>ЕСЛИ (очередь подзадач пуста ИЛИ все подзадачи NEW) ТО статус-NEW</p>
     *      <p>ЕСЛИ (Все подзадачи завершены) ТО статус-DONE</p>
     *      Во всех остальных случаях статус-IN_PROGRESS;
     */
    @Override
    public void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subTaskId = epic.getSubTaskId();
            if (subTaskId.isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                int counterNew = 0;
                int counterDone = 0;
                for (int id : subTaskId) {
                    Status markStatus = subtasks.get(id).getStatus();
                    if (markStatus.equals(Status.NEW)) {
                        counterNew++;
                    }
                    if (markStatus.equals(Status.DONE)) {
                        counterDone++;
                    }
                }
                if (subTaskId.size() == counterNew) {
                    epic.setStatus(Status.NEW);
                } else if (subTaskId.size() == counterDone) {
                    epic.setStatus(Status.DONE);
                } else epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}