package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

  List<Task> getHistory(); // Получение списка истории просмотров задач

  List<Task> getAllTasks(); // Получение списка всех задач

  List<Epic> getAllEpics(); // Получение списка всех главных задач

  List<SubTask> getAllSubTasks(); // Получение списка всех подзадач

  void deleteAllTasks(); // Удаление всех задач

  void deleteAllEpics(); // Удаление всех главных задач

  void deleteAllSubTasks(); // Удаление всех подзадач

  Task getTask(int id); //  Получение задачи по идентификатору;

  Epic getEpic(int id); //  Получение главной задачи по идентификатору;

  SubTask getSubTask(int id); //  Получение подзадачи по идентификатору;

  int addTask(Task task); // Создание задачи

  int addEpic(Epic epic); // Создание главной задачи

  int addSubTask(SubTask subTask); // Создание подзадачи

  void updateTask(Task task); // Обновление задачи

  void updateEpic(Epic epic); // Обновление главной задачи

  void updateSubTask(SubTask subTask); // Обновление подзадачи

  void deleteTask(int id); // Удаление задачи по идентификатору

  void deleteEpic(int id); // Удаление главной задачи по идентификатору

  void deleteSubTask(int id); //  Удаление подзадачи по идентификатору

  List<SubTask> getSubTasksByEpic(int id); // Получение списка всех подзадач определённого эпика.

  void setEpicStatus(int id); // Проверить статус главной задачи
}
