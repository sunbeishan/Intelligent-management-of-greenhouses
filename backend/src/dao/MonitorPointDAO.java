package dao;

import db.DatabaseConfig;
import model.MonitorPoint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonitorPointDAO {

    public List<MonitorPoint> getAllMonitorPoints() {
        List<MonitorPoint> points = new ArrayList<>();
        String sql = "SELECT * FROM monitor_points ORDER BY create_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MonitorPoint mp = new MonitorPoint();
                mp.setId(rs.getInt("id"));
                mp.setName(rs.getString("name"));
                mp.setLocation(rs.getString("location"));
                mp.setFarmlandId(rs.getInt("farmland_id"));
                mp.setStatus(rs.getString("status"));
                mp.setTemperature(rs.getDouble("temperature"));
                mp.setHumidity(rs.getInt("humidity"));
                mp.setLight(rs.getInt("light"));
                mp.setCo2(rs.getInt("co2"));
                mp.setLastUpdate(rs.getTimestamp("last_update"));
                mp.setCreateTime(rs.getTimestamp("create_time"));
                points.add(mp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }

    public MonitorPoint getMonitorPointById(int id) {
        String sql = "SELECT * FROM monitor_points WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MonitorPoint mp = new MonitorPoint();
                mp.setId(rs.getInt("id"));
                mp.setName(rs.getString("name"));
                mp.setLocation(rs.getString("location"));
                mp.setFarmlandId(rs.getInt("farmland_id"));
                mp.setStatus(rs.getString("status"));
                mp.setTemperature(rs.getDouble("temperature"));
                mp.setHumidity(rs.getInt("humidity"));
                mp.setLight(rs.getInt("light"));
                mp.setCo2(rs.getInt("co2"));
                mp.setLastUpdate(rs.getTimestamp("last_update"));
                mp.setCreateTime(rs.getTimestamp("create_time"));
                return mp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MonitorPoint getMonitorPointByName(String name) {
        String sql = "SELECT * FROM monitor_points WHERE name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MonitorPoint mp = new MonitorPoint();
                mp.setId(rs.getInt("id"));
                mp.setName(rs.getString("name"));
                mp.setLocation(rs.getString("location"));
                mp.setFarmlandId(rs.getInt("farmland_id"));
                mp.setStatus(rs.getString("status"));
                mp.setTemperature(rs.getDouble("temperature"));
                mp.setHumidity(rs.getInt("humidity"));
                mp.setLight(rs.getInt("light"));
                mp.setCo2(rs.getInt("co2"));
                mp.setLastUpdate(rs.getTimestamp("last_update"));
                mp.setCreateTime(rs.getTimestamp("create_time"));
                return mp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addMonitorPoint(MonitorPoint point) {
        String sql = "INSERT INTO monitor_points (name, location, farmland_id, status, temperature, humidity, light, co2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, point.getName());
            pstmt.setString(2, point.getLocation() != null ? point.getLocation() : "");
            pstmt.setInt(3, point.getFarmlandId());
            pstmt.setString(4, point.getStatus() != null ? point.getStatus() : "正常");
            pstmt.setDouble(5, point.getTemperature());
            pstmt.setInt(6, point.getHumidity());
            pstmt.setInt(7, point.getLight());
            pstmt.setInt(8, point.getCo2());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMonitorPoint(MonitorPoint point) {
        String sql = "UPDATE monitor_points SET name=?, location=?, farmland_id=?, status=?, temperature=?, humidity=?, light=?, co2=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, point.getName());
            pstmt.setString(2, point.getLocation() != null ? point.getLocation() : "");
            pstmt.setInt(3, point.getFarmlandId());
            pstmt.setString(4, point.getStatus());
            pstmt.setDouble(5, point.getTemperature());
            pstmt.setInt(6, point.getHumidity());
            pstmt.setInt(7, point.getLight());
            pstmt.setInt(8, point.getCo2());
            pstmt.setInt(9, point.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMonitorPoint(int id) {
        String sql = "DELETE FROM monitor_points WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MonitorPoint> getMonitorPointsByFarmlandId(int farmlandId) {
        List<MonitorPoint> points = new ArrayList<>();
        String sql = "SELECT * FROM monitor_points WHERE farmland_id = ? ORDER BY create_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmlandId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MonitorPoint mp = new MonitorPoint();
                mp.setId(rs.getInt("id"));
                mp.setName(rs.getString("name"));
                mp.setLocation(rs.getString("location"));
                mp.setFarmlandId(rs.getInt("farmland_id"));
                mp.setStatus(rs.getString("status"));
                mp.setTemperature(rs.getDouble("temperature"));
                mp.setHumidity(rs.getInt("humidity"));
                mp.setLight(rs.getInt("light"));
                mp.setCo2(rs.getInt("co2"));
                mp.setLastUpdate(rs.getTimestamp("last_update"));
                mp.setCreateTime(rs.getTimestamp("create_time"));
                points.add(mp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }
}