package management;

import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private void save (){
        try {
            FileWriter writer = new FileWriter("SavedTasks.csv");
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

    Task fromString(String value){

        try {
            String[] taskFields = value.split(",");
            int importedTaskID = Integer.parseInt(taskFields[0]);
            TaskType importedTaskType = TaskType.valueOf(taskFields[1]);
            String importedTaskName = taskFields[2];
            Status importedTaskStatus = Status.valueOf(taskFields[3]);
            String importedTaskDescription = taskFields[4];
            int importedEpicsID = 0;
            if (taskFields.length == 6) {
                importedEpicsID = Integer.parseInt(taskFields[5]);
            }
            if (importedTaskType == TaskType.TASK) {
                return new tasks.Task(importedTaskName, importedTaskDescription, importedTaskID, importedTaskStatus);
            } else if (importedTaskType == TaskType.EPIC) {
                return new tasks.Epic(importedTaskName, importedTaskDescription, importedTaskID);
            } else if (importedTaskType == TaskType.SUBTASK) {
                return new tasks.Subtask(importedEpicsID, importedTaskName, importedTaskDescription, importedTaskID,
                        importedTaskStatus);
            } else {
                System.out.println("Не удалось импортировать задачу!");
                return null;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка импорта задачи!");
        }
        return null;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        List<Integer> IDsFromHistory = new ArrayList<>();
        for (Task task : history) {
            IDsFromHistory.add(task.getId());
        }
        return String.join("," + IDsFromHistory);
    }

    static List<Integer> historyFromString(String value){
        String[] split = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String s : split) {
            try {
                history.add(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                System.out.println("Не удалось распознать номер задачи из строки!");
                ex.printStackTrace();
            }
        }
        return history;
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

