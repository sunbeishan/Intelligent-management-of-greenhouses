package dao;

import db.DatabaseConfig;
import model.Expert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpertDAO {

    public List<Expert> getAllExperts() {
        List<Expert> experts = new ArrayList<>();
        String sql = "SELECT * FROM expert ORDER BY create_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean hasAvatar = false;
            for (int i = 1; i <= columnCount; i++) {
                if ("avatar".equalsIgnoreCase(metaData.getColumnName(i))) {
                    hasAvatar = true;
                    break;
                }
            }

            while (rs.next()) {
                Expert e = new Expert();
                e.setId(rs.getInt("id"));
                e.setUsername(rs.getString("username"));
                e.setPassword(rs.getString("password"));
                e.setName(rs.getString("name"));
                e.setPhone(rs.getString("phone"));
                e.setEmail(rs.getString("email"));
                e.setSpecialty(rs.getString("specialty"));
                e.setStatus(rs.getString("status"));
                if (hasAvatar) {
                    e.setAvatar(rs.getString("avatar"));
                }
                e.setCreateTime(rs.getTimestamp("create_time"));
                e.setUpdateTime(rs.getTimestamp("update_time"));
                experts.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return experts;
    }

    public Expert getExpertById(int id) {
        String sql = "SELECT * FROM expert WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                boolean hasAvatar = false;
                for (int i = 1; i <= columnCount; i++) {
                    if ("avatar".equalsIgnoreCase(metaData.getColumnName(i))) {
                        hasAvatar = true;
                        break;
                    }
                }
                
                Expert e = new Expert();
                e.setId(rs.getInt("id"));
                e.setUsername(rs.getString("username"));
                e.setPassword(rs.getString("password"));
                e.setName(rs.getString("name"));
                e.setPhone(rs.getString("phone"));
                e.setEmail(rs.getString("email"));
                e.setSpecialty(rs.getString("specialty"));
                e.setStatus(rs.getString("status"));
                if (hasAvatar) {
                    e.setAvatar(rs.getString("avatar"));
                }
                e.setCreateTime(rs.getTimestamp("create_time"));
                e.setUpdateTime(rs.getTimestamp("update_time"));
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Expert getExpertByUsername(String username) {
        String sql = "SELECT * FROM expert WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                boolean hasAvatar = false;
                for (int i = 1; i <= columnCount; i++) {
                    if ("avatar".equalsIgnoreCase(metaData.getColumnName(i))) {
                        hasAvatar = true;
                        break;
                    }
                }
                
                Expert e = new Expert();
                e.setId(rs.getInt("id"));
                e.setUsername(rs.getString("username"));
                e.setPassword(rs.getString("password"));
                e.setName(rs.getString("name"));
                e.setPhone(rs.getString("phone"));
                e.setEmail(rs.getString("email"));
                e.setSpecialty(rs.getString("specialty"));
                e.setStatus(rs.getString("status"));
                if (hasAvatar) {
                    e.setAvatar(rs.getString("avatar"));
                }
                e.setCreateTime(rs.getTimestamp("create_time"));
                e.setUpdateTime(rs.getTimestamp("update_time"));
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addExpert(Expert expert) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 先检查表是否有avatar列
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet columns = dbMeta.getColumns(null, null, "expert", "avatar");
            boolean hasAvatar = columns.next();
            columns.close();
            
            String sql;
            if (hasAvatar) {
                sql = "INSERT INTO expert (username, password, name, phone, email, specialty, status, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO expert (username, password, name, phone, email, specialty, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, expert.getUsername());
                pstmt.setString(2, expert.getPassword());
                pstmt.setString(3, expert.getName());
                pstmt.setString(4, expert.getPhone() != null ? expert.getPhone() : "");
                pstmt.setString(5, expert.getEmail() != null ? expert.getEmail() : "");
                pstmt.setString(6, expert.getSpecialty());
                pstmt.setString(7, expert.getStatus() != null ? expert.getStatus() : "启用");
                
                if (hasAvatar) {
                    pstmt.setString(8, expert.getAvatar() != null ? expert.getAvatar() : "");
                }

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateExpert(Expert expert) {
        // 先获取现有数据
        Expert existing = getExpertById(expert.getId());
        if (existing == null) {
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            // 先检查表是否有avatar列
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet columns = dbMeta.getColumns(null, null, "expert", "avatar");
            boolean hasAvatar = columns.next();
            columns.close();
            
            String sql;
            if (hasAvatar) {
                sql = "UPDATE expert SET username=?, password=?, name=?, phone=?, email=?, specialty=?, status=?, avatar=? WHERE id=?";
            } else {
                sql = "UPDATE expert SET username=?, password=?, name=?, phone=?, email=?, specialty=?, status=? WHERE id=?";
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, expert.getUsername());
                if (expert.getPassword() != null && !expert.getPassword().isEmpty()) {
                    pstmt.setString(2, expert.getPassword());
                } else {
                    pstmt.setString(2, existing.getPassword());
                }
                pstmt.setString(3, expert.getName());
                pstmt.setString(4, expert.getPhone() != null ? expert.getPhone() : "");
                pstmt.setString(5, expert.getEmail() != null ? expert.getEmail() : "");
                pstmt.setString(6, expert.getSpecialty());
                pstmt.setString(7, expert.getStatus());
                
                if (hasAvatar) {
                    if (expert.getAvatar() != null && !expert.getAvatar().isEmpty()) {
                        pstmt.setString(8, expert.getAvatar());
                    } else {
                        pstmt.setString(8, existing.getAvatar() != null ? existing.getAvatar() : "");
                    }
                    pstmt.setInt(9, expert.getId());
                } else {
                    pstmt.setInt(8, expert.getId());
                }

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteExpert(int id) {
        String sql = "DELETE FROM expert WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}