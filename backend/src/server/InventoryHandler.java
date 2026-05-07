package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.InventoryDao;
import model.Inventory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryHandler implements HttpHandler {
    private InventoryDao inventoryDao = new InventoryDao();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                SimpleHttpServer.sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = SimpleHttpServer.parseQueryParams(query);
        
        List<Inventory> inventoryList;
        if (params.isEmpty()) {
            inventoryList = inventoryDao.getAllInventory();
        } else {
            inventoryList = inventoryDao.searchInventory(
                params.get("name"),
                params.get("type"),
                params.get("status")
            );
        }
        SimpleHttpServer.sendResponse(exchange, 200, toJsonList(inventoryList));
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);
        Inventory inventory = parseInventory(body);
        
        boolean success = inventoryDao.insertInventory(inventory);
        if (success) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"添加成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"添加失败\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
        
        String body = SimpleHttpServer.readRequestBody(exchange);
        Inventory inventory = parseInventory(body);
        inventory.setId(id);
        
        boolean success = inventoryDao.updateInventory(inventory);
        if (success) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"修改成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"修改失败\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
        
        boolean success = inventoryDao.deleteInventory(id);
        if (success) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"删除成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"删除失败\"}");
        }
    }
    
    private Inventory parseInventory(String json) {
        Inventory i = new Inventory();
        i.setName(extractValue(json, "name"));
        i.setType(extractValue(json, "type"));
        i.setCategory(extractValue(json, "category"));
        i.setSpec(extractValue(json, "spec"));
        String stockStr = extractValue(json, "stock");
        i.setStock(stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr));
        i.setUnit(extractValue(json, "unit"));
        return i;
    }
    
    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        r = Pattern.compile(pattern);
        matcher = r.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    
    private String toJson(Inventory i) {
        String lastUpdate = i.getLastUpdate() != null ? i.getLastUpdate().toString() : "";
        return String.format("{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\",\"category\":\"%s\",\"spec\":\"%s\",\"stock\":%d,\"unit\":\"%s\",\"lastUpdate\":\"%s\"}",
                i.getId(), i.getName(), i.getType(), i.getCategory(), i.getSpec(), i.getStock(), i.getUnit(), lastUpdate);
    }
    
    private String toJsonList(List<Inventory> inventoryList) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < inventoryList.size(); i++) {
            sb.append(toJson(inventoryList.get(i)));
            if (i < inventoryList.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}