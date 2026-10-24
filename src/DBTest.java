import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        try {
            // No need for Class.forName in newer versions, but you can keep it
          Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/cafeteria",
                    "root",
                    "BestHacker@777");

            System.out.println("Connected to MySQL successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}