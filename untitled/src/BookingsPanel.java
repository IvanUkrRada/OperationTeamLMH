package untitled.src;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookingsPanel extends JPanel {
    private MainFrame parentFrame;
    private CalendarPanel calendarPanel;
    private JTable bookingsTable;
    private BookingsTableModel tableModel;
    private JButton addButton, editButton, deleteButton;

    public BookingsPanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;
        this.calendarPanel = calendarPanel;

        setLayout(new BorderLayout());

        tableModel = new BookingsTableModel(calendarPanel.getAllBookings());
        bookingsTable = new JTable(tableModel);

        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Venue
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Time Slot
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Client
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Cost
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Confirmed

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Implemented only 2 funcitons Add and Delete (covers neccesary functionalit but edit button can be add too)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add Booking");
        deleteButton = new JButton("Delete Booking");

        buttonPanel.add(addButton);

        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddBookingDialog());
        deleteButton.addActionListener(e -> deleteSelectedBooking());
    }


    //Updates the view
    public void refresh() {
        tableModel.updateData(calendarPanel.getAllBookings());
        bookingsTable.repaint();
    }

    private void showAddBookingDialog() {
        // Dialog to select date and venue first
        JDialog selectDialog = new JDialog(parentFrame, "Select Date and Venue", true);
        selectDialog.setSize(300, 200);
        selectDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel dateLabel = new JLabel("Date:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        JLabel venueLabel = new JLabel("Venue:");
        JComboBox<VenueSpace> venueCombo = new JComboBox<>();
        for (VenueSpace space : calendarPanel.getVenueSpaces()) {
            venueCombo.addItem(space);
        }

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        panel.add(dateLabel);
        panel.add(dateSpinner);
        panel.add(venueLabel);
        panel.add(venueCombo);
        panel.add(cancelButton);
        panel.add(okButton);

        okButton.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(selectedDate);

            VenueSpace selectedVenue = (VenueSpace) venueCombo.getSelectedItem();

            selectDialog.dispose();
            showCustomBookingDialog(dateString, selectedVenue);
            refresh();
        });

        cancelButton.addActionListener(e -> selectDialog.dispose());

        selectDialog.add(panel);
        selectDialog.setVisible(true);
    }

    private void showCustomBookingDialog(String date, VenueSpace venue) {
        JDialog bookingDialog = new JDialog(parentFrame, "New Booking", true);
        bookingDialog.setSize(550, 450);
        bookingDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Format date for display
        LocalDate localDate = LocalDate.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = localDate.format(formatter);

        JLabel dateLabel = new JLabel("Date: " + formattedDate);

        // Handle different venue types differently
        boolean isRoom = venue.getType().equals("room");

        // Create venue label based on venue type
        String venueInfo;
        if (isRoom) {
            venueInfo = venue.getName() + " (Meeting Room)";
        } else {
            venueInfo = venue.getName() + " (Capacity: " + venue.getCapacity("standard") + ")";
        }
        JLabel venueLabel = new JLabel("Venue: " + venueInfo);

        // Room configuration selector (only for meeting rooms)
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel configLabel = new JLabel("Room Configuration:");
        JComboBox<String> configCombo = new JComboBox<>(new String[]{"Classroom", "Boardroom", "Presentation"});
        JLabel capacityLabel = new JLabel();

        if (isRoom) {
            configCombo.addActionListener(e -> {
                String config = ((String)configCombo.getSelectedItem()).toLowerCase();
                capacityLabel.setText("Capacity: " + venue.getCapacity(config));
            });
            configPanel.add(configLabel);
            configPanel.add(configCombo);
            configPanel.add(capacityLabel);

            // Set initial capacity display
            configCombo.setSelectedIndex(0);
            capacityLabel.setText("Capacity: " + venue.getCapacity("classroom"));
        }

        // Add booking type selection
        JLabel bookingTypeLabel = new JLabel("Booking Type:");
        JRadioButton hourlyButton = new JRadioButton("Hourly Rate");

        ButtonGroup bookingTypeGroup = new ButtonGroup();
        bookingTypeGroup.add(hourlyButton);
        hourlyButton.setSelected(true);

        // Add different booking options based on venue type
        JRadioButton halfDayButton;
        JRadioButton eveningButton;
        JRadioButton dailyButton = null;
        JRadioButton weeklyButton = null;

        if (isRoom) {
            eveningButton = null;
            // Room booking types
            halfDayButton = new JRadioButton("Half Day (Morning/Afternoon)");
            dailyButton = new JRadioButton("Full Day");
            weeklyButton = new JRadioButton("Weekly");

            bookingTypeGroup.add(halfDayButton);
            bookingTypeGroup.add(dailyButton);
            bookingTypeGroup.add(weeklyButton);
        } else {
            halfDayButton = null;
            // Hall booking types
            eveningButton = new JRadioButton("Evening Rate (17:00-00:00)");
            dailyButton = new JRadioButton("Daily Rate (10:00-00:00)");
            weeklyButton = new JRadioButton("Weekly Rate (if applicable)");

            bookingTypeGroup.add(eveningButton);
            bookingTypeGroup.add(dailyButton);
            bookingTypeGroup.add(weeklyButton);

            // Weekly rate only available for Rehearsal Space for halls
            weeklyButton.setEnabled(venue.getName().equals("Rehearsal Space"));
        }

        // Time selection panel (only visible for hourly bookings)
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Start time selection
        JLabel startTimeLabel = new JLabel("Start Time:");
        SpinnerModel startHourModel = new SpinnerNumberModel(10, 10, 23, 1);
        JSpinner startHourSpinner = new JSpinner(startHourModel);

        JLabel startMinLabel = new JLabel(":");
        SpinnerModel startMinModel = new SpinnerNumberModel(0, 0, 55, 5);
        JSpinner startMinSpinner = new JSpinner(startMinModel);
        JFormattedTextField startMinField = ((JSpinner.DefaultEditor) startMinSpinner.getEditor()).getTextField();
        startMinField.setColumns(2);

        // End time selection
        JLabel endTimeLabel = new JLabel("End Time:");
        SpinnerModel endHourModel = new SpinnerNumberModel(13, 10, 23, 1);
        JSpinner endHourSpinner = new JSpinner(endHourModel);

        JLabel endMinLabel = new JLabel(":");
        SpinnerModel endMinModel = new SpinnerNumberModel(0, 0, 55, 5);
        JSpinner endMinSpinner = new JSpinner(endMinModel);
        JFormattedTextField endMinField = ((JSpinner.DefaultEditor) endMinSpinner.getEditor()).getTextField();
        endMinField.setColumns(2);

        // Duration and validation labels
        JLabel durationLabel = new JLabel();
        JLabel validationLabel = new JLabel("");
        validationLabel.setForeground(Color.RED);

        // Set appropriate minimum duration text based on venue type
        if (isRoom) {
            durationLabel.setText("Duration: 3 hours (minimum 1 hour)");
        } else {
            durationLabel.setText("Duration: 3 hours (minimum 3 hours)");
        }

        // Add components to time panel
        timePanel.add(startTimeLabel);
        timePanel.add(startHourSpinner);
        timePanel.add(startMinLabel);
        timePanel.add(startMinSpinner);
        timePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        timePanel.add(endTimeLabel);
        timePanel.add(endHourSpinner);
        timePanel.add(endMinLabel);
        timePanel.add(endMinSpinner);

        // Time change listener to update duration
        ChangeListener timeChangeListener = e -> {
            int startHour = (Integer) startHourSpinner.getValue();
            int startMin = (Integer) startMinSpinner.getValue();
            int endHour = (Integer) endHourSpinner.getValue();
            int endMin = (Integer) endMinSpinner.getValue();

            LocalTime startTime = LocalTime.of(startHour, startMin);
            LocalTime endTime = LocalTime.of(endHour, endMin);

            // Calculate hours and minutes difference
            long minutesDiff = java.time.Duration.between(startTime, endTime).toMinutes();
            double hoursDiff = minutesDiff / 60.0;

            // Different minimum durations based on venue type
            double minHours = isRoom ? 1.0 : 3.0;

            if (hoursDiff < minHours) {
                durationLabel.setText(String.format("Duration: %.1f hours (minimum %.1f hours required)",
                        hoursDiff, minHours));
                durationLabel.setForeground(Color.RED);
                validationLabel.setText("Booking must be at least " + minHours + " hours");
            } else {
                durationLabel.setText(String.format("Duration: %.1f hours", hoursDiff));
                durationLabel.setForeground(Color.BLACK);
                validationLabel.setText("");
            }
        };

        startHourSpinner.addChangeListener(timeChangeListener);
        startMinSpinner.addChangeListener(timeChangeListener);
        endHourSpinner.addChangeListener(timeChangeListener);
        endMinSpinner.addChangeListener(timeChangeListener);

        // Toggle visibility of time panel based on booking type
        ActionListener bookingTypeListener = e -> {
            boolean isHourly = hourlyButton.isSelected();
            timePanel.setVisible(isHourly);
            durationLabel.setVisible(isHourly);
            validationLabel.setVisible(isHourly);
            bookingDialog.pack();
            bookingDialog.setSize(550, isHourly ? 450 : 350);
        };

        hourlyButton.addActionListener(bookingTypeListener);
        if (isRoom) {
            if (halfDayButton != null) halfDayButton.addActionListener(bookingTypeListener);
        } else {
            if (eveningButton != null) eveningButton.addActionListener(bookingTypeListener);
        }
        if (dailyButton != null) dailyButton.addActionListener(bookingTypeListener);
        if (weeklyButton != null) weeklyButton.addActionListener(bookingTypeListener);

        JLabel clientLabel = new JLabel("Client Name:");
        JTextField clientField = new JTextField(20);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);

        JLabel costLabel = new JLabel("Estimated Cost: £0.00");
        JCheckBox confirmedCheck = new JCheckBox("Booking Confirmed");

        // Calculate cost based on booking type
        JRadioButton finalDailyButton = dailyButton;
        JRadioButton finalWeeklyButton = weeklyButton;
        ActionListener costCalculationListener = e -> {
            double cost = 0.0;
            int day = localDate.getDayOfWeek().getValue();
            boolean isWeekend = (day == 5 || day == 6 || day == 7); // Friday, Saturday, Sunday

            if (hourlyButton.isSelected()) {
                int startHour = (Integer) startHourSpinner.getValue();
                int startMin = (Integer) startMinSpinner.getValue();
                int endHour = (Integer) endHourSpinner.getValue();
                int endMin = (Integer) endMinSpinner.getValue();

                LocalTime startTime = LocalTime.of(startHour, startMin);
                LocalTime endTime = LocalTime.of(endHour, endMin);

                double hoursDiff = java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;

                // Apply minimum hours based on venue type
                if (isRoom) {
                    hoursDiff = Math.max(1, hoursDiff); // Minimum 1 hour for rooms
                } else {
                    hoursDiff = Math.max(3, hoursDiff); // Minimum 3 hours for halls
                }

                cost = venue.getRate("hourly") * hoursDiff;
            } else if (isRoom) {
                // Room specific rates
                if (halfDayButton != null && halfDayButton.isSelected()) {
                    cost = venue.getRate("half_day");
                } else if (finalDailyButton != null && finalDailyButton.isSelected()) {
                    cost = venue.getRate("full_day");
                } else if (finalWeeklyButton != null && finalWeeklyButton.isSelected()) {
                    cost = venue.getRate("weekly");
                }
            } else {
                // Hall specific rates
                if (eveningButton != null && eveningButton.isSelected()) {
                    cost = isWeekend ? venue.getRate("evening_weekend") : venue.getRate("evening_weekday");
                } else if (finalDailyButton != null && finalDailyButton.isSelected()) {
                    cost = isWeekend ? venue.getRate("daily_weekend") : venue.getRate("daily_weekday");
                } else if (finalWeeklyButton != null && finalWeeklyButton.isSelected() && venue.getName().equals("Rehearsal Space")) {
                    cost = venue.getRate("weekly_full");
                }
            }

            costLabel.setText(String.format("Estimated Cost: £%.2f", cost));
        };

        hourlyButton.addActionListener(costCalculationListener);
        if (isRoom) {
            if (halfDayButton != null) halfDayButton.addActionListener(costCalculationListener);
        } else {
            if (eveningButton != null) eveningButton.addActionListener(costCalculationListener);
        }
        if (dailyButton != null) dailyButton.addActionListener(costCalculationListener);
        if (weeklyButton != null) weeklyButton.addActionListener(costCalculationListener);

        startHourSpinner.addChangeListener(e -> costCalculationListener.actionPerformed(null));
        startMinSpinner.addChangeListener(e -> costCalculationListener.actionPerformed(null));
        endHourSpinner.addChangeListener(e -> costCalculationListener.actionPerformed(null));
        endMinSpinner.addChangeListener(e -> costCalculationListener.actionPerformed(null));

        if (isRoom) {
            configCombo.addActionListener(e -> costCalculationListener.actionPerformed(null));
        }

        // Initialize cost
        costCalculationListener.actionPerformed(null);

        JButton saveButton = new JButton("Save Booking");
        JButton cancelButton = new JButton("Cancel");

        // Add components to panel
        panel.add(dateLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(venueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add configuration panel for rooms
        if (isRoom) {
            panel.add(configPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panel.add(bookingTypeLabel);
        panel.add(hourlyButton);
        if (isRoom) {
            if (halfDayButton != null) panel.add(halfDayButton);
        } else {
            if (eveningButton != null) panel.add(eveningButton);
        }
        if (dailyButton != null) panel.add(dailyButton);
        if (weeklyButton != null) panel.add(weeklyButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(timePanel);
        panel.add(durationLabel);
        panel.add(validationLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

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
        JRadioButton finalDailyButton1;
        finalDailyButton1 = dailyButton;
        saveButton.addActionListener(e -> {
            String client = clientField.getText();
            if (client.trim().isEmpty()) {
                JOptionPane.showMessageDialog(bookingDialog,
                        "Please enter a client name", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String description = descriptionArea.getText();
            double cost = 0.0;
            boolean confirmed = confirmedCheck.isSelected();

            // Build the timeSlot string based on booking type
            // Build the timeSlot string based on booking type
            String timeSlot;
            if (hourlyButton.isSelected()) {
                int startHour = (Integer) startHourSpinner.getValue();
                int startMin = (Integer) startMinSpinner.getValue();
                int endHour = (Integer) endHourSpinner.getValue();
                int endMin = (Integer) endMinSpinner.getValue();

                LocalTime startTime = LocalTime.of(startHour, startMin);
                LocalTime endTime = LocalTime.of(endHour, endMin);

                double hoursDiff = java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;

                // Validate minimum hours based on venue type
                double minHours = isRoom ? 1.0 : 3.0;

                if (hoursDiff < minHours) {
                    JOptionPane.showMessageDialog(bookingDialog,
                            "Booking must be at least " + minHours + " hours",
                            "Invalid Time Range", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                timeSlot = String.format("%02d:%02d - %02d:%02d (Hourly Rate)",
                        startHour, startMin, endHour, endMin);

                // Calculate hourly cost with appropriate minimum
                if (isRoom) {
                    cost = venue.getRate("hourly") * Math.max(1, hoursDiff);
                } else {
                    cost = venue.getRate("hourly") * Math.max(3, hoursDiff);
                }
            } else if (isRoom) {
                // Room-specific booking types
                if (halfDayButton != null && halfDayButton.isSelected()) {
                    timeSlot = "Half Day (Morning/Afternoon)";
                    cost = venue.getRate("half_day");
                } else if (finalDailyButton1 != null && finalDailyButton1.isSelected()) {
                    timeSlot = "Full Day";
                    cost = venue.getRate("full_day");
                } else { // Weekly
                    timeSlot = "Weekly";
                    cost = venue.getRate("weekly");
                }
            } else {
                // Hall-specific booking types
                if (eveningButton != null && eveningButton.isSelected()) {
                    timeSlot = "17:00 - 00:00 (Evening Rate)";
                    int day = localDate.getDayOfWeek().getValue();
                    boolean isWeekend = (day == 5 || day == 6 || day == 7); // Friday, Saturday, Sunday
                    cost = isWeekend ? venue.getRate("evening_weekend") : venue.getRate("evening_weekday");
                } else if (finalDailyButton1 != null && finalDailyButton1.isSelected()) {
                    timeSlot = "10:00 - 00:00 (Daily Rate)";
                    int day = localDate.getDayOfWeek().getValue();
                    boolean isWeekend = (day == 5 || day == 6 || day == 7);
                    cost = isWeekend ? venue.getRate("daily_weekend") : venue.getRate("daily_weekday");
                } else { // Weekly
                    timeSlot = "Weekly Rate";
                    cost = venue.getRate("weekly_full");
                }
            }

            // Create booking object with appropriate properties
            BookingEntry booking;

            if (isRoom) {
                // Include room configuration for room bookings
                String roomConfig = ((String)configCombo.getSelectedItem()).toLowerCase();
                booking = new BookingEntry(date, venue.getName(), venue.getType(), timeSlot,
                        client, description, cost);
                booking.setConfiguration(roomConfig);
            } else {
                booking = new BookingEntry(date, venue.getName(), venue.getType(), timeSlot,
                        client, description, cost);
            }

            booking.setConfirmed(confirmed);

            calendarPanel.addBooking(booking);
            bookingDialog.dispose();
            refresh();
            parentFrame.refreshAllPanels();
            parentFrame.setStatus("New booking added for " + venue.getName());
        });

        // Cancel button action
        cancelButton.addActionListener(e -> bookingDialog.dispose());

        bookingDialog.add(new JScrollPane(panel));
        bookingDialog.setVisible(true);
    }


    private void deleteSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected booking?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            BookingEntry booking = calendarPanel.getAllBookings().get(selectedRow);
            calendarPanel.removeBooking(booking);
            refresh();
            parentFrame.refreshAllPanels();
            parentFrame.setStatus("Booking deleted");
        }
    }
}