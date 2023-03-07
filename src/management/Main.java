package management;

import tasks.Status;
import tasks.Epic;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new Managers().getDefault();
        //Добавляем две задачи
        taskManager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW);
        taskManager.addNewTask("Second Task", "Task No.2 lorem ipsum", Status.NEW);
        //Добавляем первый Эпик - с тремя подзадачами
        Epic epic1 = taskManager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        taskManager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                Status.NEW);
        taskManager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                Status.IN_PROGRESS);
        taskManager.addNewSubtask(epic1, "1st Epic's Third Subtask",
                "1st Epic's 3rd Subtask Lorem ipsum",
                Status.DONE);
        //Добавляем второй Эпик - без подзадач
        taskManager.addNewEpic("Second Epic", "Epic No.2 Lorem ipsum");




        //Запрашиваем созданные задачи в разном порядке
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getTask(3));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getTask(4));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getTask(4));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getTask(3));
        System.out.println(taskManager.getHistory());

        //Проверяем, что задача после удаления пропадает и из истории
        taskManager.deleteTaskByID(2);
        System.out.println(taskManager.getHistory());

        //Проверяем, что при удалении Эпика пропадают из истории и его подзадачи
        System.out.println(taskManager.getTask(5));
        System.out.println(taskManager.getTask(6));


        System.out.println("Вот история");
        System.out.println(taskManager.getHistory());
        taskManager.deleteTaskByID(3);
        System.out.println("Вот история");
        System.out.println(taskManager.getHistory());



    }
}