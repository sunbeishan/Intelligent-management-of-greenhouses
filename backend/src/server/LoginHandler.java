package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;
import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginHandler implements HttpHandler {
    private UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("POST".equals(method) && path.equals("/api/login")) {
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

        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            SimpleHttpServer.sendResponse(exchange, 401, "{\"success\": false, \"message\": \"用户名不存在\"}");
            return;
        }

        if ("禁用".equals(user.getStatus())) {
            SimpleHttpServer.sendResponse(exchange, 403, "{\"success\": false, \"message\": \"账号已被禁用\"}");
            return;
        }

        if (!password.equals(user.getPassword())) {
            SimpleHttpServer.sendResponse(exchange, 401, "{\"success\": false, \"message\": \"密码错误\"}");
            return;
        }

        String avatar = user.getAvatar() != null ? user.getAvatar() : "";
        String response = String.format("{\"success\": true, \"message\": \"登录成功\", \"user\": {\"id\": %d, \"username\": \"%s\", \"name\": \"%s\", \"role\": \"%s\", \"status\": \"%s\", \"avatar\": \"%s\"}}",
                user.getId(), user.getUsername(), user.getName(), user.getRole(), user.getStatus(), avatar);
        SimpleHttpServer.sendResponse(exchange, 200, response);
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
}