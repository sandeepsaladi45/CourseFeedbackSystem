package feedbacksystem;

import java.sql.*;

public class FeedbackDAO {

    public void addFeedback(Feedback feedback) {

        String sql = "INSERT INTO feedback(student_id,course_id,rating,comments) VALUES (?,?,?,?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, feedback.getStudentId());
            ps.setInt(2, feedback.getCourseId());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComments());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet viewFeedback() {

        try {
            Connection conn = DBConnect.getConnection();

            String sql = "SELECT s.name, c.course_name, f.rating, f.comments " +
                    "FROM feedback f " +
                    "JOIN students s ON f.student_id = s.student_id " +
                    "JOIN courses c ON f.course_id = c.course_id";

            Statement st = conn.createStatement();
            return st.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}