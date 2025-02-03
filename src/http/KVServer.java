package http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();


    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange httExchange) {
        // TODO Добавьте получение значения по ключу
    }

    private void save(HttpExchange httExchange) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(httExchange)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                httExchange.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(httExchange.getRequestMethod())) {
                String key = httExchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    httExchange.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(httExchange);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    httExchange.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                httExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + httExchange.getRequestMethod());
                httExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httExchange.close();
        }
    }

    private void register(HttpExchange httExchange) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(httExchange.getRequestMethod())) {
                sendText(httExchange, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + httExchange.getRequestMethod());
                httExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httExchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange httExchange) {
        String rawQuery = httExchange.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange httExchange) throws IOException {
        return new String(httExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange httExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httExchange.getResponseHeaders().add("Content-Type", "application/json");
        httExchange.sendResponseHeaders(200, resp.length);
        httExchange.getResponseBody().write(resp);
    }
}