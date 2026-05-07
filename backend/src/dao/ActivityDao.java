package dao;

import db.DatabaseConfig;

import java.sql.*;

public class ActivityDao {

    public static String getRecentActivities() {
        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT type, content, create_time FROM activities ORDER BY create_time DESC LIMIT 10";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                first = false;

                String type = rs.getString("type");
                String content = rs.getString("content");
                Timestamp createTime = rs.getTimestamp("create_time");

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String timeStr = sdf.format(createTime);

                json.append(String.format("{\"time\":\"%s\",\"type\":\"%s\",\"content\":\"%s\"}",
                    timeStr, type, content));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return getMockActivities();
        }

        json.append("]");
        return json.toString();
    }

    public static boolean recordActivity(String type, String content) {
        String sql = "INSERT INTO activities (type, content) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setString(2, content);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getMockActivities() {
        return "[{\"time\":\"2026/05/07 08:30:00\",\"type\":\"环境监测\",\"content\":\"大棚A温度正常\"}," +
               "{\"time\":\"2026/05/07 08:25:00\",\"type\":\"环境监测\",\"content\":\"大棚B湿度正常\"}," +
               "{\"time\":\"2026/05/07 08:20:00\",\"type\":\"专家咨询\",\"content\":\"病虫害防治咨询已提交\"}," +
               "{\"time\":\"2026/05/07 08:15:00\",\"type\":\"农资采购\",\"content\":\"尿素采购单已创建\"}," +
               "{\"time\":\"2026/05/07 08:10:00\",\"type\":\"产品出售\",\"content\":\"西红柿销售订单已完成\"}," +
               "{\"time\":\"2026/05/07 08:05:00\",\"type\":\"农田管理\",\"content\":\"农田信息已更新\"}]";
    }
}
