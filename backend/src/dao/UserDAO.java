package dao;

import db.DatabaseConfig;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    static {
        ensureFarmlandsColumnExists();
    }
    
    private static void ensureFarmlandsColumnExists() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet columns = dbMeta.getColumns(null, null, "users", "farmlands");
            
            if (!columns.next()) {
                stmt.execute("ALTER TABLE users ADD COLUMN farmlands TEXT");
            }
            columns.close();
        } catch (SQLException e) {
            // 忽略错误，字段可能已存在或其他原因
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                try {
                    user.setAvatar(rs.getString("avatar"));
                } catch (SQLException e) {
                    user.setAvatar("");
                }
                try {
                    user.setFarmlands(rs.getString("farmlands"));
                } catch (SQLException e) {
                    user.setFarmlands("");
                }
                user.setCreateTime(rs.getTimestamp("create_time"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setName(rs.getString("name"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    try {
                        user.setAvatar(rs.getString("avatar"));
                    } catch (SQLException e) {
                        user.setAvatar("");
                    }
                    try {
                        user.setFarmlands(rs.getString("farmlands"));
                    } catch (SQLException e) {
                        user.setFarmlands("");
                    }
                    user.setCreateTime(rs.getTimestamp("create_time"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setName(rs.getString("name"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    try {
                        user.setAvatar(rs.getString("avatar"));
                    } catch (SQLException e) {
                        user.setAvatar("");
                    }
                    try {
                        user.setFarmlands(rs.getString("farmlands"));
                    } catch (SQLException e) {
                        user.setFarmlands("");
                    }
                    user.setCreateTime(rs.getTimestamp("create_time"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, name, role, status, avatar, farmlands) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            try {
                DatabaseMetaData dbMeta = conn.getMetaData();
                ResultSet columns = dbMeta.getColumns(null, null, "users", "avatar");
                if (!columns.next()) {
                    sql = "INSERT INTO users (username, password, name, role, status) VALUES (?, ?, ?, ?, ?)";
                }
            } catch (SQLException e) {
                // 如果检查失败，使用原始sql
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                pstmt.setString(paramIndex++, user.getUsername());
                pstmt.setString(paramIndex++, user.getPassword());
                pstmt.setString(paramIndex++, user.getName());
                pstmt.setString(paramIndex++, user.getRole());
                pstmt.setString(paramIndex++, user.getStatus());
                if (sql.contains("avatar")) {
                    pstmt.setString(paramIndex++, user.getAvatar() != null ? user.getAvatar() : "");
                }
                if (sql.contains("farmlands")) {
                    pstmt.setString(paramIndex++, user.getFarmlands() != null ? user.getFarmlands() : "");
                }

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, password=?, name=?, role=?, status=?, avatar=?, farmlands=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            try {
                DatabaseMetaData dbMeta = conn.getMetaData();
                ResultSet columns = dbMeta.getColumns(null, null, "users", "avatar");
                if (!columns.next()) {
                    sql = "UPDATE users SET username=?, password=?, name=?, role=?, status=? WHERE id=?";
                }
            } catch (SQLException e) {
                // 如果检查失败，使用原始sql
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                pstmt.setString(paramIndex++, user.getUsername());
                pstmt.setString(paramIndex++, user.getPassword());
                pstmt.setString(paramIndex++, user.getName());
                pstmt.setString(paramIndex++, user.getRole());
                pstmt.setString(paramIndex++, user.getStatus());
                if (sql.contains("avatar")) {
                    pstmt.setString(paramIndex++, user.getAvatar() != null ? user.getAvatar() : "");
                }
                if (sql.contains("farmlands")) {
                    pstmt.setString(paramIndex++, user.getFarmlands() != null ? user.getFarmlands() : "");
                }
                pstmt.setInt(paramIndex++, user.getId());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String deleteUserFarmlandSql = "DELETE FROM user_farmland WHERE user_id=?";
        String deleteUserSql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserFarmlandSql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // user_farmland表可能已删除，忽略此错误
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                pstmt.setInt(1, id);
                int affected = pstmt.executeUpdate();
                conn.commit();
                return affected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
