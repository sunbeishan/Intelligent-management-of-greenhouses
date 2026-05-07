package dao;

import db.DatabaseConfig;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
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
        String sql = "INSERT INTO users (username, password, name, role, status, avatar) VALUES (?, ?, ?, ?, ?, ?)";

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
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getName());
                pstmt.setString(4, user.getRole());
                pstmt.setString(5, user.getStatus());
                if (sql.contains("avatar")) {
                    pstmt.setString(6, user.getAvatar() != null ? user.getAvatar() : "");
                }

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, password=?, name=?, role=?, status=?, avatar=? WHERE id=?";

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
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getName());
                pstmt.setString(4, user.getRole());
                pstmt.setString(5, user.getStatus());
                if (sql.contains("avatar")) {
                    pstmt.setString(6, user.getAvatar() != null ? user.getAvatar() : "");
                    pstmt.setInt(7, user.getId());
                } else {
                    pstmt.setInt(6, user.getId());
                }

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";

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
