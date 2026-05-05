package dao;

import db.DatabaseConfig;
import model.Farmland;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmlandDAO {

    public List<Farmland> getAllFarmlands() {
        List<Farmland> farmlands = new ArrayList<>();
        String sql = "SELECT * FROM farmland ORDER BY create_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Farmland f = new Farmland();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setArea(rs.getString("area"));
                f.setAcreage(rs.getDouble("acreage"));
                f.setSoilType(rs.getString("soil_type"));
                f.setCrop(rs.getString("crop"));
                f.setStatus(rs.getString("status"));
                f.setCreateTime(rs.getTimestamp("create_time"));
                f.setUpdateTime(rs.getTimestamp("update_time"));
                farmlands.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return farmlands;
    }

    public Farmland getFarmlandById(int id) {
        String sql = "SELECT * FROM farmland WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Farmland f = new Farmland();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setArea(rs.getString("area"));
                f.setAcreage(rs.getDouble("acreage"));
                f.setSoilType(rs.getString("soil_type"));
                f.setCrop(rs.getString("crop"));
                f.setStatus(rs.getString("status"));
                f.setCreateTime(rs.getTimestamp("create_time"));
                f.setUpdateTime(rs.getTimestamp("update_time"));
                return f;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Farmland> searchFarmlands(String name, String area) {
        List<Farmland> farmlands = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM farmland WHERE 1=1");
        
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (area != null && !area.isEmpty()) {
            sql.append(" AND area = ?");
        }
        sql.append(" ORDER BY create_time DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (name != null && !name.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + name + "%");
            }
            if (area != null && !area.isEmpty()) {
                pstmt.setString(paramIndex++, area);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Farmland f = new Farmland();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setArea(rs.getString("area"));
                f.setAcreage(rs.getDouble("acreage"));
                f.setSoilType(rs.getString("soil_type"));
                f.setCrop(rs.getString("crop"));
                f.setStatus(rs.getString("status"));
                f.setCreateTime(rs.getTimestamp("create_time"));
                f.setUpdateTime(rs.getTimestamp("update_time"));
                farmlands.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return farmlands;
    }

    public boolean addFarmland(Farmland farmland) {
        String sql = "INSERT INTO farmland (name, area, acreage, soil_type, crop, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, farmland.getName());
            pstmt.setString(2, farmland.getArea());
            pstmt.setDouble(3, farmland.getAcreage());
            pstmt.setString(4, farmland.getSoilType());
            pstmt.setString(5, farmland.getCrop());
            pstmt.setString(6, farmland.getStatus() != null ? farmland.getStatus() : "种植中");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFarmland(Farmland farmland) {
        String sql = "UPDATE farmland SET name=?, area=?, acreage=?, soil_type=?, crop=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, farmland.getName());
            pstmt.setString(2, farmland.getArea());
            pstmt.setDouble(3, farmland.getAcreage());
            pstmt.setString(4, farmland.getSoilType());
            pstmt.setString(5, farmland.getCrop());
            pstmt.setString(6, farmland.getStatus());
            pstmt.setInt(7, farmland.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFarmland(int id) {
        String sql = "DELETE FROM farmland WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalArea() {
        String sql = "SELECT COALESCE(SUM(acreage), 0) AS total FROM farmland";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}