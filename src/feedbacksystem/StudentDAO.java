package feedbacksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class StudentDAO {

    public void addStudent(Student student) {

        String sql = "INSERT INTO students(name, email) VALUES (?, ?)";

        try {

            Connection conn = DBConnect.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed!");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Inserted successfully!");
                JOptionPane.showMessageDialog(null, "Student Inserted Successfully!");
            }

            conn.close();

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Email already exists!");
            } else {
                System.out.println("Insert Failed!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inserting student!");
            }
        }
    }
}