package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.RoleDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import model.Role;

public class RoleHandler implements HttpHandler {
    private RoleDAO roleDAO = new RoleDAO();

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
                    if (path.matches("/api/roles/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[3]);
                        Role role = roleDAO.getRoleById(id);
                        if (role != null) {
                            sendResponse(exchange, 200, roleToJson(role));
                        } else {
                            sendResponse(exchange, 404, "{\"message\": \"Role not found\"}");
                        }
                    } else {
                        List<Role> roles = roleDAO.getAllRoles();
                        sendResponse(exchange, 200, rolesToJson(roles));
                    }
                    break;
                case "POST":
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes());
                    Role newRole = parseRole(body);
                    if (roleDAO.addRole(newRole)) {
                        sendResponse(exchange, 201, "{\"message\": \"Role added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"message\": \"Failed to add role\"}");
                    }
                    break;
                case "PUT":
                    if (path.matches("/api/roles/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[3]);
                        InputStream putIs = exchange.getRequestBody();
                        String putBody = new String(putIs.readAllBytes());
                        Role updateRole = parseRole(putBody);
                        updateRole.setId(id);
                        if (roleDAO.updateRole(updateRole)) {
                            sendResponse(exchange, 200, "{\"message\": \"Role updated successfully\"}");
                        } else {
                            sendResponse(exchange, 500, "{\"message\": \"Failed to update role\"}");
                        }
                    }
                    break;
                case "DELETE":
                    if (path.matches("/api/roles/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[3]);
                        if (roleDAO.deleteRole(id)) {
                            sendResponse(exchange, 200, "{\"message\": \"Role deleted successfully\"}");
                        } else {
                            sendResponse(exchange, 500, "{\"message\": \"Failed to delete role\"}");
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

    private String roleToJson(Role role) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(role.getId()).append(",");
        sb.append("\"name\":\"").append(escapeJson(role.getName())).append("\",");
        sb.append("\"description\":\"").append(escapeJson(role.getDescription())).append("\",");
        sb.append("\"createTime\":\"").append(role.getCreateTime()).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String rolesToJson(List<Role> roles) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < roles.size(); i++) {
            sb.append(roleToJson(roles.get(i)));
            if (i < roles.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private Role parseRole(String json) {
        Role role = new Role();
        role.setName(extractString(json, "name"));
        role.setDescription(extractString(json, "description"));
        return role;
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
