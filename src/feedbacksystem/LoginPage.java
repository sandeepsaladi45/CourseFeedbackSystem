package feedbacksystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    JTextField nameField, emailField, regField;

    public LoginPage() {

        setTitle("Student Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel title = new JLabel("Student Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // Name
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        add(nameField, gbc);

        // Email
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(15);
        add(emailField, gbc);

        // Registration Number
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Registration No:"), gbc);

        gbc.gridx = 1;
        regField = new JTextField(15);
        add(regField, gbc);

        JButton loginBtn = new JButton("Login");

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> loginStudent());
    }

    private void loginStudent() {

        String nameInput = nameField.getText().trim();
        String emailInput = emailField.getText().trim();
        String regNo = regField.getText().trim();

        if (nameInput.isEmpty() || emailInput.isEmpty() || regNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required!");
            return;
        }

        try {
            Connection conn = DBConnect.getConnection();

            String sql = "SELECT * FROM STUDENTS WHERE REGISTRATION_NUMBER = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, regNo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                boolean isLoggedIn = rs.getBoolean("LOGIN_STATUS");

                if (isLoggedIn) {
                    JOptionPane.showMessageDialog(this,
                            "Student already logged in!");
                    return;
                }

                int sno = rs.getInt("SNO");
                String realName = rs.getString("NAME");
                String realEmail = rs.getString("EMAIL");

                // Update login status
                String updateSql =
                        "UPDATE STUDENTS SET LOGIN_STATUS = TRUE WHERE SNO = ?";
                PreparedStatement ps2 = conn.prepareStatement(updateSql);
                ps2.setInt(1, sno);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "Login Successful!");

                new StudentDashboard(sno, realName, realEmail, regNo);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Registration Number!");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}