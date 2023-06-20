package management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void startTheProcess() {
        manager = new FileBackedTaskManager(new File("file.txt"));
    }


    @Test
    void getAllTasks() {
    }

    @Test
    void getAllEpics() {
    }

    @Test
    void getAllSubtasks() {
    }

    @Test
    void loadFromFile() {
    }

    @Test
    void fromString() {
    }

    @Test
    void historyToString() {
    }

    @Test
    void historyFromString() {
    }

    @Test
    void deleteAllTasks() {
    }

    @Test
    void deleteAllEpics() {
    }

    @Test
    void deleteAllSubtasks() {
    }

    @Test
    void getTask() {
    }

    @Test
    void addTask() {
    }

    @Test
    void addEpic() {
    }

    @Test
    void addSubtask() {
    }

    @Test
    void changeTask() {
    }

    @Test
    void changeEpic() {
    }

    @Test
    void changeSubtask() {
    }

    @Test
    void deleteTaskByID() {
    }

    @Test
    void addNewTask() {
    }

    @Test
    void addNewEpic() {
    }

    @Test
    void addNewSubtask() {
    }
}