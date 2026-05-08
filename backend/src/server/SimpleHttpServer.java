package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import dao.InventoryDao;

public class SimpleHttpServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        new InventoryDao().syncAllInventory();

        IrrigationScheduler.start();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/materials", new MaterialHandler());
        server.createContext("/api/purchases", new PurchaseHandler());

        server.createContext("/api/products", new ProductHandler());
        server.createContext("/api/sales", new SaleHandler());

        server.createContext("/api/inventory", new InventoryHandler());

        server.createContext("/api/users", new UserHandler());
        server.createContext("/api/roles", new RoleHandler());

        server.createContext("/api/consultations", new ConsultationHandler());
        server.createContext("/api/messages", new ConsultationHandler());

        server.createContext("/api/plant-recognition", new PlantRecognitionHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/farmland", new FarmlandHandler());
        server.createContext("/api/experts", new ExpertHandler());
        server.createContext("/api/expert/login", new ExpertLoginHandler());
        server.createContext("/api/activities", new ActivityHandler());
        server.createContext("/api/irrigation/devices", new IrrigationDeviceHandler());
        server.createContext("/api/irrigation/plans", new IrrigationPlanHandler());
        server.createContext("/api/irrigation/records", new IrrigationRecordHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("服务器启动成功，端口: " + PORT);
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}