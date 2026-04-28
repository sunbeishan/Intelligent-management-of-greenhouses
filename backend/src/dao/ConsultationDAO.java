package dao;

import db.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {
    
    public List<String> getAllConsultations() {
        List<String> consultations = new ArrayList<>();
        String sql = "SELECT * FROM consultations ORDER BY create_time DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String consultation = String.format(
                    "{\"id\":\"%s\",\"expertId\":%d,\"expertName\":\"%s\",\"userName\":\"%s\",\"subject\":\"%s\",\"status\":\"%s\",\"createTime\":\"%s\"}",
                    rs.getString("id"),
                    rs.getInt("expert_id"),
                    rs.getString("expert_name"),
                    rs.getString("user_name"),
                    rs.getString("subject"),
                    rs.getString("status"),
                    rs.getTimestamp("create_time").toString()
                );
                consultations.add(consultation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultations;
    }
    
    public List<String> getConsultationsByExpertId(int expertId) {
        List<String> consultations = new ArrayList<>();
        String sql = "SELECT * FROM consultations WHERE expert_id = ? ORDER BY create_time DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, expertId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String consultation = String.format(
                        "{\"id\":\"%s\",\"expertId\":%d,\"expertName\":\"%s\",\"userName\":\"%s\",\"subject\":\"%s\",\"status\":\"%s\",\"createTime\":\"%s\"}",
                        rs.getString("id"),
                        rs.getInt("expert_id"),
                        rs.getString("expert_name"),
                        rs.getString("user_name"),
                        rs.getString("subject"),
                        rs.getString("status"),
                        rs.getTimestamp("create_time").toString()
                    );
                    consultations.add(consultation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultations;
    }
    
    public String getConsultationById(String id) {
        String sql = "SELECT * FROM consultations WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                        "{\"id\":\"%s\",\"expertId\":%d,\"expertName\":\"%s\",\"userName\":\"%s\",\"subject\":\"%s\",\"status\":\"%s\",\"createTime\":\"%s\"}",
                        rs.getString("id"),
                        rs.getInt("expert_id"),
                        rs.getString("expert_name"),
                        rs.getString("user_name"),
                        rs.getString("subject"),
                        rs.getString("status"),
                        rs.getTimestamp("create_time").toString()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addConsultation(String id, int expertId, String expertName, String userName, String subject) {
        String sql = "INSERT INTO consultations (id, expert_id, expert_name, user_name, subject) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setInt(2, expertId);
            pstmt.setString(3, expertName);
            pstmt.setString(4, userName);
            pstmt.setString(5, subject);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateConsultationStatus(String id, String status) {
        String sql = "UPDATE consultations SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 消息相关方法
    public List<String> getMessagesByConsultationId(String consultationId) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE consultation_id = ? ORDER BY send_time";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, consultationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String message = String.format(
                        "{\"sender\":\"%s\",\"content\":\"%s\",\"time\":\"%s\"}",
                        rs.getString("sender"),
                        rs.getString("content").replace("\"", "\\\""),
                        rs.getTimestamp("send_time").toString()
                    );
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
    public boolean addMessage(String consultationId, String sender, String content) {
        String sql = "INSERT INTO messages (consultation_id, sender, content) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, consultationId);
            pstmt.setString(2, sender);
            pstmt.setString(3, content);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
