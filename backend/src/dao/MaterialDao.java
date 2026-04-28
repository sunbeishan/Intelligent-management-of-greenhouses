package dao;

import db.DatabaseConfig;
import model.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDao {
    
    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Material m = new Material();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setType(rs.getString("type"));
                m.setSpec(rs.getString("spec"));
                m.setStock(rs.getInt("stock"));
                m.setUnit(rs.getString("unit"));
                m.setSupplier(rs.getString("supplier"));
                materials.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    public Material getMaterialById(int id) {
        String sql = "SELECT * FROM materials WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Material m = new Material();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setType(rs.getString("type"));
                m.setSpec(rs.getString("spec"));
                m.setStock(rs.getInt("stock"));
                m.setUnit(rs.getString("unit"));
                m.setSupplier(rs.getString("supplier"));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addMaterial(Material material) {
        String sql = "INSERT INTO materials (name, type, spec, stock, unit, supplier) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, material.getName());
            pstmt.setString(2, material.getType());
            pstmt.setString(3, material.getSpec());
            pstmt.setInt(4, material.getStock());
            pstmt.setString(5, material.getUnit());
            pstmt.setString(6, material.getSupplier());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateMaterial(Material material) {
        String sql = "UPDATE materials SET name=?, type=?, spec=?, stock=?, unit=?, supplier=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, material.getName());
            pstmt.setString(2, material.getType());
            pstmt.setString(3, material.getSpec());
            pstmt.setInt(4, material.getStock());
            pstmt.setString(5, material.getUnit());
            pstmt.setString(6, material.getSupplier());
            pstmt.setInt(7, material.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteMaterial(int id) {
        String sql = "DELETE FROM materials WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateStock(int id, int quantity) {
        String sql = "UPDATE materials SET stock = stock + ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
