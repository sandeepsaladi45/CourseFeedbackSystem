package feedbacksystem;

import javax.swing.*;
import java.awt.*;

public class FrontEnd extends JFrame {

    JTextField nameField, emailField, courseField;
    JTextArea outputArea;

    public FrontEnd() {

        setTitle("Course Feedback System");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== Main Panel =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 249)); // soft blue-grey
        add(mainPanel);

        // ===== Title =====
        JLabel title = new JLabel("Course Feedback Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // ===== Center Container =====
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.setBackground(new Color(240, 245, 249));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ====================================
        // STUDENT PANEL
        // ====================================
        JPanel studentPanel = new JPanel(new GridBagLayout());
        studentPanel.setBackground(Color.WHITE);
        studentPanel.setBorder(BorderFactory.createTitledBorder("Student Section"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        nameField = new JTextField(15);
        emailField = new JTextField(15);

        JButton addStudentBtn = styledButton("Add Student");

        gbc.gridx = 0; gbc.gridy = 0;
        studentPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        studentPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        studentPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        studentPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        studentPanel.add(addStudentBtn, gbc);

        centerPanel.add(studentPanel);

        // ====================================
        // COURSE PANEL
        // ====================================
        JPanel coursePanel = new JPanel(new GridBagLayout());
        coursePanel.setBackground(Color.WHITE);
        coursePanel.setBorder(BorderFactory.createTitledBorder("Course Section"));

        courseField = new JTextField(15);
        JButton addCourseBtn = styledButton("Add Course");

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        coursePanel.add(new JLabel("Course Name:"), gbc);

        gbc.gridx = 1;
        coursePanel.add(courseField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        coursePanel.add(addCourseBtn, gbc);

        centerPanel.add(coursePanel);

        // ====================================
        // OUTPUT AREA
        // ====================================
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("Output"));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(800, 150));

        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // ====================================
        // BUTTON ACTIONS
        // ====================================

        addStudentBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required!");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
                JOptionPane.showMessageDialog(this, "Enter valid Gmail address!");
                return;
            }

            Student student = new Student(name, email);
            new StudentDAO().addStudent(student);

            nameField.setText("");
            emailField.setText("");
        });

        addCourseBtn.addActionListener(e -> {
            String courseName = courseField.getText().trim();

            if (courseName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter course name!");
                return;
            }

            Course course = new Course(courseName);
            new CourseDAO().addCourse(course);

            courseField.setText("");
        });
    }

    // Styled Button Method
    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}