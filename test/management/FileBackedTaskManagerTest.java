package management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;

import static management.FileBackedTaskManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void startTheProcess() {
        manager = new FileBackedTaskManager(new File("file.txt"));
    }

    @Test
    void mustSaveAndLoadFileIfTasksListIsEmpty(){
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File("file.txt"));
        Assertions.assertEquals(manager.idCounter, loadedManager.idCounter, "При сохранении и записи пустого " +
                "manager idCounter разный!");
        Assertions.assertEquals(manager.epicsContainer, loadedManager.epicsContainer, "При сохранении и записи " +
                "пустого manager epicsContainer разный!");
        Assertions.assertEquals(manager.subtasksContainer, loadedManager.subtasksContainer, "При сохранении и" +
                "записи пустого manager subtasksContainer разный!");
        Assertions.assertEquals(manager.tasksContainer, loadedManager.tasksContainer, "При сохранении и" +
                "записи пустого manager tasksContainer разный!");
        Assertions.assertEquals(manager.getHistory(), loadedManager.getHistory(), "При сохранении и" +
                "записи пустого manager getHistory() разный!");
    }
    @Test
    void mustSaveAndLoadFileIfTasksListIsNotEmptyButHasEpicWithoutSubtasks(){
        manager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW,
                Duration.ofHours(1), LocalTime.of(1, 48, 0));
        manager.addNewTask("Second Task", "Task No.2 lorem ipsum", Status.IN_PROGRESS,
                Duration.ofHours(2), LocalTime.of(2, 48, 0));
        manager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        manager.getTask(1);
        manager.getTask(3);
        manager.getTask(2);
        manager.getTask(1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File("file.txt"));
        Assertions.assertEquals(manager.idCounter, loadedManager.idCounter, "При сохранении и записи пустого " +
                "epic idCounter разный!");
        Assertions.assertEquals(manager.epicsContainer, loadedManager.epicsContainer, "При сохранении и записи " +
                "пустого epic epicsContainer разный!");
        Assertions.assertEquals(manager.subtasksContainer, loadedManager.subtasksContainer, "При сохранении и" +
                "записи пустого epic subtasksContainer разный!");
        Assertions.assertEquals(manager.tasksContainer, loadedManager.tasksContainer, "При сохранении и" +
                "записи пустого epic tasksContainer разный!");
        Assertions.assertEquals(manager.getHistory(), loadedManager.getHistory(), "При сохранении и" +
                "записи пустого epic getHistory() разный!");
    }
    @Test
    void mustSaveAndLoadFileIfTasksListIsNotEmptyButHistoryIsEmpty(){
        manager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW,
                Duration.ofHours(1), LocalTime.of(1, 48, 0));
//        manager.addNewTask("Second Task", "Task No.2 lorem ipsum", Status.IN_PROGRESS);
        Epic epic1 = manager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        manager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                Status.NEW, Duration.ofHours(1), LocalTime.of(1, 48, 0));
        manager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                Status.IN_PROGRESS, Duration.ofHours(2), LocalTime.of(2, 48, 0));
        manager.addNewSubtask(epic1, "1st Epic's Third Subtask",
                "1st Epic's 3rd Subtask Lorem ipsum",
                Status.DONE, Duration.ofHours(3), LocalTime.of(3, 48, 0));
        manager.addNewEpic("Second Epic", "Epic No.2 Lorem ipsum");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File("file.txt"));
        Assertions.assertEquals(manager.idCounter, loadedManager.idCounter, "При сохранении и записи manager " +
                "с пустой history idCounter разный!");
        Assertions.assertEquals(manager.epicsContainer, loadedManager.epicsContainer, "При сохранении и записи " +
                "manager с пустой history epicsContainer разный!");
        Assertions.assertEquals(manager.subtasksContainer, loadedManager.subtasksContainer, "При сохранении и" +
                "записи manager с пустой history subtasksContainer разный!");
        Assertions.assertEquals(manager.tasksContainer, loadedManager.tasksContainer, "При сохранении и" +
                "записи manager с пустой history tasksContainer разный!");
        Assertions.assertEquals(manager.getHistory(), loadedManager.getHistory(), "При сохранении и" +
                "записи manager с пустой history getHistory() разный!");

    }
}
