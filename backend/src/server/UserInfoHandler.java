package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;
import model.User;

import java.io.IOException;

public class UserInfoHandler implements HttpHandler {
    private UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                handleGet(exchange);
            } else {
                SimpleHttpServer.sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            SimpleHttpServer.sendResponse(exchange, 500, "{\"message\": \"Internal server error\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String username = null;
        
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && "username".equals(keyValue[0])) {
                    username = keyValue[1];
                    break;
                }
            }
        }

        if (username == null || username.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"message\": \"Username is required\"}");
            return;
        }

        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"message\": \"User not found\"}");
            return;
        }

        String farmlandsJson = "[]";
        if (user.getFarmlands() != null && !user.getFarmlands().isEmpty()) {
            String[] farmlandNames = user.getFarmlands().split(",");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < farmlandNames.length; i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escapeJson(farmlandNames[i].trim())).append("\"");
            }
            json.append("]");
            farmlandsJson = json.toString();
        }

        String response = String.format(
            "{\"id\":%d,\"username\":\"%s\",\"name\":\"%s\",\"role\":\"%s\",\"status\":\"%s\",\"farmlands\":%s}",
            user.getId(),
            escapeJson(user.getUsername()),
            escapeJson(user.getName()),
            escapeJson(user.getRole()),
            escapeJson(user.getStatus()),
            farmlandsJson
        );

        SimpleHttpServer.sendResponse(exchange, 200, response);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
