package management;

import tasks.Status;
import tasks.Subtask;
import tasks.Epic;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new Managers().getDefault();
        taskManager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW);
//        Пробуем просмотреть задание
        System.out.println(taskManager.getTask(1));
//        Проверяем, что история просмотра зафиксировалась
        System.out.println(taskManager.getHistory());
//        Пробуем ещё раз просмотреть задание
        System.out.println(taskManager.getTask(1));
//        Проверяем, что второй просмотр тоже отражён
        System.out.println(taskManager.getHistory());


        taskManager.addNewTask("Second Task", "Task No.2 Lorem ipsum", Status.IN_PROGRESS);
        System.out.println(taskManager.getTask(2));
//        Проверяем, что третий просмотр тоже отражён
        System.out.println(taskManager.getHistory());
//        Проверяем, что история хранит не более 10 записей;
        for (int i = 0; i < 20; i++){
            System.out.println(taskManager.getTask(2));
        }
        System.out.println(taskManager.getHistory());

//        Проверки с третьего спринта, не знаю, нужно ли их удалять...
        Epic epic1 = taskManager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        taskManager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                Status.NEW);
        taskManager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                Status.DONE);
        Epic epic2 = taskManager.addNewEpic("Second Epic", "Epic No.2 Lorem ipsum");
        taskManager.addNewSubtask(epic2, "2nd Epic's First Subtask",
                "2nd Epic's 1st Subtask Lorem ipsum",
                Status.IN_PROGRESS);

        taskManager.changeTask(new Task("First Task v.2", "Task No.1 lorem ipsum",1,
                Status.IN_PROGRESS));
        taskManager.changeTask(new Task("Second Task v.2", "Task No.2 Lorem ipsum",2,
                Status.DONE));
        taskManager.changeSubtask(new Subtask(epic1, "1st Epic's First Subtask v.2",
                "1st Epic's 1st Subtask Lorem ipsum", 4, Status.DONE));

    }
}