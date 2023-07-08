package management;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;
    private Epic epic2;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void prepareHistoryManagerTest() {
        epic1 = new Epic("TestEpic1", "TestEpicDescription1",1);
        subtask1 = new Subtask(epic1, "TestSubtask1", "TestSubtaskDescription1", 2, Status.NEW,
                Duration.ofHours(1), LocalTime.of(1, 1, 0));
        subtask2 = new Subtask(epic1, "TestSubtask2", "TestSubtaskDescription2", 3, Status.DONE,
                Duration.ofHours(2), LocalTime.of(2, 1, 0));
        subtask3 = new Subtask(epic1, "TestSubtask3", "TestSubtaskDescription3", 4, Status.DONE,
                Duration.ofHours(3), LocalTime.of(3, 1, 0));
        epic2 = new Epic("TestEpic2", "TestEpicDescription2",5);
        task1 = new Task("TestTask1", "TestTaskDescription1",6, Status.NEW,
                Duration.ofHours(4), LocalTime.of(4, 1, 0));
        task2 = new Task("TestTask2", "TestTaskDescription2", 7, Status.IN_PROGRESS,
                Duration.ofHours(5), LocalTime.of(5, 1, 0));
    }

    @AfterEach
    public void afterEach() {
        if (historyManager.getHistory() != null) {
            historyManager.getHistory().clear();
        }
    }
    @Test
    public void historyIsEmpty() {
        assertNull(historyManager.getHistory());
    }
    @Test
    void add() {
        ArrayList<Task> testHistory = new ArrayList<>();
        historyManager.add(epic1);
        testHistory.add(epic1);
        historyManager.add(subtask1);
        testHistory.add(subtask1);
        historyManager.add(subtask2);
        testHistory.add(subtask2);
        historyManager.add(subtask3);
        testHistory.add(subtask3);
        historyManager.add(epic2);
        testHistory.add(epic2);
        historyManager.add(task1);
        testHistory.add(task1);
        historyManager.add(task2);
        testHistory.add(task2);
        Assertions.assertEquals(testHistory, historyManager.getHistory());
    }

    @Test
    void addDuplicate() {
        ArrayList<Task> testHistory = new ArrayList<>();
        historyManager.add(epic1);
        historyManager.add(subtask1);
        testHistory.add(subtask1);
        historyManager.add(subtask2);
        testHistory.add(subtask2);
        historyManager.add(subtask3);
        testHistory.add(subtask3);
        historyManager.add(epic1);
        testHistory.add(epic1);
        historyManager.add(task1);
        historyManager.add(task1);
        testHistory.add(task1);
        Assertions.assertEquals(testHistory, historyManager.getHistory());
    }
    @Test
    void removeFromBeginning() {
        ArrayList<Task> testHistory = new ArrayList<>();
        historyManager.add(epic1);
        historyManager.add(subtask1);
        testHistory.add(subtask1);
        historyManager.add(subtask2);
        testHistory.add(subtask2);
        historyManager.add(subtask3);
        testHistory.add(subtask3);
        historyManager.add(epic2);
        testHistory.add(epic2);
        historyManager.add(task1);
        testHistory.add(task1);
        historyManager.add(task2);
        testHistory.add(task2);
        historyManager.remove(1);
        Assertions.assertEquals(testHistory, historyManager.getHistory());

    }

    @Test
    void removeFromMiddle() {
        ArrayList<Task> testHistory = new ArrayList<>();
        historyManager.add(epic1);
        testHistory.add(epic1);
        historyManager.add(subtask1);
        testHistory.add(subtask1);
        historyManager.add(subtask2);
        testHistory.add(subtask2);
        historyManager.add(subtask3);
        historyManager.add(epic2);
        testHistory.add(epic2);
        historyManager.add(task1);
        testHistory.add(task1);
        historyManager.add(task2);
        testHistory.add(task2);
        historyManager.remove(4);
        Assertions.assertEquals(testHistory, historyManager.getHistory());
    }

    @Test
    void removeFromEnd() {
        ArrayList<Task> testHistory = new ArrayList<>();
        historyManager.add(epic1);
        testHistory.add(epic1);
        historyManager.add(subtask1);
        testHistory.add(subtask1);
        historyManager.add(subtask2);
        testHistory.add(subtask2);
        historyManager.add(subtask3);
        testHistory.add(subtask3);
        historyManager.add(epic2);
        testHistory.add(epic2);
        historyManager.add(task1);
        testHistory.add(task1);
        historyManager.add(task2);
        historyManager.remove(7);
        Assertions.assertEquals(testHistory, historyManager.getHistory());
    }


}