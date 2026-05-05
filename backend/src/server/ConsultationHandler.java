package server;

import dao.ConsultationDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ConsultationHandler implements HttpHandler {
    private ConsultationDAO consultationDAO = new ConsultationDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                default:
                    sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"message\": \"Internal server error\"}");
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/api/consultations/expert/")) {
            String expertIdStr = path.substring("/api/consultations/expert/".length());
            try {
                int expertId = Integer.parseInt(expertIdStr);
                List<String> consultations = consultationDAO.getConsultationsByExpertId(expertId);
                String result = buildConsultationsWithMessages(consultations);
                sendResponse(exchange, 200, result);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"message\": \"Invalid expert ID\"}");
            }
        } else if (path.matches("/api/consultations/[^/]+")) {
            String id = path.substring("/api/consultations/".length());
            String consultation = consultationDAO.getConsultationById(id);
            if (consultation != null) {
                List<String> messages = consultationDAO.getMessagesByConsultationId(id);
                String result = consultation.substring(0, consultation.length() - 1)
                    + ",\"messages\":[" + String.join(",", messages) + "]}";
                sendResponse(exchange, 200, result);
            } else {
                sendResponse(exchange, 404, "{\"message\": \"Consultation not found\"}");
            }
        } else if (path.equals("/api/consultations")) {
            List<String> consultations = consultationDAO.getAllConsultations();
            String result = buildConsultationsWithMessages(consultations);
            sendResponse(exchange, 200, result);
        } else {
            sendResponse(exchange, 404, "{\"message\": \"Not found\"}");
        }
    }

    private String buildConsultationsWithMessages(List<String> consultations) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < consultations.size(); i++) {
            String consult = consultations.get(i);
            String id = extractIdFromConsultation(consult);

            List<String> messages = consultationDAO.getMessagesByConsultationId(id);

            result.append(consult.substring(0, consult.length() - 1));
            result.append(",\"messages\":[");
            for (int j = 0; j < messages.size(); j++) {
                result.append(messages.get(j));
                if (j < messages.size() - 1) result.append(",");
            }
            result.append("]}");
            if (i < consultations.size() - 1) result.append(",");
        }
        result.append("]");
        return result.toString();
    }

    private String extractIdFromConsultation(String consult) {
        int start = consult.indexOf(":\"") + 2;
        int end = consult.indexOf("\",\"", start);
        if (end == -1) end = consult.indexOf("\"}", start);
        return consult.substring(start, end);
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes());

        if (path.equals("/api/consultations")) {
            String id = extractValue(body, "id");
            String expertIdStr = extractValue(body, "expertId");
            String expertName = extractValue(body, "expertName");
            String userName = extractValue(body, "userName");
            String subject = extractValue(body, "subject");
            String content = extractValue(body, "content");

            if (id.isEmpty() || expertIdStr.isEmpty() || expertName.isEmpty() || subject.isEmpty() || content.isEmpty()) {
                sendResponse(exchange, 400, "{\"message\": \"Missing required fields\"}");
                return;
            }

            int expertId;
            try {
                expertId = Integer.parseInt(expertIdStr);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"message\": \"Invalid expert ID\"}");
                return;
            }

            if (consultationDAO.addConsultation(id, expertId, expertName, userName, subject)) {
                consultationDAO.addMessage(id, "user", content);
                sendResponse(exchange, 201, "{\"message\": \"Consultation added successfully\"}");
            } else {
                sendResponse(exchange, 500, "{\"message\": \"Failed to add consultation\"}");
            }
        } else if (path.equals("/api/messages")) {
            String consultationId = extractValue(body, "consultationId");
            String sender = extractValue(body, "sender");
            String content = extractValue(body, "content");

            if (consultationDAO.addMessage(consultationId, sender, content)) {
                sendResponse(exchange, 201, "{\"message\": \"Message added successfully\"}");
            } else {
                sendResponse(exchange, 500, "{\"message\": \"Failed to add message\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"message\": \"Not found\"}");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes());

        if (path.equals("/api/consultations/status")) {
            String id = extractValue(body, "id");
            String status = extractValue(body, "status");

            if (consultationDAO.updateConsultationStatus(id, status)) {
                sendResponse(exchange, 200, "{\"message\": \"Status updated successfully\"}");
            } else {
                sendResponse(exchange, 500, "{\"message\": \"Failed to update status\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"message\": \"Not found\"}");
        }
    }

    private String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\":");
        if (start == -1) return "";

        start = json.indexOf(":", start) + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()) return "";

        if (json.charAt(start) == '"') {
            start++;
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') {
                    break;
                }
                end++;
            }
            return json.substring(start, end).replace("\\\"", "\"");
        } else {
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.')) {
                end++;
            }
            return json.substring(start, end);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
