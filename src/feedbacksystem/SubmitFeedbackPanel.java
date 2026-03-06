package feedbacksystem;

import javax.swing.*;
import java.awt.*;

public class SubmitFeedbackPanel extends JPanel {

    private JTextArea commentBox;

    public SubmitFeedbackPanel() {

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(380,450));

        // FORM BACKGROUND
        JPanel form = new JPanel();
        form.setLayout(null);
        form.setBounds(0,0,380,450);
        form.setBackground(new Color(0,0,0,180));
        form.setBorder(BorderFactory.createLineBorder(new Color(0,120,255),2));

        add(form);

        JLabel title = new JLabel("Semester 1 Feedback");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,22));
        title.setBounds(80,20,250,30);
        form.add(title);

        int y = 80;

        // SUBJECTS
        y = addSubject(form,"Mathematics",y);
        y = addSubject(form,"Programming in C",y);
        y = addSubject(form,"Physics",y);
        y = addSubject(form,"Chemistry Lab",y);
        y = addSubject(form,"Workshop",y);

        // COMMENT LABEL
        JLabel commentLabel = new JLabel("Overall Comment");
        commentLabel.setForeground(Color.WHITE);
        commentLabel.setBounds(40,y+10,200,20);
        form.add(commentLabel);

        commentBox = new JTextArea();
        commentBox.setBounds(40,y+35,300,60);
        commentBox.setBackground(new Color(0,0,0,120));
        commentBox.setForeground(Color.WHITE);
        commentBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        form.add(commentBox);

        // SUBMIT BUTTON
        JButton submit = new JButton("Submit Feedback");
        submit.setBounds(90,y+110,200,35);
        submit.setBackground(new Color(0,120,255));
        submit.setForeground(Color.WHITE);
        submit.setFocusPainted(false);

        submit.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,"Feedback Submitted!");
        });

        form.add(submit);
    }

    // ADD SUBJECT WITH STARS
    private int addSubject(JPanel panel,String subject,int y){

        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setForeground(Color.WHITE);
        subjectLabel.setBounds(40,y,150,25);

        panel.add(subjectLabel);

        JPanel stars = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        stars.setOpaque(false);
        stars.setBounds(190,y,150,25);

        for(int i=1;i<=5;i++){

            JLabel star = new JLabel("☆");
            star.setFont(new Font("Segoe UI",Font.BOLD,18));
            star.setForeground(Color.YELLOW);

            int rating = i;

            star.addMouseListener(new java.awt.event.MouseAdapter(){

                public void mouseClicked(java.awt.event.MouseEvent e){

                    Component[] comps = stars.getComponents();

                    for(int j=0;j<comps.length;j++){

                        JLabel s = (JLabel) comps[j];

                        if(j < rating)
                            s.setText("★");
                        else
                            s.setText("☆");
                    }
                }
            });

            stars.add(star);
        }

        panel.add(stars);

        return y+40;
    }
}