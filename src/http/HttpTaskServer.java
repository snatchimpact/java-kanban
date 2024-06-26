package http;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import management.FileBackedTaskManager;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .serializeNulls()
            .create();;
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

        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");


    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(String.valueOf(exchange.getRequestURI()), exchange.getRequestMethod());

            switch (endpoint) {
                case UNKNOWN:{
                    System.out.println("В handle пришел кейс UNKNOWN");
                    break;
                }

                case GET_ALL_TASKS: {
                    handleGetAllTasks(exchange);
                    break;
                }
                case GET_ALL_EPICS: {
                    handleGetAllEpics(exchange);
                    break;
                }

                case GET_ALL_SUBTASKS: {
                    handleGetAllSubtasks(exchange);
                    break;
                }
                case GET_TASK_BY_ID:{
                    handleGetTaskByID(exchange);
                    break;
                }
                case GET_HISTORY: {
                    handleGetHistory(exchange);
                    break;
                }
                case POST_TASK_BY_BODY:{
                    handlePostTaskByBody(exchange);
                    break;
                }
                case POST_EPIC_BY_BODY:{
                    handlePostEpicByBody(exchange);
                    break;
                }
                case POST_SUBTASK_BY_BODY:{
                    handlePostSubtaskByBody(exchange);
                    break;
                }


                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }


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

}
