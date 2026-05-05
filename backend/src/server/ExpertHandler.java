package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ExpertDAO;
import model.Expert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpertHandler implements HttpHandler {
    private ExpertDAO dao = new ExpertDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/experts")) {
            handleGetAll(exchange);
        } else if ("GET".equals(method) && path.matches("/api/experts/\\d+")) {
            handleGetById(exchange);
        } else if ("POST".equals(method) && path.equals("/api/experts")) {
            handlePost(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/experts/\\d+")) {
            handlePut(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/experts/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Expert> experts = dao.getAllExperts();
        String json = toJsonList(experts);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        Expert expert = dao.getExpertById(id);
        
        if (expert != null) {
            String json = toJson(expert);
            SimpleHttpServer.sendResponse(exchange, 200, json);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Expert not found\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String username = extractValue(body, "username");
        String password = extractValue(body, "password");
        String name = extractValue(body, "name");
        String phone = extractValue(body, "phone");
        String email = extractValue(body, "email");
        String specialty = extractValue(body, "specialty");
        String status = extractValue(body, "status");
        String avatar = extractValue(body, "avatar");

        if (username == null || username.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"用户名不能为空\"}");
            return;
        }
        if (password == null || password.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"密码不能为空\"}");
            return;
        }
        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"姓名不能为空\"}");
            return;
        }

        Expert expert = new Expert();
        expert.setUsername(username);
        expert.setPassword(password);
        expert.setName(name);
        expert.setPhone(phone != null ? phone : "");
        expert.setEmail(email != null ? email : "");
        expert.setSpecialty(specialty != null ? specialty : "");
        expert.setStatus(status != null ? status : "启用");
        expert.setAvatar(avatar != null ? avatar : "");

        if (dao.addExpert(expert)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"添加成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"添加失败\"}");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        String body = SimpleHttpServer.readRequestBody(exchange);

        String username = extractValue(body, "username");
        String password = extractValue(body, "password");
        String name = extractValue(body, "name");
        String phone = extractValue(body, "phone");
        String email = extractValue(body, "email");
        String specialty = extractValue(body, "specialty");
        String status = extractValue(body, "status");
        String avatar = extractValue(body, "avatar");

        if (username == null || username.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"用户名不能为空\"}");
            return;
        }
        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"姓名不能为空\"}");
            return;
        }

        Expert expert = new Expert();
        expert.setId(id);
        expert.setUsername(username);
        expert.setPassword(password);
        expert.setName(name);
        expert.setPhone(phone != null ? phone : "");
        expert.setEmail(email != null ? email : "");
        expert.setSpecialty(specialty != null ? specialty : "");
        expert.setStatus(status != null ? status : "启用");
        expert.setAvatar(avatar != null ? avatar : "");

        if (dao.updateExpert(expert)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deleteExpert(id)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"删除成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"删除失败\"}");
        }
    }

    private String extractValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([^,}]+)");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private String toJsonList(List<Expert> experts) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < experts.size(); i++) {
            sb.append(toJson(experts.get(i)));
            if (i < experts.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Expert e) {
        return String.format(
            "{\"id\":%d,\"username\":\"%s\",\"name\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\",\"specialty\":\"%s\",\"status\":\"%s\",\"avatar\":\"%s\",\"createTime\":\"%s\",\"updateTime\":\"%s\"}",
            e.getId(), escapeJson(e.getUsername()), escapeJson(e.getName()),
            escapeJson(e.getPhone()), escapeJson(e.getEmail()), escapeJson(e.getSpecialty()),
            escapeJson(e.getStatus()), escapeJson(e.getAvatar()),
            e.getCreateTime() != null ? e.getCreateTime().toString() : "",
            e.getUpdateTime() != null ? e.getUpdateTime().toString() : ""
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}