package manager.task;

import exception.ManagerException;
import manager.history.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
 * @see #updateEpicStatus(Epic) Проверить статус Большой задачи
 * @see #getPrioritizedTasks()
 */
public class InMemoryTaskManager implements TaskManager {
    private int generateId = 1;

    protected final HistoryManager historyManager;
    /**
     * Безопасно.
     * private tasks, subtasks, epics;
     * Небезопасно, НО нужно для загрузки истории из файла в обход get-метода в наследнике FileBackedTasksManager
     * protected final Map<Integer, T extend Task> tasks, subtasks, epics;
     */
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final Comparator<Task> compareToTime = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(compareToTime);


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    /**
     * Обновление generateId после загрузки задач из файла (для учёта новой задачи)
     *
     * @param id id созданной задачи
     */
    protected void setGenerateId(int id) {
        this.generateId = ++id;
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
        if (epics.isEmpty()) {
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
        if (subtasks.isEmpty()) {
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

    /**
     * Обновление задачи
     */
    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
            updatePrioritizedTasks(task);
        }
    }

    /**
     * Обновление Большой задачи
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
        }
    }

    /**
     * Обновление подзадачи
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            updatePrioritizedTasks(subTask);
            updateEpicStatus(epic);
            updateEpicTime(epic);
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
        if (task != null) {
            historyManager.add(task);
        }
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
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    /**
     * Получение подзадачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public SubTask getSubTask(int id) {
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    /**
     * Создание задачи
     * @param task объект задача
     */
    @Override
    public Task addTask(Task task) {
        if (task != null) {
            task.setId(generateId++);
            tasks.put(task.getId(), task);
            addPrioritizedTasks(task);
            return task;
        }
        throw new ManagerException("Задача не создана");
    }

    /**
     * Создание Большой задачи
     * @param epic объект Большая задача
     * @return Уникальный номер в Карте для связки с подзадачами
     */
    @Override
    public Epic addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(generateId++);
            epics.put(epic.getId(), epic);
            return epic;
        }
        throw new ManagerException("Задача не создана");
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
    public SubTask addSubTask(SubTask subTask) {
        if (subTask != null) {
            int epicId = subTask.getEpicId();
            if (epics.containsKey(epicId)) {
                int subtaskId = generateId++;
                subTask.setId(subtaskId);
                subtasks.put(subtaskId, subTask);
                epics.get(epicId).getSubTaskIds().add(subtaskId);
                addPrioritizedTasks(subTask);
                updateEpicStatus(epics.get(epicId));
                updateEpicTime(epics.get(epicId));
                return subTask;
            }
            throw new ManagerException("Сначала создайте главную задачу.");
        }
        throw new ManagerException("Задача не создана");
    }

    /**
     * Удаление задачи по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.removeIf(task -> id == task.getId());
            tasks.remove(id);
            historyManager.remove(id);
        }
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
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subTaskId : epic.getSubTaskIds()) {
                prioritizedTasks.removeIf(task -> subTaskId == task.getId());
                subtasks.remove(subTaskId);
                historyManager.remove(subTaskId);
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
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            prioritizedTasks.removeIf(task -> id == task.getId());
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
     *     <li>Продолжительность эпика /{@code duration}/ : от начала первой до окончания последней.</li>
     *     <li>Время начала /{@code startTime}/ : дата старта самой ранней подзадачи.</li>
     *     <li>Время завершения /{@code endTime}/ : время окончания самой поздней из задач.</li>
     * </ul>
     *
     * @param epic эпик
     */
    private void updateEpicTime(Epic epic) {
        if(epic.getSubTaskIds().isEmpty()){
            LocalDateTime nullTime = LocalDateTime.ofEpochSecond(0L,0, ZoneOffset.UTC);
            epic.setStartEndTime(nullTime, nullTime);
        } else {
            List<SubTask> subTaskList = epic.getSubTaskIds()
                    .stream()
                    .map(subtasks::get)
                    .sorted(Comparator.comparing(Task::getStartTime))
                    .collect(Collectors.toList());
            int lastIndex = subTaskList.size() - 1;
            LocalDateTime startTimeSubTaskFirst = subTaskList.get(0).getStartTime();
            LocalDateTime endTimeSubTaskLast = subTaskList.get(lastIndex).getStartTime();
            Duration durationSubTaskLast = subTaskList.get(lastIndex).getDuration();
            LocalDateTime endTimeEpic = endTimeSubTaskLast.plusMinutes(durationSubTaskLast.toMinutes());
            epic.setStartEndTime(startTimeSubTaskFirst, endTimeEpic);
        }
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
    public void updateEpicStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            List<Integer> subTaskId = epic.getSubTaskIds();
            if (!subTaskId.isEmpty()) {
                epic.setStatus(calculateStatus(subTaskId));
                epics.put(epic.getId(), epic);
            } else {
                epic.setStatus(NEW);
            }
        }
    }

    private Status calculateStatus(List<Integer> subTaskId){
        if (subTaskId.size() == 0) return NEW;
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

    /**
     * Обновление в списке приоритета
     *
     * @param task задача для обновления
     */
    public void updatePrioritizedTasks(Task task) {
        prioritizedTasks.removeIf(task1 -> task1.getId() == task.getId());
        addPrioritizedTasks(task);
    }

    /**
     * Добавление задач и подзадач в список приоритета
     *
     * @param task задача для добавления
     */
    public void addPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
        checkTaskPriority();
    }

    /**
     * Проверка временного приоритета задач
     */
    private void checkTaskPriority() {
        List<Task> tasks = getPrioritizedTasks();
        for (int i = 1; i < tasks.size(); i++) {
            Task taskChecking = tasks.get(i);
            boolean tasksCrossingTime = checkTimeCrossing(taskChecking, tasks);
            if (tasksCrossingTime) {
                throw new ManagerException("Task[" + taskChecking.getId() + "] overlaps Task[" + tasks.get(i - 1) + "]");
            }
        }
    }

    /**
     * Конвертор LocalDateTime в список Instant
     *
     * @param time LocalDateTime
     * @return Instant
     */
    private Instant timeToInstant(LocalDateTime time) {
        return time.toInstant(ZoneOffset.UTC);
    }

    /**
     * Получение времени начала и расчёт конца задачи
     *
     * @param task задача
     * @return Список мгновений времени для сравнения времени выполнения задач
     */
    private List<Instant> getInstantsArrayFromTask(Task task) {
        LocalDateTime start = task.getStartTime();
        long minutes = task.getDuration().toMinutes();
        LocalDateTime end = start.plusMinutes(minutes);

        return List.of(timeToInstant(start), timeToInstant(end));
    }

    /**
     * Проверка пересечения времени выполнения задач
     *
     * @param taskCheck Задача на проверку
     * @param tasks     список задач по приоритету (копия)
     * @return true - есть пересечение: false - задачи по порядку.
     */
    private boolean checkTimeCrossing(Task taskCheck, List<Task> tasks) {

        List<Instant> taskTime = getInstantsArrayFromTask(taskCheck);
        Instant taskStart = taskTime.get(0);
        Instant taskEnd = taskTime.get(1);

        for (Task task : tasks) {

            List<Instant> prioritizeTaskTime = getInstantsArrayFromTask(task);
            Instant prioritizeTaskStart = prioritizeTaskTime.get(0);
            Instant prioritizeTaskEnd = prioritizeTaskTime.get(1);


            //taskStart<prioritizeTaskStart
            boolean taskStartBeforePrioritizeTaskStart = taskStart.isBefore(prioritizeTaskStart);
            //taskEnd>prioritizeTaskStart
            boolean taskEndAfterPrioritizeTaskStart = taskEnd.isAfter(prioritizeTaskStart);
            //taskEnd>prioritizeTaskEnd
            boolean taskEndAfterPrioritizeTaskEnd = taskEnd.isAfter(prioritizeTaskEnd);
            //taskStart<prioritizeTaskEnd
            boolean taskStartBeforePrioritizeTaskEnd = taskStart.isBefore(prioritizeTaskEnd);

            if (taskStartBeforePrioritizeTaskStart) {
                if (taskEndAfterPrioritizeTaskStart) {
                    return true;
                } else if (taskEndAfterPrioritizeTaskEnd) {
                    return true;
                }
            } else if (taskEndAfterPrioritizeTaskEnd) {
                if (taskStartBeforePrioritizeTaskEnd) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Получение копии списка приоритетов
     *
     * @return список приоритетов (копия)
     */
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }
}