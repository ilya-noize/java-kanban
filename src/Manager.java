import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Хранение задач всех типов.</p>
 * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
 * <ul>
 *     <li>Получение списка всех задач;</li>
 *     <li>Удаление всех задач;</li>
 *     <li>Получение по идентификатору;</li>
 *     <li>Создание;</li>
 *     <li>Обновление;</li>
 *     <li>Удаление по идентификатору;</li>
 * </ul>
 * Дополнительные методы:
 * Получение списка всех подзадач определённого эпика.
 * Управление статусами осуществляется по следующему правилу:
 * <ul>
 *     <li>
 *         <b>Менеджер сам не выбирает статус для задачи.</b>
 *         <p>Информация о нём приходит менеджеру вместе с информацией о самой задаче.
 *            По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.</p>
 *      </li>
 *      <li>
 *          <b>Для эпиков:</b>
 *          <p>if(epic.sequenceTask.isEmpty() || epic.sequenceTask.isAllNew()) return NEW;</p>
 *          <p>if(epic.sequenceTask.isAllDone) return DONE;</p>
 *          return IN_PROGRESS;
 *      </li>
 * </ul>
 *
 * @see #showAllTasks() Получение списка всех задач
 * @see #showAllEpics() Получение списка всех главных задач
 * @see #showAllSubTasks() Получение списка всех подзадач
 * @see #removeAllTasks() Удаление всех задач
 * @see #removeAllEpics() Удаление всех главных задач
 * @see #removeAllSubTasks() Удаление всех подзадач
 * @see #showTaskByUIN(int)  Получение задачи по идентификатору;
 * @see #showEpicByUIN(int)  Получение главной задачи по идентификатору;
 * @see #showSubTaskByUIN(int)  Получение подзадачи по идентификатору;
 * @see #createTask(Task) Создание задачи
 * @see #createEpic(Epic) Создание главной задачи
 * @see #createSubTask(SubTask) Создание подзадачи
 * @see #editTask(Task) Обновление задачи
 * @see #editEpic(Epic) Обновление главной задачи
 * @see #editSubTask(SubTask) Обновление подзадачи
 * @see #removeTaskByUIN(int) Удаление задачи по идентификатору
 * @see #removeEpicByUIN(int)  Удаление главной задачи по идентификатору
 * @see #removeSubTaskByUIN(int)  Удаление подзадачи по идентификатору
 * @see #showAllSubTaskByEpic(int)  Получение списка всех подзадач определённого эпика.
 */
public class Manager {

    private Map<Integer, Task> taskRegister = new HashMap<>();
    private Map<Integer, SubTask> subTaskRegister = new HashMap<>();
    private Map<Integer, Epic> epicRegister = new HashMap<>();

    private int generateUIN = 1;


    /**
     * SHOW_ALL METHODS
     */
    public List<Task> showAllTasks() {
        List<Task> tasks = new ArrayList<>();
        if (taskRegister.isEmpty()) return new ArrayList<>();
        for (Task task: taskRegister.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public List<Epic> showAllEpics() {
        List<Epic> epics = new ArrayList<>();
        if (taskRegister.isEmpty()) return new ArrayList<>();
        for (Epic epic: epicRegister.values()){
            epics.add(epic);
        }
        return epics;
    }

    public List<SubTask> showAllSubTasks() {
        List<SubTask> subTasks= new ArrayList<>();
        if (taskRegister.isEmpty()) return new ArrayList<>();
        for (SubTask subtask: subTaskRegister.values()){
            subTasks.add(subtask);
        }
        return subTasks;
    }

    /**
     * REMOVE METHODS
     */
    public void removeAllTasks() {
        taskRegister.clear();
    }

    public void removeAllEpics() {
        for (Epic epic: epicRegister.values()){
            removeEpicByUIN(epic.getUIN());
        }
    }

    public void removeAllSubTasks() {
        for (SubTask subTask: subTaskRegister.values()) {
            removeSubTaskByUIN(subTask.getUIN());
        }
    }

    /**
     * EDIT METHODS
     */
    public boolean editTask(Task task) {
        if (taskRegister.containsKey(task.getUIN())){
            taskRegister.replace(task.getUIN(), task);
            return true;
        }
        return false;
    }

    public boolean editEpic(Epic epic) {
        if (epicRegister.containsKey(epic.getUIN())){
            epicRegister.replace(epic.getUIN(), epic);
            return true;
        }
        return false;
    }

    public boolean editSubTask(SubTask subTask) {
        if (subTaskRegister.containsKey(subTask.getUIN())){
            subTaskRegister.replace(subTask.getUIN(), subTask);
            return true;
        }
        return false;
    }

    public Task showTaskByUIN(int uIN) {
        if (taskRegister.containsKey(uIN))
            return taskRegister.get(uIN);
        return new Task();
    }

    public Epic showEpicByUIN(int uIN) {
        if (epicRegister.containsKey(uIN))
            return epicRegister.get(uIN);
        return new Epic();
    }

    public SubTask showSubTaskByUIN(int uIN) {
        if (subTaskRegister.containsKey(uIN))
            return subTaskRegister.get(uIN);
        return new SubTask();
    }
    /**
     * <p>Запись новой задачи</p>
     * @param task Новая задача
     */
    public void createTask(Task task) {
        task.setUIN(generateUIN++);
        taskRegister.put(task.getUIN(), task);
    }

    /**
     * <p>Запись новой главной задачи</p>
     * @param epic Новая Главная задача
     * @return Уникальный номер в Карте
     */
    public int createEpic(Epic epic) {
        epic.setUIN(generateUIN++);
        epicRegister.put(epic.getUIN(), epic);
        return epic.getUIN();
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
     *
     * @param subTask Подзадача для главной задачи
     */
    public boolean createSubTask(SubTask subTask) {
        int referenceEpicUIN = subTask.getReferenceToEpic();
        if (epicRegister.containsKey(referenceEpicUIN)) {
            int subTaskUIN = generateUIN++;
            subTask.setUIN(subTaskUIN);
            subTaskRegister.put(subTaskUIN, subTask);
            epicRegister.get(referenceEpicUIN).getSequenceTask().add(subTaskUIN);
            return true;
        }
        return false;
    }

    public boolean removeTaskByUIN(int uIN) {
        if (taskRegister.containsKey(uIN)) {
            taskRegister.remove(uIN);
            return true;
        }
        return false;
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
     * @param uIN unique identification number
     */
    public boolean removeEpicByUIN(int uIN) {
        if (epicRegister.containsKey(uIN)) {
            if (epicRegister.get(uIN).getSequenceTask().size() != 0) {
                epicRegister.remove(uIN);
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Удаление подзадачи по УИН</p>
     * <p>Если подзадача с таким УИН существует, то обращаемся к её главной задаче для удаления взаимосвязи
     * и последующим удалением подзадачи из Карты, вернув ИСТИНУ.</p>
     * Иначе ЛОЖЬ
     *
     * @param uIN unique identification number
     */
    public boolean removeSubTaskByUIN(int uIN) {
        if (subTaskRegister.containsKey(uIN)
                && epicRegister.get(subTaskRegister.get(uIN).getReferenceToEpic()).getSequenceTask().contains(uIN)) {
            epicRegister.get(subTaskRegister.get(uIN).getReferenceToEpic()).getSequenceTask().remove(uIN);
            subTaskRegister.remove(uIN);
            return true;
        }
        return false;
    }

    /**
     * <p>Получение всех задач определённой главной задачи</p>
     * @param epicUIN Уникальный номер главной задачи
     * @return Список связанных с главной задачей мелких подзадач.
     */
    public List<SubTask> showAllSubTaskByEpic(int epicUIN) {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        if (epicRegister.containsKey(epicUIN)) {
            for (Integer subTaskUIN : showEpicByUIN(epicUIN).getSequenceTask()) {
                subTasksByEpic.add(showSubTaskByUIN(subTaskUIN));
            }
        }
        return subTasksByEpic;
    }

    /**
     * <p>Метод просчёта статуса главной задачи</p>
     *
     * <ul>
     *     <li>Получаем доступ к объекту главная задача из Карты</li>
     *     <li>Получаем список УИН связанных подзадач</li>
     *     <li>Объявляем счётчик состояний</li>
     *     <li>Ищем все связанные задачи для получения доступа к статусу задачи</li>
     *     <li>Подсчёт всех состояний</li>
     *     <li>NEW: если все подзадачи NEW</li>
     *     <li>DONE: если все подзадачи DONE</li>
     *     <li>все остальные случаи - IN_PROGRESS</li>
     * </ul>
     */
    public void makeEpicStatus() {
        for (Epic epic: epicRegister.values()) {
            List<Integer> sequenceTask = epic.getSequenceTask();
            if(sequenceTask.isEmpty())
                epic.setStatus(Status.NEW.getStatus());
            else{
                int[] status = new int[] {0, 0, 0};
                for (int subTaskUIN: sequenceTask) {
                    for (Status s: Status.values()) {
                        if(showSubTaskByUIN(subTaskUIN).getStatus() == s.getStatus())
                            status[s.getStatus()]++;
                    }
                }
                if (sequenceTask.size() == status[Status.NEW.getStatus()])
                    epic.setStatus(Status.NEW.getStatus());
                else if (sequenceTask.size() == status[Status.DONE.getStatus()])
                    epic.setStatus(Status.DONE.getStatus());
                else epic.setStatus(Status.IN_PROGRESS.getStatus());
            }
        }
    }
}
