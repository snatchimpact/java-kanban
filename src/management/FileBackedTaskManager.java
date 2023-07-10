package management;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File ourFile) {
        file = ourFile;
    }
    public FileBackedTaskManager() {
    }

    public static void main(String[] args){
        //Идём по пунктам ТЗ "Проверка работы менджера"
        File ourFile = new File("file.txt");
        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(ourFile);
        fileBackedTasksManager.addNewTask("First Task", "Task No.1 lorem ipsum", Status.NEW,
                Duration.ofMinutes(30), LocalTime.of(1, 0, 0));
        fileBackedTasksManager.addNewTask("Second Task", "Task No.2 lorem ipsum", Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalTime.of(5, 0, 0));
        Epic epic1 = fileBackedTasksManager.addNewEpic("First Epic", "Epic No.1 Lorem ipsum");
        fileBackedTasksManager.addNewSubtask(epic1, "1st Epic's First Subtask",
                "1st Epic's 1st Subtask Lorem ipsum",
                Status.NEW, Duration.ofMinutes(30), LocalTime.of(4, 0, 0));
        fileBackedTasksManager.addNewSubtask(epic1, "1st Epic's Second Subtask",
                "1st Epic's 2nd Subtask Lorem ipsum",
                Status.IN_PROGRESS, Duration.ofMinutes(30), LocalTime.of(3, 0, 0));
        fileBackedTasksManager.addNewSubtask(epic1, "1st Epic's Third Subtask",
                "1st Epic's 3rd Subtask Lorem ipsum",
                Status.DONE, Duration.ofMinutes(30), LocalTime.of(2, 0, 0));
        fileBackedTasksManager.addNewEpic("Second Epic", "Epic No.2 Lorem ipsum");

        //	Запросите некоторые из них, чтобы заполнилась история просмотра.
        System.out.println(fileBackedTasksManager.getTask(1));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(2));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(3));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(4));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(4));
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTask(3));
        System.out.println(fileBackedTasksManager.getHistory());

//      Проверим работу приоретизированных задач и пересечения
        System.out.println("Печатаем приоритизированные задачи");
        System.out.println(fileBackedTasksManager.getPrioritizedTasks());

        //	Создадим новый FileBackedTasksManager менеджер из этого же файла.

        FileBackedTaskManager restoredFileBackedTasksManager = loadFromFile(ourFile);

        // Проверим, что что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были
        // в старом, есть в новом менеджере.
        System.out.println("Печатаем изначальный менеджер");
        System.out.println(fileBackedTasksManager);
        System.out.println("Печатаем восстановленный менеджер");
        System.out.println(restoredFileBackedTasksManager);
        System.out.println("Печатаем изначальный хистори менеджер");
        System.out.println(fileBackedTasksManager.inMemoryHistoryManager);
        System.out.println("Печатаем восстановленный хистори менеджер");
        System.out.println(restoredFileBackedTasksManager.inMemoryHistoryManager);
    }


    private void save () throws ManagerSaveException {
        try {
            //Удаляем старый файл, если он есть
            if(!file.delete()) {
                System.out.println("Старый файл удалить не удалось");
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic\n");
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
                fileWriter.write(Objects.requireNonNull(historyToString(inMemoryHistoryManager)));
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
    public static FileBackedTaskManager loadFromFile(File file){
        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager();
        fileBackedTasksManager.idCounter = 0;
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

                    if(Integer.parseInt(taskFields[0]) > fileBackedTasksManager.idCounter){
                        fileBackedTasksManager.idCounter = Integer.parseInt(taskFields[0]);
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
                        fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.
                                tasksContainer.get(taskNumber));
                    }
                    else if (fileBackedTasksManager.epicsContainer.containsKey(taskNumber)){
                        fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.
                                epicsContainer.get(taskNumber));
                    }
                    else if (fileBackedTasksManager.subtasksContainer.containsKey(taskNumber)){
                        fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.
                                subtasksContainer.get(taskNumber));
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
            String importedTaskTitle = taskFields[2];
            Status importedTaskStatus = Status.valueOf(taskFields[3]);
            String importedTaskDescription = taskFields[4];

            Duration importedTaskDuration;
            if(!taskFields[5].equals("null")) {
                importedTaskDuration = Duration.parse(taskFields[5]);
            } else {
                importedTaskDuration = null;
            }

            LocalTime importedStartTime;
            if(!taskFields[6].equals("null")) {
                importedStartTime = LocalTime.parse(taskFields[6]);
            } else {
                importedStartTime = null;
            }

            LocalTime importedEndTime;
            if(!taskFields[7].equals("null")) {
                importedEndTime = LocalTime.parse(taskFields[7]);
            } else {
                importedEndTime = null;
            }

            int importedEpicsID = 0;
            if (taskFields.length == 9) {
                importedEpicsID = Integer.parseInt(taskFields[8]);
            }
            if (importedTaskType == Type.TASK) {
                return new tasks.Task(importedTaskTitle, importedTaskDescription, importedTaskID, importedTaskStatus,
                        importedTaskDuration, importedStartTime);
            } else if (importedTaskType == Type.EPIC) {
                return new tasks.Epic(importedTaskTitle, importedTaskDescription, importedTaskID, importedTaskStatus,
                        importedTaskDuration, importedStartTime, importedEndTime);
            } else if (importedTaskType == Type.SUBTASK) {
                return new tasks.Subtask(importedEpicsID, importedTaskTitle, importedTaskDescription, importedTaskID,
                        importedTaskStatus, importedTaskDuration, importedStartTime);
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
    public Task addNewTask(String tasksTitle, String description, Status status, Duration duration,
                           LocalTime localTime) {
        Task task = super.addNewTask(tasksTitle, description, status, duration, localTime);
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
    public Subtask addNewSubtask(Epic epic, String subtasksTitle, String description, Status status, Duration duration,
                                 LocalTime localTime) {
        Subtask subtask = super.addNewSubtask(epic,subtasksTitle,description,status, duration, localTime);
        save();
        return subtask;
    }



    }

