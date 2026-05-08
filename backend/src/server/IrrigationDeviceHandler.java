package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.IrrigationDeviceDAO;
import model.IrrigationDevice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IrrigationDeviceHandler implements HttpHandler {
    private IrrigationDeviceDAO dao = new IrrigationDeviceDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/irrigation/devices")) {
            handleGet(exchange);
        } else if ("GET".equals(method) && path.matches("/api/irrigation/devices/\\d+")) {
            handleGetById(exchange);
        } else if ("POST".equals(method) && path.equals("/api/irrigation/devices")) {
            handlePost(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/irrigation/devices/\\d+")) {
            handlePut(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/irrigation/devices/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String farmlandIdStr = extractQueryParam(query, "farmlandId");

        List<IrrigationDevice> devices;
        if (farmlandIdStr != null && !farmlandIdStr.isEmpty()) {
            try {
                int farmlandId = Integer.parseInt(farmlandIdStr);
                devices = dao.getDevicesByFarmland(farmlandId);
            } catch (NumberFormatException e) {
                devices = dao.getAllDevices();
            }
        } else {
            devices = dao.getAllDevices();
        }

        String json = toJsonList(devices);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        IrrigationDevice device = dao.getDeviceById(id);
        if (device != null) {
            String json = toJson(device);
            SimpleHttpServer.sendResponse(exchange, 200, json);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Device not found\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String name = extractValue(body, "name");
        String farmlandIdStr = extractValue(body, "farmlandId");
        String deviceType = extractValue(body, "deviceType");
        String status = extractValue(body, "status");
        String waterFlowStr = extractValue(body, "waterFlow");
        String coverageAreaStr = extractValue(body, "coverageArea");
        String installDate = extractValue(body, "installDate");

        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"设备名称不能为空\"}");
            return;
        }

        if (farmlandIdStr == null || farmlandIdStr.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"请选择所属农田\"}");
            return;
        }

        int farmlandId;
        try {
            farmlandId = Integer.parseInt(farmlandIdStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田ID无效\"}");
            return;
        }

        double waterFlow = 0;
        double coverageArea = 0;
        try {
            if (waterFlowStr != null && !waterFlowStr.isEmpty()) {
                waterFlow = Double.parseDouble(waterFlowStr);
            }
            if (coverageAreaStr != null && !coverageAreaStr.isEmpty()) {
                coverageArea = Double.parseDouble(coverageAreaStr);
            }
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"数值格式错误\"}");
            return;
        }

        Date installDateVal = null;
        if (installDate != null && !installDate.isEmpty()) {
            try {
                installDateVal = Date.valueOf(installDate);
            } catch (IllegalArgumentException e) {
                installDateVal = new Date(System.currentTimeMillis());
            }
        }

        IrrigationDevice device = new IrrigationDevice();
        device.setName(name);
        device.setFarmlandId(farmlandId);
        device.setDeviceType(deviceType);
        device.setStatus(status != null ? status : "正常");
        device.setWaterFlow(waterFlow);
        device.setCoverageArea(coverageArea);
        device.setInstallDate(installDateVal);

        if (dao.addDevice(device)) {
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
        String farmlandIdStr = extractValue(body, "farmlandId");
        String deviceType = extractValue(body, "deviceType");
        String status = extractValue(body, "status");
        String waterFlowStr = extractValue(body, "waterFlow");
        String coverageAreaStr = extractValue(body, "coverageArea");
        String installDate = extractValue(body, "installDate");

        if (name == null || name.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"设备名称不能为空\"}");
            return;
        }

        int farmlandId;
        try {
            farmlandId = Integer.parseInt(farmlandIdStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"农田ID无效\"}");
            return;
        }

        double waterFlow = 0;
        double coverageArea = 0;
        try {
            if (waterFlowStr != null && !waterFlowStr.isEmpty()) {
                waterFlow = Double.parseDouble(waterFlowStr);
            }
            if (coverageAreaStr != null && !coverageAreaStr.isEmpty()) {
                coverageArea = Double.parseDouble(coverageAreaStr);
            }
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"数值格式错误\"}");
            return;
        }

        Date installDateVal = null;
        if (installDate != null && !installDate.isEmpty()) {
            try {
                installDateVal = Date.valueOf(installDate);
            } catch (IllegalArgumentException e) {
                installDateVal = new Date(System.currentTimeMillis());
            }
        }

        IrrigationDevice device = new IrrigationDevice();
        device.setId(id);
        device.setName(name);
        device.setFarmlandId(farmlandId);
        device.setDeviceType(deviceType);
        device.setStatus(status != null ? status : "正常");
        device.setWaterFlow(waterFlow);
        device.setCoverageArea(coverageArea);
        device.setInstallDate(installDateVal);

        if (dao.updateDevice(device)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deleteDevice(id)) {
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

    private String toJsonList(List<IrrigationDevice> devices) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < devices.size(); i++) {
            sb.append(toJson(devices.get(i)));
            if (i < devices.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(IrrigationDevice d) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"farmlandId\":%d,\"farmlandName\":\"%s\",\"deviceType\":\"%s\",\"status\":\"%s\",\"waterFlow\":%.2f,\"coverageArea\":%.2f,\"installDate\":\"%s\",\"createTime\":\"%s\",\"updateTime\":\"%s\"}",
            d.getId(), escapeJson(d.getName()), d.getFarmlandId(), escapeJson(d.getFarmlandName()),
            escapeJson(d.getDeviceType()), escapeJson(d.getStatus()), d.getWaterFlow(),
            d.getCoverageArea(), d.getInstallDate() != null ? d.getInstallDate().toString() : "",
            d.getCreateTime() != null ? d.getCreateTime().toString() : "",
            d.getUpdateTime() != null ? d.getUpdateTime().toString() : ""
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}