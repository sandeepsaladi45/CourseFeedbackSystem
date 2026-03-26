package feedbacksystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class FeedbackListView extends JPanel {

    public FeedbackListView() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JLabel title = new JLabel("My Submitted Feedback");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Table Model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Semester");
        model.addColumn("Subject/Course");
        model.addColumn("Rating");
        model.addColumn("Comment");

        JTable table = new JTable(model);
        styleTable(table);

        // Load Data
        loadFeedbackData(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        
        table.setBackground(new Color(255, 255, 255, 0)); // Fully transparent
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(15, 25, 45, 120));
        table.getTableHeader().setForeground(new Color(180, 200, 255));
        table.setSelectionBackground(new Color(0, 120, 255, 100));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 255, 255, 30));
        table.setShowVerticalLines(false);
    }

    private void loadFeedbackData(DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check semester_feedback table
            String sql = "SELECT semester, subject_name, rating, comment FROM semester_feedback ORDER BY id DESC";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                
                while (rs.next()) {
                    int semCode = rs.getInt("semester");
                    // Assuming old semester codes might be 1, default them to 1-1 ("11").
                    if (semCode < 10) semCode = 11;
                    String classSem = (semCode / 10) + "-" + (semCode % 10);
                    
                    int r = rs.getInt("rating");
                    String ratingStr = r + " / 5"; // Replace stars with literal text to fix encoding boxes
                    model.addRow(new Object[]{
                        classSem,
                        rs.getString("subject_name"),
                        ratingStr,
                        rs.getString("comment")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
