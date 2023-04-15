import management.FileBackedTasksManager;
import tasks.Status;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static management.FileBackedTasksManager.loadFromFile;

public class Main {
    public static void main(String[] args){

        //Эта часть - чтоб из файла создавать менеджер
        File ourFile = new File("file.txt");
        FileBackedTasksManager fileBackedTasksManager = loadFromFile(ourFile);
        System.out.println(fileBackedTasksManager);
        System.out.println(fileBackedTasksManager.inMemoryHistoryManager);

        //Эта часть - чтоб перезаписывать файл, создавая операции

        /*FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(ourFile);
        fileBackedTasksManager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW);
        fileBackedTasksManager.addNewTask("Second Task", "Task No.2 lorem ipsum", Status.IN_PROGRESS);
        Epic epic1 = fileBackedTasksManager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        fileBackedTasksManager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                Status.NEW);
        fileBackedTasksManager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                Status.IN_PROGRESS);
        fileBackedTasksManager.addNewSubtask(epic1, "1st Epic's Third Subtask",
                "1st Epic's 3rd Subtask Lorem ipsum",
                Status.DONE);
        //Добавляем второй Эпик - без подзадач
        fileBackedTasksManager.addNewEpic("Second Epic", "Epic No.2 Lorem ipsum");

        //Запрашиваем созданные задачи в разном порядке
        System.out.println(fileBackedTasksManager.getTask(1));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(2));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(3));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(4));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(4));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(3));
        System.out.println(fileBackedTasksManager.getHistory());

        System.out.println("beg");
        System.out.println(historyToString(fileBackedTasksManager.inMemoryHistoryManager));
        System.out.println("end");*/


    }
}