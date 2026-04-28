package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/agriculture_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "Scs20050122+.";

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功");
            return conn;
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            throw e;
        }
    }
}