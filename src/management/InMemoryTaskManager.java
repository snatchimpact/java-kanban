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
        } else {
            System.out.println("Задачи, эпика, или подзадачи с ID " + taskID + " в менеджере задач - нет.");
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
