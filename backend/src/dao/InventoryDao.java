package dao;

import db.DatabaseConfig;
import model.Inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDao {
    
    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY type, name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Inventory i = new Inventory();
                i.setId(rs.getInt("id"));
                i.setName(rs.getString("name"));
                i.setType(rs.getString("type"));
                i.setCategory(rs.getString("category"));
                i.setSpec(rs.getString("spec"));
                i.setStock(rs.getInt("stock"));
                i.setUnit(rs.getString("unit"));
                i.setLastUpdate(rs.getString("last_update"));
                inventoryList.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
    
    public List<Inventory> searchInventory(String name, String type, String status) {
        List<Inventory> inventoryList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM inventory WHERE 1=1");
        
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
        }
        if (status != null && !status.isEmpty()) {
            if ("low".equals(status)) {
                sql.append(" AND stock < 50");
            } else if ("normal".equals(status)) {
                sql.append(" AND stock >= 50 AND stock < 100");
            } else if ("excess".equals(status)) {
                sql.append(" AND stock >= 100");
            }
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int index = 1;
            if (name != null && !name.isEmpty()) {
                pstmt.setString(index++, "%" + name + "%");
            }
            if (type != null && !type.isEmpty()) {
                pstmt.setString(index++, type);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Inventory i = new Inventory();
                i.setId(rs.getInt("id"));
                i.setName(rs.getString("name"));
                i.setType(rs.getString("type"));
                i.setCategory(rs.getString("category"));
                i.setSpec(rs.getString("spec"));
                i.setStock(rs.getInt("stock"));
                i.setUnit(rs.getString("unit"));
                i.setLastUpdate(rs.getString("last_update"));
                inventoryList.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
    
    public boolean addOrUpdateInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (name, type, category, spec, stock, unit, last_update) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE stock=VALUES(stock), last_update=VALUES(last_update)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, inventory.getName());
            pstmt.setString(2, inventory.getType());
            pstmt.setString(3, inventory.getCategory());
            pstmt.setString(4, inventory.getSpec());
            pstmt.setInt(5, inventory.getStock());
            pstmt.setString(6, inventory.getUnit());
            pstmt.setString(7, inventory.getLastUpdate());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateInventoryStock(String name, String type, int quantity) {
        String sql = "UPDATE inventory SET stock = stock + ?, last_update = NOW() WHERE name = ? AND type = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setString(2, name);
            pstmt.setString(3, type);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
