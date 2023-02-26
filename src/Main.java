import manager.Manager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Manager manager = new Manager();

    public static void main(String[] args) {

        String[] tasks = {
                "Перевести деньги в оффшор-ы;Заводим наличными 6 лямов в РосСовБанк;Меняем банковские реквизиты;"
                        + "Перевод несколькими траншами на кипрские счета;Делаем би-валютную корзину;Меняем реквизиты;"
                        + "Делаем перевод Rajastenbank;Как физическое лицо, дифферефицируем всю сумму в трёх равных"
                        + " долях в Швейцарию;6 лямов отмыты;",
                "Заработать на долгострое;Арендуем землю в подмосковье на 2 года;Вырываем катлован;"
                        + "Объявляем цены;Добавляем по 5 лямов на квартиру;"};/*
                        + "Объём ЖК 1600 квартир (~ 1,5 ярда - затраты на монолит и пеноблоки);Собираем взносы;"
                        + "Замораживаем стройку;Объявляем застройщика банкротом;Получаем чистыми 1 ярд 360 лямов;"
                        + "Выводим всю сумму в оффшор-ы"
        };*/
        //Тесты простых задач
        String[] taskArray = tasks[0].split(";");
        for (String s : taskArray) {
            Task task = new Task(s,"");
            manager.addTask(task);
        }
        checkTasks();

        //Тест связанных задач
        taskArray = tasks[1].split(";");
        Epic epic = new Epic(taskArray[0], "");
        int epicID = manager.addEpic(epic);
        List<Integer> subTaskIDs = new ArrayList<>();
        for (int i = 1; i < taskArray.length; i++) {
            SubTask subTask = new SubTask(taskArray[i], "", epicID);
            subTaskIDs.add(manager.addSubTask(subTask));
        }
        System.out.println(manager.getEpicById(epicID));
        System.out.println("Subtask is DONE step-by-step");
        for (Integer id : subTaskIDs) {
            SubTask updateSubTask = manager.getSubTaskById(id);
            updateSubTask.setStatus(Status.DONE);
            manager.updateSubTask(updateSubTask);
            System.out.println("\t subTask id=" + id + " is " + Status.DONE);
        }
        System.out.println("Check`t");
        System.out.println(manager.getSubTasksByEpic(epicID));
        System.out.println("Set Epics Status");
        manager.setEpicsStatus();
        checkEpics();


        System.out.println("Delete All Tasks");
        manager.deleteAllTasks();
        checkTasks();


        System.out.println("Delete All SubTasks");
        manager.deleteAllSubTasks();
        checkSubTasks();

        System.out.println("Delete Epic By ID = " + epicID);
        manager.deleteEpicByID(epicID);
        checkEpics();
    }

    private static void checkTasks(){
        System.out.println("Checking Tasks");
        System.out.println(manager.getAllTasks());

    }
    private static void checkSubTasks(){
        System.out.println("Checking SubTasks");
        System.out.println(manager.getAllSubTasks());
    }
    private static void checkEpics(){
        System.out.println("Checking Epics");
        System.out.println(manager.getAllEpics());
    }
}
