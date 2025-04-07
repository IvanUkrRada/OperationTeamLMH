package untitled.src;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReviewPanel extends JPanel {
    private MainFrame parentFrame;
    private CalendarPanel calendarPanel;
    private JTextArea reviewsArea;
    private JComboBox<String> filterCombo;
    private JButton respondButton, refreshButton;

    public ReviewPanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;
        this.calendarPanel = calendarPanel;

        setLayout(new BorderLayout());


        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Filter by:"));

        String[] filters = {"All Reviews", "Venue Reviews", "Room Reviews", "Show Reviews", "Recent Reviews"};
        filterCombo = new JComboBox<>(filters);
        controlPanel.add(filterCombo);

        refreshButton = new JButton("Refresh");
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // Reviews to view
        reviewsArea = new JTextArea();
        reviewsArea.setEditable(false);
        reviewsArea.setLineWrap(true);
        reviewsArea.setWrapStyleWord(true);
        add(new JScrollPane(reviewsArea), BorderLayout.CENTER);




        filterCombo.addActionListener(e -> refresh());
        refreshButton.addActionListener(e -> refresh());



        loadSampleReviews();
    }

    private void loadSampleReviews() {
        StringBuilder sb = new StringBuilder();
        //to be Done

        reviewsArea.setText(sb.toString());
    }

    public static class Review {
        private int reviewId;
        private int clientId;
        private String clientName;
        private int rating;
        private String comments;
        private String date;
        private String reviewType; // "venue", "room", "show"

        public Review(int reviewId, int clientId, String clientName, int rating,
                      String comments, String date, String reviewType) {
            this.reviewId = reviewId;
            this.clientId = clientId;
            this.clientName = clientName;
            this.rating = rating;
            this.comments = comments;
            this.date = date;
            this.reviewType = reviewType;
        }

        // Add getters and setters
        public int getReviewId() { return reviewId; }
        public int getClientId() { return clientId; }
        public String getClientName() { return clientName; }
        public int getRating() { return rating; }
        public String getComments() { return comments; }
        public String getDate() { return date; }
        public String getReviewType() { return reviewType; }

        @Override
        public String toString() {
            return String.format("%s (%d★) - %s\n%s\n",
                    clientName, rating, date, comments);
        }
    }

    // Update the refresh method in ReviewPanel.java
    public void refresh() {
        String filter = (String) filterCombo.getSelectedItem();

        StringBuilder sb = new StringBuilder();
        sb.append("== ").append(filter).append(" ==\n\n");

        try {
            DatabaseManagment dbManager = DatabaseManagment.getInstance();
            ArrayList<Review> reviews = new ArrayList<>();

            switch (filter) {
                case "All Reviews":
                    reviews = dbManager.getAllReviews();
                    break;
                case "Venue Reviews":
                    reviews = dbManager.getVenueReviews();
                    break;
                case "Room Reviews":
                    reviews = dbManager.getRoomReviews();
                    break;
                case "Show Reviews":
                    reviews = dbManager.getShowReviews();
                    break;
                case "Recent Reviews":
                    reviews = dbManager.getRecentReviews(30); // Last 30 days
                    break;
                default:
                    loadSampleReviews();
                    return;
            }

            if (reviews.isEmpty()) {
                sb.append("No reviews found for this filter.");
            } else {
                for (Review review : reviews) {
                    sb.append(review.toString()).append("\n");
                }
            }

            reviewsArea.setText(sb.toString());

        } catch (Exception e) {
            System.err.println("Error loading reviews: " + e.getMessage());
            e.printStackTrace();
            reviewsArea.setText("Error loading reviews from database.");
        }
    }

}
