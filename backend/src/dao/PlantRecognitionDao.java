package dao;

import db.DatabaseConfig;
import model.PlantRecognition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlantRecognitionDao {

    public List<PlantRecognition> getAllRecords() {
        List<PlantRecognition> records = new ArrayList<>();
        String sql = "SELECT * FROM plant_recognition ORDER BY recognize_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PlantRecognition p = new PlantRecognition();
                p.setId(rs.getInt("id"));
                p.setImageName(rs.getString("image_name"));
                p.setImagePath(rs.getString("image_path"));
                p.setRecognitionResult(rs.getString("recognition_result"));
                p.setConfidence(rs.getDouble("confidence"));
                p.setRecognizeTime(rs.getString("recognize_time"));
                records.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public PlantRecognition getRecordById(int id) {
        String sql = "SELECT * FROM plant_recognition WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlantRecognition p = new PlantRecognition();
                p.setId(rs.getInt("id"));
                p.setImageName(rs.getString("image_name"));
                p.setImagePath(rs.getString("image_path"));
                p.setRecognitionResult(rs.getString("recognition_result"));
                p.setConfidence(rs.getDouble("confidence"));
                p.setRecognizeTime(rs.getString("recognize_time"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRecord(PlantRecognition record) {
        String sql = "INSERT INTO plant_recognition (image_name, image_path, recognition_result, confidence) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getImageName());
            pstmt.setString(2, record.getImagePath());
            pstmt.setString(3, record.getRecognitionResult());
            pstmt.setDouble(4, record.getConfidence());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRecord(int id) {
        String sql = "DELETE FROM plant_recognition WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getNextId() {
        String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'agriculture_db' AND TABLE_NAME = 'plant_recognition'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("AUTO_INCREMENT");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}