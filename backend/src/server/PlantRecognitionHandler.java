package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PlantRecognitionDao;
import model.PlantRecognition;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantRecognitionHandler implements HttpHandler {
    private PlantRecognitionDao dao = new PlantRecognitionDao();

    private static final String API_KEY = "UtlzMli4PqSFILV2hqmeKiDG";
    private static final String SECRET_KEY = "mgYHB5GSR0ozfhCquD2pN4eX9DYD5EZP";
    private static final String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String PLANT_URL = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/plant-recognition")) {
            handleGet(exchange);
        } else if ("POST".equals(method) && path.equals("/api/plant-recognition")) {
            handlePost(exchange);
        } else if ("DELETE".equals(method) && path.matches("/api/plant-recognition/\\d+")) {
            handleDelete(exchange);
        } else {
            SimpleHttpServer.sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<PlantRecognition> records = dao.getAllRecords();
        String json = toJsonList(records);
        SimpleHttpServer.sendResponse(exchange, 200, json);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = SimpleHttpServer.readRequestBody(exchange);

        String imageBase64 = extractValue(body, "imageBase64");
        String imageName = extractValue(body, "imageName");

        if (imageBase64 == null || imageBase64.isEmpty()) {
            SimpleHttpServer.sendResponse(exchange, 400, "{\"error\": \"imageBase64 is required\"}");
            return;
        }

        try {
            String accessToken = getAccessToken();
            String result = callPlantApiWithBase64(accessToken, imageBase64);

            String plantName = extractPlantName(result);
            double confidence = extractConfidence(result);
            String category = extractCategory(result);
            String description = extractDescription(result);

            PlantRecognition record = new PlantRecognition();
            record.setImageName(imageName != null ? imageName : "unknown");
            record.setImagePath("uploaded");
            record.setRecognitionResult(plantName);
            record.setConfidence(confidence);

            dao.addRecord(record);

            String response = String.format("{\"success\": true, \"result\": \"%s\", \"confidence\": %.4f, \"category\": \"%s\", \"description\": \"%s\", \"message\": \"识别成功\"}",
                    plantName, confidence, category, description);
            SimpleHttpServer.sendResponse(exchange, 200, response);

        } catch (Exception e) {
            e.printStackTrace();
            SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"识别失败: " + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        if (dao.deleteRecord(id)) {
            SimpleHttpServer.sendResponse(exchange, 200, "{\"success\": true, \"message\": \"删除成功\"}");
        } else {
            SimpleHttpServer.sendResponse(exchange, 500, "{\"error\": \"删除失败\"}");
        }
    }

    private String getAccessToken() throws IOException {
        String params = "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY;

        URL url = new URL(ACCESS_TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                String json = response.toString();
                return extractAccessToken(json);
            }
        } else {
            throw new IOException("Failed to get access token, response code: " + responseCode);
        }
    }

    private String callPlantApiWithBase64(String accessToken, String imageBase64) throws IOException {
        String params = "image=" + URLEncoder.encode(imageBase64, StandardCharsets.UTF_8);
        String fullUrl = PLANT_URL + "?access_token=" + accessToken;

        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json");
        conn.setReadTimeout(30000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            String errorMsg = "Failed to call plant API, response code: " + responseCode;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                errorMsg += ", error: " + response.toString();
            }
            throw new IOException(errorMsg);
        }
    }

    private String extractAccessToken(String json) {
        Pattern pattern = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String extractPlantName(String json) {
        Pattern pattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "未知植物";
    }

    private double extractConfidence(String json) {
        Pattern pattern = Pattern.compile("\"score\"\\s*:\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        pattern = Pattern.compile("\"confidence\"\\s*:\\s*([0-9.]+)");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0.0;
    }

    private String extractCategory(String json) {
        Pattern pattern = Pattern.compile("\"family\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = Pattern.compile("\"genus\"\\s*:\\s*\"([^\"]+)\"");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "未知分类";
    }

    private String extractDescription(String json) {
        Pattern pattern = Pattern.compile("\"description\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = Pattern.compile("\"brief\"\\s*:\\s*\"([^\"]+)\"");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
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

    private String toJsonList(List<PlantRecognition> records) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < records.size(); i++) {
            PlantRecognition p = records.get(i);
            sb.append(String.format("{\"id\":%d,\"imageName\":\"%s\",\"imagePath\":\"%s\",\"recognitionResult\":\"%s\",\"confidence\":%.4f,\"recognizeTime\":\"%s\"}",
                    p.getId(), escapeJson(p.getImageName()), escapeJson(p.getImagePath()),
                    escapeJson(p.getRecognitionResult()), p.getConfidence(), escapeJson(p.getRecognizeTime())));
            if (i < records.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}