package management;

import tasks.Subtask;
import tasks.Epic;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = manager.addNewTask("First Task", "Task 1 lorem ipsum", "NEW");
        Task task2 = manager.addNewTask("Second Task", "Task 2 Lorem ipsum", "IN_PROGRESS");
        Epic epic1 = manager.addNewEpic("First Epic", "Epic 1 Lorem ipsum");
        Subtask subtask11 = manager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                "NEW");
        Subtask subtask12 = manager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                "DONE");

//        System.out.println(manager.tasksContainer);
//        System.out.println(manager.getListOfAllTasks());
//        System.out.println(manager.epicsContainer);
//        System.out.println(manager.getListOfAllEpics());
//        System.out.println(manager.subtasksContainer);
//        System.out.println(manager.getListOfAllSubtasks());
//        System.out.println(manager);

        manager.changeTask(new Task("First Task v.2", "Task 1 lorem ipsum",1,"IN_PROGRESS"));
//        System.out.println(manager.tasksContainer);
        manager.changeSubtask(new Subtask(epic1, "1st Epic's First Subtask v.2",
                "1st Epic's 1st Subtask Lorem ipsum", 4, "DONE"));
//        System.out.println(manager.epicsContainer);
//        System.out.println(manager.getListOfAllEpics());
//        System.out.println(manager.subtasksContainer);
//        System.out.println(manager.getListOfAllSubtasks());
//        System.out.println(manager);
//
//        manager.deleteTaskByID(1);
//        manager.deleteTaskByID(3);
//        System.out.println(manager);


    }
}