package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PurchaseDao;
import dao.MaterialDao;
import dao.InventoryDao;
import dao.ActivityDao;
import model.Purchase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PurchaseHandler implements HttpHandler {
    private PurchaseDao purchaseDao = new PurchaseDao();
    private MaterialDao materialDao = new MaterialDao();
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
            default:
                SimpleHttpServer.sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Purchase> purchases = purchaseDao.getAllPurchases();
        SimpleHttpServer.sendResponse(exchange, 200, toJsonList(purchases));
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);
        Purchase purchase = parsePurchase(body);

        String id = "P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                    String.format("%03d", (int)(Math.random() * 1000));
        purchase.setId(id);

        boolean success = purchaseDao.addPurchase(purchase);
        if (success) {
            updateMaterialStock(purchase);
            ActivityDao.recordActivity("农资采购", purchase.getMaterialName() + "采购单已创建");
            SimpleHttpServer.sendResponse(exchange, 201, "{\"message\": \"Purchase added successfully\", \"id\": \"" + id + "\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to add purchase\"}");
        }
    }

    private void updateMaterialStock(Purchase purchase) {
        List<model.Material> materials = new MaterialDao().getAllMaterials();
        for (model.Material m : materials) {
            if (m.getName().equals(purchase.getMaterialName())) {
                materialDao.updateStock(m.getId(), purchase.getQuantity());
                inventoryDao.updateInventoryStock(m.getName(), "农资", purchase.getQuantity());
                break;
            }
        }
    }

    private Purchase parsePurchase(String json) {
        Purchase p = new Purchase();
        p.setMaterialName(extractValue(json, "materialName"));
        p.setQuantity(Integer.parseInt(extractValue(json, "quantity")));
        p.setUnitPrice(Double.parseDouble(extractValue(json, "unitPrice")));
        p.setTotalPrice(Double.parseDouble(extractValue(json, "totalPrice")));
        p.setSupplier(extractValue(json, "supplier"));
        p.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return p;
    }

    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = r.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = "\"" + key + "\"\\s*:\\s*(\\d+\\.?\\d*)";
        r = java.util.regex.Pattern.compile(pattern);
        matcher = r.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String toJson(Purchase p) {
        return String.format("{\"id\":\"%s\",\"materialName\":\"%s\",\"quantity\":%d,\"unitPrice\":%.2f,\"totalPrice\":%.2f,\"supplier\":\"%s\",\"purchaseDate\":\"%s\"}",
                p.getId(), p.getMaterialName(), p.getQuantity(), p.getUnitPrice(), p.getTotalPrice(), p.getSupplier(), p.getPurchaseDate());
    }

    private String toJsonList(List<Purchase> purchases) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < purchases.size(); i++) {
            sb.append(toJson(purchases.get(i)));
            if (i < purchases.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
