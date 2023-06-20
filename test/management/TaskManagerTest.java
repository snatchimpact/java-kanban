package management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


abstract class TaskManagerTest <T extends TaskManager>{
    T manager;


    @Test
    void mustAddTaskToTheContainer(){
        Task task1 = manager.addNewTask("TestTask1","TestTaskDescription1", Status.NEW);
        Task savedTask = manager.getTask(task1.getId());
        Assertions.assertNotNull(savedTask, "Задача не найдена!");
        Assertions.assertEquals(task1,savedTask,"Задачи не совпадают!");
    }

    @Test
    void emptyEpicMustHaveNoSubtasks(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        Assertions.assertTrue(epic1.getSubtasksIDsList().isEmpty(), "У нового пустого эпика не должно быть подзадач!");
    }

    @Test
    void emptyEpicMustHaveStatusNEW(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        Assertions.assertEquals(epic1.getStatus(), Status.NEW, "У нового пустого эпика статус не NEW!");
    }

    @Test
    void epicWithAllSubtasksInStatusNEWMustHaveStatusNEW(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.NEW);
        Assertions.assertEquals(epic1.getStatus(), Status.NEW, "Эпик со всеми сабтасками NEW имеет " +
                "статус не NEW!");
    }
    @Test
    void epicWithAllSubtasksInStatusDONEMustHaveStatusDONE(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.DONE);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.DONE);
        Assertions.assertEquals(epic1.getStatus(), Status.DONE, "Эпик со всеми задачами DONE имеет " +
                "статус не DONE!");
    }
    @Test
    void epicWithSubtasksInStatusDONEAndWithSubtasksInStatusNEWMustHaveStatusINPROGRESS(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.DONE);
        Assertions.assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Эпик с " +
                "задачами NEW, DONE, DONE имеет статус не IN_PROGRESS!");
    }

    @Test
    void epicWithSomeOrAllSubtasksInStatusIN_PROGRESMustHaveStatusIN_PROGRESS(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.IN_PROGRESS);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.IN_PROGRESS);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.IN_PROGRESS);
        Assertions.assertEquals(epic1.getStatus(), Status.IN_PROGRESS,"Эпик с " +
                "задачами IN_PROGRESS, IN_PROGRESS, IN_PROGRESS имеет статус не IN_PROGRESS!");
    }

    @Test
    void savedSubtaskMustHaveEpic(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        Subtask subtask1 = manager.addNewSubtask(epic1,"TestSubtask1",
                "TestSubtaskDescription1", Status.IN_PROGRESS);
        Assertions.assertEquals(epic1, manager.getTask(subtask1.getEpicID()),"По ID эпика, записанного в" +
                " сабтаске, возвращается другой эпик!");
    }

    @Test
    void getNextIDMustStartFromZeroAndIncreaseNumberingByOne() {
        int i = manager.getNextID();
        int plus1 = manager.getNextID();
        int plus2 = manager.getNextID();
        Assertions.assertEquals(i, 1,"Счетчик ID-шников задач начинает не с нуля!");
        Assertions.assertEquals(plus1,2, "Счетчик ID-шников задач, вызываясь второй раз, дает номер " +
                "не 2!");
        Assertions.assertEquals(plus2,3,"Счетчик ID-шников задач, вызываясь третий раз, дает номер " +
                "не 3!");

    }

    @Test
    void listOfAllTasksMustReturnAllTasks(){
        List<Integer> listOfTestTasks = new ArrayList<>();
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        listOfTestTasks.add(epic1.getId());
        Subtask subtask1 = manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        listOfTestTasks.add(subtask1.getId());
        Subtask subtask2 = manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        listOfTestTasks.add(subtask2.getId());
        Subtask subtask3 = manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.DONE);
        listOfTestTasks.add(subtask3.getId());
        Epic epic2 = manager.addNewEpic("TestEpic2", "TestEpicDescription2");
        listOfTestTasks.add(epic2.getId());
        Task task1 = manager.addNewTask("TestTask1","TestTaskDescription1",Status.NEW);
        listOfTestTasks.add(task1.getId());
        Task task2 = manager.addNewTask("TestTask2","TestTaskDescription2",Status.IN_PROGRESS);
        listOfTestTasks.add(task2.getId());

        Set<Integer> setOfTestTasks = new HashSet<>(listOfTestTasks);
        List<Integer> listOfSavedTestTasks = new ArrayList<>();
        listOfSavedTestTasks.addAll(manager.getListOfAllTasks());
        listOfSavedTestTasks.addAll(manager.getListOfAllEpics());
        listOfSavedTestTasks.addAll(manager.getListOfAllSubtasks());
        Set<Integer> setOfSavedTestTasks = new HashSet<>(listOfSavedTestTasks);
        Assertions.assertEquals(setOfTestTasks, setOfSavedTestTasks);
    }

    @Test
    void listOfAllEpicsMustReturnAllEpics() {
        List<Integer> listOfTestEpics = new ArrayList<>();
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        listOfTestEpics.add(epic1.getId());
        manager.addNewSubtask(epic1, "TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1, "TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        manager.addNewSubtask(epic1, "TestSubtask3", "TestSubtaskDescription3", Status.DONE);
        Epic epic2 = manager.addNewEpic("TestEpic2", "TestEpicDescription2");
        listOfTestEpics.add(epic2.getId());


        Set<Integer> setOfTestEpics = new HashSet<>(listOfTestEpics);
        List<Integer> listOfSavedTestEpics = new ArrayList<>(manager.getListOfAllEpics());
        Set<Integer> setOfSavedTestEpics = new HashSet<>(listOfSavedTestEpics);
        Assertions.assertEquals(setOfTestEpics, setOfSavedTestEpics);
    }

    @Test
    void listOfAllSubtasksMustReturnAllSubtasks() {
        List<Integer> listOfTestSubtasks = new ArrayList<>();
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        Subtask subtask1 = manager.addNewSubtask(epic1, "TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        listOfTestSubtasks.add(subtask1.getId());
        Subtask subtask2 = manager.addNewSubtask(epic1, "TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        listOfTestSubtasks.add(subtask2.getId());
        Subtask subtask3 = manager.addNewSubtask(epic1, "TestSubtask3", "TestSubtaskDescription3", Status.DONE);
        listOfTestSubtasks.add(subtask3.getId());
        Epic epic2 = manager.addNewEpic("TestEpic2", "TestEpicDescription2");
        Subtask subtask4 = manager.addNewSubtask(epic2, "TestSubtask4", "TestSubtaskDescription4", Status.IN_PROGRESS);
        listOfTestSubtasks.add(subtask4.getId());

        Set<Integer> setOfTestSubtasks = new HashSet<>(listOfTestSubtasks);
        List<Integer> listOfSavedTestSubtasks = new ArrayList<>(manager.getListOfAllSubtasks());
        Set<Integer> setOfSavedTestSubtasks = new HashSet<>(listOfSavedTestSubtasks);
        Assertions.assertEquals(setOfTestSubtasks, setOfSavedTestSubtasks);
    }

@Test
    void mustReturnCorrectHistory(){
    Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
    manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
    manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
    manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.IN_PROGRESS);
    manager.addNewEpic("TestEpic2", "TestEpicDescription2");
    manager.addNewTask("TestTask1","TestTaskDescription1",Status.NEW);
    manager.addNewTask("TestTask2","TestTaskDescription2",Status.IN_PROGRESS);
    List<Task> listOfViewedTasks = new ArrayList<>();

    manager.getTask(1);
    Task testTask2 = manager.getTask(2);
    listOfViewedTasks.add(testTask2);
    Task testTask3 = manager.getTask(3);
    listOfViewedTasks.add(testTask3);
    Task testTask41 = manager.getTask(1);
    listOfViewedTasks.add(testTask41);
    Assertions.assertEquals(listOfViewedTasks, manager.getHistory());
}

    @Test
    void mustReturnEmptyHistory(){
        Assertions.assertNull(manager.getHistory());

    }

    @Test
    void mustAddTask(){
        Task task1 = manager.addNewTask("TestTask1","TestTaskDescription1",Status.NEW);
        Assertions.assertEquals(task1, manager.getTask(1));
        Assertions.assertNotEquals(task1, manager.getTask(2));
    }

    @Test
    void mustAddEpic(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        Assertions.assertEquals(epic1, manager.getTask(1));
        Assertions.assertNotEquals(epic1, manager.getTask(2));
    }
    @Test
    void mustAddSubtask(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        Subtask subtask3 = manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.IN_PROGRESS);
        Assertions.assertEquals(subtask3, manager.getTask(4));
        Assertions.assertNotEquals(subtask3, manager.getTask(3));
        Assertions.assertNotEquals(subtask3, manager.getTask(2));
        Assertions.assertNotEquals(subtask3, manager.getTask(1));
    }

    @Test
    void mustChangeTask(){
        Task task1 = manager.addNewTask("TestTask1","TestTaskDescription1",Status.NEW);
        Task task1Changed = new Task("TestTask1Changed", "TestTaskDescription1Changed", 1,
                Status.IN_PROGRESS);
        manager.changeTask(task1Changed);
        Assertions.assertEquals(task1Changed,manager.getTask(1));
        Assertions.assertNotEquals(task1, manager.getTask(1));
    }

    @Test
    void mustChangeEpic(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.IN_PROGRESS);
        Epic epic1Changed = new Epic("Epic1Changed", "TestEpicDescription1Changed", 1,
                Status.NEW);
        manager.changeEpic(epic1Changed);
        Assertions.assertEquals(epic1Changed,manager.getTask(1));
        Assertions.assertNotEquals(epic1, manager.getTask(1));
    }

    @Test
    void mustChangeSubtask(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        Subtask subtask1 = manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.IN_PROGRESS);
        Subtask subtask1Changed = new Subtask(epic1,"Subtask1Changed", "TestSubtaskDescription1Changed", 2,
                Status.DONE);
        manager.changeSubtask(subtask1Changed);
        Assertions.assertEquals(subtask1Changed,manager.getTask(2));
        Assertions.assertNotEquals(subtask1, manager.getTask(2));
    }

    @Test
    void mustDeleteTask(){
        Epic epic1 = manager.addNewEpic("TestEpic1", "TestEpicDescription1");
        manager.addNewSubtask(epic1,"TestSubtask1", "TestSubtaskDescription1", Status.NEW);
        manager.addNewSubtask(epic1,"TestSubtask2", "TestSubtaskDescription2", Status.DONE);
        manager.addNewSubtask(epic1,"TestSubtask3", "TestSubtaskDescription3", Status.IN_PROGRESS);
        manager.addNewEpic("TestEpic2", "TestEpicDescription2");
        manager.addNewTask("TestTask1","TestTaskDescription1",Status.NEW);
        manager.addNewTask("TestTask2","TestTaskDescription2",Status.IN_PROGRESS);
        manager.deleteTaskByID(1);
        manager.deleteTaskByID(5);
        manager.deleteTaskByID(6);
        manager.deleteTaskByID(7);
        Assertions.assertNull(manager.getHistory());
        Assertions.assertNull(manager.getTask(2));
        Assertions.assertTrue(manager.getListOfAllTasks().isEmpty());

    }
}


