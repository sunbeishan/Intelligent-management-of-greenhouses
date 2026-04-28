package dao;

import db.DatabaseConfig;
import model.Sale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDao {
    
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getString("id"));
                s.setProductName(rs.getString("product_name"));
                s.setQuantity(rs.getInt("quantity"));
                s.setUnitPrice(rs.getDouble("unit_price"));
                s.setTotalPrice(rs.getDouble("total_price"));
                s.setCustomer(rs.getString("customer"));
                s.setSaleDate(rs.getString("sale_date"));
                sales.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }
    
    public boolean addSale(Sale sale) {
        String sql = "INSERT INTO sales (id, product_name, quantity, unit_price, total_price, customer, sale_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sale.getId());
            pstmt.setString(2, sale.getProductName());
            pstmt.setInt(3, sale.getQuantity());
            pstmt.setDouble(4, sale.getUnitPrice());
            pstmt.setDouble(5, sale.getTotalPrice());
            pstmt.setString(6, sale.getCustomer());
            pstmt.setString(7, sale.getSaleDate());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Sale getSaleById(String id) {
        String sql = "SELECT * FROM sales WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getString("id"));
                s.setProductName(rs.getString("product_name"));
                s.setQuantity(rs.getInt("quantity"));
                s.setUnitPrice(rs.getDouble("unit_price"));
                s.setTotalPrice(rs.getDouble("total_price"));
                s.setCustomer(rs.getString("customer"));
                s.setSaleDate(rs.getString("sale_date"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
