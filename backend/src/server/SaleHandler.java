package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.SaleDao;
import dao.ProductDao;
import model.Sale;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SaleHandler implements HttpHandler {
    private SaleDao saleDao = new SaleDao();
    private ProductDao productDao = new ProductDao();
    
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
        List<Sale> sales = saleDao.getAllSales();
        SimpleHttpServer.sendResponse(exchange, 200, toJsonList(sales));
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);
        Sale sale = parseSale(body);
        
        // 生成销售编号
        String id = "S" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                    String.format("%03d", (int)(Math.random() * 1000));
        sale.setId(id);
        
        boolean success = saleDao.addSale(sale);
        if (success) {
            // 更新产品库存
            updateProductStock(sale);
            SimpleHttpServer.sendResponse(exchange, 201, "{\"message\": \"Sale added successfully\", \"id\": \"" + id + "\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"Failed to add sale\"}");
        }
    }
    
    private void updateProductStock(Sale sale) {
        // 根据产品名称查找ID并更新库存
        List<model.Product> products = new ProductDao().getAllProducts();
        for (model.Product p : products) {
            if (p.getName().equals(sale.getProductName())) {
                productDao.updateStock(p.getId(), sale.getQuantity());
                break;
            }
        }
    }
    
    private Sale parseSale(String json) {
        Sale s = new Sale();
        s.setProductName(extractValue(json, "productName"));
        s.setQuantity(Integer.parseInt(extractValue(json, "quantity")));
        s.setUnitPrice(Double.parseDouble(extractValue(json, "unitPrice")));
        s.setTotalPrice(Double.parseDouble(extractValue(json, "totalPrice")));
        s.setCustomer(extractValue(json, "customer"));
        s.setSaleDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return s;
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
    
    private String toJson(Sale s) {
        return String.format("{\"id\":\"%s\",\"productName\":\"%s\",\"quantity\":%d,\"unitPrice\":%.2f,\"totalPrice\":%.2f,\"customer\":\"%s\",\"saleDate\":\"%s\"}",
                s.getId(), s.getProductName(), s.getQuantity(), s.getUnitPrice(), s.getTotalPrice(), s.getCustomer(), s.getSaleDate());
    }
    
    private String toJsonList(List<Sale> sales) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sales.size(); i++) {
            sb.append(toJson(sales.get(i)));
            if (i < sales.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
