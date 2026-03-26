package feedbacksystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class DashboardPage extends JFrame {

    private int studentSno = -1;
    private String studentName;
    private String studentEmail;
    private String studentRegNo;
    private boolean isLoggedIn = false;
    private JPanel authLinksPanel;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private CardLayout rootCardLayout;
    private JPanel rootCardPanel;

    // Feedback Variables
    private HashMap<String, Integer> ratings = new HashMap<>();
    private JTextArea commentArea;
    private JButton activeNav; // Track currently selected menu item
    private JComboBox<String> semesterDropdown;
    private JPanel dynamicSubjectsPanel;
    private int currentExpectedSubjects = 5;
    private JScrollPane formScrollPane;

    public DashboardPage() {
        setTitle("Course Feedback System - Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Fixed Top Nav Bar
        mainPanel.add(createTopNav(), BorderLayout.NORTH);

        rootCardLayout = new CardLayout();
        rootCardPanel = new JPanel(rootCardLayout);
        rootCardPanel.setOpaque(false);
        mainPanel.add(rootCardPanel, BorderLayout.CENTER);

        // Landing Page Screen
        JPanel landingPage = createLandingPage();
        rootCardPanel.add(landingPage, "LANDING");

        // Dashboard Overlay Screen
        JPanel dashboardView = new JPanel(new BorderLayout());
        dashboardView.setOpaque(false);

        // Sidebar inside dashboard view
        JPanel sidebar = createSidebar();
        dashboardView.add(sidebar, BorderLayout.WEST);

        // Content Area inside dashboard view
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(createSubmitFeedbackCard(), "SUBMIT");
        contentPanel.add(new FeedbackListView(), "LIST");

        dashboardView.add(contentPanel, BorderLayout.CENTER);
        rootCardPanel.add(dashboardView, "APP_DASHBOARD");

        // Start at Landing
        rootCardLayout.show(rootCardPanel, "LANDING");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Glass effect fade (Semi-transparent overlay over the main background)
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint glassPaint = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 20),
                    getWidth(), 0, new Color(255, 255, 255, 2)
                );
                g2d.setPaint(glassPaint);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setOpaque(false); // Essential! Let main background pass through
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 255, 255, 30)));

        // Profile Section removed per request

        // Menu Buttons
        JPanel menuPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Menu Buttons
        JButton submitBtn = createMenuButton("Submit Feedback", "SUBMIT");
        menuPanel.add(submitBtn);
        menuPanel.add(createMenuButton("My Feedback", "LIST"));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Automatically set highlighting active on the first button after UI renders
        SwingUtilities.invokeLater(() -> submitBtn.doClick());

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(new Color(200, 210, 240)); // Idle soft blue-white
        btn.setOpaque(false); // Clean transparency for glass bg
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNav) {
                    btn.setForeground(Color.WHITE); // Brighten on hover
                }
            }

            public void mouseExited(MouseEvent e) {
                if (btn != activeNav) {
                    btn.setForeground(new Color(200, 210, 240)); // Return to normal
                }
            }
        });

        if (cardName != null) {
            btn.addActionListener(e -> {
                if (activeNav != null) {
                    activeNav.setForeground(new Color(200, 210, 240));
                    activeNav.setBorder(new EmptyBorder(12, 25, 12, 25));
                }
                activeNav = btn;
                btn.setForeground(new Color(255, 60, 90)); // Soft Red/Pink Active highlight
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(255, 60, 90)),
                    new EmptyBorder(12, 21, 12, 25)
                ));
                cardLayout.show(contentPanel, cardName);
            });
        }
        return btn;
    }

    public void loginSuccess(int sno, String name, String email, String regNo) {
        this.studentSno = sno;
        this.studentName = name;
        this.studentEmail = email;
        this.studentRegNo = regNo;
        this.isLoggedIn = true;
        updateAuthUI();
    }

    private void updateAuthUI() {
        if (authLinksPanel == null) return;
        authLinksPanel.removeAll();
        if (isLoggedIn) {
            JLabel regLbl = new JLabel("Reg. No: " + studentRegNo + " ▼");
            regLbl.setForeground(Color.WHITE);
            regLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            regLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            regLbl.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JPopupMenu logoutMenu = new JPopupMenu();
                    JMenuItem logoutItem = new JMenuItem("Logout");
                    logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    logoutItem.addActionListener(ev -> {
                        int confirm = JOptionPane.showConfirmDialog(DashboardPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            isLoggedIn = false;
                            studentSno = -1;
                            studentName = null;
                            studentEmail = null;
                            studentRegNo = null;
                            updateAuthUI();
                            rootCardLayout.show(rootCardPanel, "LANDING");
                        }
                    });
                    logoutMenu.add(logoutItem);
                    logoutMenu.show(regLbl, 0, regLbl.getHeight());
                }
            });
            authLinksPanel.add(regLbl);
        } else {
            JLabel loginLbl = new JLabel("Log in");
            loginLbl.setForeground(Color.WHITE);
            loginLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            loginLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            loginLbl.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    new LoginPage(DashboardPage.this, true).setVisible(true);
                }
            });
            authLinksPanel.add(loginLbl);

            JButton signupBtn = new JButton("Sign up");
            signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            signupBtn.setForeground(Color.WHITE);
            signupBtn.setBackground(new Color(255, 60, 90));
            signupBtn.setBorder(new EmptyBorder(6, 20, 6, 20));
            signupBtn.setFocusPainted(false);
            signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            signupBtn.addActionListener(e -> {
                new LoginPage(DashboardPage.this, false).setVisible(true);
            });
            authLinksPanel.add(signupBtn);
        }
        authLinksPanel.revalidate();
        authLinksPanel.repaint();
    }

    private JPanel createTopNav() {
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setOpaque(false);
        topNav.setBorder(new EmptyBorder(30, 50, 0, 50));

        JLabel logoLabel = new JLabel("Course Feedback System");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        topNav.add(logoLabel, BorderLayout.WEST);

        JPanel navLinks = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        navLinks.setOpaque(false);
        String[] links = {"Home", "About", "Notifications", "Contact us"};
        for (String link : links) {
            JLabel lbl = new JLabel(link);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lbl.setForeground(new Color(220, 230, 255));
            lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            lbl.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { lbl.setForeground(Color.WHITE); }
                public void mouseExited(MouseEvent e) { lbl.setForeground(new Color(220, 230, 255)); }
                public void mouseClicked(MouseEvent e) {
                    if (link.equals("Home")) {
                        rootCardLayout.show(rootCardPanel, "LANDING");
                    } else if (link.equals("Notifications")) {
                        JPopupMenu popup = new JPopupMenu();
                        popup.setBackground(new Color(15, 30, 50));
                        popup.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1));
                        
                        JPanel notifPanel = new JPanel(new BorderLayout());
                        notifPanel.setOpaque(false);
                        notifPanel.setPreferredSize(new Dimension(250, 100));
                        
                        JLabel txt = new JLabel("Empty Notifications", SwingConstants.CENTER);
                        txt.setForeground(Color.WHITE);
                        txt.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                        
                        notifPanel.add(txt, BorderLayout.CENTER);
                        popup.add(notifPanel);
                        popup.show(lbl, 0, lbl.getHeight() + 10);
                    }
                }
            });
            navLinks.add(lbl);
        }
        topNav.add(navLinks, BorderLayout.CENTER);

        authLinksPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        authLinksPanel.setOpaque(false);
        updateAuthUI();

        topNav.add(authLinksPanel, BorderLayout.EAST);
        return topNav;
    }

    private JPanel createLandingPage() {
        // --- MAIN CONTENT (LEFT ALIGNED) ---
        JPanel panel = new JPanel(null); // Absolute positioning for exact image matching
        panel.setOpaque(false);

        JLabel welcomeLabel = new JLabel(""); // For animation
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 54)); // Adjusted for image template
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(80, 150, 800, 80);
        panel.add(welcomeLabel);

        // Subtext perfectly matching the left-aligned layout
        JLabel subLabel = new JLabel("<html><div style='width: 350px; line-height: 1.5;'>"
                + "Your feedback is crucial for continuous improvement. Help us enhance the learning experience "
                + "by providing honest and constructive reviews for your courses."
                + "</div></html>");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subLabel.setForeground(new Color(200, 210, 240));
        subLabel.setBounds(80, 230, 500, 80);
        panel.add(subLabel);

        // Matching Red Pill Button from the image -> switches to Dashboard
        JButton seeMoreBtn = new JButton("Dashboard");
        seeMoreBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        seeMoreBtn.setForeground(Color.WHITE);
        seeMoreBtn.setBackground(new Color(210, 50, 70)); // Deep vibrant red
        seeMoreBtn.setBounds(80, 330, 160, 45);
        seeMoreBtn.setFocusPainted(false);
        seeMoreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        seeMoreBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true));
        seeMoreBtn.addActionListener(e -> {
            if (!isLoggedIn) {
                JOptionPane.showMessageDialog(this, "Please log in first to access the Dashboard!");
                new LoginPage(DashboardPage.this, true).setVisible(true);
                return;
            }
            rootCardLayout.show(rootCardPanel, "APP_DASHBOARD");
            cardLayout.show(contentPanel, "SUBMIT"); // Open submit feedback by default
        });
        panel.add(seeMoreBtn);

        // Start Welcome Animation
        Timer timer = new Timer(150, null);
        timer.addActionListener(new ActionListener() {
            int stage = 0;
            int expand = 2;
            boolean grow = true;

            public void actionPerformed(ActionEvent e) {
                switch (stage) {
                    case 0: welcomeLabel.setText("W"); break;
                    case 1: welcomeLabel.setText("We"); break;
                    case 2: welcomeLabel.setText("Wel"); break;
                    case 3:
                        if (grow) {
                            expand++;
                            welcomeLabel.setText("W" + "e".repeat(expand));
                            if (expand >= 8) grow = false;
                        } else {
                            expand--;
                            welcomeLabel.setText("W" + "e".repeat(expand));
                            if (expand <= 2) stage++;
                        }
                        return;
                    case 4: welcomeLabel.setText("WELCOME"); break;
                    case 5:
                        // After animation, show final text
                        welcomeLabel.setText("Course Feedback System");
                        subLabel.setVisible(true);
                        seeMoreBtn.setVisible(true);
                        ((Timer) e.getSource()).stop();
                        return;
                }
                stage++;
            }
        });
        
        // Hide elements initially so only animation is visible
        subLabel.setVisible(false);
        seeMoreBtn.setVisible(false);
        timer.start();

        return panel;
    }
    
    private JLabel createLinkButton(String text, String urlString) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Segoe UI", Font.BOLD, 15));
        link.setForeground(new Color(100, 180, 255));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                link.setForeground(Color.WHITE);
                link.setText("<html><u>" + text + "</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                link.setForeground(new Color(100, 180, 255));
                link.setText(text);
            }
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(urlString));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return link;
    }

    // --- SUBMIT FEEDBACK CARD (REDESIGNED) ---
    private JPanel createSubmitFeedbackCard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(30, 60, 30, 60));

        JPanel formPanel = new JPanel(null);
        formPanel.setOpaque(false);

        JLabel title = new JLabel("Semester Feedback Form");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(60, 30, 400, 40);
        formPanel.add(title);

        JLabel semLabel = new JLabel("Select Semester:");
        semLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semLabel.setForeground(Color.WHITE);
        semLabel.setBounds(460, 35, 140, 30);
        formPanel.add(semLabel);

        String[] semesters = {"1-1", "1-2", "2-1", "2-2", "3-1", "3-2", "4-1", "4-2"};
        semesterDropdown = new JComboBox<>(semesters);
        semesterDropdown.setBounds(600, 35, 100, 30);
        semesterDropdown.setFont(new Font("Segoe UI", Font.BOLD, 14));
        semesterDropdown.addActionListener(e -> renderSubjects());
        formPanel.add(semesterDropdown);

        // Likert Scale Headers
        JPanel headerRow = new JPanel(new GridLayout(1, 5, 10, 0));
        headerRow.setOpaque(false);
        headerRow.setBounds(300, 100, 400, 20);
        String[] headers = {"Poor", "Fair", "Good", "Very Good", "Excellent"};
        for (String h : headers) {
            JLabel hl = new JLabel(h, SwingConstants.CENTER);
            hl.setForeground(new Color(200, 220, 255));
            hl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            headerRow.add(hl);
        }
        formPanel.add(headerRow);

        dynamicSubjectsPanel = new JPanel(null);
        dynamicSubjectsPanel.setOpaque(false);
        dynamicSubjectsPanel.setBounds(0, 120, 800, 600); // Leave space for subjects to render
        formPanel.add(dynamicSubjectsPanel);

        wrapper.add(formPanel, BorderLayout.CENTER);
        
        // Render 1-1 initially
        renderSubjects();
        
        return wrapper;
    }

    private void renderSubjects() {
        dynamicSubjectsPanel.removeAll();
        ratings.clear();

        String sem = (String) semesterDropdown.getSelectedItem();
        int y = 5;

        if (sem.equals("1-1")) {
            currentExpectedSubjects = 5;
            y = addSubjectField(dynamicSubjectsPanel, "Mathematics", y);
            y = addSubjectField(dynamicSubjectsPanel, "Programming in C", y);
            y = addSubjectField(dynamicSubjectsPanel, "Physics", y);
            y = addSubjectField(dynamicSubjectsPanel, "Chemistry Lab", y);
            y = addSubjectField(dynamicSubjectsPanel, "Workshop", y);
        } else if (sem.equals("1-2")) {
            currentExpectedSubjects = 4;
            // TODO: Uncomment and add subjects identically as 1-1
            // y = addSubjectField(dynamicSubjectsPanel, "Data Structures", y);
            // y = addSubjectField(dynamicSubjectsPanel, "English", y);
            // y = addSubjectField(dynamicSubjectsPanel, "Digital Logic", y);
            // y = addSubjectField(dynamicSubjectsPanel, "Physics Lab", y);
            
            JLabel pending = new JLabel("Subjects for " + sem + " will be added soon.");
            pending.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            pending.setForeground(Color.GRAY);
            pending.setBounds(60, y, 400, 30);
            dynamicSubjectsPanel.add(pending);
            y += 40;
        } else if (sem.equals("2-1")) {
            currentExpectedSubjects = 0;
            // TODO: Add subjects for 2-1

            JLabel pending = new JLabel("Subjects for " + sem + " will be added soon.");
            pending.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            pending.setForeground(Color.GRAY);
            pending.setBounds(60, y, 400, 30);
            dynamicSubjectsPanel.add(pending);
            y += 40;
        } else {
            currentExpectedSubjects = 0;
            // Catch all for remaining semesters
            // TODO: Add subjects for 2-2, 3-1, 3-2, 4-1, 4-2
            
            JLabel pending = new JLabel("Subjects for " + sem + " will be added soon.");
            pending.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            pending.setForeground(Color.GRAY);
            pending.setBounds(60, y, 400, 30);
            dynamicSubjectsPanel.add(pending);
            y += 40;
        }

        JLabel commentLabel = new JLabel("Overall Comment (Optional):");
        commentLabel.setForeground(new Color(200, 220, 255));
        commentLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        commentLabel.setBounds(60, y + 10, 300, 25);
        dynamicSubjectsPanel.add(commentLabel);

        commentArea = new JTextArea();
        commentArea.setBounds(60, y + 40, 600, 70);
        commentArea.setBackground(new Color(8, 15, 35, 200));
        commentArea.setForeground(Color.WHITE);
        commentArea.setCaretColor(Color.WHITE);
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        commentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        dynamicSubjectsPanel.add(commentArea);

        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.setBounds(60, y + 130, 220, 45);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitBtn.setBackground(new Color(0, 120, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        submitBtn.addActionListener(e -> saveFeedback());
        dynamicSubjectsPanel.add(submitBtn);

        dynamicSubjectsPanel.revalidate();
        dynamicSubjectsPanel.repaint();
    }

    private int addSubjectField(JPanel panel, String subject, int y) {
        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setForeground(Color.WHITE);
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subjectLabel.setBounds(60, y, 220, 35);
        panel.add(subjectLabel);

        // Radio Button Form Matrix
        JPanel ratingPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        ratingPanel.setOpaque(false);
        ratingPanel.setBounds(300, y, 400, 35);

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < 5; i++) {
            JRadioButton rb = new JRadioButton();
            rb.setOpaque(false);
            rb.setHorizontalAlignment(SwingConstants.CENTER);
            rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            int ratingVal = i + 1;
            rb.addActionListener(e -> ratings.put(subject, ratingVal));

            group.add(rb);
            ratingPanel.add(rb);
        }

        panel.add(ratingPanel);
        return y + 45; // Spacing between rows
    }

    private void saveFeedback() {
        if (ratings.size() < currentExpectedSubjects || currentExpectedSubjects == 0) {
            JOptionPane.showMessageDialog(this, "Please select a rating for all subjects before submitting!");
            return;
        }

        String comment = commentArea.getText();
        String sem = (String) semesterDropdown.getSelectedItem();
        // Maps "1-1" -> 11, "2-1" -> 21, etc.
        int semCode = Integer.parseInt(sem.replace("-", ""));

        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO semester_feedback(semester,subject_name,rating,comment) VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            for (String subject : ratings.keySet()) {
                ps.setInt(1, semCode);
                ps.setString(2, subject);
                ps.setInt(3, ratings.get(subject));
                ps.setString(4, comment);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Thank you! Feedback saved successfully.");

            // Clear selections and text
            renderSubjects();

            // Refresh form UI by instantiating list card again to fetch DB data
            contentPanel.add(new FeedbackListView(), "LIST");
            cardLayout.show(contentPanel, "LIST");

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: could not save feedback.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- BACKGROUND ----------------
    class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Base background
            g2d.setColor(new Color(12, 22, 45)); // Deep dark blue
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Red glow top right
            RadialGradientPaint r1 = new RadialGradientPaint(
                    new Point(getWidth(), 0), getWidth() * 0.8f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(210, 50, 70, 180), new Color(12, 22, 45, 0)}
            );
            g2d.setPaint(r1);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Blue glow center-left
            RadialGradientPaint r2 = new RadialGradientPaint(
                    new Point(100, getHeight() / 2), getWidth() * 0.6f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(40, 90, 150, 150), new Color(12, 22, 45, 0)}
            );
            g2d.setPaint(r2);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw fluid mesh curves matching the image
            g2d.setStroke(new BasicStroke(1.2f));
            for (int i = 0; i < 35; i++) {
                float alpha = (float) Math.max(0.05, 0.6 - (i * 0.015));
                g2d.setColor(new Color(255, 180, 200, (int) (alpha * 255)));

                java.awt.geom.Path2D path = new java.awt.geom.Path2D.Float();
                path.moveTo(0, getHeight() / 2);

                for (int x = 0; x <= getWidth(); x += 15) {
                    double freq1 = 0.003 + (i * 0.00008);
                    double freq2 = 0.005 - (i * 0.00003);
                    double amp1 = 100 + (i * 8);
                    double amp2 = 80 - (i * 2);

                    double y = (getHeight() / 2.0)
                            + Math.sin(x * freq1) * amp1
                            + Math.cos((x + 300) * freq2) * amp2
                            - Math.sin(x * 0.001) * 80;

                    path.lineTo(x, y);
                }
                g2d.draw(path);
            }
        }
    }
}