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
 * @see #removeAllTasks() Удаление всех задач
 * @see #removeAllEpics() Удаление всех главных задач
 * @see #removeAllSubTasks() Удаление всех подзадач
 * @see #getTaskById(int)  Получение задачи по идентификатору;
 * @see #getEpicById(int)  Получение главной задачи по идентификатору;
 * @see #getSubTaskById(int)  Получение подзадачи по идентификатору;
 * @see #addTask(Task) Создание задачи
 * @see #addEpic(Epic) Создание главной задачи
 * @see #addSubTask(SubTask) Создание подзадачи
 * @see #updateTask(Task) Обновление задачи
 * @see #updateEpic(Epic) Обновление главной задачи
 * @see #updateSubTask(SubTask) Обновление подзадачи
 * @see #deleteTaskByUIN(int) Удаление задачи по идентификатору
 * @see #deleteEpicByUIN(int) Удаление главной задачи по идентификатору
 * @see #deleteSubTaskByUIN(int)  Удаление подзадачи по идентификатору
 * @see #getSubTasksByEpic(int) Получение списка всех подзадач определённого эпика.
 * @see #setEpicsStatus() Проверить статус главной задачи
 */
public class Manager {

    Map<Integer, Task> taskRegister = new HashMap<>();
    Map<Integer, SubTask> subTaskRegister = new HashMap<>();
    Map<Integer, Epic> epicRegister = new HashMap<>();

    private int generateId = 1;


    /**
     * Получение списка всех задач
     * @return Список объектов TASK
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        if (taskRegister.isEmpty()) return new ArrayList<>();
        for (Task task : taskRegister.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Получение списка всех главных задач
     * @return Список объектов EPIC
     */
    public List<Epic> getAllEpics() {
        List<Epic> epics = new ArrayList<>();
        if (taskRegister.isEmpty()) return new ArrayList<>();
        for (Epic epic : epicRegister.values()) {
            epics.add(epic);
        }
        return epics;
    }

    /**
     * Получение списка всех подзадач
     * @return Список объектов SUBTASK
     */
    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();
        if (taskRegister.isEmpty()) return new ArrayList<>();
        for (SubTask subtask : subTaskRegister.values()) {
            subTasks.add(subtask);
        }
        return subTasks;
    }

    /**
     * Удаление всех задач
     */
    public void removeAllTasks() {
        taskRegister.clear();
    }

    /**
     * Удаление всех главных задач.
     */
    public void removeAllEpics() {
        for (Epic epic : epicRegister.values()) {
            deleteEpicByUIN(epic.getID());
        }
    }

    /**
     * Удаление всех подзадач.
     */
    public void removeAllSubTasks() {
        for (SubTask subTask : subTaskRegister.values()) {
            deleteSubTaskByUIN(subTask.getID());
        }
    }

    /**
     * Обновление задачи
     */
    public void updateTask(Task task) {
        if (taskRegister.containsKey(task.getID()))
            taskRegister.replace(task.getID(), task);
    }

    /**
     * Обновление главной задачи
     */
    public void updateEpic(Epic epic) {
        if (epicRegister.containsKey(epic.getID()))
            epicRegister.replace(epic.getID(), epic);
    }

    /**
     * Обновление подзадачи
     */
    public void updateSubTask(SubTask subTask) {
        if (subTaskRegister.containsKey(subTask.getID()))
            subTaskRegister.replace(subTask.getID(), subTask);
    }

    /**
     * Получение задачи по идентификатору
     * @param id идентификатор
     */
    public Task getTaskById(int id) {
        if (taskRegister.containsKey(id))
            return taskRegister.get(id);
        return new Task();
    }

    /**
     * Получение главной задачи по идентификатору
     * @param id идентификатор
     */
    public Epic getEpicById(int id) {
        if (epicRegister.containsKey(id))
            return epicRegister.get(id);
        return new Epic();
    }

    /**
     * Получение подзадачи по идентификатору
     * @param id идентификатор
     */
    public SubTask getSubTaskById(int id) {
        if (subTaskRegister.containsKey(id))
            return subTaskRegister.get(id);
        return new SubTask();
    }

    /**
     * Создание задачи
     */
    public void addTask(Task task) {
        task.setID(generateId++);
        taskRegister.put(task.getID(), task);
    }

    /**
     * Создание главной задачи
     * @param epic Объект Главная задача
     * @return Уникальный номер в Карте для связки с подзадачами
     */
    public int addEpic(Epic epic) {
        epic.setID(generateId++);
        epicRegister.put(epic.getID(), epic);
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
    public boolean addSubTask(SubTask subTask) {
        int referenceEpicId = subTask.getEpicId();
        if (epicRegister.containsKey(referenceEpicId)) {
            int subTaskId = generateId++;
            subTask.setID(subTaskId);
            subTaskRegister.put(subTaskId, subTask);
            epicRegister.get(referenceEpicId).getIdsSubTask().add(subTaskId);
            return true;
        }
        return false;
    }

    /**
     * Удаление задачи по идентификатору
     * @param id идентификатор
     */
    public void deleteTaskByUIN(int id) {
        if (taskRegister.containsKey(id))
            taskRegister.remove(id);
    }

    /**
     * <p>Удаление главной задачи по УИН</p>
     * <b>ЕСЛИ:</b>
     * <ul>
     *     <li>Главная задача существует в Карте</li>
     *     <li>Список связанных с ней подзадач пуст</li>
     * </ul> то удаление главной задачи из Карты (вернуть ИСТИНА),
     * иначе вернуть ЛОЖЬ.
     * @param id идентификатор
     */
    public boolean deleteEpicByUIN(int id) {
        if (epicRegister.containsKey(id)) {
            if (epicRegister.get(id).getIdsSubTask().size() != 0) {
                epicRegister.remove(id);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаление подзадачи по УИН
     * <p>Если подзадача с таким УИН существует, то обращаемся к её главной задаче для удаления взаимосвязи
     * и последующим удалением подзадачи из Карты, вернув ИСТИНУ.</p>
     * Иначе ЛОЖЬ
     * @param id идентификатор
     * @return "Как всё прошло?"
     */
    public boolean deleteSubTaskByUIN(int id) {
        if (subTaskRegister.containsKey(id)
                && epicRegister.get(subTaskRegister.get(id).getEpicId()).getIdsSubTask().contains(id)) {
            epicRegister.get(subTaskRegister.get(id).getEpicId()).getIdsSubTask().remove(id);
            subTaskRegister.remove(id);
            return true;
        }
        return false;
    }

    /**
     * <p>Получение всех задач определённой главной задачи</p>
     * @param epicID идентификатор главной задачи
     * @return Список связанных с главной задачей мелких подзадач.
     */
    public List<SubTask> getSubTasksByEpic(int epicID) {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        if (epicRegister.containsKey(epicID)) {
            for (Integer subTaskId : getEpicById(epicID).getIdsSubTask()) {
                subTasksByEpic.add(getSubTaskById(subTaskId));
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
    public void setEpicsStatus() {
        for (Epic epic : epicRegister.values()) {
            List<Integer> epicIdsSubTask = epic.getIdsSubTask();
            if (epicIdsSubTask.isEmpty())
                epic.setStatus(Status.NEW.getStatus());
            else {
                int[] status = new int[]{0, 0, 0};
                for (int subTaskId : epicIdsSubTask) {
                    for (Status s : Status.values()) {
                        if (getSubTaskById(subTaskId).getStatus() == s.getStatus())
                            status[s.getStatus()]++;
                    }
                }
                if (epicIdsSubTask.size() == status[Status.NEW.getStatus()])
                    epic.setStatus(Status.NEW.getStatus());
                else if (epicIdsSubTask.size() == status[Status.DONE.getStatus()])
                    epic.setStatus(Status.DONE.getStatus());
                else epic.setStatus(Status.IN_PROGRESS.getStatus());
            }
        }
    }
}
