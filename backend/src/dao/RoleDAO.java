package dao;

import db.DatabaseConfig;
import model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                role.setCreateTime(rs.getTimestamp("create_time"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public Role getRoleById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setName(rs.getString("name"));
                    role.setDescription(rs.getString("description"));
                    role.setCreateTime(rs.getTimestamp("create_time"));
                    return role;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRole(Role role) {
        String sql = "INSERT INTO roles (name, description) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.getName());
            pstmt.setString(2, role.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(Role role) {
        String sql = "UPDATE roles SET name=?, description=? WHERE id=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.getName());
            pstmt.setString(2, role.getDescription());
            pstmt.setInt(3, role.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRole(int id) {
        String sql = "DELETE FROM roles WHERE id=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
