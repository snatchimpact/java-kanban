package http;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import management.FileBackedTaskManager;
import management.TaskManager;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class HttpTaskServer {

    private TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        this.taskManager = taskManager;
        createHTTPServer();
    }

    private static final String TASK = "task";
    private static final String EPIC = "epic";
    private static final String SUBTASK = "subtask";
    private static final String HISTORY = "history";

    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public Gson getGson() {
        return gson;
    }

    private void createHTTPServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }

    public void startHttpServer() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
    public void stopHttpServer() {
        httpServer.stop(1);
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String methodRequest = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");

            if (splitPath.length == 2 && methodRequest.equals("GET")) {
                handleGetPrioritizedTasks(httpExchange);
            }


            switch (methodRequest) {
                case "POST": {
                    if (splitPath[2].equals(TASK)) {
                        handlePostAddUpdateTask(httpExchange);
                    } else if (splitPath[2].equals(EPIC)) {
                        handlePostAddUpdateEpic(httpExchange);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handlePostAddUpdateSubtask(httpExchange);
                    } else {
                        outputStreamWrite(httpExchange, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                }

                case "GET": {
                    if (splitPath[2].equals(TASK)) {
                        handleGetTaskGetTasksMap(httpExchange);

                    } else if (splitPath[2].equals(EPIC)) {
                        handleGetEpicGetEpicsMap(httpExchange);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handleGetSubTaskGetSubTasksMap(httpExchange);
                    } else if (splitPath[2].equals(HISTORY)) {
                        handleGetHistory(httpExchange);
                    } else {
                        outputStreamWrite(httpExchange, "Запрашиваемая страница не найдена", 404);
                    }
                    break;

                    case "DELETE":
                        if (splitPath[2].equals(TASK)) {
                            handleDeleteTask(httpExchange);
                        } else if (splitPath[2].equals(EPIC)) {
                            handleDeleteEpic(httpExchange);
                        } else if (splitPath[2].equals(SUBTASK)) {
                            handleDeleteSubTask(httpExchange);
                        } else {
                            outputStreamWrite(httpExchange, "Запрашиваемая страница не найдена", 404);
                        }
                        break;
                    default:
                        outputStreamWrite(httpExchange, "Неизвестный HTTP запрос", 405);
                }
            }
        }
    public void handleDeleteSubTask(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            int idSubtask = setId(httpExchange);
            if (taskManager.getSubtasks().containsKey(idSubtask)) {
                Subtask subtask = taskManager.getSubtasks().get(idSubtask);
                taskManager.deleteTaskByID(subtask.getId());
                outputStreamWrite(httpExchange, "Удалили " + gson.toJson(subtask), 200);
            } else {
                outputStreamWrite(httpExchange, "Подзадача с Id " + idSubtask + " не найдена в базе.", 404);
            }
        } else {
            handleDeleteTasksEpicsSubTasksMap(httpExchange);
        }
    }
    public void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            int idEpic = setId(httpExchange);
            if (taskManager.getEpics().containsKey(idEpic)) {
                Epic epic = taskManager.getEpics().get(idEpic);
                taskManager.deleteTaskByID(epic.getId());
                outputStreamWrite(httpExchange, "Удалили " + gson.toJson(epic), 200);
            } else {
                outputStreamWrite(httpExchange, "Эпик с Id " + idEpic + " не найден в базе.", 404);
            }
        } else {
            handleDeleteTasksEpicsSubTasksMap(httpExchange);
        }
    }


    public void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            int idTask = setId(httpExchange);
            if (taskManager.getTasks().containsKey(idTask)) {
                Task task = taskManager.getTasks().get(idTask);
                taskManager.deleteTaskByID(task.getId());
                outputStreamWrite(httpExchange, "Удалили " + gson.toJson(task), 200);
            } else {
                outputStreamWrite(httpExchange, "Задача с Id " + idTask + " не найдена в базе.", 404);
            }
        } else {
            handleDeleteTasksEpicsSubTasksMap(httpExchange);
        }
    }

    public void handleDeleteTasksEpicsSubTasksMap(HttpExchange httpExchange) throws IOException {
        if (!taskManager.getTasks().isEmpty() ||
                !taskManager.getEpics().isEmpty() ||
                !taskManager.getSubtasks().isEmpty()) {
            taskManager.deleteAllTasks();
            taskManager.deleteAllSubtasks();
            taskManager.deleteAllEpics();
            outputStreamWrite(httpExchange, "Все задачи удалены.", 200);
        } else {
            outputStreamWrite(httpExchange, "Задач для удаления нет.", 404);
        }
    }

    public void handleGetSubTaskGetSubTasksMap(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() != null) {
            int idSubTask = setId(h);
            if (taskManager.getSubtasks().containsKey(idSubTask)) {
                Subtask subtask = taskManager.getSubtask(idSubTask);
                outputStreamWrite(h, gson.toJson(subtask), 200);
            } else {
                outputStreamWrite(h, "Подзадача с Id " + idSubTask + " не найдена в базе.", 404);
            }
        } else {
            if (!taskManager.getSubtasks().isEmpty()) {
                outputStreamWrite(h, gson.toJson(taskManager.getSubtasks()), 200);
            } else {
                outputStreamWrite(h, "Список подзадач не найден в базе.", 404);
            }
        }
    }
    public void handlePostAddUpdateSubTask(HttpExchange httpexchange) throws IOException {
        String body = readText(httpexchange);
        if (body.isEmpty()) {
            outputStreamWrite(httpexchange, "Ничего не передано.", 400);
            return;
        }
        Subtask subtask = gson.fromJson(body, Subtask.class);
        Integer idSubtask = subtask.getId();
        if (idSubtask == null) {
            if (taskManager.getEpics().containsKey(subtask.getEpicID())) {
                taskManager.addSubtask(subtask);
                outputStreamWrite(httpexchange, "Создали новую подзадачу с Id " + subtask.getId(), 200);
            } else {
                outputStreamWrite(httpexchange, "Эпика с Id " + subtask.getEpicID() + " нет в базе.", 404);
            }
        } else {
            if (taskManager.getSubtasks().containsKey(idSubtask)) {
                taskManager.updateSubtask(subtask);
                outputStreamWrite(httpexchange, "Обновили подзадачу с Id "+ idSubtask, 200);
            } else {
                outputStreamWrite(httpexchange, "Подзадачи с Id " + idSubtask + " нет в базе.", 404);
            }
        }
    }
    public void handleGetEpicGetEpicsMap(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() != null) {
            int idEpic = setId(h);
            if (taskManager.getEpics().containsKey(idEpic)) {
                Epic epic = taskManager.getEpic(idEpic);
                outputStreamWrite(h, gson.toJson(epic), 200);
            } else {
                outputStreamWrite(h, "Эпик с Id " + idEpic + " не найден в базе.", 404);
            }
        } else {
            if (!taskManager.getEpics().isEmpty()) {
                outputStreamWrite(h, gson.toJson(taskManager.getEpics()), 200);
            } else {
                String message = "Список эпиков не найден в базе.";
                outputStreamWrite(h, message, 404);
            }
        }
    }
    public void handleGetTaskGetTasksMap(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            int idTask = setId(httpExchange);
            if (taskManager.getTasks().containsKey(idTask)) {
                Task task = taskManager.getTask(idTask);
                outputStreamWrite(httpExchange, gson.toJson(task), 200);
            } else {
                outputStreamWrite(httpExchange, "Задача с Id " + idTask + " не найдена в базе.", 404);
            }
        } else {
            if (!taskManager.getTasks().isEmpty()) {
                outputStreamWrite(httpExchange, gson.toJson(taskManager.getTasks()), 200);
            } else {
                outputStreamWrite(httpExchange, "Список задач не найден в базе.", 404);
            }
        }
    }

    int setId(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().toString()
                .split("\\?")[1].split("=")[1]);
        return id;
    }
    private static Endpoint getEndpoint(String requestURI, String requestMethod) {
        String[] URIParts = requestURI.split("/");
        System.out.println(URIParts.length);
        System.out.println(requestMethod);

        if ((URIParts.length == 3 && URIParts[2].equals("tasks") && requestMethod.equals("GET")) || (URIParts.length == 2 && URIParts[1].equals("tasks") && requestMethod.equals("GET"))) {
            System.out.println("getEndpoint says GET_ALL_TASKS");
            return Endpoint.GET_ALL_TASKS;
        }

        if (URIParts.length == 3 && URIParts[2].equals("epics") && requestMethod.equals("GET")) {
            return Endpoint.GET_ALL_EPICS;
        }
        if (URIParts.length == 3 && URIParts[2].equals("subtasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_ALL_SUBTASKS;
        }

        if (URIParts.length == 4 && requestMethod.equals("GET")) {
            String[] lastSegment = URIParts[3].split("\\?");
            if(lastSegment.length == 2 && !(lastSegment[1] == null) ){
                boolean isIDcorrect;
                try {
                    isIDcorrect = true;
                } catch (NumberFormatException e) {
                    isIDcorrect = false;
                    System.out.println("В качестве номера задачи передан не Integer, а символ " + lastSegment[1].split("=")[1]);
                }
                if(isIDcorrect){
                    if(URIParts[2].equals("task") || URIParts[2].equals("subtask") || URIParts[2].equals("epic")){
                        return Endpoint.GET_TASK_BY_ID;
                    }  else {
                        System.out.println("Запрос задачи по её номеру сформирован неверно, вместо task или subtask или epic указано: " + URIParts[2]);
                    }
                }
            }
        }
        

        if (URIParts.length == 3 && URIParts[2].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }

        if (URIParts.length == 3 && requestMethod.equals("POST")){
                    if(URIParts[2].equals("task")){
                        return Endpoint.POST_TASK_BY_BODY;
                    } else if (URIParts[2].equals("subtask")){
                        return Endpoint.POST_SUBTASK_BY_BODY;
                    } else if (URIParts[2].equals("epic")){
                        return Endpoint.POST_EPIC_BY_BODY;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
            }

        return Endpoint.UNKNOWN;
    }

    private static void handleGetAllTasks(HttpExchange exchange) throws IOException {
        System.out.println("Executing handleGetAllTasks");
        writeResponse(exchange, gson.toJson(fileBackedTasksManager.getAllTasks()), 200);
    }

    private static void handleGetAllEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(fileBackedTasksManager.getAllEpics()), 200);
    }

    private static void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(fileBackedTasksManager.getAllSubtasks()), 200);
    }
    private static void handleGetTaskByID(HttpExchange exchange) throws IOException {
        int RequestedTaskNumber = getIDfromRequest(exchange);
        Type RequestedTaskType = Type.TASK;
        String RequestedTaskTypeName = getTaskTypefromRequest(exchange);
        if(RequestedTaskTypeName.equals("task")){
            RequestedTaskType = Type.TASK;
        } else if (RequestedTaskTypeName.equals("epic")){
            RequestedTaskType = Type.EPIC;
        } else if (RequestedTaskTypeName.equals("subtask")){
            RequestedTaskType = Type.SUBTASK;
        }
        Type RealTaskTypeOfTheRequestedTask = fileBackedTasksManager.getTask(RequestedTaskNumber).getType();
        if(RequestedTaskType.equals(RealTaskTypeOfTheRequestedTask)){
            writeResponse(exchange, gson.toJson(fileBackedTasksManager.getTask(getIDfromRequest(exchange))), 200);
        } else {
            writeResponse(exchange, "URL-запрос ищет задачу с типом " + RequestedTaskType + " и номером " + getIDfromRequest(exchange) +
                    ", но задачи с таким типом и таким номером нет", 400);
        }
    }

    private static Integer getIDfromRequest(HttpExchange exchange){
        return Integer.parseInt(exchange.getRequestURI().toString().split("\\?")[1].split("=")[1]);
    }
    private static String getTaskTypefromRequest(HttpExchange exchange){
        return exchange.getRequestURI().toString().split("/")[2];
    }

    private static void handleGetHistory(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(fileBackedTasksManager.getAllSubtasks()), 200);
    }

    private static void handlePostTaskByBody(HttpExchange exchange) throws IOException {
        String body = httpBodyToString(exchange);
        if (body.isEmpty()) {
            writeResponse(exchange, "Ничего не передано.", 400);
        } else {
            try {
                fileBackedTasksManager.addTask(gson.fromJson(body, Task.class));
                writeResponse(exchange, "Task добавлен",
                        201);
            } catch (JsonSyntaxException e){
                System.out.println("Печатаем эксепшен");
                System.out.println(e);
                System.out.println("Распечатали эксепшен");
                writeResponse(exchange, "Получен некорректный JSON с Task", 400);
            }
        }
        System.out.println(fileBackedTasksManager);
    }

    private static void handlePostEpicByBody(HttpExchange exchange) throws IOException {
        String body = httpBodyToString(exchange);
        if (body.isEmpty()) {
            writeResponse(exchange, "Ничего не передано.", 400);
        } else {
            try {
                fileBackedTasksManager.addEpic(gson.fromJson(body, Epic.class));
                writeResponse(exchange, "Epic добавлен",
                        201);
            } catch (JsonSyntaxException e){
                System.out.println("Печатаем эксепшен");
                System.out.println(e);
                System.out.println("Распечатали эксепшен");
                writeResponse(exchange, "Получен некорректный JSON с Epic", 400);
            }
        }
        System.out.println(fileBackedTasksManager);
    }
    private static void handlePostSubtaskByBody(HttpExchange exchange) throws IOException {
        String body = httpBodyToString(exchange);
        if (body.isEmpty()) {
            writeResponse(exchange, "Ничего не передано.", 400);
        } else {
            try {
                fileBackedTasksManager.addSubtask(gson.fromJson(body, Subtask.class));
                writeResponse(exchange, "Subtask добавлен",
                        201);
            } catch (JsonSyntaxException e){
                System.out.println("Печатаем эксепшен");
                System.out.println(e);
                System.out.println("Распечатали эксепшен");
                writeResponse(exchange, "Получен некорректный JSON с Subtask", 400);
            }
        }
        System.out.println(fileBackedTasksManager);
    }

    private static String httpBodyToString(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }
    private static void writeResponse(HttpExchange exchange,
                                      String responseString,
                                      int responseCode) throws IOException {
        if(responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    enum Endpoint {
        UNKNOWN,
        GET_ALL_TASKS,
        GET_ALL_EPICS,
        GET_ALL_SUBTASKS,
        GET_TASK_BY_ID,
        GET_HISTORY,

        POST_TASK_BY_BODY,
        POST_EPIC_BY_BODY,
        POST_SUBTASK_BY_BODY,
        DELETE_TASK_BY_ID,
        DELETE_ALL_TASKS,
        GET_SUBTASK_METHODS,
        GET_EPIC_METHODS,
        GET_EPICS_SUBTASKS_BY_EPICS_ID,
        GET_PRIORITIZED_TASKS
    }

    private static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        private final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalTime localTime) throws IOException {
            if (localTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localTime.format(formatterWriter));
        }

        @Override
        public LocalTime read(final JsonReader jsonReader) throws IOException {
            return LocalTime.parse(jsonReader.nextString(), formatterWriter);
        }
    }



    public void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        if (!taskManager.getPrioritizedTasks().isEmpty()) {
            outputStreamWrite(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
        } else {
            outputStreamWrite(exchange, "Отсортированный список задач не найден в базе.", 404);
        }
    }


    public void handlePostAddUpdateTask(HttpExchange httpExchange) throws IOException {
        String body = readText(httpExchange);
        if (body.isEmpty()) {
            outputStreamWrite(httpExchange, "Ничего не передано.", 400);
            return;
        }
        Task task = gson.fromJson(body, Task.class);
        Integer idTask = task.getId();
        if (idTask == null) {
            taskManager.addTask(task);
            outputStreamWrite(httpExchange, "Создали новую задачу с Id " + task.getId(), 200);
        } else {
            if (taskManager.getTasks().containsKey(idTask)) {
                taskManager.updateTask(task);
                outputStreamWrite(httpExchange, "Обновили задачу с Id "+ idTask, 200);
            } else {
                outputStreamWrite(httpExchange, "Задачи с Id " + idTask + " нет в базе.", 404);
            }
        }
    }

    public void handlePostAddUpdateEpic(HttpExchange httpExchange) throws IOException {
        String body = readText(httpExchange);
        if (body.isEmpty()) {
            outputStreamWrite(httpExchange, "Ничего не передано.", 400);
            return;
        }
        Epic epic = gson.fromJson(body, Epic.class);
        Integer idEpic = epic.getId();
        if (idEpic == null) {
            taskManager.addEpic(epic);
            outputStreamWrite(httpExchange, "Создали новый эпик с Id "+ epic.getId(), 200);
        } else {
            if (taskManager.getEpics().containsKey(idEpic)) {
                taskManager.updateEpic(epic);
                outputStreamWrite(httpExchange, "Обновили эпик с Id "+ idEpic, 200);
            } else {
                outputStreamWrite(httpExchange, "Эпика с Id " + idEpic + " нет в базе.", 404);
            }
        }
    }

    public void handlePostAddUpdateSubtask(HttpExchange httpExchange) throws IOException {
        String body = readText(httpExchange);
        if (body.isEmpty()) {
            outputStreamWrite(httpExchange, "Ничего не передано.", 400);
            return;
        }
        Subtask subtask = gson.fromJson(body, Subtask.class);
        Integer idSubTask = subtask.getId();
        if (idSubTask == null) {
            if (taskManager.getEpics().containsKey(subtask.getEpicID())) {
                taskManager.addSubtask(subtask);
                outputStreamWrite(httpExchange, "Создали новую подзадачу с Id " + subtask.getId(), 200);
            } else {
                outputStreamWrite(httpExchange, "Эпика с Id " + subtask.getEpicID() + " нет в базе.", 404);
            }
        } else {
            if (taskManager.getSubtasks().containsKey(idSubTask)) {
                taskManager.updateSubtask(subtask);
                outputStreamWrite(httpExchange, "Обновили подзадачу с Id " + idSubTask, 200);
            } else {
                outputStreamWrite(httpExchange, "Подзадачи с Id " + idSubTask + " нет в базе.", 404);
            }
        }
    }
    void outputStreamWrite(HttpExchange exchange, String response, int code) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    //походу ненужное
    private static File ourFile = new File("file.txt");
    private static FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(ourFile);

    public static void main(String[] args) throws IOException {
        //Сначала наполним ТасксМенеджер задачами

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




// Создадим http сервер




    }
}
