package management;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;




    private void save () throws ManagerSaveException {
        try {
            //Удаляем старый файл, если он есть
            if(!file.delete()) {
                System.out.println("Старый файл удалить не удалось");
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("id,type,name,status,description,epic\n");
            HashMap<Integer, Task> allTasks = getAllTasks();
            for (Task i : allTasks.values()){
                fileWriter.write(String.valueOf(i));
            }
            HashMap<Integer, Epic> allEpics = getAllEpics();
            for (Epic i : allEpics.values()){
                fileWriter.write(String.valueOf(i));
            }
            HashMap<Integer, Subtask> allSubtasks = getAllSubtasks();
            for (Subtask i : allSubtasks.values()){
                fileWriter.write(String.valueOf(i));
            }
            fileWriter.write("\n");
            if (historyToString(inMemoryHistoryManager) == null){
                fileWriter.close();
            } else {
                fileWriter.write(historyToString(inMemoryHistoryManager));
                fileWriter.close();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Проблема сохранения файла!");
        }
    }

    //Геттеры чтоб извлекать все задачи для Save без регистрации просмотров InMemoryHistoryManager-ом
    public HashMap<Integer, Task> getAllTasks(){
        return tasksContainer;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epicsContainer;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasksContainer;
    }

    //Статический метод для чтения менеджера из файла
    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()){
                lines.add(br.readLine());
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла!");
        }
//Список со строками менеджера из файла получили, создаем теперь задачи и раскладываем их по контейнерам
        if (lines.size() < 2) {
            System.out.println("Файл без задач, создать задачи невозможно!");
            return fileBackedTasksManager;
        } else {
            for (int i = 1; i < lines.size() - 1 ; i++) {
//Проверяем, не пустая ли у нас строка. Пустая - значит мы прошли все задачи и добрались до разделителя
                if(lines.get(i).length() == 0){
                    System.out.println("Перебирая строки файла с задачами, достигли конца файла.");
                    break;
                } else {
                    //Проверяем, какой тип задачи создавать, чтоб правильно сложить
                    String[] taskFields = lines.get(i).split(",");
                    Type importedTaskType = Type.valueOf(taskFields[1]);
                    Task task = fromString(lines.get(i));
                    if (importedTaskType == Type.TASK){
                        fileBackedTasksManager.tasksContainer.put(Integer.parseInt(taskFields[0]), task);
                    } else if (importedTaskType == Type.SUBTASK) {
                        fileBackedTasksManager.subtasksContainer.put(Integer.parseInt(taskFields[0]), (Subtask) task);
                    }
                    else if (importedTaskType == Type.EPIC) {
                        fileBackedTasksManager.epicsContainer.put(Integer.parseInt(taskFields[0]), (Epic) task);
                    }

                }
            }
            //Проверяем, не пуста ли последняя строка
            if (lines.get((lines.size() - 1)).length() == 0) {
                System.out.println("История в файле - пуста, TasksManager будет содержать задачи без истории");
            } else {
                List<Integer> listHistoryFromString = historyFromString(lines.get(lines.size() - 1));
                for (Integer taskNumber : listHistoryFromString){
                    if(fileBackedTasksManager.tasksContainer.containsKey(taskNumber)){
                        fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.tasksContainer.get(taskNumber));
                    }
                    else if (fileBackedTasksManager.epicsContainer.containsKey(taskNumber)){
                        fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.epicsContainer.get(taskNumber));
                    }
                    else if (fileBackedTasksManager.subtasksContainer.containsKey(taskNumber)){
                        fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.subtasksContainer.get(taskNumber));
                    }
                    else {
                        System.out.println("Какая-то странная ситуация: пытаемся восстановить в истории задачу, " +
                                "которой нет среди задач");
                    }
                }

            }

        }

        return fileBackedTasksManager;
    }

    static Task fromString(String value){
        try {
            String[] taskFields = value.split(",");
            int importedTaskID = Integer.parseInt(taskFields[0]);
            Type importedTaskType = Type.valueOf(taskFields[1]);
            String importedTaskName = taskFields[2];
            Status importedTaskStatus = Status.valueOf(taskFields[3]);
            String importedTaskDescription = taskFields[4];
            int importedEpicsID = 0;
            if (taskFields.length == 6) {
                importedEpicsID = Integer.parseInt(taskFields[5]);
            }
            if (importedTaskType == Type.TASK) {
                return new tasks.Task(importedTaskName, importedTaskDescription, importedTaskID, importedTaskStatus);
            } else if (importedTaskType == Type.EPIC) {
                return new tasks.Epic(importedTaskName, importedTaskDescription, importedTaskID, importedTaskStatus);
            } else if (importedTaskType == Type.SUBTASK) {
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
        List<String> IDsFromHistory = new ArrayList<>();
        if(history == null){
            return null;
        } else {
            for (Task task : history) {
                IDsFromHistory.add(String.valueOf(task.getId()));
            }
            return String.join(",", IDsFromHistory);
        }
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
    public Task getTask(int taskID){
        Task task = super.getTask(taskID);
        save();
        return task;
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
        Task task = super.addNewTask(tasksTitle, description, status);
        save();
        return task;
    }

    @Override
    public Epic addNewEpic(String epicsTitle, String description) {
        Epic epic = super.addNewEpic(epicsTitle, description);
        save();
        return epic;
    }

    @Override
    public Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status) {
        Subtask subtask = super.addNewSubtask(epic,subtasksTitle,description,status);
        save();
        return subtask;
    }



    }

