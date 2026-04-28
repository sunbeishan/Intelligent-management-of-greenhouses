package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.InventoryDao;
import model.Inventory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InventoryHandler implements HttpHandler {
    private InventoryDao inventoryDao = new InventoryDao();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        switch (method) {
            case "GET":
                handleGet(exchange);
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
    
    private String toJson(Inventory i) {
        return String.format("{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\",\"category\":\"%s\",\"spec\":\"%s\",\"stock\":%d,\"unit\":\"%s\",\"lastUpdate\":\"%s\"}",
                i.getId(), i.getName(), i.getType(), i.getCategory(), i.getSpec(), i.getStock(), i.getUnit(), i.getLastUpdate());
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
