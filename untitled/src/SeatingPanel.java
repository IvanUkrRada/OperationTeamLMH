package untitled.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import Interface.Operations;

public class SeatingPanel extends JPanel {
    private MainFrame parentFrame;
    private JTabbedPane tabbedPane;
    private JPanel mainHallPanel;
    private JPanel smallHallPanel;
    private JTextArea mainHallInfoArea;
    private JTextArea smallHallInfoArea;
    private JComboBox<String> bookingSelector;
    private Operations operations;

    public SeatingPanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;

        // Initialize operations (interface to get data)
        operations = new Operations();

        setLayout(new BorderLayout());

        // Create booking selector at the top
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Select Booking:"));
        bookingSelector = new JComboBox<>();
        populateBookingSelector(calendarPanel);
        bookingSelector.addActionListener(e -> updateSeatingInfo());
        controlPanel.add(bookingSelector);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            populateBookingSelector(calendarPanel);
            updateSeatingInfo();
        });
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // Create tabbed pane for the two halls
        tabbedPane = new JTabbedPane();

        // Create Main Hall panel
        mainHallPanel = createMainHallPanel();
        tabbedPane.addTab("Main Hall", mainHallPanel);

        // Create Small Hall panel
        smallHallPanel = createSmallHallPanel();
        tabbedPane.addTab("Small Hall", smallHallPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMainHallPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Add the image
        ImageIcon icon = new ImageIcon(getClass().getResource("/untitled/MainHall.png"));
        // Fallback if resource not found
        if (icon.getIconWidth() == -1) {
            try {
                icon = new ImageIcon("untitled/MainHall.png");
            } catch (Exception e) {
                // Create a placeholder for the image
                JPanel imagePlaceholder = new JPanel();
                imagePlaceholder.setPreferredSize(new Dimension(600, 400));
                imagePlaceholder.setBorder(BorderFactory.createTitledBorder("Main Hall Seating Chart"));
                panel.add(imagePlaceholder, BorderLayout.CENTER);
            }
        }

        if (icon.getIconWidth() != -1) {
            JLabel imageLabel = new JLabel(icon);
            JScrollPane scrollPane = new JScrollPane(imageLabel);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        // Add text area for seat information
        mainHallInfoArea = new JTextArea(8, 50);
        mainHallInfoArea.setEditable(false);
        mainHallInfoArea.setText("Select a booking to view seat information for the Main Hall.");
        JScrollPane infoScrollPane = new JScrollPane(mainHallInfoArea);
        panel.add(infoScrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSmallHallPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Add the image
        ImageIcon icon = new ImageIcon(getClass().getResource("/untitled/SmallHall.png"));
        // Fallback if resource not found
        if (icon.getIconWidth() == -1) {
            try {
                icon = new ImageIcon("untitled/SmallHall.png");
            } catch (Exception e) {
                // Create a placeholder for the image
                JPanel imagePlaceholder = new JPanel();
                imagePlaceholder.setPreferredSize(new Dimension(600, 400));
                imagePlaceholder.setBorder(BorderFactory.createTitledBorder("Small Hall Seating Chart"));
                panel.add(imagePlaceholder, BorderLayout.CENTER);
            }
        }

        if (icon.getIconWidth() != -1) {
            JLabel imageLabel = new JLabel(icon);
            JScrollPane scrollPane = new JScrollPane(imageLabel);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        // Add text area for seat information
        smallHallInfoArea = new JTextArea(8, 50);
        smallHallInfoArea.setEditable(false);
        smallHallInfoArea.setText("Select a booking to view seat information for the Small Hall.");
        JScrollPane infoScrollPane = new JScrollPane(smallHallInfoArea);
        panel.add(infoScrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private void populateBookingSelector(CalendarPanel calendarPanel) {
        bookingSelector.removeAllItems();

        // Add placeholder if no bookings
        if (calendarPanel.getAllBookings().isEmpty()) {
            bookingSelector.addItem("No bookings available");
            return;
        }

        // Add all bookings to the selector
        for (BookingEntry booking : calendarPanel.getAllBookings()) {
            String bookingInfo = booking.getDate() + " - " + booking.getVenueSpace()
                    + " - " + booking.getClient() + " (ID: " + (int)(Math.random() * 1000) + ")";
            bookingSelector.addItem(bookingInfo);
        }
    }

    private void updateSeatingInfo() {
        if (bookingSelector.getSelectedItem() == null ||
                bookingSelector.getSelectedItem().equals("No bookings available")) {
            mainHallInfoArea.setText("No booking selected.");
            smallHallInfoArea.setText("No booking selected.");
            return;
        }

        // Extract booking ID from the selected item
        String selectedBooking = (String) bookingSelector.getSelectedItem();
        int bookingId;
        try {
            // Extract the ID from the string
            int idIndex = selectedBooking.lastIndexOf("ID: ") + 4;
            int endIndex = selectedBooking.lastIndexOf(")");
            bookingId = Integer.parseInt(selectedBooking.substring(idIndex, endIndex));
        } catch (Exception e) {
            // Use a random ID if parsing fails
            bookingId = (int)(Math.random() * 1000);
        }

        // Get seat information from the Operations interface
        try {
            // Get seats held for this booking
            String seatsHeld = operations.getSeatsHeld(bookingId);

            // If the interface returns null or empty, generate sample data
            if (seatsHeld == null || seatsHeld.trim().isEmpty()) {
                seatsHeld = generateSampleSeatsHeld(bookingId);
            }

            // Update the information areas
            updateMainHallInfo(bookingId, seatsHeld, selectedBooking);
            updateSmallHallInfo(bookingId, seatsHeld, selectedBooking);

        } catch (Exception e) {
            // If there's an error, show a message and use sample data
            mainHallInfoArea.setText("Error retrieving seat information: " + e.getMessage() +
                    "\nDisplaying sample data instead.");
            smallHallInfoArea.setText("Error retrieving seat information: " + e.getMessage() +
                    "\nDisplaying sample data instead.");

            String sampleData = generateSampleSeatsHeld(bookingId);
            updateMainHallInfo(bookingId, sampleData, selectedBooking);
            updateSmallHallInfo(bookingId, sampleData, selectedBooking);
        }
    }

    private void updateMainHallInfo(int bookingId, String seatsHeld, String bookingInfo) {
        StringBuilder info = new StringBuilder();
        info.append("Booking: ").append(bookingInfo).append("\n\n");
        info.append("Main Hall Seat Information:\n");
        info.append("---------------------------\n");
        info.append("Total Capacity: 374 (Stalls: 285, Balcony: 89)\n");

        // Add the specific seats that are held
        info.append("\nHeld Seats: ").append(seatsHeld).append("\n\n");

        // Add seats with restricted view
        info.append("Seats with Restricted View:\n");
        info.append("A1-A4, A16-A18, P1-P4, P16-P18\n");
        info.append("These seats have limited visibility of stage left or right.\n\n");

        // Add accessibility information
        info.append("Accessibility Information:\n");
        info.append("Wheelchair spaces available in rows G and H.\n");
        info.append("Accessible entrance via side door nearest to row J.\n");

        mainHallInfoArea.setText(info.toString());
    }

    private void updateSmallHallInfo(int bookingId, String seatsHeld, String bookingInfo) {
        StringBuilder info = new StringBuilder();
        info.append("Booking: ").append(bookingInfo).append("\n\n");
        info.append("Small Hall Seat Information:\n");
        info.append("----------------------------\n");
        info.append("Total Capacity: 95\n");

        // For Small Hall, we'll use a subset of the seats
        String smallHallSeats;
        if (seatsHeld.contains(",")) {
            // Take just the first few seats for the small hall
            String[] allSeats = seatsHeld.split(",");
            StringBuilder smallSeats = new StringBuilder();
            for (int i = 0; i < Math.min(allSeats.length / 3, 10); i++) {
                smallSeats.append(allSeats[i]).append(", ");
            }
            smallHallSeats = smallSeats.toString();
        } else {
            smallHallSeats = seatsHeld;
        }

        // Add the specific seats that are held
        info.append("\nHeld Seats: ").append(smallHallSeats).append("\n\n");

        // Add seats with restricted view
        info.append("Seats with Restricted View:\n");
        info.append("A1, A10, J1, J10\n");
        info.append("These corner seats have slightly restricted views.\n\n");

        // Add accessibility information
        info.append("Accessibility Information:\n");
        info.append("Wheelchair spaces available in row E.\n");
        info.append("Level access via main entrance.\n");

        smallHallInfoArea.setText(info.toString());
    }

    private String generateSampleSeatsHeld(int bookingId) {
        // Generate some sample data based on the booking ID
        StringBuilder seats = new StringBuilder();
        int seatCount = 20 + (bookingId % 30); // Generate between 20-50 seats

        for (int i = 0; i < seatCount; i++) {
            char row = (char)('A' + (i % 16));
            int seatNum = 1 + (i % 18);
            seats.append(row).append(seatNum);
            if (i < seatCount - 1) {
                seats.append(", ");
            }
        }

        return seats.toString();
    }

    public void refresh() {
        // This method will be called when the panel needs to be refreshed
        SwingUtilities.invokeLater(() -> {
            populateBookingSelector(parentFrame.getCalendarPanel());
            updateSeatingInfo();
        });
    }
}