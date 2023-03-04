package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
  List<Object> getHistory(); // Вывести историю просмотра задач
  List<Task> getAllTasks(); // Получение списка всех задач
  List<Epic> getAllEpics(); // Получение списка всех главных задач
  List<SubTask> getAllSubTasks(); // Получение списка всех подзадач
  void deleteAllTasks(); // Удаление всех задач
  void deleteAllEpics(); // Удаление всех главных задач
  void deleteAllSubTasks(); // Удаление всех подзадач
  Task getTaskById(int id); //  Получение задачи по идентификатору;
  Epic getEpicById(int id); //  Получение главной задачи по идентификатору;
  SubTask getSubTaskById(int id); //  Получение подзадачи по идентификатору;
  int addTask(Task task); // Создание задачи
  int addEpic(Epic epic); // Создание главной задачи
  int addSubTask(SubTask subTask); // Создание подзадачи
  void updateTask(Task task); // Обновление задачи
  void updateEpic(Epic epic); // Обновление главной задачи
  void updateSubTask(SubTask subTask); // Обновление подзадачи
  void deleteTaskByID(int id); // Удаление задачи по идентификатору
  void deleteEpicByID(int id); // Удаление главной задачи по идентификатору
  void deleteSubTaskByID(int id); //  Удаление подзадачи по идентификатору
  List<SubTask> getSubTasksByEpic(int id); // Получение списка всех подзадач определённого эпика.
  void setEpicsStatus(); // Проверить статус главной задачи
}
