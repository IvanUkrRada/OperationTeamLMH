package untitled.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ReviewPanel extends JPanel {
    private MainFrame parentFrame;
    private CalendarPanel calendarPanel;
    private JTable reviewsTable;
    private ReviewTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JButton addButton, refreshButton;

    public ReviewPanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;
        this.calendarPanel = calendarPanel;

        setLayout(new BorderLayout());

        // Load reviews from database
        DatabaseManagment dbManagement = new DatabaseManagment();
        ArrayList<ReviewEntry> reviews = dbManagement.getAllReviews();

        // Control panel for filter
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Filter by:"));

        String[] filters = {"All Reviews", "Venue Reviews", "Room Reviews", "Show Reviews", "Recent Reviews"};
        filterCombo = new JComboBox<>(filters);
        controlPanel.add(filterCombo);

        refreshButton = new JButton("Refresh");
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);


        tableModel = new ReviewTableModel(reviews);
        reviewsTable = new JTable(tableModel);


        reviewsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reviewsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        reviewsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reviewsTable.getColumnModel().getColumn(3).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(reviewsTable);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add Review");
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);


        filterCombo.addActionListener(e -> refresh());
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> addReview());
    }

    public void refresh() {
        // Reload the reviews from the database
        DatabaseManagment dbManagement = new DatabaseManagment();
        ArrayList<ReviewEntry> reviews = dbManagement.getAllReviews();
        tableModel = new ReviewTableModel(reviews);
        reviewsTable.setModel(tableModel);

        // Adjust column widths
        reviewsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reviewsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        reviewsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reviewsTable.getColumnModel().getColumn(3).setPreferredWidth(350);

        reviewsTable.repaint();
    }

    private void addReview() {
        JDialog reviewDialog = new JDialog(parentFrame, "Add Review", true);
        reviewDialog.setSize(500, 400);
        reviewDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lName = new JLabel("Name:");
        JTextField tName = new JTextField(30);

        JLabel lRating = new JLabel("Rating (1-5):");
        JComboBox<Integer> cRating = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cRating.setSelectedIndex(4); // Default to 5

        JLabel lReview = new JLabel("Review:");
        JTextArea tReview = new JTextArea(5, 30);
        tReview.setLineWrap(true);
        tReview.setWrapStyleWord(true);
        JScrollPane reviewScrollPane = new JScrollPane(tReview);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lName, gbc);

        gbc.gridx = 1;
        panel.add(tName, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lRating, gbc);

        gbc.gridx = 1;
        panel.add(cRating, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lReview, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(reviewScrollPane, gbc);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        okButton.addActionListener(e -> {
            String name = tName.getText();
            int rating = (Integer) cRating.getSelectedItem();
            String review = tReview.getText();

            if (areFieldsValid(name, review)) {
                String reviewId = (tableModel.getNumberReviews() + 1) + "";

                try {

                    DatabaseManagment dbManagement = new DatabaseManagment();
                    ReviewEntry newReview = new ReviewEntry(reviewId, rating, name, review);

                    if (dbManagement.addReview(newReview)) {

                        tableModel.addReview(reviewId, rating, name, review);
                        JOptionPane.showMessageDialog(null, "Review successfully added.");
                        reviewDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error! Unable to add review to database.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error adding review: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(reviewDialog, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> reviewDialog.dispose());

        reviewDialog.add(panel, BorderLayout.CENTER);
        reviewDialog.add(buttonPanel, BorderLayout.SOUTH);

        reviewDialog.setVisible(true);
    }

    private boolean areFieldsValid(String name, String review) {
        return !name.isEmpty() && !review.isEmpty();
    }
}