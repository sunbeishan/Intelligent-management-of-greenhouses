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
                i.setLastUpdate(rs.getTimestamp("last_update"));
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
                i.setLastUpdate(rs.getTimestamp("last_update"));
                inventoryList.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
    
    public boolean insertInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (name, type, category, spec, stock, unit, last_update) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, inventory.getName());
            pstmt.setString(2, inventory.getType());
            pstmt.setString(3, inventory.getCategory());
            pstmt.setString(4, inventory.getSpec());
            pstmt.setInt(5, inventory.getStock());
            pstmt.setString(6, inventory.getUnit());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateInventory(Inventory inventory) {
        String sql = "UPDATE inventory SET name=?, type=?, category=?, spec=?, stock=?, unit=?, last_update=NOW() WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, inventory.getName());
            pstmt.setString(2, inventory.getType());
            pstmt.setString(3, inventory.getCategory());
            pstmt.setString(4, inventory.getSpec());
            pstmt.setInt(5, inventory.getStock());
            pstmt.setString(6, inventory.getUnit());
            pstmt.setInt(7, inventory.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteInventory(int id) {
        String sql = "DELETE FROM inventory WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
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
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                String selectSql = "";
                if ("农资".equals(type)) {
                    selectSql = "SELECT type, spec, stock, unit FROM materials WHERE name = ?";
                } else if ("产品".equals(type)) {
                    selectSql = "SELECT type, spec, stock, unit FROM products WHERE name = ?";
                }
                
                if (!selectSql.isEmpty()) {
                    try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
                        selectPstmt.setString(1, name);
                        try (ResultSet rs = selectPstmt.executeQuery()) {
                            if (rs.next()) {
                                String insertSql = "INSERT INTO inventory (name, type, category, spec, stock, unit, last_update) " +
                                                   "VALUES (?, ?, ?, ?, ?, ?, NOW())";
                                try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                                    insertPstmt.setString(1, name);
                                    insertPstmt.setString(2, type);
                                    insertPstmt.setString(3, rs.getString("type"));
                                    insertPstmt.setString(4, rs.getString("spec"));
                                    insertPstmt.setInt(5, rs.getInt("stock") + quantity);
                                    insertPstmt.setString(6, rs.getString("unit"));
                                    insertPstmt.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void syncAllInventory() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String clearSql = "TRUNCATE TABLE inventory";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(clearSql);
            }
            
            String insertMaterialsSql = "INSERT INTO inventory (name, type, category, spec, stock, unit, last_update) " +
                                       "SELECT name, '农资', type, spec, stock, unit, NOW() FROM materials";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(insertMaterialsSql);
            }
            
            String insertProductsSql = "INSERT INTO inventory (name, type, category, spec, stock, unit, last_update) " +
                                       "SELECT name, '产品', type, spec, stock, unit, NOW() FROM products";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(insertProductsSql);
            }
            
            System.out.println("Inventory synchronized successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
