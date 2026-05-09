package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.IrrigationPlanDAO;
import model.IrrigationPlan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IrrigationPlanHandler implements HttpHandler {
    private IrrigationPlanDAO dao = new IrrigationPlanDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/irrigation/plans")) {
            handleGet(exchange);
        } else if ("GET".equals(method) && path.matches("/api/irrigation/plans/\\d+")) {
            handleGetById(exchange);
        } else if ("POST".equals(method) && path.equals("/api/irrigation/plans")) {
            handlePost(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/irrigation/plans/\\d+")) {
            handlePut(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/irrigation/plans/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String farmlandIdsParam = null;
        
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && "farmlandIds".equals(keyValue[0])) {
                    farmlandIdsParam = keyValue[1];
                    break;
                }
            }
        }

        List<IrrigationPlan> plans;
        if (farmlandIdsParam != null && !farmlandIdsParam.isEmpty()) {
            String[] idStrings = farmlandIdsParam.split(",");
            int[] farmlandIds = new int[idStrings.length];
            for (int i = 0; i < idStrings.length; i++) {
                try {
                    farmlandIds[i] = Integer.parseInt(idStrings[i].trim());
                } catch (NumberFormatException e) {
                    farmlandIds[i] = -1;
                }
            }
            plans = dao.getPlansByFarmlandIds(farmlandIds);
        } else {
            plans = dao.getAllPlans();
        }
        
        String json = toJsonList(plans);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        IrrigationPlan plan = dao.getPlanById(id);
        if (plan != null) {
            String json = toJson(plan);
            SimpleHttpServer.sendResponse(exchange, 200, json);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Plan not found\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String farmlandIdStr = extractValue(body, "farmlandId");
        String deviceIdStr = extractValue(body, "deviceId");
        String planName = extractValue(body, "planName");
        String startTime = extractValue(body, "startTime");
        String durationStr = extractValue(body, "duration");
        String waterAmountStr = extractValue(body, "waterAmount");
        String frequency = extractValue(body, "frequency");
        String weekDays = extractValue(body, "weekDays");
        String status = extractValue(body, "status");

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

        Time startTimeVal = null;
        if (startTime != null && !startTime.isEmpty()) {
            try {
                startTimeVal = Time.valueOf(startTime);
            } catch (IllegalArgumentException e) {
                startTimeVal = new Time(8 * 60 * 60 * 1000);
            }
        }

        IrrigationPlan plan = new IrrigationPlan();
        plan.setFarmlandId(farmlandId);
        plan.setDeviceId(deviceId);
        plan.setPlanName(planName);
        plan.setStartTime(startTimeVal);
        plan.setDuration(duration);
        plan.setWaterAmount(waterAmount);
        plan.setFrequency(frequency != null ? frequency : "每天");
        plan.setWeekDays(weekDays != null ? weekDays : "1,2,3,4,5,6,7");
        plan.setStatus(status != null ? status : "启用");

        if (dao.addPlan(plan)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"添加成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"添加失败\"}");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        String body = SimpleHttpServer.readRequestBody(exchange);

        String farmlandIdStr = extractValue(body, "farmlandId");
        String deviceIdStr = extractValue(body, "deviceId");
        String planName = extractValue(body, "planName");
        String startTime = extractValue(body, "startTime");
        String durationStr = extractValue(body, "duration");
        String waterAmountStr = extractValue(body, "waterAmount");
        String frequency = extractValue(body, "frequency");
        String weekDays = extractValue(body, "weekDays");
        String status = extractValue(body, "status");

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

        Time startTimeVal = null;
        if (startTime != null && !startTime.isEmpty()) {
            try {
                startTimeVal = Time.valueOf(startTime);
            } catch (IllegalArgumentException e) {
                startTimeVal = new Time(8 * 60 * 60 * 1000);
            }
        }

        IrrigationPlan plan = new IrrigationPlan();
        plan.setId(id);
        plan.setFarmlandId(farmlandId);
        plan.setDeviceId(deviceId);
        plan.setPlanName(planName);
        plan.setStartTime(startTimeVal);
        plan.setDuration(duration);
        plan.setWaterAmount(waterAmount);
        plan.setFrequency(frequency != null ? frequency : "每天");
        plan.setWeekDays(weekDays != null ? weekDays : "1,2,3,4,5,6,7");
        plan.setStatus(status != null ? status : "启用");

        if (dao.updatePlan(plan)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deletePlan(id)) {
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

    private String toJsonList(List<IrrigationPlan> plans) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < plans.size(); i++) {
            sb.append(toJson(plans.get(i)));
            if (i < plans.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(IrrigationPlan p) {
        return String.format(
            "{\"id\":%d,\"farmlandId\":%d,\"farmlandName\":\"%s\",\"deviceId\":%d,\"deviceName\":\"%s\",\"planName\":\"%s\",\"startTime\":\"%s\",\"duration\":%d,\"waterAmount\":%.2f,\"frequency\":\"%s\",\"weekDays\":\"%s\",\"status\":\"%s\",\"createTime\":\"%s\",\"updateTime\":\"%s\"}",
            p.getId(), p.getFarmlandId(), escapeJson(p.getFarmlandName()), p.getDeviceId(),
            escapeJson(p.getDeviceName()), escapeJson(p.getPlanName()),
            p.getStartTime() != null ? p.getStartTime().toString() : "",
            p.getDuration(), p.getWaterAmount(), escapeJson(p.getFrequency()),
            escapeJson(p.getWeekDays()), escapeJson(p.getStatus()),
            p.getCreateTime() != null ? p.getCreateTime().toString() : "",
            p.getUpdateTime() != null ? p.getUpdateTime().toString() : ""
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}