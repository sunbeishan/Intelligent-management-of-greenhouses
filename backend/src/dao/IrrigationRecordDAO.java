package dao;

import db.DatabaseConfig;
import model.IrrigationRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IrrigationRecordDAO {

    public List<IrrigationRecord> getAllRecords() {
        List<IrrigationRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, f.name as farmland_name, d.name as device_name, p.plan_name " +
                     "FROM irrigation_records r " +
                     "LEFT JOIN farmland f ON r.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON r.device_id = d.id " +
                     "LEFT JOIN irrigation_plans p ON r.plan_id = p.id " +
                     "ORDER BY r.start_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public IrrigationRecord getRecordById(int id) {
        String sql = "SELECT r.*, f.name as farmland_name, d.name as device_name, p.plan_name " +
                     "FROM irrigation_records r " +
                     "LEFT JOIN farmland f ON r.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON r.device_id = d.id " +
                     "LEFT JOIN irrigation_plans p ON r.plan_id = p.id " +
                     "WHERE r.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IrrigationRecord> getRecordsByFarmland(int farmlandId) {
        List<IrrigationRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, f.name as farmland_name, d.name as device_name, p.plan_name " +
                     "FROM irrigation_records r " +
                     "LEFT JOIN farmland f ON r.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON r.device_id = d.id " +
                     "LEFT JOIN irrigation_plans p ON r.plan_id = p.id " +
                     "WHERE r.farmland_id = ? ORDER BY r.start_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmlandId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<IrrigationRecord> getRecordsByDateRange(String startDate, String endDate) {
        List<IrrigationRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, f.name as farmland_name, d.name as device_name, p.plan_name " +
                     "FROM irrigation_records r " +
                     "LEFT JOIN farmland f ON r.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON r.device_id = d.id " +
                     "LEFT JOIN irrigation_plans p ON r.plan_id = p.id " +
                     "WHERE DATE(r.start_time) BETWEEN ? AND ? ORDER BY r.start_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public boolean addRecord(IrrigationRecord record) {
        String sql = "INSERT INTO irrigation_records (farmland_id, device_id, plan_id, start_time, end_time, duration, water_amount, type, status, operator, remark) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getFarmlandId());
            pstmt.setInt(2, record.getDeviceId());
            if (record.getPlanId() > 0) {
                pstmt.setInt(3, record.getPlanId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setTimestamp(4, record.getStartTime());
            pstmt.setTimestamp(5, record.getEndTime());
            pstmt.setInt(6, record.getDuration());
            pstmt.setDouble(7, record.getWaterAmount());
            pstmt.setString(8, record.getType() != null ? record.getType() : "手动");
            pstmt.setString(9, record.getStatus() != null ? record.getStatus() : "已完成");
            pstmt.setString(10, record.getOperator() != null ? record.getOperator() : "");
            pstmt.setString(11, record.getRemark() != null ? record.getRemark() : "");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRecord(IrrigationRecord record) {
        String sql = "UPDATE irrigation_records SET farmland_id=?, device_id=?, plan_id=?, start_time=?, " +
                     "end_time=?, duration=?, water_amount=?, type=?, status=?, operator=?, remark=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getFarmlandId());
            pstmt.setInt(2, record.getDeviceId());
            if (record.getPlanId() > 0) {
                pstmt.setInt(3, record.getPlanId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setTimestamp(4, record.getStartTime());
            pstmt.setTimestamp(5, record.getEndTime());
            pstmt.setInt(6, record.getDuration());
            pstmt.setDouble(7, record.getWaterAmount());
            pstmt.setString(8, record.getType() != null ? record.getType() : "手动");
            pstmt.setString(9, record.getStatus() != null ? record.getStatus() : "已完成");
            pstmt.setString(10, record.getOperator() != null ? record.getOperator() : "");
            pstmt.setString(11, record.getRemark() != null ? record.getRemark() : "");
            pstmt.setInt(12, record.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRecord(int id) {
        String sql = "DELETE FROM irrigation_records WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalWaterUsage(String startDate, String endDate) {
        String sql = "SELECT COALESCE(SUM(water_amount), 0) as total FROM irrigation_records " +
                     "WHERE DATE(start_time) BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private IrrigationRecord extractRecord(ResultSet rs) throws SQLException {
        IrrigationRecord r = new IrrigationRecord();
        r.setId(rs.getInt("id"));
        r.setFarmlandId(rs.getInt("farmland_id"));
        r.setFarmlandName(rs.getString("farmland_name"));
        r.setDeviceId(rs.getInt("device_id"));
        r.setDeviceName(rs.getString("device_name"));
        r.setPlanId(rs.getInt("plan_id"));
        r.setPlanName(rs.getString("plan_name"));
        r.setStartTime(rs.getTimestamp("start_time"));
        r.setEndTime(rs.getTimestamp("end_time"));
        r.setDuration(rs.getInt("duration"));
        r.setWaterAmount(rs.getDouble("water_amount"));
        r.setType(rs.getString("type"));
        r.setStatus(rs.getString("status"));
        r.setOperator(rs.getString("operator"));
        r.setRemark(rs.getString("remark"));
        r.setCreateTime(rs.getTimestamp("create_time"));
        return r;
    }
}