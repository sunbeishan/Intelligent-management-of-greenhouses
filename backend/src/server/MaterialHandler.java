package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.MaterialDao;
import model.Material;

import java.io.IOException;
import java.util.List;

public class MaterialHandler implements HttpHandler {
    private MaterialDao materialDao = new MaterialDao();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        switch (method) {
            case "GET":
                handleGet(exchange, path);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange, path);
                break;
            case "DELETE":
                handleDelete(exchange, path);
                break;
            default:
                SimpleHttpServer.sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/materials/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Material material = materialDao.getMaterialById(id);
            if (material != null) {
                SimpleHttpServer.sendResponse(exchange, 200, toJson(material));
            } else {
                SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Material not found\"}");
            }
        } else {
            List<Material> materials = materialDao.getAllMaterials();
            SimpleHttpServer.sendResponse(exchange, 200, toJsonList(materials));
        }
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);
        Material material = parseMaterial(body);
        boolean success = materialDao.addMaterial(material);
        if (success) {
            SimpleHttpServer.sendResponse(exchange, 201, "{\"message\": \"Material added successfully\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to add material\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/materials/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            String body = SimpleHttpServer.readRequestBody(exchange);
            Material material = parseMaterial(body);
            material.setId(id);
            boolean success = materialDao.updateMaterial(material);
            if (success) {
                SimpleHttpServer.sendResponse(exchange, 200, "{\"message\": \"Material updated successfully\"}");
            } else {
                SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to update material\"}");
            }
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/materials/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            boolean success = materialDao.deleteMaterial(id);
            if (success) {
                SimpleHttpServer.sendResponse(exchange, 200, "{\"message\": \"Material deleted successfully\"}");
            } else {
                SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to delete material\"}");
            }
        }
    }
    
    private Material parseMaterial(String json) {
        Material m = new Material();
        m.setName(extractValue(json, "name"));
        m.setType(extractValue(json, "type"));
        m.setSpec(extractValue(json, "spec"));
        m.setStock(Integer.parseInt(extractValue(json, "stock")));
        m.setUnit(extractValue(json, "unit"));
        m.setSupplier(extractValue(json, "supplier"));
        return m;
    }
    
    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = r.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        r = java.util.regex.Pattern.compile(pattern);
        matcher = r.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    
    private String toJson(Material m) {
        return String.format("{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\",\"spec\":\"%s\",\"stock\":%d,\"unit\":\"%s\",\"supplier\":\"%s\"}",
                m.getId(), m.getName(), m.getType(), m.getSpec(), m.getStock(), m.getUnit(), m.getSupplier());
    }
    
    private String toJsonList(List<Material> materials) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < materials.size(); i++) {
            sb.append(toJson(materials.get(i)));
            if (i < materials.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
