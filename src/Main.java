public class Main {

    public static void main(String[] args) {

        String[] tasks = {
                "Перевести деньги в оффшор-ы;Заводим наличными 6 лямов в РосСовБанк;Меняем банковские реквизиты;"
                        + "Перевод несколькими траншами на кипрские счета;Делаем би-валютную корзину;Меняем реквизиты;"
                        + "Делаем перевод Rajastenbank;Как физическое лицо, дифферефицируем всю сумму в трёх равных"
                        + " долях в Швейцарию;6 лямов отмыты;",
                "Заработать на долгострое;Арендуем землю в подмосковье на 2 года;Вырываем катлован;"
                        + "Объявляем цены;Добавляем по 5 лямов на квартиру;"
                        + "Объём ЖК 1600 квартир (~ 1,5 ярда - затраты на монолит и пеноблоки);Собираем взносы;"
                        + "Замораживаем стройку;Объявляем застройщика банкротом;Получаем чистыми 1 ярд 360 лямов;"
                        + "Выводим всю сумму в оффшор-ы"
        };
        //Тесты простых задач
        String[] taskArray = tasks[0].split(";");
        Manager manager = new Manager();
        for (String s : taskArray) {
            Task task = new Task(s,"");
            manager.createTask(task);
        }
        System.out.println(manager.showAllTasks());

        //Тест связанных задач
        taskArray = tasks[1].split(";");
        Epic epic = new Epic(taskArray[0], "");
        int epicUIN = manager.createEpic(epic);
        for (int i = 1; i < taskArray.length; i++) {
            SubTask subTask = new SubTask(taskArray[i], "", epicUIN);
            manager.createSubTask(subTask);
        }
        System.out.println(manager.showAllSubTaskByEpic(epicUIN));
    }
}
