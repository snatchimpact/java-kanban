package management;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    int idCounter = 0;
    List<Task> viewingHistory = new ArrayList<>();
    HashMap<Integer, Task> tasksContainer = new HashMap<>();
    HashMap<Integer, Epic> epicsContainer = new HashMap<>();
    HashMap<Integer, Subtask> subtasksContainer = new HashMap<>();

    int getNextID();

    List<Integer> getListOfAllTasks();

    List<Integer> getListOfAllEpics();

    List<Integer> getListOfAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int taskNumber);

    List<Task> getHistory();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void changeTask(Task task);

    void changeEpic(Epic epic);

    void changeSubtask(Subtask subtask);

    void deleteTaskByID (int taskNumber);

    Task addNewTask(String tasksTitle, String description, Status status, Duration duration, LocalTime localTime);

    Epic addNewEpic(String epicsTitle, String description);

    Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status, Duration duration,
                          LocalTime localTime);

    Status deduceEpicsStatus(Epic epic);

    Duration deduceEpicsDuration(Epic epic);
    LocalTime deduceEpicsStartTime(Epic epic);
    LocalTime deduceEpicsEndTime(Epic epic);

    boolean checkIfAllSubtasksHaveSameStatus (Epic epic);


    @Override
    String toString();
}
