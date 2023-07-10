package management;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    public void checkTasksForIntersections();
    public Set<Task> getPrioritizedTasks();

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
