package feedbacksystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JDialog {

    JTextField nameField, emailField, regField;
    JPasswordField passwordField;
    boolean isLoginMode = true;
    JPanel glassPanel;
    DashboardPage parentDashboard;

    public LoginPage(DashboardPage parent, boolean startAsLogin) {
        super(parent, "Course Feedback Authentication", true);
        this.parentDashboard = parent;
        this.isLoginMode = startAsLogin;

        setUndecorated(true);
        setSize(500, 550);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1));
        add(mainPanel);

        // Top Bar for Close Button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        topBar.setOpaque(false);
        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(new Color(200, 200, 200));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { closeBtn.setForeground(new Color(200, 200, 200)); }
        });
        closeBtn.addActionListener(e -> dispose());
        topBar.add(closeBtn);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Center Wrapper
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Glass Panel
        glassPanel = new JPanel();
        glassPanel.setBackground(new Color(255, 255, 255, 20));
        glassPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        glassPanel.setLayout(new GridBagLayout());

        centerWrapper.add(glassPanel);

        // Pre-instantiate form fields
        nameField = new JTextField(15);
        styleTextField(nameField);
        emailField = new JTextField(15);
        styleTextField(emailField);
        regField = new JTextField(15);
        styleTextField(regField);
        passwordField = new JPasswordField(15);
        styleTextField(passwordField);

        buildUI();
    }

    private void buildUI() {
        glassPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel(isLoginMode ? "Welcome Back" : "Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 10, 25, 10);
        glassPanel.add(title, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 1;

        if (!isLoginMode) {
            styleLabel(glassPanel, "Name:", row, gbc);
            gbc.gridx = 1;
            glassPanel.add(nameField, gbc);
            row++;
        }

        styleLabel(glassPanel, "Email:", row, gbc);
        gbc.gridx = 1;
        glassPanel.add(emailField, gbc);
        row++;

        if (!isLoginMode) {
            styleLabel(glassPanel, "Reg No:", row, gbc);
            gbc.gridx = 1;
            glassPanel.add(regField, gbc);
            row++;
        }

        styleLabel(glassPanel, "Password:", row, gbc);
        gbc.gridx = 1;
        glassPanel.add(passwordField, gbc);
        row++;

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        btnPanel.setOpaque(false);

        if (isLoginMode) {
            JButton loginBtn = new JButton("Login");
            styleButton(loginBtn, true);
            btnPanel.add(loginBtn);
            loginBtn.addActionListener(e -> loginStudent());

            JButton showRegBtn = new JButton("Don't have an account? Sign up");
            styleButton(showRegBtn, false);
            btnPanel.add(showRegBtn);
            showRegBtn.addActionListener(e -> {
                isLoginMode = false;
                buildUI();
            });
        } else {
            JButton registerBtn = new JButton("Register");
            styleButton(registerBtn, true);
            btnPanel.add(registerBtn);
            registerBtn.addActionListener(e -> registerStudent());

            JButton backLoginBtn = new JButton("Already have an account? Log in");
            styleButton(backLoginBtn, false);
            btnPanel.add(backLoginBtn);
            backLoginBtn.addActionListener(e -> {
                isLoginMode = true;
                buildUI();
            });
        }

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        glassPanel.add(btnPanel, gbc);

        glassPanel.revalidate();
        glassPanel.repaint();
    }

    private void styleLabel(JPanel panel, String text, int y, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(220, 220, 255));
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(label, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setOpaque(true);
        field.setBackground(new Color(10, 20, 40));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    }

    private void styleButton(JButton btn, boolean isPrimary) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        if (isPrimary) {
            btn.setBackground(new Color(255, 60, 90)); // Soft red/pink
        } else {
            btn.setBackground(new Color(40, 50, 70)); // Dark blue-grey
        }
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (isPrimary) btn.setBackground(new Color(255, 80, 110));
                else btn.setBackground(new Color(60, 70, 90));
            }
            public void mouseExited(MouseEvent e) {
                if (isPrimary) btn.setBackground(new Color(255, 60, 90));
                else btn.setBackground(new Color(40, 50, 70));
            }
        });
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Base dark blue
            g2d.setColor(new Color(12, 22, 45)); 
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Red glow top right
            RadialGradientPaint r1 = new RadialGradientPaint(
                    new Point(getWidth(), 0), getWidth() * 0.9f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(210, 50, 70, 150), new Color(12, 22, 45, 0)}
            );
            g2d.setPaint(r1);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Blue glow bottom left
            RadialGradientPaint r2 = new RadialGradientPaint(
                    new Point(0, getHeight()), getWidth() * 0.9f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(40, 90, 150, 150), new Color(12, 22, 45, 0)}
            );
            g2d.setPaint(r2);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void loginStudent() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and Password are required for login!");
            return;
        }

        // ---------------------------------------------------------
        // REAL DATABASE LOGIN
        // ---------------------------------------------------------
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            String sql = "SELECT * FROM students WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int sno = rs.getInt("sno");
                String dbName = rs.getString("name");
                String dbEmail = rs.getString("email");
                String dbRegNo = rs.getString("registration_number");

                // Update status to logged in
                String updateSql = "UPDATE students SET login_status = TRUE WHERE sno = ?";
                PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                psUpdate.setInt(1, sno);
                psUpdate.executeUpdate();

                JOptionPane.showMessageDialog(this, "Login Successful!");
                if (parentDashboard != null) {
                    parentDashboard.loginSuccess(sno, dbName, dbEmail, dbRegNo);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password! Or you may need to register first.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.");
        }
    }

    private void registerStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String regNo = regField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || regNo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (Name, Email, Reg No, Password) are required for registration!");
            return;
        }

        if (!email.toLowerCase().endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Email must be a valid @gmail.com address!");
            return;
        }

        String regRegex = "^\\d{2}[aA]3(1[aA]|5[aA])12\\d{2}$";
        if (!regNo.matches(regRegex)) {
            JOptionPane.showMessageDialog(this, "Invalid Registration Number format! Expected format like 22a31a1204 or 23a35a1204");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            // Check if student already exists
            String checkSql = "SELECT * FROM students WHERE registration_number = ? OR email = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, regNo);
            psCheck.setString(2, email);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Student with this Registration Number or Email already exists!");
                return;
            }

            String insertSql = "INSERT INTO students (name, email, registration_number, password, login_status) VALUES (?, ?, ?, ?, FALSE)";
            PreparedStatement psInsert = conn.prepareStatement(insertSql);
            psInsert.setString(1, name);
            psInsert.setString(2, email);
            psInsert.setString(3, regNo);
            psInsert.setString(4, password);

            int rows = psInsert.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! You can now login using Email and Password.");
                nameField.setText("");
                regField.setText("");
                passwordField.setText("");
                isLoginMode = true;
                buildUI();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.");
        }
    }
}
