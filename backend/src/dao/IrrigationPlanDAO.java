package dao;

import db.DatabaseConfig;
import model.IrrigationPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IrrigationPlanDAO {

    public List<IrrigationPlan> getAllPlans() {
        List<IrrigationPlan> plans = new ArrayList<>();
        String sql = "SELECT p.*, f.name as farmland_name, d.name as device_name " +
                     "FROM irrigation_plans p " +
                     "LEFT JOIN farmland f ON p.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON p.device_id = d.id " +
                     "ORDER BY p.create_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                plans.add(extractPlan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }

    public IrrigationPlan getPlanById(int id) {
        String sql = "SELECT p.*, f.name as farmland_name, d.name as device_name " +
                     "FROM irrigation_plans p " +
                     "LEFT JOIN farmland f ON p.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON p.device_id = d.id " +
                     "WHERE p.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPlan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IrrigationPlan> getPlansByFarmland(int farmlandId) {
        List<IrrigationPlan> plans = new ArrayList<>();
        String sql = "SELECT p.*, f.name as farmland_name, d.name as device_name " +
                     "FROM irrigation_plans p " +
                     "LEFT JOIN farmland f ON p.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON p.device_id = d.id " +
                     "WHERE p.farmland_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmlandId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                plans.add(extractPlan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }

    public List<IrrigationPlan> getActivePlans() {
        List<IrrigationPlan> plans = new ArrayList<>();
        String sql = "SELECT p.*, f.name as farmland_name, d.name as device_name " +
                     "FROM irrigation_plans p " +
                     "LEFT JOIN farmland f ON p.farmland_id = f.id " +
                     "LEFT JOIN irrigation_devices d ON p.device_id = d.id " +
                     "WHERE p.status = '启用'";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                plans.add(extractPlan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }

    public boolean addPlan(IrrigationPlan plan) {
        String sql = "INSERT INTO irrigation_plans (farmland_id, device_id, plan_name, start_time, duration, water_amount, frequency, week_days, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, plan.getFarmlandId());
            pstmt.setInt(2, plan.getDeviceId());
            pstmt.setString(3, plan.getPlanName() != null ? plan.getPlanName() : "");
            pstmt.setTime(4, plan.getStartTime());
            pstmt.setInt(5, plan.getDuration());
            pstmt.setDouble(6, plan.getWaterAmount());
            pstmt.setString(7, plan.getFrequency() != null ? plan.getFrequency() : "每天");
            pstmt.setString(8, plan.getWeekDays() != null ? plan.getWeekDays() : "1,2,3,4,5,6,7");
            pstmt.setString(9, plan.getStatus() != null ? plan.getStatus() : "启用");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePlan(IrrigationPlan plan) {
        String sql = "UPDATE irrigation_plans SET farmland_id=?, device_id=?, plan_name=?, start_time=?, " +
                     "duration=?, water_amount=?, frequency=?, week_days=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, plan.getFarmlandId());
            pstmt.setInt(2, plan.getDeviceId());
            pstmt.setString(3, plan.getPlanName() != null ? plan.getPlanName() : "");
            pstmt.setTime(4, plan.getStartTime());
            pstmt.setInt(5, plan.getDuration());
            pstmt.setDouble(6, plan.getWaterAmount());
            pstmt.setString(7, plan.getFrequency() != null ? plan.getFrequency() : "每天");
            pstmt.setString(8, plan.getWeekDays() != null ? plan.getWeekDays() : "1,2,3,4,5,6,7");
            pstmt.setString(9, plan.getStatus() != null ? plan.getStatus() : "启用");
            pstmt.setInt(10, plan.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePlan(int id) {
        String sql = "DELETE FROM irrigation_plans WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private IrrigationPlan extractPlan(ResultSet rs) throws SQLException {
        IrrigationPlan p = new IrrigationPlan();
        p.setId(rs.getInt("id"));
        p.setFarmlandId(rs.getInt("farmland_id"));
        p.setFarmlandName(rs.getString("farmland_name"));
        p.setDeviceId(rs.getInt("device_id"));
        p.setDeviceName(rs.getString("device_name"));
        p.setPlanName(rs.getString("plan_name"));
        p.setStartTime(rs.getTime("start_time"));
        p.setDuration(rs.getInt("duration"));
        p.setWaterAmount(rs.getDouble("water_amount"));
        p.setFrequency(rs.getString("frequency"));
        p.setWeekDays(rs.getString("week_days"));
        p.setStatus(rs.getString("status"));
        p.setCreateTime(rs.getTimestamp("create_time"));
        p.setUpdateTime(rs.getTimestamp("update_time"));
        return p;
    }
}