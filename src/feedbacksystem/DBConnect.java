package feedbacksystem;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            String url = "jdbc:mysql://localhost:3306/feedback_system";
            String user = "root";
            String password = "S@ndeep45";

            Connection con = DriverManager.getConnection(url,user,password);

            return con;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}