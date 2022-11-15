package management;

import tasks.Status;
import tasks.Subtask;
import tasks.Epic;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW);
        manager.addNewTask("Second Task", "Task No.2 Lorem ipsum", Status.IN_PROGRESS);
        Epic epic1 = manager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        manager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                Status.NEW);
        manager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                Status.DONE);
        Epic epic2 = manager.addNewEpic("Second Epic", "Epic No.2 Lorem ipsum");
        manager.addNewSubtask(epic2, "2nd Epic's First Subtask",
                "2nd Epic's 1st Subtask Lorem ipsum",
                Status.IN_PROGRESS);

        //Закончили первичное создание объектов, печатаем
        System.out.println(manager);

        //Печатаем списки эпиков, задач и подзадач
        System.out.println(manager.getListOfAllTasks());
        System.out.println(manager.getListOfAllEpics());
        System.out.println(manager.getListOfAllSubtasks());

        manager.changeTask(new Task("First Task v.2", "Task No.1 lorem ipsum",1,
                Status.IN_PROGRESS));
        manager.changeTask(new Task("Second Task v.2", "Task No.2 Lorem ipsum",2,
                Status.DONE));
        manager.changeSubtask(new Subtask(epic1, "1st Epic's First Subtask v.2",
                "1st Epic's 1st Subtask Lorem ipsum", 4, Status.DONE));

        //Закончили изменение первичных объектов, печатаем и сравниваем, что изменения отражены хорошо
        System.out.println(manager);

        manager.deleteTaskByID(1);
        manager.deleteTaskByID(6);

        //Закончили удаление первичных объектов, печатаем и сравниваем, что изменения отражены хорошо
        System.out.println(manager);
    }
}