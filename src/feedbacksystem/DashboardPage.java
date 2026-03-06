package feedbacksystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DashboardPage extends JFrame {
	
	//private int[] ratings = new int[5]; 
	//private JTextArea commentArea;
	
	private java.util.HashMap<String,Integer> ratings = new java.util.HashMap<>();
	private JTextArea commentArea;
	
    private CardLayout cardLayout;
    private JPanel contentPanel;

    private JLabel welcomeLabel;
    private JButton dashboardBtn;

    private JPanel menuPanel;
    private JPanel menuBox;
    private JPanel feedbackPanel;

    public DashboardPage() {

        setTitle("Course Feedback System");
        setSize(1000,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        JLabel title = new JLabel("Course Feedback System");
        title.setFont(new Font("Segoe UI",Font.BOLD,22));
        title.setForeground(Color.WHITE);

        header.add(title,BorderLayout.WEST);

        JPanel icons = new JPanel(new FlowLayout(FlowLayout.RIGHT,20,0));
        icons.setOpaque(false);

        icons.add(new JLabel("🔔"));
        icons.add(new JLabel("👤"));

        header.add(icons,BorderLayout.EAST);

        mainPanel.add(header,BorderLayout.NORTH);

        // CONTENT
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(createWelcomePage(),"WELCOME");
        contentPanel.add(createMenuPage(),"MENU");

        mainPanel.add(contentPanel,BorderLayout.CENTER);

        cardLayout.show(contentPanel,"WELCOME");
    }

    // ---------------- WELCOME PAGE ----------------
    private JPanel createWelcomePage(){

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);

        welcomeLabel = new JLabel("");
        welcomeLabel.setFont(new Font("Segoe UI",Font.BOLD,80));
        welcomeLabel.setForeground(Color.WHITE);

        gbc.gridy=0;
        panel.add(welcomeLabel,gbc);

        dashboardBtn = new JButton("Open Dashboard");
        dashboardBtn.setFont(new Font("Segoe UI",Font.BOLD,18));
        dashboardBtn.setBackground(new Color(0,120,255));
        dashboardBtn.setForeground(Color.WHITE);
        dashboardBtn.setFocusPainted(false);
        dashboardBtn.setVisible(false);

        gbc.gridy=1;
        panel.add(dashboardBtn,gbc);

        startAnimation();

        dashboardBtn.addActionListener(e -> startFadeOut());

        return panel;
    }

    // ---------------- MENU PAGE ----------------
    private JPanel createMenuPage(){

        menuPanel = new JPanel(null);
        menuPanel.setOpaque(false);

        menuBox = new JPanel(new GridLayout(4,1,25,25));
        menuBox.setOpaque(false);

        // CENTER MENU POSITION
        menuBox.setBounds(350,180,300,250);

        String[] items = {
                "Submit Feedback",
                "My Feedback",
                "Profile",
                "Logout"
        };

        for(String item:items){

            JButton btn = new JButton(item);
            btn.setFont(new Font("Segoe UI",Font.BOLD,26));
            btn.setForeground(Color.WHITE);

            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new MouseAdapter(){

                public void mouseEntered(MouseEvent e){
                    btn.setFont(new Font("Segoe UI",Font.BOLD,30));
                    btn.setForeground(new Color(0,180,255));
                }

                public void mouseExited(MouseEvent e){
                    btn.setFont(new Font("Segoe UI",Font.BOLD,26));
                    btn.setForeground(Color.WHITE);
                }
            });

            btn.addActionListener(e->{

                if(item.equals("Submit Feedback")){
                    openFeedbackForm();
                }

                if(item.equals("Logout")){
                    JOptionPane.showMessageDialog(null,"Logged Out");
                    System.exit(0);
                }

            });

            menuBox.add(btn);
        }

        menuPanel.add(menuBox);

        return menuPanel;
    }

    // ---------------- FEEDBACK FORM ----------------
    private JPanel createFeedbackForm(){

        feedbackPanel = new JPanel(null);

        // ------------------------------------------------
        // TRANSPARENT GLASS BACKGROUND
        // change last value (alpha) if you want darker/lighter
        // ------------------------------------------------
        feedbackPanel.setBackground(new Color(20,30,60,120));

        feedbackPanel.setBorder(BorderFactory.createLineBorder(
                new Color(120,180,255,120),1));

        JLabel title = new JLabel("Semester 1 Feedback");
        title.setFont(new Font("Segoe UI",Font.BOLD,24));
        title.setForeground(Color.WHITE);
        title.setBounds(80,20,250,30);

        feedbackPanel.add(title);

        int y = 80;

        y = addSubject("Mathematics",y);
        y = addSubject("Programming in C",y);
        y = addSubject("Physics",y);
        y = addSubject("Chemistry Lab",y);
        y = addSubject("Workshop",y);

        JLabel commentLabel = new JLabel("Overall Comment");
        commentLabel.setForeground(Color.WHITE);
        commentLabel.setFont(new Font("Segoe UI",Font.BOLD,14));
        commentLabel.setBounds(40,y,200,20);

        feedbackPanel.add(commentLabel);

        commentArea = new JTextArea();
        commentArea.setBounds(40,y+25,260,70);

        // transparent textbox
        commentArea.setBackground(new Color(0,0,0,80));
        commentArea.setForeground(Color.WHITE);
        commentArea.setBorder(BorderFactory.createLineBorder(new Color(200,200,200,80)));

        feedbackPanel.add(commentArea);

        JButton submit = new JButton("Submit Feedback");
        submit.setBounds(90,y+110,180,35);

        submit.setBackground(new Color(0,120,255));
        submit.setForeground(Color.WHITE);
        submit.setFocusPainted(false);

        feedbackPanel.add(submit);

        return feedbackPanel;
    }
    // ---------------- SUBJECT + STARS ----------------
    private int addSubject(String subject,int y){

        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setForeground(Color.WHITE);
        subjectLabel.setBounds(40,y,150,25);
        feedbackPanel.add(subjectLabel);

        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        starPanel.setOpaque(false);
        starPanel.setBounds(200,y,150,25);

        JLabel[] stars = new JLabel[5];

        for(int i=0;i<5;i++){

            stars[i] = new JLabel("☆");
            stars[i].setFont(new Font("Segoe UI Symbol",Font.BOLD,22));
            stars[i].setForeground(Color.YELLOW);

            int starIndex = i;

            stars[i].addMouseListener(new MouseAdapter(){

                public void mouseClicked(MouseEvent e){

                    ratings.put(subject, starIndex + 1);

                    for(int j=0;j<5;j++){

                        if(j <= starIndex)
                            stars[j].setText("★");
                        else
                            stars[j].setText("☆");
                    }
                }
            });

            starPanel.add(stars[i]);
        }

        feedbackPanel.add(starPanel);

        return y + 40;
    }
    // ---------------- OPEN FEEDBACK FORM ----------------
    private void openFeedbackForm(){

        feedbackPanel = createFeedbackForm();

        feedbackPanel.setBounds(1000,160,360,460);

        menuPanel.add(feedbackPanel);

        Timer slide = new Timer(10,null);

        slide.addActionListener(new ActionListener(){

            int menuX = 350;
            int formX = 1000;

            public void actionPerformed(ActionEvent e){

                menuX -= 8;
                formX -= 12;

                menuBox.setLocation(menuX,180);
                feedbackPanel.setLocation(formX,160);

                menuPanel.repaint();

                if(menuX <= 120 && formX <= 520){
                    slide.stop();
                }
            }
        });

        slide.start();
    }

    // ---------------- WELCOME ANIMATION ----------------
    private void startAnimation(){

        Timer timer = new Timer(150,null);

        timer.addActionListener(new ActionListener(){

            int stage=0;
            int expand=2;
            boolean grow=true;

            public void actionPerformed(ActionEvent e){

                switch(stage){

                    case 0: welcomeLabel.setText("W"); break;
                    case 1: welcomeLabel.setText("We"); break;
                    case 2: welcomeLabel.setText("Wee"); break;

                    case 3:

                        if(grow){
                            expand++;
                            welcomeLabel.setText("W"+"e".repeat(expand));
                            if(expand>=8) grow=false;
                        }
                        else{
                            expand--;
                            welcomeLabel.setText("W"+"e".repeat(expand));
                            if(expand<=2) stage++;
                        }
                        return;

                    case 4: welcomeLabel.setText("WELCOME"); break;

                    case 5:
                        dashboardBtn.setVisible(true);
                        timer.stop();
                        return;
                }

                stage++;
            }
        });

        timer.start();
    }

    // ---------------- FADE OUT ----------------
    private void startFadeOut(){

        Timer fade = new Timer(40,null);

        fade.addActionListener(new ActionListener(){

            float opacity = 1f;

            public void actionPerformed(ActionEvent e){

                opacity -= 0.05f;

                welcomeLabel.setForeground(new Color(255,255,255,(int)(opacity*255)));
                dashboardBtn.setForeground(new Color(255,255,255,(int)(opacity*255)));

                if(opacity<=0){
                    fade.stop();
                    cardLayout.show(contentPanel,"MENU");
                }
            }
        });

        fade.start();
    }
    
    private void saveFeedback(){

        String comment = commentArea.getText();

        try{

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO semester_feedback(semester,subject_name,rating,comment) VALUES(?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            for(String subject : ratings.keySet()){

                ps.setInt(1,1); // semester number
                ps.setString(2,subject);
                ps.setInt(3,ratings.get(subject));
                ps.setString(4,comment);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,"Feedback saved successfully!");
            submit.addActionListener(e -> saveFeedback());

            commentArea.setText("");
            ratings.clear();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    // ---------------- BACKGROUND ----------------
    class GradientPanel extends JPanel{

        protected void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2d=(Graphics2D)g;

            GradientPaint gp = new GradientPaint(
                    0,0,new Color(8,20,45),
                    0,getHeight(),new Color(15,60,130));

            g2d.setPaint(gp);
            g2d.fillRect(0,0,getWidth(),getHeight());
        }
    }
}