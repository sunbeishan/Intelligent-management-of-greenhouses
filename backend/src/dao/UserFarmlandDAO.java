package dao;

import db.DatabaseConfig;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserFarmlandDAO {

    public UserFarmlandDAO() {
        ensureFarmlandsColumnExists();
    }

    private void ensureFarmlandsColumnExists() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet columns = dbMeta.getColumns(null, null, "users", "farmlands");
            
            if (!columns.next()) {
                stmt.execute("ALTER TABLE users ADD COLUMN farmlands TEXT");
            }
        } catch (SQLException e) {
            // 忽略错误，可能字段已存在
        }
    }

    public int getFarmlandUserCount(int farmlandId) {
        String farmlandName = getFarmlandNameById(farmlandId);
        if (farmlandName == null) {
            return 0;
        }
        
        int count = 0;
        String sql = "SELECT COUNT(*) as cnt FROM users WHERE farmlands LIKE ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + farmlandName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private String getFarmlandNameById(int farmlandId) {
        String sql = "SELECT name FROM farmland WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, farmlandId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUserFarmland(int userId, int farmlandId) {
        String farmlandName = getFarmlandNameById(farmlandId);
        if (farmlandName == null) {
            return false;
        }
        
        String sql = "UPDATE users SET farmlands = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);
            
            if (user != null) {
                String currentFarmlands = user.getFarmlands();
                String newFarmlands;
                
                if (currentFarmlands == null || currentFarmlands.isEmpty()) {
                    newFarmlands = farmlandName;
                } else {
                    newFarmlands = currentFarmlands + "," + farmlandName;
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newFarmlands);
                    pstmt.setInt(2, userId);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getUserFarmlands(int userId) {
        List<String> farmlandNames = new ArrayList<>();
        
        String sql = "SELECT farmlands FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String farmlandsStr = rs.getString("farmlands");
                if (farmlandsStr != null && !farmlandsStr.isEmpty()) {
                    String[] names = farmlandsStr.split(",");
                    for (String name : names) {
                        if (!name.trim().isEmpty()) {
                            farmlandNames.add(name.trim());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return farmlandNames;
    }

    public String getUserFarmlandsJson(int userId) {
        List<String> farmlands = getUserFarmlands(userId);
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < farmlands.size(); i++) {
            json.append("\"").append(escapeJson(farmlands.get(i))).append("\"");
            if (i < farmlands.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    public boolean deleteUserFarmland(int userId, int farmlandId) {
        String farmlandName = getFarmlandNameById(farmlandId);
        if (farmlandName == null) {
            return false;
        }
        
        return deleteUserFarmlandByName(userId, farmlandName);
    }

    public boolean deleteUserFarmlandByName(int userId, String farmlandName) {
        String sql = "UPDATE users SET farmlands = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);
            
            if (user != null && user.getFarmlands() != null) {
                String[] farmlandNames = user.getFarmlands().split(",");
                StringBuilder newFarmlands = new StringBuilder();
                
                for (String name : farmlandNames) {
                    if (!name.trim().equals(farmlandName)) {
                        if (newFarmlands.length() > 0) {
                            newFarmlands.append(",");
                        }
                        newFarmlands.append(name.trim());
                    }
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newFarmlands.toString());
                    pstmt.setInt(2, userId);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByUserId(int userId) {
        String sql = "UPDATE users SET farmlands = '' WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
