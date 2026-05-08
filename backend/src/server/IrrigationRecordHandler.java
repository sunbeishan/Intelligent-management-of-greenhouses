package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ActivityDao;
import dao.IrrigationRecordDAO;
import model.IrrigationRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IrrigationRecordHandler implements HttpHandler {
    private IrrigationRecordDAO dao = new IrrigationRecordDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/irrigation/records")) {
            handleGet(exchange);
        } else if ("GET".equals(method) && path.matches("/api/irrigation/records/\\d+")) {
            handleGetById(exchange);
        } else if ("GET".equals(method) && path.equals("/api/irrigation/records/statistics")) {
            handleGetStatistics(exchange);
        } else if ("POST".equals(method) && path.equals("/api/irrigation/records")) {
            handlePost(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/irrigation/records/\\d+")) {
            handlePut(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/irrigation/records/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String farmlandIdStr = extractQueryParam(query, "farmlandId");
        String startDate = extractQueryParam(query, "startDate");
        String endDate = extractQueryParam(query, "endDate");

        List<IrrigationRecord> records;
        if ((startDate != null && !startDate.isEmpty()) && (endDate != null && !endDate.isEmpty())) {
            records = dao.getRecordsByDateRange(startDate, endDate);
        } else if (farmlandIdStr != null && !farmlandIdStr.isEmpty()) {
            try {
                int farmlandId = Integer.parseInt(farmlandIdStr);
                records = dao.getRecordsByFarmland(farmlandId);
            } catch (NumberFormatException e) {
                records = dao.getAllRecords();
            }
        } else {
            records = dao.getAllRecords();
        }

        String json = toJsonList(records);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        IrrigationRecord record = dao.getRecordById(id);
        if (record != null) {
            String json = toJson(record);
            SimpleHttpServer.sendResponse(exchange, 200, json);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Record not found\"}");
        }
    }

    private void handleGetStatistics(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String startDate = extractQueryParam(query, "startDate");
        String endDate = extractQueryParam(query, "endDate");

        if (startDate == null || startDate.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDate = sdf.format(new Date());
        }
        if (endDate == null || endDate.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            endDate = sdf.format(new Date());
        }

        double totalWater = dao.getTotalWaterUsage(startDate, endDate);
        String json = String.format("{\"totalWater\": %.2f, \"startDate\": \"%s\", \"endDate\": \"%s\"}", totalWater, startDate, endDate);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String farmlandIdStr = extractValue(body, "farmlandId");
        String deviceIdStr = extractValue(body, "deviceId");
        String durationStr = extractValue(body, "duration");
        String waterAmountStr = extractValue(body, "waterAmount");
        String type = extractValue(body, "type");
        String operator = extractValue(body, "operator");
        String remark = extractValue(body, "remark");

        if (farmlandIdStr == null || farmlandIdStr.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"请选择农田\"}");
            return;
        }

        if (deviceIdStr == null || deviceIdStr.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"请选择设备\"}");
            return;
        }

        int farmlandId, deviceId, duration;
        double waterAmount;
        try {
            farmlandId = Integer.parseInt(farmlandIdStr);
            deviceId = Integer.parseInt(deviceIdStr);
            duration = Integer.parseInt(durationStr);
            waterAmount = Double.parseDouble(waterAmountStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"数值格式错误\"}");
            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + duration * 60 * 1000L);

        IrrigationRecord record = new IrrigationRecord();
        record.setFarmlandId(farmlandId);
        record.setDeviceId(deviceId);
        record.setPlanId(0);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setDuration(duration);
        record.setWaterAmount(waterAmount);
        record.setType(type != null ? type : "手动");
        record.setStatus("已完成");
        record.setOperator(operator != null ? operator : "");
        record.setRemark(remark != null ? remark : "");

        if (dao.addRecord(record)) {
            String activityContent = "手动灌溉完成: 水量" + waterAmount + "升, 时长" + duration + "分钟";
            ActivityDao.recordActivity("农田管理", activityContent);
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"灌溉记录已创建\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"创建失败\"}");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        String body = SimpleHttpServer.readRequestBody(exchange);

        String farmlandIdStr = extractValue(body, "farmlandId");
        String deviceIdStr = extractValue(body, "deviceId");
        String durationStr = extractValue(body, "duration");
        String waterAmountStr = extractValue(body, "waterAmount");
        String type = extractValue(body, "type");
        String status = extractValue(body, "status");
        String operator = extractValue(body, "operator");
        String remark = extractValue(body, "remark");

        int farmlandId, deviceId, duration;
        double waterAmount;
        try {
            farmlandId = Integer.parseInt(farmlandIdStr);
            deviceId = Integer.parseInt(deviceIdStr);
            duration = Integer.parseInt(durationStr);
            waterAmount = Double.parseDouble(waterAmountStr);
        } catch (NumberFormatException e) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"success\": false, \"message\": \"数值格式错误\"}");
            return;
        }

        IrrigationRecord record = new IrrigationRecord();
        record.setId(id);
        record.setFarmlandId(farmlandId);
        record.setDeviceId(deviceId);
        record.setPlanId(0);
        record.setStartTime(new Timestamp(System.currentTimeMillis()));
        record.setEndTime(new Timestamp(System.currentTimeMillis() + duration * 60 * 1000L));
        record.setDuration(duration);
        record.setWaterAmount(waterAmount);
        record.setType(type != null ? type : "手动");
        record.setStatus(status != null ? status : "已完成");
        record.setOperator(operator != null ? operator : "");
        record.setRemark(remark != null ? remark : "");

        if (dao.updateRecord(record)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deleteRecord(id)) {
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

    private String toJsonList(List<IrrigationRecord> records) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < records.size(); i++) {
            sb.append(toJson(records.get(i)));
            if (i < records.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(IrrigationRecord r) {
        return String.format(
            "{\"id\":%d,\"farmlandId\":%d,\"farmlandName\":\"%s\",\"deviceId\":%d,\"deviceName\":\"%s\",\"planId\":%d,\"planName\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\",\"duration\":%d,\"waterAmount\":%.2f,\"type\":\"%s\",\"status\":\"%s\",\"operator\":\"%s\",\"remark\":\"%s\",\"createTime\":\"%s\"}",
            r.getId(), r.getFarmlandId(), escapeJson(r.getFarmlandName()), r.getDeviceId(),
            escapeJson(r.getDeviceName()), r.getPlanId(), escapeJson(r.getPlanName()),
            r.getStartTime() != null ? r.getStartTime().toString() : "",
            r.getEndTime() != null ? r.getEndTime().toString() : "",
            r.getDuration(), r.getWaterAmount(), escapeJson(r.getType()),
            escapeJson(r.getStatus()), escapeJson(r.getOperator()), escapeJson(r.getRemark()),
            r.getCreateTime() != null ? r.getCreateTime().toString() : ""
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}