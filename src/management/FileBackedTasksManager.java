package management;

import tasks.*;

import java.io.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private void save (){
        try {
            FileWriter writer = new FileWriter("/Users/artur/tmp/csv/sto1.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Конструктор, чтобы начинать работу со чтения файла
    FileBackedTasksManager () {
}

    static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("Файл сохранения " + file + " не найден в рабочей директории, список задач пуст.");
        }
        return fileBackedTasksManager;
    }
//блять я все делал неправильно, не надо использовать AddNewTask. Надо просто записывать таски как они есть из файла.
    Task fromString(String value){
        String[] taskFields = value.split(",");
        TaskType importedTaskType = TaskType.valueOf(taskFields[1]);
        Status importedTaskStatus = Status.valueOf(taskFields[3]);
        try{
            if(importedTaskType==TaskType.TASK){
                return addNewTask(taskFields[2], taskFields[4], importedTaskStatus);
            } else if (importedTaskType==TaskType.EPIC) {
                return addNewEpic(taskFields[2], taskFields[4]);
            } else if (importedTaskType==TaskType.SUBTASK) {
                return addNewSubtask(getTask(taskFields[5]),taskFields[2],importedTaskStatus);
            }
            int importedTaskID = Integer.parseInt(taskFields[0]);
            return new Task(taskFields[2], taskFields[4], importedTaskID, importedTaskStatus);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        System.out.println("Не удалось импортировать задачу!");
        return null;
    }


    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void changeTask(Task task) {
        super.changeTask(task);
        save();
    }

    @Override
    public void changeEpic(Epic epic) {
        super.changeEpic(epic);
        save();
    }

    @Override
    public void changeSubtask(Subtask subtask) {
        super.changeSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskByID(int taskNumber) {
        super.deleteTaskByID(taskNumber);
        save();
    }

    @Override
    public Task addNewTask(String tasksTitle, String description, Status status) {
        return null;
    }

    @Override
    public Epic addNewEpic(String epicsTitle, String description) {
        return null;
    }

    @Override
    public Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status) {
        return null;
    }

    @Override
    public Status deduceEpicsStatus(Epic epic) {
        return null;
    }

    @Override
    public boolean checkIfAllSubtasksHaveSameStatus(Epic epic) {
        return false;
    }
    }

