package management;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Epic> getEpics();
    void checkTasksForIntersections();
    Set<Task> getPrioritizedTasks();
    HistoryManager getHistoryManager();

    int getNextID();

    List<Integer> getListOfAllTasks();

    List<Integer> getListOfAllEpics();

    List<Integer> getListOfAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int taskNumber);

    Epic getEpic(int epicNumber);

    Subtask getSubtask(int subtaskNumber);

    List<Task> getHistory();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

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
