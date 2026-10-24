package dbpackage;

import java.sql.*;

public class DBConnection {
    public static Connection getConnection() throws Exception {
        // Make sure the MySQL JDBC driver JAR is added to your classpath
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cafeteria", "root", "BestHacker@777");
    }
}
