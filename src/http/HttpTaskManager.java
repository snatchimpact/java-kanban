package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import management.FileBackedTaskManager;
import management.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(String url, HistoryManager historyManager) {
        this(url, historyManager, false);
    }
    public HttpTaskManager(String url, HistoryManager historyManager, boolean isLoadNeeded) {
        super(historyManager, null);
        this.kvTaskClient = new KVTaskClient(url);
        if (isLoadNeeded) {
            loadFromServer();
        }
    }

    private void save() throws ManagerSaveException {
        String jsonTasks = gson.toJson(new ArrayList<>(super.getTasks().values()));
        kvTaskClient.put("tasks", jsonTasks);

        String jsonEpics = gson.toJson(new ArrayList<>(super.getEpics().values()));
        kvTaskClient.put("epics", jsonEpics);

        String jsonSubTasks = gson.toJson(new ArrayList<>(super.getSubtasks().values()));
        kvTaskClient.put("subtasks", jsonSubTasks);

        String jsonHistory = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        kvTaskClient.put("history", jsonHistory);
    }

    private void loadFromServer() {
        String jsonTasks = this.kvTaskClient.load("tasks");
        if (!jsonTasks.isEmpty()) {
            tasksContainer.clear();
            ArrayList<Task> taskList = gson.fromJson(jsonTasks, new TypeToken<ArrayList<Task>>() {
            }.getType());
            for (Task task : taskList) {
                tasksContainer.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        }

        String jsonEpics = this.kvTaskClient.load("epics");
        if (!jsonEpics.isEmpty()) {
            epicsContainer.clear();
            ArrayList<Epic> epicList = gson.fromJson(jsonEpics, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            for (Epic epic : epicList) {
                epicsContainer.put(epic.getId(), epic);
            }
        }

        String jsonSubTasks = this.kvTaskClient.load("subtasks");
        if (!jsonSubTasks.isEmpty()) {
            subtasksContainer.clear();
            ArrayList<Subtask> subTaskList = gson.fromJson(jsonSubTasks, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            for (Subtask subtask : subTaskList) {
                Epic epic = epicsContainer.get(subtask.getEpicID());
                if (epic != null) {
                    checkTasksForIntersections();
                    subtasksContainer.put(subtask.getId(), subtask);
                    prioritizedTasks.add(subtask);
                    epic.addSubtaskToSubtasksList(subtask);
                    deduceEpicsStatus(epic);
                    deduceEpicsEndTime(epic);
                } else {
                    System.out.println("Эпик не найден.");
                }

            }
        }

        idCounter = getMaxId();

        HistoryManager historyManager = getHistoryManager();
        String jsonHistory = this.kvTaskClient.load("history");
        if (!jsonHistory.isEmpty()) {
            ArrayList<Integer> taskHistoryIds = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
            }.getType());
            for (Integer id : taskHistoryIds) {
                if (getTasks().containsKey(id)) { historyManager.add(getTask(id)); }
                if (getEpics().containsKey(id)) { historyManager.add(getEpic(id)); }
                if (getTasks().containsKey(id)) { historyManager.add(getTask(id)); }
            }
        }

    }

    private int getMaxId() {
        Collection<Integer> merged = Stream.of(tasksContainer.keySet(), epicsContainer.keySet(), subtasksContainer.keySet())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (merged.size() == 0) {
            return 0;
        } else {
            return Collections.max(merged);
        }
    }

}