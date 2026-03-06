package feedbacksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class CourseDAO {

    public void addCourse(Course course) {

        String sql = "INSERT INTO courses(course_name) VALUES (?)";

        try {

            Connection conn = DBConnect.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed!");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, course.getCourseName());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Course Inserted Successfully!");
            }

            conn.close();

        } catch (SQLException e) {

            System.out.println("SQL ERROR CODE: " + e.getErrorCode());
            System.out.println("SQL MESSAGE: " + e.getMessage());
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Error inserting course!");
        }
    }
}