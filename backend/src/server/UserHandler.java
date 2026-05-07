package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import model.User;

public class UserHandler implements HttpHandler {
    private UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            switch (method) {
                case "GET":
                    if (path.matches("/api/users/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[3]);
                        User user = userDAO.getUserById(id);
                        if (user != null) {
                            sendResponse(exchange, 200, userToJson(user));
                        } else {
                            sendResponse(exchange, 404, "{\"message\": \"User not found\"}");
                        }
                    } else {
                        List<User> users = userDAO.getAllUsers();
                        sendResponse(exchange, 200, usersToJson(users));
                    }
                    break;
                case "POST":
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes());
                    User newUser = parseUser(body);
                    if (userDAO.addUser(newUser)) {
                        sendResponse(exchange, 201, "{\"message\": \"User added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"message\": \"Failed to add user\"}");
                    }
                    break;
                case "PUT":
                    if (path.matches("/api/users/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[3]);
                        InputStream putIs = exchange.getRequestBody();
                        String putBody = new String(putIs.readAllBytes());
                        User updateUser = parseUser(putBody);
                        updateUser.setId(id);
                        if (userDAO.updateUser(updateUser)) {
                            sendResponse(exchange, 200, "{\"message\": \"User updated successfully\"}");
                        } else {
                            sendResponse(exchange, 500, "{\"message\": \"Failed to update user\"}");
                        }
                    }
                    break;
                case "DELETE":
                    if (path.matches("/api/users/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[3]);
                        if (userDAO.deleteUser(id)) {
                            sendResponse(exchange, 200, "{\"message\": \"User deleted successfully\"}");
                        } else {
                            sendResponse(exchange, 500, "{\"message\": \"Failed to delete user\"}");
                        }
                    }
                    break;
                default:
                    sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"message\": \"Internal server error\"}");
        }
    }

    private String userToJson(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(user.getId()).append(",");
        sb.append("\"username\":\"").append(escapeJson(user.getUsername())).append("\",");
        sb.append("\"password\":\"").append(escapeJson(user.getPassword())).append("\",");
        sb.append("\"name\":\"").append(escapeJson(user.getName())).append("\",");
        sb.append("\"role\":\"").append(escapeJson(user.getRole())).append("\",");
        sb.append("\"status\":\"").append(escapeJson(user.getStatus())).append("\",");
        sb.append("\"avatar\":\"").append(escapeJson(user.getAvatar())).append("\",");
        sb.append("\"createTime\":\"").append(user.getCreateTime()).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String usersToJson(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < users.size(); i++) {
            sb.append(userToJson(users.get(i)));
            if (i < users.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private User parseUser(String json) {
        User user = new User();
        user.setUsername(extractString(json, "username"));
        user.setPassword(extractString(json, "password"));
        user.setName(extractString(json, "name"));
        user.setRole(extractString(json, "role"));
        user.setStatus(extractString(json, "status"));
        user.setAvatar(extractString(json, "avatar"));
        return user;
    }

    private String extractString(String json, String key) {
        int start = json.indexOf("\"" + key + "\":");
        if (start == -1) return "";
        start = json.indexOf("\"", start + key.length() + 2) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
