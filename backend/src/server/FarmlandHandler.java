package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.FarmlandDAO;
import dao.UserFarmlandDAO;
import model.Farmland;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FarmlandHandler implements HttpHandler {
    private FarmlandDAO dao = new FarmlandDAO();
    private UserFarmlandDAO userFarmlandDAO = new UserFarmlandDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/farmland")) {
            handleGet(exchange);
        } else if ("GET".equals(method) && path.equals("/api/farmland/total-area")) {
            handleGetTotalArea(exchange);
        } else if ("POST".equals(method) && path.equals("/api/farmland")) {
            handlePost(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/farmland/\\d+")) {
            handlePut(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/farmland/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String name = extractQueryParam(query, "name");
        String area = extractQueryParam(query, "area");

        List<Farmland> farmlands;
        if ((name != null && !name.isEmpty()) || (area != null && !area.isEmpty())) {
            farmlands = dao.searchFarmlands(name, area);
        } else {
            farmlands = dao.getAllFarmlands();
        }

        String json = toJsonList(farmlands);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handleGetTotalArea(HttpExchange exchange) throws IOException {
        double totalArea = dao.getTotalArea();
        String json = String.format("{\"totalArea\": %.2f}", totalArea);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String name = extractValue(body, "name");
        String area = extractValue(body, "area");
        String acreageStr = extractValue(body, "acreage");
        String soilType = extractValue(body, "soilType");
        String crop = extractValue(body, "crop");
        String status = extractValue(body, "status");

        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田名称不能为空\"}");
            return;
        }

        double acreage = 0;
        try {
            acreage = Double.parseDouble(acreageStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"面积必须是数字\"}");
            return;
        }

        Farmland farmland = new Farmland();
        farmland.setName(name);
        farmland.setArea(area != null ? area : "");
        farmland.setAcreage(acreage);
        farmland.setSoilType(soilType != null ? soilType : "");
        farmland.setCrop(crop != null ? crop : "");
        farmland.setStatus(status != null ? status : "种植中");

        if (dao.addFarmland(farmland)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"添加成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"添加失败\"}");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        String body = SimpleHttpServer.readRequestBody(exchange);

        String name = extractValue(body, "name");
        String area = extractValue(body, "area");
        String acreageStr = extractValue(body, "acreage");
        String soilType = extractValue(body, "soilType");
        String crop = extractValue(body, "crop");
        String status = extractValue(body, "status");

        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田名称不能为空\"}");
            return;
        }

        double acreage = 0;
        try {
            acreage = Double.parseDouble(acreageStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"面积必须是数字\"}");
            return;
        }

        Farmland farmland = new Farmland();
        farmland.setId(id);
        farmland.setName(name);
        farmland.setArea(area != null ? area : "");
        farmland.setAcreage(acreage);
        farmland.setSoilType(soilType != null ? soilType : "");
        farmland.setCrop(crop != null ? crop : "");
        farmland.setStatus(status != null ? status : "种植中");

        if (dao.updateFarmland(farmland)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deleteFarmland(id)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"删除成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"删除失败\"}");
        }
    }

    private String extractQueryParam(String query, String key) {
        if (query == null) return null;
        Pattern pattern = Pattern.compile(key + "=([^&]+)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return java.net.URLDecoder.decode(matcher.group(1), StandardCharsets.UTF_8);
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

    private String toJsonList(List<Farmland> farmlands) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < farmlands.size(); i++) {
            Farmland f = farmlands.get(i);
            int userCount = userFarmlandDAO.getFarmlandUserCount(f.getId());
            sb.append(String.format(
                "{\"id\":%d,\"name\":\"%s\",\"area\":\"%s\",\"acreage\":%.2f,\"soilType\":\"%s\",\"crop\":\"%s\",\"status\":\"%s\",\"userCount\":%d,\"createTime\":\"%s\",\"updateTime\":\"%s\"}",
                f.getId(), escapeJson(f.getName()), escapeJson(f.getArea()), f.getAcreage(),
                escapeJson(f.getSoilType()), escapeJson(f.getCrop()), escapeJson(f.getStatus()),
                userCount,
                f.getCreateTime() != null ? f.getCreateTime().toString() : "",
                f.getUpdateTime() != null ? f.getUpdateTime().toString() : ""
            ));
            if (i < farmlands.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}