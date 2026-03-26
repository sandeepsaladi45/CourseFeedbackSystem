package feedbacksystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentDashboard extends JFrame {

    int studentSno;

    public StudentDashboard(int sno, String name,
                            String email, String regNo) {

        this.studentSno = sno;

        setTitle("Student Dashboard");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6,1,10,10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("Welcome Student!", JLabel.CENTER));
        add(new JLabel("Name: " + name));
        add(new JLabel("Email: " + email));
        add(new JLabel("Registration No: " + regNo));

        JButton feedbackBtn = new JButton("Give Feedback");
        JButton logoutBtn = new JButton("Logout");

        add(feedbackBtn);
        add(logoutBtn);

        feedbackBtn.addActionListener(e -> {
            new FeedbackPage();
            dispose();
        });

        logoutBtn.addActionListener(e -> logout());
    }

    private void logout() {

        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                    "UPDATE STUDENTS SET LOGIN_STATUS = FALSE WHERE SNO = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentSno);
            ps.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(this,
                    "Logged out successfully!");

            new DashboardPage().setVisible(true);
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}