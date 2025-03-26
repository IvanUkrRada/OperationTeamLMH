import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class CalendarPanel extends JPanel {
    private MainFrame parentFrame;
    private JPanel calendarView;
    private JLabel monthYearLabel;
    private JComboBox<VenueSpace> venueSelector;
    private JButton prevButton, nextButton;
    private int currentMonth, currentYear;
    private Map<String, ArrayList<BookingEntry>> bookings;
    private ArrayList<VenueSpace> venueSpaces;

    public CalendarPanel(MainFrame parent) {
        this.parentFrame = parent;
        this.bookings = new HashMap<>();
        this.venueSpaces = new ArrayList<>();

        initializeVenueSpaces();

        setLayout(new BorderLayout());


        JPanel controlPanel = new JPanel(new BorderLayout());

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        venueSelector = new JComboBox<>();
        for (VenueSpace space : venueSpaces) {
            venueSelector.addItem(space);
        }
        venueSelector.addActionListener(e -> refresh());
        leftControls.add(new JLabel("Space: "));
        leftControls.add(venueSelector);

        JPanel centerControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("◀");
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        nextButton = new JButton("▶");

        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));

        centerControls.add(prevButton);
        centerControls.add(monthYearLabel);
        centerControls.add(nextButton);

        controlPanel.add(leftControls, BorderLayout.WEST);
        controlPanel.add(centerControls, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.NORTH);

        // Calendar view
        calendarView = new JPanel(new BorderLayout());
        add(calendarView, BorderLayout.CENTER);

        // Set current month and year
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);

        refresh();
    }

    private void initializeVenueSpaces() {
        // Create hall spaces based on the requirements
        VenueSpace mainHall = new VenueSpace("Main Hall", "hall");
        mainHall.addCapacity("standard", 374);
        mainHall.addCapacity("stalls", 285);
        mainHall.addCapacity("balcony", 89);
        mainHall.addRate("hourly", 325.0);
        mainHall.addRate("evening_weekday", 1850.0);
        mainHall.addRate("evening_weekend", 2200.0);
        mainHall.addRate("daily_weekday", 3800.0);
        mainHall.addRate("daily_weekend", 4200.0);

        VenueSpace smallHall = new VenueSpace("Small Hall", "hall");
        smallHall.addCapacity("standard", 95);
        smallHall.addRate("hourly", 225.0);
        smallHall.addRate("evening_weekday", 950.0);
        smallHall.addRate("evening_weekend", 1300.0);
        smallHall.addRate("daily_weekday", 2200.0);
        smallHall.addRate("daily_weekend", 2500.0);

        VenueSpace rehearsalSpace = new VenueSpace("Rehearsal Space", "hall");
        rehearsalSpace.addCapacity("standard", 50);
        rehearsalSpace.addRate("hourly", 60.0);
        rehearsalSpace.addRate("daily_weekday", 240.0);
        rehearsalSpace.addRate("daily_weekend", 340.0);
        rehearsalSpace.addRate("evening_weekday", 450.0);
        rehearsalSpace.addRate("evening_weekend", 500.0);
        rehearsalSpace.addRate("weekly_day", 1000.0);
        rehearsalSpace.addRate("weekly_full", 1500.0);

        // Add the meeting rooms
        VenueSpace greenRoom = new VenueSpace("The Green Room", "room");
        greenRoom.addCapacity("classroom", 12);
        greenRoom.addCapacity("boardroom", 10);
        greenRoom.addCapacity("presentation", 20);
        greenRoom.addRate("hourly", 25.0);
        greenRoom.addRate("half_day", 75.0);
        greenRoom.addRate("full_day", 130.0);
        greenRoom.addRate("weekly", 600.0);

        VenueSpace bronteRoom = new VenueSpace("Brontë Boardroom", "room");
        bronteRoom.addCapacity("classroom", 25);
        bronteRoom.addCapacity("boardroom", 18);
        bronteRoom.addCapacity("presentation", 40);
        bronteRoom.addRate("hourly", 40.0);
        bronteRoom.addRate("half_day", 120.0);
        bronteRoom.addRate("full_day", 200.0);
        bronteRoom.addRate("weekly", 900.0);

        VenueSpace dickensDen = new VenueSpace("Dickens Den", "room");
        dickensDen.addCapacity("classroom", 15);
        dickensDen.addCapacity("boardroom", 12);
        dickensDen.addCapacity("presentation", 25);
        dickensDen.addRate("hourly", 30.0);
        dickensDen.addRate("half_day", 90.0);
        dickensDen.addRate("full_day", 150.0);
        dickensDen.addRate("weekly", 700.0);

        VenueSpace poeParlor = new VenueSpace("Poe Parlor", "room");
        poeParlor.addCapacity("classroom", 20);
        poeParlor.addCapacity("boardroom", 14);
        poeParlor.addCapacity("presentation", 30);
        poeParlor.addRate("hourly", 35.0);
        poeParlor.addRate("half_day", 100.0);
        poeParlor.addRate("full_day", 170.0);
        poeParlor.addRate("weekly", 800.0);

        VenueSpace globeRoom = new VenueSpace("Globe Room", "room");
        globeRoom.addCapacity("classroom", 30);
        globeRoom.addCapacity("boardroom", 20);
        globeRoom.addCapacity("presentation", 50);
        globeRoom.addRate("hourly", 50.0);
        globeRoom.addRate("half_day", 150.0);
        globeRoom.addRate("full_day", 250.0);
        globeRoom.addRate("weekly", 1100.0);

        VenueSpace chekhovChamber = new VenueSpace("Chekhov Chamber", "room");
        chekhovChamber.addCapacity("classroom", 18);
        chekhovChamber.addCapacity("boardroom", 16);
        chekhovChamber.addCapacity("presentation", 35);
        chekhovChamber.addRate("hourly", 38.0);
        chekhovChamber.addRate("half_day", 110.0);
        chekhovChamber.addRate("full_day", 180.0);
        chekhovChamber.addRate("weekly", 850.0);

        // Add all venues to the list
        venueSpaces.add(mainHall);
        venueSpaces.add(smallHall);
        venueSpaces.add(rehearsalSpace);
        venueSpaces.add(greenRoom);
        venueSpaces.add(bronteRoom);
        venueSpaces.add(dickensDen);
        venueSpaces.add(poeParlor);
        venueSpaces.add(globeRoom);
        venueSpaces.add(chekhovChamber);
    }
    public void refresh() {
        updateCalendarView();
        parentFrame.setStatus("Calendar updated");
    }

    private void updateCalendarView() {
        calendarView.removeAll();

        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        monthYearLabel.setText(monthNames[currentMonth] + " " + currentYear);

        JPanel daysPanel = new JPanel(new GridLayout(0, 7));

        // Add day labels
        String[] dayLabels = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayLabel : dayLabels) {
            JLabel label = new JLabel(dayLabel, SwingConstants.CENTER);
            label.setBackground(new Color(220, 220, 220));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            daysPanel.add(label);
        }

        // Get the first day of the month
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);
        int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday

        // Add empty cells before the first day
        for (int i = 0; i < firstDayOfMonth; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Get the number of days in the month
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add day cells
        VenueSpace selectedVenue = (VenueSpace) venueSelector.getSelectedItem();
        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;

            JPanel dayCell = new JPanel(new BorderLayout());
            dayCell.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayCell.add(dayLabel, BorderLayout.NORTH);

            // Format the date string
            String dateString = String.format("%04d-%02d-%02d", currentYear, currentMonth + 1, day);

            // Check if there are bookings for this day and venue
            JPanel bookingsPanel = new JPanel();
            bookingsPanel.setLayout(new BoxLayout(bookingsPanel, BoxLayout.Y_AXIS));

            ArrayList<BookingEntry> dayBookings = bookings.get(dateString);
            if (dayBookings != null) {
                for (BookingEntry booking : dayBookings) {
                    if (booking.getVenueSpace().equals(selectedVenue.getName())) {
                        JLabel bookingLabel = new JLabel(booking.getTimeSlot() + " - " + booking.getClient());
                        bookingLabel.setForeground(Color.BLUE);
                        bookingsPanel.add(bookingLabel);
                    }
                }
            }

            JScrollPane scrollPane = new JScrollPane(bookingsPanel);
            scrollPane.setPreferredSize(new Dimension(50, 50));
            dayCell.add(scrollPane, BorderLayout.CENTER);

            // Add click listener to add booking
            dayCell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showBookingDialog(dateString, selectedVenue);
                }
            });

            daysPanel.add(dayCell);
        }

        calendarView.add(new JScrollPane(daysPanel), BorderLayout.CENTER);
        calendarView.revalidate();
        calendarView.repaint();
    }

    private void changeMonth(int delta) {
        currentMonth += delta;

        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        } else if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }

        refresh();
    }

    public void showBookingDialog(String date, VenueSpace venue) {
        JDialog bookingDialog = new JDialog(parentFrame, "New Booking", true);
        bookingDialog.setSize(500, 400);
        bookingDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Format date for display
        LocalDate localDate = LocalDate.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = localDate.format(formatter);

        JLabel dateLabel = new JLabel("Date: " + formattedDate);
        JLabel venueLabel = new JLabel("Venue: " + venue.getName() + " (Capacity: " + venue.getDefaultCapacity() + ")");

        String[] timeSlots = {"Morning (10:00-13:00)", "Afternoon (13:00-17:00)",
                "Evening (17:00-00:00)", "Full Day (10:00-00:00)"};
        JComboBox<String> timeSlotCombo = new JComboBox<>(timeSlots);

        JLabel clientLabel = new JLabel("Client Name:");
        JTextField clientField = new JTextField(20);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);

        JLabel costLabel = new JLabel("Estimated Cost: £0.00");
        JCheckBox confirmedCheck = new JCheckBox("Booking Confirmed");

        // Calculate cost based on selected time slot
        timeSlotCombo.addActionListener(e -> {
            String selectedTimeSlot = (String) timeSlotCombo.getSelectedItem();
            double cost = calculateCost(venue, selectedTimeSlot, date);
            costLabel.setText(String.format("Estimated Cost: £%.2f", cost));
        });

        JButton saveButton = new JButton("Save Booking");
        JButton cancelButton = new JButton("Cancel");

        // Add components to panel
        panel.add(dateLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(venueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.add(new JLabel("Time Slot:"));
        timePanel.add(timeSlotCombo);
        panel.add(timePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.add(clientLabel);
        clientPanel.add(clientField);
        panel.add(clientPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(descriptionLabel);
        panel.add(descScroll);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(costLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(confirmedCheck);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        // Save button action
        saveButton.addActionListener(e -> {
            String client = clientField.getText();
            if (client.trim().isEmpty()) {
                JOptionPane.showMessageDialog(bookingDialog,
                        "Please enter a client name", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedTimeSlot = (String) timeSlotCombo.getSelectedItem();
            String description = descriptionArea.getText();
            double cost = calculateCost(venue, selectedTimeSlot, date);
            boolean confirmed = confirmedCheck.isSelected();

            BookingEntry booking = new BookingEntry(date, venue.getName(), venue.getType(), selectedTimeSlot,
                    client, description, cost);
            booking.setConfirmed(confirmed);

            addBooking(booking);
            bookingDialog.dispose();
            refresh();
            parentFrame.refreshAllPanels();
        });

        // Cancel button action
        cancelButton.addActionListener(e -> bookingDialog.dispose());

        bookingDialog.add(panel);
        bookingDialog.setVisible(true);
    }

    private double calculateCost(VenueSpace venue, String timeSlot, String dateString) {
        // Check if the date is a weekday or weekend
        LocalDate date = LocalDate.parse(dateString);
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        boolean isWeekend = (dayOfWeek >= 5); // Friday, Saturday are weekend

        switch (timeSlot) {
            case "Morning (10:00-13:00):":
            case "Afternoon (13:00-17:00)":
                return venue.getRate("hourly") * 3; // 3 hours
            case "Evening (17:00-00:00)":
                return isWeekend ? venue.getRate("evening_weekend") : venue.getRate("evening_weekday");
            case "Full Day (10:00-00:00)":
                return isWeekend ? venue.getRate("daily_weekend") : venue.getRate("daily_weekday");
            default:
                return 0.0;
        }
    }

    public void addBooking(BookingEntry booking) {
        String date = booking.getDate();
        if (!bookings.containsKey(date)) {
            bookings.put(date, new ArrayList<>());
        }
        bookings.get(date).add(booking);
    }

    public ArrayList<BookingEntry> getAllBookings() {
        ArrayList<BookingEntry> allBookings = new ArrayList<>();
        for (ArrayList<BookingEntry> dateBookings : bookings.values()) {
            allBookings.addAll(dateBookings);
        }
        return allBookings;
    }

    public double calculateRevenue() {
        double total = 0.0;
        for (ArrayList<BookingEntry> dateBookings : bookings.values()) {
            for (BookingEntry booking : dateBookings) {
                if (booking.isConfirmed()) {
                    total += booking.getCost();
                }
            }
        }
        return total;
    }

    public ArrayList<VenueSpace> getVenueSpaces() {
        return venueSpaces;
    }
    public void removeBooking(BookingEntry booking) {
        String date = booking.getDate();
        if (bookings.containsKey(date)) {
            bookings.get(date).remove(booking);
            if (bookings.get(date).isEmpty()) {
                bookings.remove(date);
            }
        }
    }
}