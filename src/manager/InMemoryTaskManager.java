package manager;

import exception.ManagerException;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static tasks.Status.*;

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

    /**
     * Безопасно.
     * private tasks, subtasks, epics;
     * Небезопасно, НО нужно для загрузки истории из файла в обход get-метода в наследнике FileBackedTasksManager
     * protected final Map<Integer, T extend Task> tasks, subtasks, epics;
     */
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    public final HistoryManager historyManager = Managers.getDefaultHistory();

    /**
     * Обновление generateId после загрузки задач из файла (для учёта новой задачи)
     * @param id id созданной задачи
     */
    protected void setGenerateId(int id) {
        this.generateId = ++id;
    }

    protected int getGenerateId() {
        return generateId;
    }

    /**
     * Получение списка истории просмотров задач
     *
     * @return Список объектов TASK
     */
    @Override
    public List<Task> getHistory() {
        List<Task> history = historyManager.getHistory();
        if (history.isEmpty()) {
            return new ArrayList<>();
        }
        return history;
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
        //tasks.clear();
        while (tasks.size() > 0) {
            deleteTask((int) tasks.keySet().toArray()[0]);
        }
    }

    /**
     * Удаление всех главных задач.
     */
    @Override
    public void deleteAllEpics() {
        while (epics.size() > 0) {
            deleteEpic((int) epics.keySet().toArray()[0]);
        }
    }

    /**
     * Удаление всех подзадач.
     */
    @Override
    public void deleteAllSubTasks() {
        while (subtasks.size() > 0) {
            deleteSubTask((int) subtasks.keySet().toArray()[0]);
        }
    }

    private boolean updateIsValid(Task task) {
        int id = task.getId();
        if (task.getTitle().isBlank() || task.getDescription().isBlank())
            throw new ManagerException("Название и описание задачи не могут быть пустыми.");
        if (task.getStatus() == null)
            throw new ManagerException("Статус задачи не может быть пустым.");
        if (!epics.containsKey(id) && !subtasks.containsKey(id) && !tasks.containsKey(id))
            throw new ManagerException("Такой задачи не существует.");

        return true;
    }

    /**
     * Обновление задачи
     */
    @Override
    public void updateTask(Task task) {
        if (updateIsValid(task)) {
            if (!tasks.containsKey(task.getId())) {
                throw new ManagerException("Такой задачи не существует.");
            }
            tasks.replace(task.getId(), task);
        }
    }

    /**
     * Обновление Большой задачи
     */
    @Override
    public void updateEpic(Epic epic) {
        if (updateIsValid(epic)) {
            if (!epics.containsKey(epic.getId())) {
                throw new ManagerException("Такой задачи не существует.");
            }
            epics.replace(epic.getId(), epic);
        }
    }

    /**
     * Обновление подзадачи
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        if (updateIsValid(subTask)) {
            if (!subtasks.containsKey(subTask.getId())) {
                throw new ManagerException("Такой задачи не существует.");
            }
            subtasks.replace(subTask.getId(), subTask);
            int epicId = subTask.getEpicId();
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
    }

    /**
     * Получение задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public Task getTask(int id) {
        try {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } catch (ManagerException e) {
            throw new ManagerException("Task's not exists: " + e.getMessage());
        }
    }

    /**
     * Получение Большой задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public Epic getEpic(int id) {
        try {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        } catch (NullPointerException e) {
            throw new NullPointerException("Epic's not exists: " + e.getMessage());
        }
    }

    /**
     * Получение подзадачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public SubTask getSubTask(int id) {
        try {
            SubTask subTask = subtasks.get(id);
            historyManager.add(subTask);
            return subTask;
        } catch (NullPointerException e) {
            throw new NullPointerException("SubTask's not exists: " + e.getMessage());
        }
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
     *     <li>Сохраняем подзадачу в HashMap</li>
     *     <li>Обращаемся к Большой задаче в HashMap
     *     для обновления:<br><u>её связей с подзадачей</u>, <u>срока выполнения</u>, <u>даты старта</u>.</li>
     *     <li>Возвращаем ID</li>
     * </ul>
     * Иначе возвращаем исключение "Создайте Epic";
     * @param subTask Подзадача для Большой задачи
     */
    @Override
    public int addSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        if (epics.containsKey(epicId)) {
            int subtaskId = generateId++;
            subTask.setId(subtaskId);
            subtasks.put(subtaskId, subTask);
            epics.get(epicId).getSubTaskIds().add(subtaskId);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);

            return subTask.getId();
        }
        throw new ManagerException("Сначала создайте главную задачу.");
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
            List<Integer> subtaskIds = epics.get(id).getSubTaskIds();
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
        if (epics.get(epicId).getSubTaskIds().contains(id)) {
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
        if (epics.containsKey(epicId)) { // todo Can't tested!
            subTasksByEpic = getEpic(epicId).getSubTaskIds().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
        }

        return subTasksByEpic;
    }

    /**
     * <p>Заполнение 'расчётных полей' Главной задачи</p>
     * <ul>
     *     <li>Продолжительность эпика /{@code duration}/ : сумма продолжительности всех его подзадач.</li>
     *     <li>Время начала /{@code startTime}/ : дата старта самой ранней подзадачи.</li>
     *     <li>Время завершения /{@code endTime}/ : время окончания самой поздней из задач.</li>
     * </ul>
     *
     * @param epicId ID главной задачи
     */
    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTaskList = epic.getSubTaskIds()
                .stream()
                .map(subtasks::get)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
        int lastIndex = subTaskList.size() - 1;
        LocalDateTime startTime = subTaskList.get(0).getStartTime();
        LocalDateTime endTime = subTaskList.get(lastIndex).getStartTime();
        Duration duration = subTaskList.get(lastIndex).getDuration();
        LocalDateTime endTimeEpic = endTime.plusMinutes(duration.toMinutes());
        epic.setStartEndTime(startTime, endTimeEpic);
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
            List<Integer> subTaskId = epic.getSubTaskIds();
            if (subTaskId.isEmpty()) {
                epic.setStatusNew();
            } else {
                epics.replace(epicId, epic, epicStatusUpdate(calculateStatus(subTaskId), epic));
            }
        }
    }

    private Status calculateStatus(List<Integer> subTaskId){
        int counterNew = 0;
        int counterDone = 0;
        int count = subTaskId.size();
        for (int id : subTaskId) {
            Status markStatus = subtasks.get(id).getStatus();
            if (markStatus.equals(NEW)) {
                counterNew++;
            }
            if (markStatus.equals(DONE)) {
                counterDone++;
            }
        }
        if (count == counterNew) return NEW;
        if (count == counterDone) return DONE;
        return IN_PROGRESS;
    }

    private Epic epicStatusUpdate(Status status, Epic epic) {
        Epic epicNew = new Epic(
                epic.getId(),
                epic.getTitle(),
                epic.getDescription(),
                status,
                epic.getStartTimeToString(),
                epic.getDurationToString()
        );
        epicNew.getSubTaskIds().addAll(epic.getSubTaskIds());
        return epicNew;
    }
    public Set<Task> getPrioritizedTasks(){
        Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
            LocalDateTime time1 = task1.getStartTime();
            LocalDateTime time2 = task2.getStartTime();

            if (time1 == null) {
                return 1;
            }

            if (time2 == null) {
                return -1;
            }

            return time1.compareTo(time2);
        });

        prioritizedTasks.addAll(getAllTasks());
        prioritizedTasks.addAll(getAllSubTasks());
        prioritizedTasks.addAll(getAllEpics());

        return prioritizedTasks;
    }
}