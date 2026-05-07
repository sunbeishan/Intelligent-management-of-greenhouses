package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ProductDao;
import dao.InventoryDao;
import model.Product;
import model.Inventory;

import java.io.IOException;
import java.util.List;

public class ProductHandler implements HttpHandler {
    private ProductDao productDao = new ProductDao();
    private InventoryDao inventoryDao = new InventoryDao();
    
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
        if (path.matches("/api/products/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Product product = productDao.getProductById(id);
            if (product != null) {
                SimpleHttpServer.sendResponse(exchange, 200, toJson(product));
            } else {
                SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Product not found\"}");
            }
        } else {
            List<Product> products = productDao.getAllProducts();
            SimpleHttpServer.sendResponse(exchange, 200, toJsonList(products));
        }
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);
        Product product = parseProduct(body);
        boolean success = productDao.addProduct(product);
        if (success) {
            Inventory inventory = new Inventory();
            inventory.setName(product.getName());
            inventory.setType("产品");
            inventory.setCategory(product.getType());
            inventory.setSpec(product.getSpec());
            inventory.setStock(product.getStock());
            inventory.setUnit(product.getUnit());
            inventoryDao.insertInventory(inventory);
            SimpleHttpServer.sendResponse(exchange, 201, "{\"message\": \"Product added successfully\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to add product\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/products/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            String body = SimpleHttpServer.readRequestBody(exchange);
            Product product = parseProduct(body);
            product.setId(id);
            
            Product oldProduct = productDao.getProductById(id);
            boolean success = productDao.updateProduct(product);
            if (success) {
                inventoryDao.updateInventoryStock(oldProduct.getName(), "产品", product.getStock() - oldProduct.getStock());
                SimpleHttpServer.sendResponse(exchange, 200, "{\"message\": \"Product updated successfully\"}");
            } else {
                SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to update product\"}");
            }
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/products/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
            Product product = productDao.getProductById(id);
            boolean success = productDao.deleteProduct(id);
            if (success) {
                inventoryDao.updateInventoryStock(product.getName(), "产品", -product.getStock());
                SimpleHttpServer.sendResponse(exchange, 200, "{\"message\": \"Product deleted successfully\"}");
            } else {
                SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to delete product\"}");
            }
        }
    }
    
    private Product parseProduct(String json) {
        Product p = new Product();
        p.setName(extractValue(json, "name"));
        p.setType(extractValue(json, "type"));
        p.setSpec(extractValue(json, "spec"));
        p.setStock(Integer.parseInt(extractValue(json, "stock")));
        p.setUnit(extractValue(json, "unit"));
        p.setPrice(Double.parseDouble(extractValue(json, "price")));
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
    
    private String toJson(Product p) {
        return String.format("{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\",\"spec\":\"%s\",\"stock\":%d,\"unit\":\"%s\",\"price\":%.2f}",
                p.getId(), p.getName(), p.getType(), p.getSpec(), p.getStock(), p.getUnit(), p.getPrice());
    }
    
    private String toJsonList(List<Product> products) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < products.size(); i++) {
            sb.append(toJson(products.get(i)));
            if (i < products.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
