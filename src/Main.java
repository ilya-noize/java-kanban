import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final TaskManager TASK_MANAGER = Managers.getDefaultTask();

    public static void main(String[] args) {

        String[] tasks = {
                "Перевести деньги в оффшор-ы;Заводим наличными 6 лямов в РосСовБанк;Меняем банковские реквизиты;"
                        + "Перевод несколькими траншами на кипрские счета;Делаем би-валютную корзину;Меняем реквизиты;"
                        + "Делаем перевод Rajastenbank;Как физическое лицо, дифферефицируем всю сумму в трёх равных"
                        + " долях в Швейцарию;6 лямов отмыты;",
                "Заработать на долгострое;Арендуем землю в подмосковье на 2 года;Вырываем котлован;"
                        + "Объявляем цены;Добавляем по 5 лямов на квартиру;"
                        + "Объём ЖК 1600 квартир (~ 1,5 ярда - затраты на монолит и пеноблоки);Собираем взносы;"
                        + "Замораживаем стройку;Объявляем застройщика банкротом;Получаем чистыми 1 ярд 360 лямов;"
                        + "Выводим всю сумму в оффшор-ы"
        };
        System.out.println("Testing TASK_MANAGER.addTask");
        String[] taskArray = tasks[0].split(";");
        for (String s : taskArray) {
            Task task = new Task(s, "");
            TASK_MANAGER.addTask(task);
        }

        System.out.println("Testing TASK_MANAGER.getAllTasks");
        checkTasks();

        //getHistoryOfTasks();

        taskArray = tasks[1].split(";");
        Epic epic = new Epic(taskArray[0], "");
        System.out.println("Testing TASK_MANAGER.addEpic\n-+-" + epic.toString());
        int epicID = TASK_MANAGER.addEpic(epic);
        List<Integer> subTaskIDs = new ArrayList<>();
        for (int i = 1; i < taskArray.length; i++) {
            SubTask subTask = new SubTask(taskArray[i], "", epicID);
            System.out.println("Testing TASK_MANAGER.addSubTask\n---" + subTask.toString());
            subTaskIDs.add(TASK_MANAGER.addSubTask(subTask));
        }

        getHistoryOfTasks();

        System.out.println("Testing getEpic - - - - - - - - - - - - - - - - - -");
        System.out.println(TASK_MANAGER.getEpic(epicID));
        System.out.println("Subtask is DONE step-by-step - - - - - - - - - - - ");
        System.out.println("Изменить статус подзадач на DONE - - - - - - - - - ");
        System.out.println("Testing TASK_MANAGER.getSubTask - - - - - - - - - -");
        //Testing updateSubTask
        for (Integer id : subTaskIDs) {
            SubTask updateSubTask = TASK_MANAGER.getSubTask(id);
            updateSubTask.setStatus(Status.DONE);
            TASK_MANAGER.updateSubTask(updateSubTask);
            System.out.println("\t TASK_MANAGER.updateSubTask id=" + id + " is " + Status.DONE);
        }
        System.out.println("Testing TASK_MANAGER.getSubTasksByEpic - - - - - - ");
        System.out.println(TASK_MANAGER.getSubTasksByEpic(epicID));
        System.out.println("Testing TASK_MANAGER.setEpicStatus - - - - - - - - ");
        TASK_MANAGER.updateEpicStatus(epicID);
        checkAllEpics();

        getHistoryOfTasks();


        destroyData(epicID);
    }


    private static void getHistoryOfTasks() {
        System.out.println("Testing HISTORY_MANAGER.getHistory ----------------");
        for (Object task : TASK_MANAGER.getHistory()) {
            System.out.println(task.toString());
        }
        System.out.println("-------------------------HISTORY_MANAGER.getHistory");
    }

    private static void destroyData(int epicID) {
        System.out.println("Testing TASK_MANAGER.deleteAllTasks - - - - - - - -");
        TASK_MANAGER.deleteAllTasks();
        checkTasks();
        System.out.println("Testing TASK_MANAGER.deleteAllSubTasks - - - - - - ");
        TASK_MANAGER.deleteAllSubTasks();
        checkAllSubTasks();
        System.out.println("Testing TASK_MANAGER.deleteEpic id = " + epicID);
        TASK_MANAGER.deleteEpic(epicID);
        checkAllEpics();
    }

    private static void checkTasks() {
        System.out.println("Testing TASK_MANAGER.getAllTasks - - - - - - - - - ");
        System.out.println(TASK_MANAGER.getAllTasks());
    }

    private static void checkAllSubTasks() {
        System.out.println("Testing TASK_MANAGER.getAllSubTasks - - - - - - - -");
        System.out.println(TASK_MANAGER.getAllSubTasks());
    }

    private static void checkAllEpics() {
        System.out.println("Testing TASK_MANAGER.getAllEpics - - - - - - - - - ");
        System.out.println(TASK_MANAGER.getAllEpics());
    }
}
