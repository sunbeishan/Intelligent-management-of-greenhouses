import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/agriculture_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Scs20050122+.";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("驱动加载成功");
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("数据库连接成功");
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("连接失败: " + e.getMessage());
        }
    }
}