package management;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import static management.Managers.inMemoryHistoryManager;

public class InMemoryTaskManager implements TaskManager {
    int idCounter = 0;
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
//    public List<Task> viewingHistory = new ArrayList<>();
    public HashMap<Integer, Task> tasksContainer = new HashMap<>();
    public HashMap<Integer, Epic> epicsContainer = new HashMap<>();
    public HashMap<Integer, Subtask> subtasksContainer = new HashMap<>();


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
        tasksContainer.clear();
    }

    @Override
    public void deleteAllEpics(){
        subtasksContainer.clear();
        epicsContainer.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicsContainer.values()) {
            epic.clearSubtasksIDsList();
            epic.setStatus(deduceEpicsStatus(epic));
        }
        subtasksContainer.clear();
    }

    @Override
    public Task getTask(int taskNumber){
        if (tasksContainer.containsKey(taskNumber)){
            inMemoryHistoryManager.add(tasksContainer.get(taskNumber));
            return tasksContainer.get(taskNumber);
        } else if (epicsContainer.containsKey(taskNumber)){
            inMemoryHistoryManager.add(tasksContainer.get(taskNumber));
            return epicsContainer.get(taskNumber);
        } else if (subtasksContainer.containsKey(taskNumber)){
            inMemoryHistoryManager.add(tasksContainer.get(taskNumber));
            return subtasksContainer.get(taskNumber);
        } else {
            System.out.println("Задачи, эпика, или подзадачи с ID " + taskNumber + " в менеджере задач - нет.");
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
    }

    @Override
    public void changeTask(Task task) {
        if (tasksContainer.get(task.getId()) != null) {
            tasksContainer.put(task.getId(), task);
        }
    }

    @Override
    public void changeEpic(Epic epic){
        if (epicsContainer.get(epic.getId()) != null){
            epicsContainer.put(epic.getId(), epic);
        }
    }

    @Override
    public void changeSubtask(Subtask subtask){
        if (subtasksContainer.get(subtask.getId()) != null){
            Epic epic = epicsContainer.get(subtask.getEpicID());
            epic.addSubtaskToSubtasksList(subtask);
            subtasksContainer.put(subtask.getId(), subtask);
            epic.setStatus(deduceEpicsStatus(epic));
        }
    }

    @Override
    public void deleteTaskByID (int taskNumber){
        if (tasksContainer.containsKey(taskNumber)){
            tasksContainer.remove(taskNumber);
        } else if (epicsContainer.containsKey(taskNumber)){
            Epic epic = epicsContainer.get(taskNumber);
            ArrayList<Integer> subtasksList = epic.getSubtasksIDsList();
            for (int subtasksID : subtasksList) {
                subtasksContainer.remove(subtasksID);
            }
            epicsContainer.remove(taskNumber);
        } else if (subtasksContainer.containsKey(taskNumber)){
            Epic epic = epicsContainer.get(subtasksContainer.get(taskNumber).getEpicID());
            epic.removeSubtaskFromSubtasksIDsList(taskNumber);
            subtasksContainer.remove(taskNumber);
            epic.setStatus(deduceEpicsStatus(epic));
        } else {
            System.out.println("Задачи, эпика, или подзадачи с ID " + taskNumber + " в менеджере задач - нет.");
        }
    }

    @Override
    public Task addNewTask(String tasksTitle, String description, Status status){
        Task task = new tasks.Task(tasksTitle, description, getNextID(), status);
        tasksContainer.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addNewEpic(String epicsTitle, String description){
        Epic epic = new tasks.Epic(epicsTitle, description, getNextID());
        epicsContainer.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status){
        Subtask subtask = new tasks.Subtask(epic, subtasksTitle, description, getNextID(), status);
        epic.addSubtaskToSubtasksList(subtask);
        subtasksContainer.put(subtask.getId(), subtask);
        epic.setStatus(deduceEpicsStatus(epic));
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
    public boolean checkIfAllSubtasksHaveSameStatus (Epic epic) {
        ArrayList<Integer> subtasksIDsList = epic.getSubtasksIDsList();
        boolean doAllSubtasksHaveSameStatus = true;
        for (int subtaskID : subtasksIDsList) {
            if (!subtasksContainer.get(subtaskID).getStatus().equals(subtasksContainer.get(subtasksIDsList.get(0)).getStatus())) {
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
