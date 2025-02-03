package management;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {



    int idCounter = 0;
    public HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    public HashMap<Integer, Task> tasksContainer = new HashMap<>();
    public HashMap<Integer, Epic> epicsContainer = new HashMap<>();
    public HashMap<Integer, Subtask> subtasksContainer = new HashMap<>();

    protected Set<Task> prioritizedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) return o1.getId() - o2.getId();
        if (o1.getStartTime() == null) return 1;
        if (o2.getStartTime() == null) return -1;
        if (o1.getStartTime().isAfter(o2.getStartTime())) return 1;
        if (o1.getStartTime().isBefore(o2.getStartTime())) return -1;
        if (o1.getStartTime().equals(o2.getStartTime())) return o1.getId() - o2.getId();
        return 0;
    });

    @Override
    public HashMap<Integer, Task> getTasks() { return new HashMap<>(tasksContainer); }
    @Override
    public HashMap<Integer, Epic> getEpics() { return new HashMap<>(epicsContainer); }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() { return new HashMap<>(subtasksContainer); }
    @Override
    public void checkTasksForIntersections(){
        boolean firstControlTimeIsSet = false;
        LocalTime controlTime = null;
        for (Task task : prioritizedTasks){
            if(!firstControlTimeIsSet){
                controlTime = task.getEndTime();
                firstControlTimeIsSet = true;
            } else if (task.getStartTime() != null) {
                if (task.getStartTime().isBefore(controlTime)) {
                    throw new ManagerSaveException("Задачи пересекаются по времени, исправьте временные метки задач! " +
                            "Задачи не должны пересекаться по времени!");
                }
                if (task.getStartTime().isAfter(controlTime) || task.getStartTime().equals(controlTime)) {
                    controlTime = task.getEndTime();
                }
            }
        }
    }
    @Override
    public Set<Task> getPrioritizedTasks(){
        checkTasksForIntersections();
        return prioritizedTasks;
    }
    @Override
    public int getNextID(){
        idCounter = idCounter + 1;
        return idCounter;
    }

    @Override
    public ArrayList<Integer> getListOfAllTasks(){
        return new ArrayList<>(tasksContainer.keySet());
    }

    @Override
    public ArrayList<Integer> getListOfAllEpics() {
        return new ArrayList<>(epicsContainer.keySet());
    }

    @Override
    public ArrayList<Integer> getListOfAllSubtasks() {
        return new ArrayList<>(subtasksContainer.keySet());
    }

    @Override
    public void deleteAllTasks(){
        for (int taskID : tasksContainer.keySet()){
            inMemoryHistoryManager.remove(taskID);
        }
        tasksContainer.clear();
    }

    @Override
    public void deleteAllEpics(){
        for (int taskID : subtasksContainer.keySet()){
            inMemoryHistoryManager.remove(taskID);
        }
        subtasksContainer.clear();
        for (int taskID : epicsContainer.keySet()){
            inMemoryHistoryManager.remove(taskID);
        }
        epicsContainer.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicsContainer.values()) {
            epic.clearSubtasksIDsList();
            epic.setStatus(deduceEpicsStatus(epic));
            epic.setDuration(deduceEpicsDuration(epic));
            epic.setStartTime(deduceEpicsStartTime(epic));
            epic.setEndTime(deduceEpicsEndTime(epic));
        }
        for (int taskID : subtasksContainer.keySet()){
            inMemoryHistoryManager.remove(taskID);
        }
        subtasksContainer.clear();
    }

    @Override
    public Task getTask(int taskID){
        if (tasksContainer.containsKey(taskID)){
            inMemoryHistoryManager.add(tasksContainer.get(taskID));
            return tasksContainer.get(taskID);
        } else if (epicsContainer.containsKey(taskID)){
            inMemoryHistoryManager.add(epicsContainer.get(taskID));
            return epicsContainer.get(taskID);
        } else if (subtasksContainer.containsKey(taskID)){
            inMemoryHistoryManager.add(subtasksContainer.get(taskID));
            return subtasksContainer.get(taskID);
        } else {
            System.out.println("Задачи, эпика, или подзадачи с ID " + taskID + " в менеджере задач - нет.");
        }
        return null;
    }

    @Override
    public List<Task> getHistory(){
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public void addTask(Task task) {
        tasksContainer.put(task.getId(), task);
    }

    public void addEpic(Epic epic){
        epicsContainer.put(epic.getId(), epic);

    }

    @Override
    public void addSubtask(Subtask subtask){
        Epic epic = epicsContainer.get(subtask.getEpicID());
        epic.addSubtaskToSubtasksList(subtask);
        subtasksContainer.put(subtask.getId(), subtask);
        epic.setStatus(deduceEpicsStatus(epic));
        epic.setDuration(deduceEpicsDuration(epic));
        epic.setStartTime(deduceEpicsStartTime(epic));
        epic.setEndTime(deduceEpicsEndTime(epic));
    }

    @Override
    public void updateTask(Task task) {
        if (tasksContainer.get(task.getId()) != null) {
            tasksContainer.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic){
        if (epicsContainer.get(epic.getId()) != null){
            epicsContainer.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask){
        if (subtasksContainer.get(subtask.getId()) != null){
            Epic epic = epicsContainer.get(subtask.getEpicID());
            epic.addSubtaskToSubtasksList(subtask);
            subtasksContainer.put(subtask.getId(), subtask);
            epic.setStatus(deduceEpicsStatus(epic));
            epic.setDuration(deduceEpicsDuration(epic));
            epic.setStartTime(deduceEpicsStartTime(epic));
            epic.setEndTime(deduceEpicsEndTime(epic));
        }
    }

    @Override
    public void deleteTaskByID (int taskID){
        if (tasksContainer.containsKey(taskID)){
            tasksContainer.remove(taskID);
            inMemoryHistoryManager.remove(taskID);
        } else if (epicsContainer.containsKey(taskID)){
            Epic epic = epicsContainer.get(taskID);
            ArrayList<Integer> subtasksList = epic.getSubtasksIDsList();
            for (int subtasksID : subtasksList) {
                inMemoryHistoryManager.remove(subtasksID);
                subtasksContainer.remove(subtasksID);
            }
            inMemoryHistoryManager.remove(taskID);
            epicsContainer.remove(taskID);
        } else if (subtasksContainer.containsKey(taskID)){
            Epic epic = epicsContainer.get(subtasksContainer.get(taskID).getEpicID());
            epic.removeSubtaskFromSubtasksIDsList(taskID);
            subtasksContainer.remove(taskID);
            epic.setStatus(deduceEpicsStatus(epic));
            epic.setDuration(deduceEpicsDuration(epic));
            epic.setStartTime(deduceEpicsStartTime(epic));
            epic.setEndTime(deduceEpicsEndTime(epic));
        } else {
            System.out.println("Задачи, эпика, или подзадачи с ID " + taskID + " в менеджере задач - нет.");
        }
    }

    @Override
    public Task addNewTask(String tasksTitle, String description, Status status, Duration duration, LocalTime localTime){
        Task task = new tasks.Task(tasksTitle, description, getNextID(), status, duration, localTime);
        tasksContainer.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic addNewEpic(String epicsTitle, String description){
        Epic epic = new tasks.Epic(epicsTitle, description, getNextID());
        epicsContainer.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status, Duration duration,
                                 LocalTime localTime){
        Subtask subtask = new tasks.Subtask(epic, subtasksTitle, description, getNextID(), status, duration, localTime);
        epic.addSubtaskToSubtasksList(subtask);
        subtasksContainer.put(subtask.getId(), subtask);
        epic.setStatus(deduceEpicsStatus(epic));
        epic.setDuration(deduceEpicsDuration(epic));
        epic.setStartTime(deduceEpicsStartTime(epic));
        epic.setEndTime(deduceEpicsEndTime(epic));
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Status deduceEpicsStatus(Epic epic) {
        ArrayList<Integer> subtasksIDsList = epic.getSubtasksIDsList();
        Status deducedStatus = Status.IN_PROGRESS;
        if(checkIfAllSubtasksHaveSameStatus(epic)){
            deducedStatus = subtasksContainer.get(subtasksIDsList.get(0)).getStatus();
        } else if (subtasksIDsList.isEmpty()){
            deducedStatus = Status.NEW;
        }
        return deducedStatus;
    }

    @Override
    public Duration deduceEpicsDuration(Epic epic){
        ArrayList<Integer> subtasksIDsList = epic.getSubtasksIDsList();
        Duration epicsCalculatedDuration = Duration.ZERO;
        if (!subtasksIDsList.isEmpty()) {
            for (int subtaskNumber : subtasksIDsList) {
                epicsCalculatedDuration = epicsCalculatedDuration.plus(subtasksContainer.get(subtaskNumber)
                        .getDuration());
            }
        }
        return epicsCalculatedDuration;
    }
    @Override
    public LocalTime deduceEpicsStartTime(Epic epic){
        ArrayList<Integer> subtasksIDsList = epic.getSubtasksIDsList();
        LocalTime earliestSubtaskStartTime = subtasksContainer.get(subtasksIDsList.get(0)).getStartTime();
            for (int subtaskNumber : subtasksIDsList) {
                if(earliestSubtaskStartTime.isAfter(subtasksContainer.get(subtaskNumber).getStartTime())){
                    earliestSubtaskStartTime = subtasksContainer.get(subtaskNumber).getStartTime();
                }
        }
        return earliestSubtaskStartTime;
    }
    @Override
    public LocalTime deduceEpicsEndTime(Epic epic){
        ArrayList<Integer> subtasksIDsList = epic.getSubtasksIDsList();
        LocalTime latestSubtaskEndTime = subtasksContainer.get(subtasksIDsList.get(0)).getEndTime();
        for (int subtaskNumber : subtasksIDsList) {
            if(latestSubtaskEndTime.isBefore(subtasksContainer.get(subtaskNumber).getEndTime())){
                latestSubtaskEndTime = subtasksContainer.get(subtaskNumber).getEndTime();
            }
        }
        return latestSubtaskEndTime;
    }


    @Override
    public boolean checkIfAllSubtasksHaveSameStatus (Epic epic) {
        ArrayList<Integer> subtasksIDsList = epic.getSubtasksIDsList();
        boolean doAllSubtasksHaveSameStatus = true;
        for (int subtaskID : subtasksIDsList) {
            if (!subtasksContainer.get(subtaskID).getStatus().equals(subtasksContainer.get(subtasksIDsList.
                    get(0)).getStatus())) {
                doAllSubtasksHaveSameStatus = false;
            }
        }
        return doAllSubtasksHaveSameStatus;
    }



    @Override
    public String toString() {
        return "Manager{" +
                "idCounter=" + idCounter +
                ", tasksContainer=" + tasksContainer +
                ", epicsContainer=" + epicsContainer +
                ", subtasksContainer=" + subtasksContainer +
                '}';
    }

}

