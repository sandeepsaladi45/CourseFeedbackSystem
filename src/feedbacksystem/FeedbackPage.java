package feedbacksystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FeedbackPage extends JFrame {

    JComboBox<String> studentBox;
    JComboBox<String> courseBox;
    JComboBox<Integer> ratingBox;
    JTextArea commentArea;

    public FeedbackPage() {

        setTitle("Course Feedback System");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Course Feedback System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);

        JPanel rightIcons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightIcons.setOpaque(false);

        JLabel notificationIcon = new JLabel("🔔");
        notificationIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel profileIcon = new JLabel("👤");
        profileIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        rightIcons.add(notificationIcon);
        rightIcons.add(profileIcon);

        header.add(rightIcons, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // ================= CENTER FORM =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Student (Reg No):"), gbc);

        gbc.gridx = 1;
        studentBox = new JComboBox<>();
        loadStudents();
        formPanel.add(studentBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Select Course:"), gbc);

        gbc.gridx = 1;
        courseBox = new JComboBox<>();
        loadCourses();
        formPanel.add(courseBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Rating:"), gbc);

        gbc.gridx = 1;
        ratingBox = new JComboBox<>(new Integer[]{1,2,3,4,5});
        formPanel.add(ratingBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Comments:"), gbc);

        gbc.gridx = 1;
        commentArea = new JTextArea(5,15);
        formPanel.add(new JScrollPane(commentArea), gbc);

        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.setBackground(new Color(0, 123, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> submitFeedback());

        mainPanel.add(formPanel, BorderLayout.CENTER);
    }

    // ================= GRADIENT PANEL =================
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();

            Color color1 = new Color(0, 123, 255);
            Color color2 = new Color(155, 89, 182);

            GradientPaint gp = new GradientPaint(
                    0, 0, color1,
                    0, height, color2);

            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    // ================= DATABASE METHODS =================
    private void loadStudents() {
        try {
            Connection conn = DBConnect.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT REGISTRATION_NUMBER FROM STUDENTS");

            while (rs.next()) {
                studentBox.addItem(
                        rs.getString("REGISTRATION_NUMBER"));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCourses() {
        try {
            Connection conn = DBConnect.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT COURSE_NAME FROM COURSES");

            while (rs.next()) {
                courseBox.addItem(
                        rs.getString("COURSE_NAME"));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitFeedback() {

        String regNo = (String) studentBox.getSelectedItem();
        String courseName = (String) courseBox.getSelectedItem();
        int rating = (int) ratingBox.getSelectedItem();
        String comments = commentArea.getText();

        try {
            Connection conn = DBConnect.getConnection();

            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT SNO FROM STUDENTS WHERE REGISTRATION_NUMBER=?");
            ps1.setString(1, regNo);
            ResultSet rs1 = ps1.executeQuery();

            int studentSno = 0;
            if (rs1.next()) {
                studentSno = rs1.getInt("SNO");
            }

            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT COURSE_ID FROM COURSES WHERE COURSE_NAME=?");
            ps2.setString(1, courseName);
            ResultSet rs2 = ps2.executeQuery();

            int courseId = 0;
            if (rs2.next()) {
                courseId = rs2.getInt("COURSE_ID");
            }

            PreparedStatement ps3 = conn.prepareStatement(
                    "INSERT INTO FEEDBACK(STUDENT_SNO, COURSE_ID, RATING, COMMENTS) VALUES (?, ?, ?, ?)");

            ps3.setInt(1, studentSno);
            ps3.setInt(2, courseId);
            ps3.setInt(3, rating);
            ps3.setString(4, comments);

            ps3.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Feedback Submitted Successfully!");

            commentArea.setText("");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}