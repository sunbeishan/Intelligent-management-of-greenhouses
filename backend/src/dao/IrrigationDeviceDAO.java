package dao;

import db.DatabaseConfig;
import model.IrrigationDevice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IrrigationDeviceDAO {

    public List<IrrigationDevice> getAllDevices() {
        List<IrrigationDevice> devices = new ArrayList<>();
        String sql = "SELECT d.*, f.name as farmland_name FROM irrigation_devices d " +
                     "LEFT JOIN farmland f ON d.farmland_id = f.id ORDER BY d.create_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                devices.add(extractDevice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    public IrrigationDevice getDeviceById(int id) {
        String sql = "SELECT d.*, f.name as farmland_name FROM irrigation_devices d " +
                     "LEFT JOIN farmland f ON d.farmland_id = f.id WHERE d.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDevice(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IrrigationDevice> getDevicesByFarmland(int farmlandId) {
        List<IrrigationDevice> devices = new ArrayList<>();
        String sql = "SELECT d.*, f.name as farmland_name FROM irrigation_devices d " +
                     "LEFT JOIN farmland f ON d.farmland_id = f.id WHERE d.farmland_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmlandId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                devices.add(extractDevice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    public boolean addDevice(IrrigationDevice device) {
        String sql = "INSERT INTO irrigation_devices (name, farmland_id, device_type, status, water_flow, coverage_area, install_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, device.getName());
            pstmt.setInt(2, device.getFarmlandId());
            pstmt.setString(3, device.getDeviceType() != null ? device.getDeviceType() : "");
            pstmt.setString(4, device.getStatus() != null ? device.getStatus() : "正常");
            pstmt.setDouble(5, device.getWaterFlow());
            pstmt.setDouble(6, device.getCoverageArea());
            pstmt.setDate(7, device.getInstallDate());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDevice(IrrigationDevice device) {
        String sql = "UPDATE irrigation_devices SET name=?, farmland_id=?, device_type=?, status=?, " +
                     "water_flow=?, coverage_area=?, install_date=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, device.getName());
            pstmt.setInt(2, device.getFarmlandId());
            pstmt.setString(3, device.getDeviceType() != null ? device.getDeviceType() : "");
            pstmt.setString(4, device.getStatus() != null ? device.getStatus() : "正常");
            pstmt.setDouble(5, device.getWaterFlow());
            pstmt.setDouble(6, device.getCoverageArea());
            pstmt.setDate(7, device.getInstallDate());
            pstmt.setInt(8, device.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDevice(int id) {
        String sql = "DELETE FROM irrigation_devices WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private IrrigationDevice extractDevice(ResultSet rs) throws SQLException {
        IrrigationDevice d = new IrrigationDevice();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setFarmlandId(rs.getInt("farmland_id"));
        d.setFarmlandName(rs.getString("farmland_name"));
        d.setDeviceType(rs.getString("device_type"));
        d.setStatus(rs.getString("status"));
        d.setWaterFlow(rs.getDouble("water_flow"));
        d.setCoverageArea(rs.getDouble("coverage_area"));
        d.setInstallDate(rs.getDate("install_date"));
        d.setCreateTime(rs.getTimestamp("create_time"));
        d.setUpdateTime(rs.getTimestamp("update_time"));
        return d;
    }
}