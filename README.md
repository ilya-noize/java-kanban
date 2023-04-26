# Введение
Как человек обычно делает покупки? Если ему нужен не один продукт,
а несколько, то очень вероятно, что сначала он составит список,
чтобы ничего не забыть.

Сделать это можно где угодно: на листе бумаги, в приложении для заметок или, например, в сообщении самому себе в мессенджере.

А теперь представьте, что это список не продуктов, а полноценных дел. И не каких-нибудь простых вроде «помыть посуду» или «позвонить бабушке», а сложных — например, «организовать большой семейный праздник» или «купить квартиру».

Каждая из таких задач может разбиваться на несколько этапов со своими нюансами и сроками. А если над их выполнением будет работать не один человек, а целая команда, то организация процесса станет ещё сложнее.
# Трекер задач [ _спринт 3_ ]
Как системы контроля версий помогают команде работать с общим кодом, так и трекеры задач позволяют эффективно организовать совместную работу над задачами. Вам предстоит написать бэкенд для такого трекера.

В итоге должна получиться программа, отвечающая за формирование модели данных для этой страницы:

<img src = 'https://pictures.s3.yandex.net/resources/Untitled_25_1639469823.png'/>

<blockquote> Пользователь не будет видеть консоль вашего приложения. Поэтому нужно сделать так, чтобы методы не просто печатали что-то в консоль, но и возвращали объекты нужных типов.
Вы можете добавить консольный вывод для самопроверки в класcе Main, но на работу методов он влиять не должен.</blockquote>

---
## Типы задач
Простейшим кирпичиком такой системы является задача.

У задачи есть следующие свойства:
1. **Название**, кратко описывающее суть задачи (например, «Переезд»).
2. **Описание**, в котором раскрываются детали.
3. **Уникальный идентификационный номер задачи**, по которому её можно будет найти.
4. **Статус**, отображающий её прогресс. Мы будем выделять следующие этапы жизни задачи:

    4.1.`NEW` — задача только создана, но к её выполнению ещё не приступили.
    4.2.`IN_PROGRESS` — над задачей ведётся работа.
    4.3.`DONE` — задача выполнена.

Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask).
Большую задачу, которая делится на подзадачи, мы будем называть эпиком (англ. epic).

Таким образом, в нашей системе задачи могут быть трёх типов: обычные задачи, эпики и подзадачи. Для них должны выполняться следующие условия:

* Для каждой подзадачи известно, в рамках какого эпика она выполняется.
* Каждый эпик знает, какие подзадачи в него входят.
* Завершение всех подзадач эпика считается завершением эпика.
<blockquote>
Подсказка: как организовать классы для хранения задач

У одной и той же проблемы в программировании может быть несколько решений.

К примеру, вам нужно представить в программе три вида связанных сущностей: задачи, подзадачи и эпики.

Вы можете завести один абстрактный класс и связать три других с ним.

Или создать один не абстрактный класс и двух его наследников.

Или сделать три отдельных класса.

Задача программиста — не только сделать выбор, но и обосновать его.

Вне зависимости от того, по какому пути вы решите пойти, каждое из этих решений будет лучше в одних ситуациях и хуже в других.

На наш взгляд, самым безопасным способом решения этой задачи будет создание публичного не абстрактного класса Task.

Он представляет отдельно стоящую задачу.

Далее от него создать два подкласса: Subtask и Process.

Такая структура с одной стороны позволит менять свойства сразу всех видов задач, а с другой — оставит пространство для манёвров, если потребуется изменить только одну из них.
</blockquote>

---

## Идентификатор задачи
У каждого типа задач есть идентификатор. Это целое число, уникальное для всех типов задач. По нему мы находим, обновляем, удаляем задачи. При создании задачи менеджер присваивает ей новый идентификатор.

<blockquote>
Для генерации идентификаторов можно использовать числовое поле класса менеджер, увеличивая его на 1, когда нужно получить новое значение.</blockquote>

---

## Менеджер
Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера. Он будет запускаться на старте программы и управлять всеми задачами. В нём должны быть реализованы следующие функции:

1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):

    2.1. Получение списка всех задач.

    2.2. Удаление всех задач.

    2.3. Получение по идентификатору.

    2.4. Создание. Сам объект должен передаваться в качестве параметра.

    2.5. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    2.6. Удаление по идентификатору.

3. Дополнительные методы:

   3.1. Получение списка всех подзадач определённого эпика.
4. Управление статусами осуществляется по следующему правилу:

   4.1. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.

   4.2. Для эпиков:
   если у эпика нет подзадач или все они имеют статус `NEW`, то статус должен быть NEW.
   если все подзадачи имеют статус `DONE`, то и эпик считается завершённым — со статусом DONE.
   во всех остальных случаях статус должен быть `IN_PROGRESS`.

<blockquote>

---

Хранение задач

Итак, вам нужно:
1. Получать задачи по идентификатору.
2. Выводить списки задач разных типов.
   Один из способов организовать такое хранение — это присвоить соответствие между идентификатором и задачей при помощи HashMap. Поскольку идентификатор не может повторяться (иначе он не был бы идентификатором), такой подход позволит быстро получать задачу.
   Чтобы получать разные типы задач, вы можете создать три HashMap по одной на каждый из видов задач.

---

---
Обновление данных

При обновлении можете считать, что на вход подаётся новый объект, который должен полностью заменить старый. К примеру, метод для обновления эпика может принимать эпик в качестве входных данных public void updateTask(Task task). Если вы храните эпики в HashMap, где ключами являются идентификаторы, то обновление — это запись нового эпика `tasks.put(task.getId(), task))`.

---

---
Обновление статуса задачи

Фраза «информация приходит вместе с информацией по задаче» означает, что не существует отдельного метода, который занимался бы только обновлением статуса задачи. Вместо этого статус задачи обновляется вместе с полным обновлением задачи.

---

---
Обновление эпиков

Из описания задачи видно, что эпик не управляет своим статусом самостоятельно. Это значит:
1. Пользователь не должен иметь возможности поменять статус эпика самостоятельно.
2. Когда меняется статус любой подзадачи в эпике, вам необходимо проверить, что статус эпика изменится соответствующим образом. При этом изменение статуса эпика может и не произойти, если в нём, к примеру, всё ещё есть незакрытые задачи.

---
</blockquote>

### Дальнейшая разработка алгоритма с CustomLinkedList и HashMap
<p>Программа должна запоминать порядок вызовов метода add,
ведь именно в этом порядке просмотры будут выстраиваться в истории.
Для хранения порядка вызовов удобно использовать список.</p>
<p>Если какая-либо задача просматривалась несколько раз, в истории должен отобразиться только последний просмотр.
Предыдущий просмотр должен быть удалён сразу же после появления нового — за O(1)</p>

## Тестирование JUnit
