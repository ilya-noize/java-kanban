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
 * @see #getEpic(int)  Получение главной задачи по идентификатору;
 * @see #getSubTask(int)  Получение подзадачи по идентификатору;
 * @see #addTask(Task) Создание задачи
 * @see #addEpic(Epic) Создание главной задачи
 * @see #addSubTask(SubTask) Создание подзадачи
 * @see #updateTask(Task) Обновление задачи
 * @see #updateEpic(Epic) Обновление главной задачи
 * @see #updateSubTask(SubTask) Обновление подзадачи
 * @see #deleteTask(int) Удаление задачи по идентификатору
 * @see #deleteEpic(int) Удаление главной задачи по идентификатору
 * @see #deleteSubTask(int)  Удаление подзадачи по идентификатору
 * @see #getSubTasksByEpic(int) Получение списка всех подзадач определённого эпика.
 * @see #updateEpicStatus(int) Проверить статус главной задачи
 */
public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateId = 1;

    /**
     * Получение списка истории просмотров задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
        tasks.clear();
    }

    /**
     * Удаление всех главных задач.
     */
    @Override
    public void deleteAllEpics() { // todo subTasks тоже нужно почистить.
        for (Epic epic : epics.values()) {
            deleteEpic(epic.getID());
        }
    }

    /**
     * Удаление всех подзадач.
     */
    @Override
    public void deleteAllSubTasks() {
        while (subtasks.isEmpty()) {
            deleteSubTask(subtasks.get(0).getID());
        }
    }

    /**
     * Обновление задачи
     */
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getID())) {
            tasks.replace(task.getID(), task);
        }
    }

    /**
     * Обновление главной задачи
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getID())) {
            epics.replace(epic.getID(), epic);
        }
    }

    /**
     * Обновление подзадачи
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getID())) {
            subtasks.replace(subTask.getID(), subTask);
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
        historyManager.addTask(task);

        return task;
    }

    /**
     * Получение главной задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addTask(epic);

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
        historyManager.addTask(subTask);

        return subTask;
    }

    /**
     * Создание задачи
     * @param task объект задача
     */
    @Override
    public int addTask(Task task) {
        task.setID(generateId++);
        tasks.put(task.getID(), task);

        return task.getID();
    }

    /**
     * Создание главной задачи
     * @param epic объект Главная задача
     * @return Уникальный номер в Карте для связки с подзадачами
     */
    @Override
    public int addEpic(Epic epic) {
        epic.setID(generateId++);
        epics.put(epic.getID(), epic);

        return epic.getID();
    }

    /**
     * <p>Запись новой подзадачи.</p>
     * Если главная задача с УИН, с которой связана текущая подзадача, существует, то:
     * <ul>
     *     <li>Генерим новый УИН</li>
     *     <li>Редактируем поле УИН объекта подзадачи</li>
     *     <li>Сохраняем подзадачу в Карту</li>
     *     <li>Обращаемся к главной задаче в Карте для обновления её связей с подзадачей.</li>
     *     <li>Возвращаем ИСТИНУ</li>
     * </ul>
     * Иначе возвращаем ЛОЖЬ.
     * @param subTask Подзадача для главной задачи
     */
    @Override
    public int addSubTask(SubTask subTask) {
        int epicID = subTask.getEpicId();
        if (epics.containsKey(epicID)) {
            int subtaskID = generateId++;
            subTask.setID(subtaskID);
            subtasks.put(subtaskID, subTask);
            epics.get(epicID).getSubtaskID().add(subtaskID);

            return subTask.getID();
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
        tasks.remove(id);
    }

    /**
     * <p>Удаление главной задачи по УИН</p>
     * <b>ЕСЛИ:</b>
     * <ul>
     *     <li>Главная задача существует в Карте</li>
     *     <li>Список связанных с ней подзадач пуст</li>
     * </ul> то удаление главной задачи из Карты (вернуть ИСТИНА),
     * иначе вернуть ЛОЖЬ.
     *
     * @param epicID идентификатор
     */
    @Override
    public void deleteEpic(int epicID) {
        if (epics.containsKey(epicID)) {
            List<Integer> subtaskIDs = epics.get(epicID).getSubtaskID();
            while (!subtaskIDs.isEmpty()) {
                deleteSubTask(subtaskIDs.get(0));
            }
//            }
            epics.remove(epicID);
        }
    }

    /**
     * Удаление подзадачи по УИН
     * <p>Если подзадача с таким УИН существует, то обращаемся к её главной задаче для удаления взаимосвязи
     * и последующим удалением подзадачи из Карты.</p>
     *
     * @param id идентификатор
     */
    @Override
    public void deleteSubTask(int id) {
        int epicID = subtasks.get(id).getEpicId();
        if (epics.get(epicID).getSubtaskID().contains(id)) {
            epics.get(epicID).removeSubtaskID(id);
            updateEpicStatus(epicID);
            subtasks.remove(id);
        }
    }

    /**
     * <p>Получение всех задач определённой главной задачи</p>
     * @param epicID идентификатор главной задачи
     * @return Список связанных с главной задачей мелких подзадач.
     */
    @Override
    public List<SubTask> getSubTasksByEpic(int epicID) {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        if (epics.containsKey(epicID)) {
            for (Integer subTaskId : getEpic(epicID).getSubtaskID()) {
                subTasksByEpic.add(getSubTask(subTaskId));
            }
        }

        return subTasksByEpic;
    }

    /**
     * Проверить статус всех главных задач
     * <ul>
     *     <li>Получаем доступ к объекту главная задача из Карты</li>
     *     <li>Получаем список УИН связанных подзадач</li>
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
    public void updateEpicStatus(int epicID) {
        Epic epic = epics.get(epicID);
        List<Integer> subtaskID = epic.getSubtaskID();
        if (subtaskID.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            int counterNew = 0;
            int counterDone = 0;
            for (int id : subtaskID) {
                Status markStatus = subtasks.get(id).getStatus();
                if (markStatus.equals(Status.NEW)) {
                    counterNew++;
                }
                if (markStatus.equals(Status.DONE)) {
                    counterDone++;
                }
            }
            if (subtaskID.size() == counterNew) {
                epic.setStatus(Status.NEW);
            } else if (subtaskID.size() == counterDone) {
                epic.setStatus(Status.DONE);
            } else epic.setStatus(Status.IN_PROGRESS);
        }
    }
}