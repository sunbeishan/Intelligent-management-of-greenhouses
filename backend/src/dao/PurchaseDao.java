package dao;

import db.DatabaseConfig;
import model.Purchase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDao {
    
    public List<Purchase> getAllPurchases() {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM purchases ORDER BY purchase_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Purchase p = new Purchase();
                p.setId(rs.getString("id"));
                p.setMaterialName(rs.getString("material_name"));
                p.setQuantity(rs.getInt("quantity"));
                p.setUnitPrice(rs.getDouble("unit_price"));
                p.setTotalPrice(rs.getDouble("total_price"));
                p.setSupplier(rs.getString("supplier"));
                p.setPurchaseDate(rs.getString("purchase_date"));
                purchases.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }
    
    public boolean addPurchase(Purchase purchase) {
        String sql = "INSERT INTO purchases (id, material_name, quantity, unit_price, total_price, supplier, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, purchase.getId());
            pstmt.setString(2, purchase.getMaterialName());
            pstmt.setInt(3, purchase.getQuantity());
            pstmt.setDouble(4, purchase.getUnitPrice());
            pstmt.setDouble(5, purchase.getTotalPrice());
            pstmt.setString(6, purchase.getSupplier());
            pstmt.setString(7, purchase.getPurchaseDate());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Purchase getPurchaseById(String id) {
        String sql = "SELECT * FROM purchases WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Purchase p = new Purchase();
                p.setId(rs.getString("id"));
                p.setMaterialName(rs.getString("material_name"));
                p.setQuantity(rs.getInt("quantity"));
                p.setUnitPrice(rs.getDouble("unit_price"));
                p.setTotalPrice(rs.getDouble("total_price"));
                p.setSupplier(rs.getString("supplier"));
                p.setPurchaseDate(rs.getString("purchase_date"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
