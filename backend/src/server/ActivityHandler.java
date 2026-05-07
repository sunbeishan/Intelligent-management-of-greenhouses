package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ActivityDao;

import java.io.IOException;

public class ActivityHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        if ("GET".equals(method)) {
            handleGet(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 405, "{\"success\": false, \"message\": \"方法不支持\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            String json = ActivityDao.getRecentActivities();
            SimpleHttpServer.sendResponse(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            SimpleHttpServer.sendResponse(exchange, 500, "{\"success\": false, \"message\": \"获取活动记录失败\"}");
        }
    }
}
