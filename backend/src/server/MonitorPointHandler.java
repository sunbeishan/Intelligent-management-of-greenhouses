package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.MonitorPointDAO;
import model.MonitorPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonitorPointHandler implements HttpHandler {
    private MonitorPointDAO dao = new MonitorPointDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/monitor-points")) {
            handleGet(exchange);
        } else if ("GET".equals(method) && path.matches("/api/monitor-points/\\d+")) {
            handleGetById(exchange);
        } else if ("POST".equals(method) && path.equals("/api/monitor-points")) {
            handlePost(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/monitor-points/\\d+")) {
            handlePut(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/monitor-points/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String farmlandIdStr = extractQueryParam(query, "farmlandId");

        List<MonitorPoint> points;
        if (farmlandIdStr != null && !farmlandIdStr.isEmpty()) {
            try {
                int farmlandId = Integer.parseInt(farmlandIdStr);
                points = dao.getMonitorPointsByFarmlandId(farmlandId);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田ID必须是数字\"}");
                return;
            }
        } else {
            points = dao.getAllMonitorPoints();
        }

        String json = toJsonList(points);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        MonitorPoint point = dao.getMonitorPointById(id);
        if (point != null) {
            String json = toJson(point);
            SimpleHttpServer.sendResponse(exchange, 200, json);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"success\": false, \"message\": \"监测点不存在\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String name = extractValue(body, "name");
        String location = extractValue(body, "location");
        String farmlandIdStr = extractValue(body, "farmlandId");
        String status = extractValue(body, "status");
        String temperatureStr = extractValue(body, "temperature");
        String humidityStr = extractValue(body, "humidity");
        String lightStr = extractValue(body, "light");
        String co2Str = extractValue(body, "co2");

        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"监测点名称不能为空\"}");
            return;
        }

        int farmlandId = 0;
        try {
            farmlandId = Integer.parseInt(farmlandIdStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田ID必须是数字\"}");
            return;
        }

        double temperature = 25.0;
        if (temperatureStr != null && !temperatureStr.isEmpty()) {
            try {
                temperature = Double.parseDouble(temperatureStr);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"温度必须是数字\"}");
                return;
            }
        }

        int humidity = 65;
        if (humidityStr != null && !humidityStr.isEmpty()) {
            try {
                humidity = Integer.parseInt(humidityStr);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"湿度必须是数字\"}");
                return;
            }
        }

        int light = 8000;
        if (lightStr != null && !lightStr.isEmpty()) {
            try {
                light = Integer.parseInt(lightStr);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"光照必须是数字\"}");
                return;
            }
        }

        int co2 = 450;
        if (co2Str != null && !co2Str.isEmpty()) {
            try {
                co2 = Integer.parseInt(co2Str);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"CO2必须是数字\"}");
                return;
            }
        }

        MonitorPoint point = new MonitorPoint();
        point.setName(name);
        point.setLocation(location != null ? location : "");
        point.setFarmlandId(farmlandId);
        point.setStatus(status != null ? status : "正常");
        point.setTemperature(temperature);
        point.setHumidity(humidity);
        point.setLight(light);
        point.setCo2(co2);

        if (dao.addMonitorPoint(point)) {
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
        String location = extractValue(body, "location");
        String farmlandIdStr = extractValue(body, "farmlandId");
        String status = extractValue(body, "status");
        String temperatureStr = extractValue(body, "temperature");
        String humidityStr = extractValue(body, "humidity");
        String lightStr = extractValue(body, "light");
        String co2Str = extractValue(body, "co2");

        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"监测点名称不能为空\"}");
            return;
        }

        int farmlandId = 0;
        try {
            farmlandId = Integer.parseInt(farmlandIdStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田ID必须是数字\"}");
            return;
        }

        double temperature = 25.0;
        if (temperatureStr != null && !temperatureStr.isEmpty()) {
            try {
                temperature = Double.parseDouble(temperatureStr);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"温度必须是数字\"}");
                return;
            }
        }

        int humidity = 65;
        if (humidityStr != null && !humidityStr.isEmpty()) {
            try {
                humidity = Integer.parseInt(humidityStr);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"湿度必须是数字\"}");
                return;
            }
        }

        int light = 8000;
        if (lightStr != null && !lightStr.isEmpty()) {
            try {
                light = Integer.parseInt(lightStr);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"光照必须是数字\"}");
                return;
            }
        }

        int co2 = 450;
        if (co2Str != null && !co2Str.isEmpty()) {
            try {
                co2 = Integer.parseInt(co2Str);
            } catch (NumberFormatException e) {
                SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"CO2必须是数字\"}");
                return;
            }
        }

        MonitorPoint point = new MonitorPoint();
        point.setId(id);
        point.setName(name);
        point.setLocation(location != null ? location : "");
        point.setFarmlandId(farmlandId);
        point.setStatus(status != null ? status : "正常");
        point.setTemperature(temperature);
        point.setHumidity(humidity);
        point.setLight(light);
        point.setCo2(co2);

        if (dao.updateMonitorPoint(point)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deleteMonitorPoint(id)) {
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

    private String toJsonList(List<MonitorPoint> points) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < points.size(); i++) {
            MonitorPoint mp = points.get(i);
            sb.append(toJson(mp));
            if (i < points.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(MonitorPoint mp) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"location\":\"%s\",\"farmlandId\":%d,\"status\":\"%s\",\"temperature\":%.1f,\"humidity\":%d,\"light\":%d,\"co2\":%d,\"lastUpdate\":\"%s\",\"createTime\":\"%s\"}",
            mp.getId(), escapeJson(mp.getName()), escapeJson(mp.getLocation()), mp.getFarmlandId(),
            escapeJson(mp.getStatus()), mp.getTemperature(), mp.getHumidity(), mp.getLight(), mp.getCo2(),
            mp.getLastUpdate() != null ? mp.getLastUpdate().toString() : "",
            mp.getCreateTime() != null ? mp.getCreateTime().toString() : ""
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}