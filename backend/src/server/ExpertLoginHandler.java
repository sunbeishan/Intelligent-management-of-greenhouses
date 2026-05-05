package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ExpertDAO;
import model.Expert;

import java.io.IOException;

public class ExpertLoginHandler implements HttpHandler {
    private ExpertDAO dao = new ExpertDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()) && "/api/expert/login".equals(exchange.getRequestURI().getPath())) {
            handleLogin(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String username = extractValue(body, "username");
        String password = extractValue(body, "password");

        if (username == null || username.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"用户名不能为空\"}");
            return;
        }
        if (password == null || password.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"密码不能为空\"}");
            return;
        }

        Expert expert = dao.getExpertByUsername(username);

        if (expert == null) {
            SimpleHttpServer.sendResponse(exchange, 401, "{\"success\": false, \"message\": \"用户名不存在\"}");
            return;
        }

        if (!expert.getStatus().equals("启用")) {
            SimpleHttpServer.sendResponse(exchange, 401, "{\"success\": false, \"message\": \"账号已被禁用\"}");
            return;
        }

        if (!expert.getPassword().equals(password)) {
            SimpleHttpServer.sendResponse(exchange, 401, "{\"success\": false, \"message\": \"密码错误\"}");
            return;
        }

        String createTimeStr = expert.getCreateTime() != null ? expert.getCreateTime().toString() : "";
        String json = String.format(
            "{\"success\": true, \"message\": \"登录成功\", \"expert\": {\"id\":%d, \"username\":\"%s\", \"name\":\"%s\", \"phone\":\"%s\", \"email\":\"%s\", \"specialty\":\"%s\", \"status\":\"%s\", \"avatar\":\"%s\", \"createTime\":\"%s\"}}",
            expert.getId(), escapeJson(expert.getUsername()), escapeJson(expert.getName()),
            escapeJson(expert.getPhone()), escapeJson(expert.getEmail()), escapeJson(expert.getSpecialty()),
            escapeJson(expert.getStatus()), escapeJson(expert.getAvatar()), escapeJson(createTimeStr)
        );
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return "";
        int colon = json.indexOf(":", start);
        int quoteStart = json.indexOf("\"", colon + 1);
        int quoteEnd = json.indexOf("\"", quoteStart + 1);
        if (quoteStart == -1 || quoteEnd == -1) return "";
        return json.substring(quoteStart + 1, quoteEnd);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}