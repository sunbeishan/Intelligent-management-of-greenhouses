package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.FarmlandDAO;
import dao.UserDAO;
import dao.UserFarmlandDAO;
import model.Farmland;
import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterHandler implements HttpHandler {
    private UserDAO userDAO = new UserDAO();
    private UserFarmlandDAO userFarmlandDAO = new UserFarmlandDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("POST".equals(method) && path.equals("/api/register")) {
            handleRegister(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);
        
        String username = extractValue(body, "username");
        String password = extractValue(body, "password");
        String name = extractValue(body, "name");
        String farmlandIdsStr = extractValue(body, "farmlandIds");

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

        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            SimpleHttpServer.sendResponse(exchange, 409, "{\"success\": false, \"message\": \"用户名已存在\"}");
            return;
        }

        if (farmlandIdsStr != null && !farmlandIdsStr.isEmpty()) {
            String[] farmlandIdArray = farmlandIdsStr.replace("[", "").replace("]", "").split(",");
            for (String idStr : farmlandIdArray) {
                try {
                    int farmlandId = Integer.parseInt(idStr.trim());
                    if (userFarmlandDAO.getFarmlandUserCount(farmlandId) >= 2) {
                        String farmlandName = getFarmlandName(farmlandId);
                        SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田 '" + farmlandName + "' 已达到最大管理人数限制（2人）\"}");
                        return;
                    }
                } catch (NumberFormatException e) {
                    // 忽略无效ID
                }
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setRole("普通用户");
        user.setStatus("待审批");
        
        if (farmlandIdsStr != null && !farmlandIdsStr.isEmpty()) {
            String[] farmlandIdArray = farmlandIdsStr.replace("[", "").replace("]", "").split(",");
            StringBuilder farmlandsSb = new StringBuilder();
            FarmlandDAO farmlandDAO = new FarmlandDAO();
            List<Farmland> allFarmlands = farmlandDAO.getAllFarmlands();
            
            for (int i = 0; i < farmlandIdArray.length; i++) {
                try {
                    int farmlandId = Integer.parseInt(farmlandIdArray[i].trim());
                    String farmlandName = getFarmlandNameById(allFarmlands, farmlandId);
                    if (farmlandName != null) {
                        if (i > 0) {
                            farmlandsSb.append(",");
                        }
                        farmlandsSb.append(farmlandName);
                    }
                } catch (NumberFormatException e) {
                    // 忽略无效ID
                }
            }
            user.setFarmlands(farmlandsSb.toString());
        }

        if (userDAO.addUser(user)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"注册成功，请等待管理员审批\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"注册失败，请重试\"}");
        }
    }

    private String getFarmlandName(int farmlandId) {
        FarmlandDAO farmlandDAO = new FarmlandDAO();
        List<Farmland> farmlands = farmlandDAO.getAllFarmlands();
        return getFarmlandNameById(farmlands, farmlandId);
    }
    
    private String getFarmlandNameById(List<Farmland> farmlands, int farmlandId) {
        for (Farmland f : farmlands) {
            if (f.getId() == farmlandId) {
                return f.getName();
            }
        }
        return null;
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