package management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager>{
    T manager;

    @Test
    void createTaskTest(){
        Task task = manager.addNewTask("TestTask1","TestDescription1", Status.NEW);
        final Task savedTask = manager.getTask(task.getId());
        Assertions.assertNotNull(savedTask, "Задача не найдена!");
        Assertions.assertEquals(task,savedTask,"Задачи не совпадают!");
    }

    /*
    static Task task1;
    static Task task2;
    static Task epic1;
    static Task epic2;
    static Task subtask1;
    static Task subtask2;
    static Task subtask3;

void createAllTasks(){
    task1 = manager.addNewTask("TestTask1", "TestTaskDescription1", Status.NEW);
    task2 = manager.addNewTask("TestTask2", "TestTaskDescription2", Status.NEW);
    epic1 = manager.addNewEpic("TestEpic1","DestEpicDescription1");

}


    @Test
    public void checkIfEmptyEpicHasStatusNEW(){
        Assertions.assertEquals(Status.NEW, epic1.getStatus(), "У нового пустого Epic статус не NEW!");
}

    @Test
    int getNextID() {
        return 0;
    }

    @Test
    List<Integer> getListOfAllTasks() {
        return null;
    }

    @Test
    List<Integer> getListOfAllEpics() {
        return null;
    }

    @Test
    List<Integer> getListOfAllSubtasks() {
        return null;
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
    Task getTask(int taskNumber) {
        return null;
    }

    @Test
    List<Task> getHistory() {
        return null;
    }

    @Test
    void addTask(Task task) {
    }

    @Test
    void addEpic(Epic epic) {
    }

    @Test
    void addSubtask(Subtask subtask) {
    }

    @Test
    void changeTask(Task task) {
    }

    @Test
    void changeEpic(Epic epic) {
    }

    @Test
    void changeSubtask(Subtask subtask) {

    }

    @Test
    void deleteTaskByID(int taskNumber) {

    }

    @Test
    Task addNewTask(String tasksTitle, String description, Status status) {


        return null;
    }

    @Test
    Epic addNewEpic(String epicsTitle, String description) {
        return null;
    }

    @Test
    Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status) {
        return null;
    }

    @Test
    Status deduceEpicsStatus(Epic epic) {
        return null;
    }

    @Test
    boolean checkIfAllSubtasksHaveSameStatus(Epic epic) {
        return false;
    }

     */
}


