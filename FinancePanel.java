import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Locale;


//V2
public class FinancePanel extends JPanel {
    private MainFrame parentFrame;
    private CalendarPanel calendarPanel;
    private JLabel totalRevenueLabel;
    private JTable financeTable;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    private JComboBox<String> periodSelector;
    private JComboBox<String> exportOptionSelector;

    public FinancePanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;
        this.calendarPanel = calendarPanel;
        setLayout(new BorderLayout());

    // Top wrapper split into two rows
        JPanel topWrapper = new JPanel(new GridLayout(2, 1));

    // Buttons Panel
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton ticketSalesButton = new JButton("Ticket Sales");
        JButton revenueButton = new JButton("Revenue");
        JButton contractsButton = new JButton("Contracts");

    // Button listeners
        ticketSalesButton.addActionListener(e -> {
            loadTicketSalesData();
            updateRevenueChart();
        });
        revenueButton.addActionListener(e -> {
            loadRevenueData();
            updateRevenueChart();
        });
        contractsButton.addActionListener(e -> loadContractData());

        switchPanel.add(ticketSalesButton);
        switchPanel.add(revenueButton);
        switchPanel.add(contractsButton);

    // Controls Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalRevenueLabel = new JLabel("Total Revenue: £0.00");
        topPanel.add(totalRevenueLabel);

        String[] periodOptions = {"Current Month", "Current Quarter", "Current Year"};
        periodSelector = new JComboBox<>(periodOptions);
        topPanel.add(new JLabel("Period:"));
        topPanel.add(periodSelector);

        String[] exportOptions = {"Current Table Only", "All Finance Views"};
        exportOptionSelector = new JComboBox<>(exportOptions);
        JButton exportButton = new JButton("Export");
        topPanel.add(exportOptionSelector);
        topPanel.add(exportButton);

        JButton compareButton = new JButton("Compare to Previous");
        topPanel.add(compareButton);
        compareButton.addActionListener(e -> showComparisonDialog());

        JButton invoiceButton = new JButton("Generate Invoices");
        invoiceButton.addActionListener(e -> generateInvoices());
        topPanel.add(invoiceButton);

    // Assemble top section
        exportButton.addActionListener(this::handleExport);
        topWrapper.add(switchPanel);
        topWrapper.add(topPanel);
        add(topWrapper, BorderLayout.NORTH);

    // Table
        String[] defaultColumns = {"Event Name", "Tickets Sold", "Total Revenue"};
        tableModel = new DefaultTableModel(defaultColumns, 0);
        financeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(financeTable);
        add(tableScrollPane, BorderLayout.CENTER);

    // Chart panel
        chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(100, 150));
        chartPanel.setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.SOUTH);

        loadTicketSalesData();
        updateRevenueChart();
    }

    private boolean isInSelectedPeriod(LocalDate date) {
        LocalDate now = LocalDate.now();
        String selected = (String) periodSelector.getSelectedItem();

        if ("Current Month".equals(selected)) {
            return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
        } else if ("Current Quarter".equals(selected)) {
            int currentQuarter = (now.getMonthValue() - 1) / 3;
            int dateQuarter = (date.getMonthValue() - 1) / 3;
            return date.getYear() == now.getYear() && dateQuarter == currentQuarter;
        } else {
            return date.getYear() == now.getYear();
        }
    }

    private void loadTicketSalesData() {
        String[] columnNames = {"Event Name", "Tickets Sold", "Total Revenue"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        financeTable.setModel(model);

        ArrayList<BookingEntry> bookings = calendarPanel.getAllBookings();
        double total = 0.0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (BookingEntry booking : bookings) {
            if (!booking.isConfirmed()) continue;

            try {
                LocalDate date = LocalDate.parse(booking.getDate(), formatter);
                if (!isInSelectedPeriod(date)) continue;

                String eventName = booking.getVenueSpace() + " - " + booking.getTimeSlot();

                double cost = booking.getCost();
                int ticketsSold = (int) (cost / 20.0);

                model.addRow(new Object[]{
                        eventName,
                        ticketsSold,
                        String.format("£%.2f", cost)
                });
                total += cost;


            } catch (Exception ignored) {
            }
        }

        totalRevenueLabel.setText("Total Revenue: £" + String.format("%.2f", total));

        updateRevenueChart();

    }

    private void loadRevenueData() {
        String[] columnNames = {"Event Name", "Ticket Revenue", "Venue Revenue", "Total Revenue", "Client Share"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        financeTable.setModel(model);

        ArrayList<BookingEntry> bookings = calendarPanel.getAllBookings();
        double total = 0.0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (BookingEntry booking : bookings) {
            if (!booking.isConfirmed()) continue;

            try {
                LocalDate date = LocalDate.parse(booking.getDate(), formatter);
                if (!isInSelectedPeriod(date)) continue;

                String eventName = booking.getVenueSpace() + " - " + booking.getTimeSlot();
                double totalRevenue = booking.getCost();
                double venueRevenue = totalRevenue;
                double clientShare = totalRevenue * 0.25;

                total += totalRevenue;

                model.addRow(new Object[]{eventName,
                        String.format("£%.2f", totalRevenue - clientShare),
                        String.format("£%.2f", venueRevenue),
                        String.format("£%.2f", totalRevenue),
                        String.format("£%.2f", clientShare)});
            } catch (Exception ignored) {
            }
        }

        totalRevenueLabel.setText("Total Revenue: £" + String.format("%.2f", total));

        updateRevenueChart();

    }

    private void loadContractData() {
        String[] columnNames = {"Event Name", "Client Name", "Booking Cost", "Booking Details"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        financeTable.setModel(model);

        ArrayList<BookingEntry> bookings = calendarPanel.getAllBookings();
        double total = 0.0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (BookingEntry booking : bookings) {
            if (!booking.isConfirmed()) continue;

            try {
                LocalDate date = LocalDate.parse(booking.getDate(), formatter);
                if (!isInSelectedPeriod(date)) continue;

                String eventName = booking.getVenueSpace() + " - " + booking.getTimeSlot();
                String clientName = booking.getClient();
                double cost = booking.getCost();
                String details = booking.getDate() + " - " + booking.getVenueSpace() + " - " + booking.getTimeSlot();


                model.addRow(new Object[]{eventName, clientName, String.format("£%.2f", cost), details});
                total += cost;

            } catch (Exception ignored) {}
        }

        totalRevenueLabel.setText("Total Revenue: £" + String.format("%.2f", total));
        updateRevenueChart();
    }


    private void handleExport(ActionEvent e) {
        String selectedExport = (String) exportOptionSelector.getSelectedItem();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try {
            FileWriter writer = new FileWriter(file);

            if ("Current Table Only".equals(selectedExport)) {
                for (int i = 0; i < financeTable.getColumnCount(); i++) {
                    writer.write(financeTable.getColumnName(i) + (i < financeTable.getColumnCount() - 1 ? "," : "\n"));
                }
                for (int i = 0; i < financeTable.getRowCount(); i++) {
                    for (int j = 0; j < financeTable.getColumnCount(); j++) {
                        writer.write(financeTable.getValueAt(i, j).toString());
                        if (j < financeTable.getColumnCount() - 1) writer.write(",");
                    }
                    writer.write("\n");
                }
            } else {
                exportView(writer, "Ticket Sales", this::loadTicketSalesData);
                exportView(writer, "Revenue", this::loadRevenueData);
                exportView(writer, "Contracts", this::loadContractData);
            }

            // Always export chart regardless of selection
            writer.write("\nMonthly Revenue Chart (Full Year View: Current vs Previous Year)\n");
            String[] monthLabels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int currentYear = LocalDate.now().getYear();
            double[] current = new double[12];
            double[] previous = new double[12];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (BookingEntry booking : calendarPanel.getAllBookings()) {
                if (!booking.isConfirmed()) continue;
                try {
                    LocalDate date = LocalDate.parse(booking.getDate(), formatter);
                    int month = date.getMonthValue() - 1;
                    if (date.getYear() == currentYear) current[month] += booking.getCost();
                    else if (date.getYear() == currentYear - 1) previous[month] += booking.getCost();
                } catch (Exception ignored) {
                }
            }

            for (int i = 0; i < 12; i++) {
                writer.write(String.format("%s,%s,%.2f,%.2f\n",
                        monthLabels[i], "Revenue (Curr / Prev)", current[i], previous[i]));
            }

            writer.close();
            JOptionPane.showMessageDialog(this, "Data exported successfully.");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage());
        }

    }

    private void exportView(FileWriter writer, String title, Runnable loader) throws IOException {
        writer.write("\n" + title + "\n");
        loader.run();
        for (int i = 0; i < financeTable.getColumnCount(); i++) {
            writer.write(financeTable.getColumnName(i) + (i < financeTable.getColumnCount() - 1 ? "," : "\n"));
        }
        for (int i = 0; i < financeTable.getRowCount(); i++) {
            for (int j = 0; j < financeTable.getColumnCount(); j++) {
                writer.write(financeTable.getValueAt(i, j).toString());
                if (j < financeTable.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");
        }
    }

    private void showComparisonDialog() {
        JDialog comparisonDialog = new JDialog(parentFrame, "Revenue Comparison", true);
        comparisonDialog.setSize(400, 250);
        comparisonDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout());

        double current = 0.0;
        double previous = 0.0;

        String selected = (String) periodSelector.getSelectedItem();  // Current Month etc.
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (BookingEntry booking : calendarPanel.getAllBookings()) {
            if (!booking.isConfirmed()) continue;

            try {
                LocalDate date = LocalDate.parse(booking.getDate(), formatter);
                double cost = booking.getCost();

                if ("Current Month".equals(selected)) {
                    if (date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
                        current += cost;
                    } else if (date.getMonth() == now.minusMonths(1).getMonth()
                            && date.getYear() == now.minusMonths(1).getYear()) {
                        previous += cost;
                    }

                } else if ("Current Quarter".equals(selected)) {
                    int currentQ = (now.getMonthValue() - 1) / 3;
                    int dateQ = (date.getMonthValue() - 1) / 3;
                    if (date.getYear() == now.getYear() && dateQ == currentQ) {
                        current += cost;
                    } else if (date.getYear() == now.minusYears(1).getYear() && dateQ == currentQ) {
                        previous += cost;
                    }

                } else { // Current Year
                    if (date.getYear() == now.getYear()) {
                        current += cost;
                    } else if (date.getYear() == now.getYear() - 1) {
                        previous += cost;
                    }
                }

            } catch (Exception e) {
                System.err.println("Invalid date: " + booking.getDate());
            }
        }

        // Labels and formatting
        JPanel revenuePanel = new JPanel(new GridLayout(3, 1, 10, 5));
        revenuePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String periodLabel = selected.replace("Current ", "");
        revenuePanel.add(new JLabel("This " + periodLabel + ": £" + String.format("%.2f", current)));
        revenuePanel.add(new JLabel("Last " + periodLabel + ": £" + String.format("%.2f", previous)));


        double diff = current - previous;
        String message = (diff >= 0 ? "↑ Increase: " : "↓ Decrease: ") + "£" + String.format("%.2f", Math.abs(diff));
        JLabel changeLabel = new JLabel(message, SwingConstants.CENTER);
        changeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        revenuePanel.add(changeLabel);

        panel.add(revenuePanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> comparisonDialog.dispose());
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        comparisonDialog.add(panel);
        comparisonDialog.setVisible(true);
    }


    private void updateRevenueChart() {
        chartPanel.removeAll();
        JTextArea chartArea = new JTextArea();
        chartArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chartArea.setEditable(false);

        double[] current = new double[12];
        double[] previous = new double[12];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int currentYear = LocalDate.now().getYear();

        for (BookingEntry booking : calendarPanel.getAllBookings()) {
            if (!booking.isConfirmed()) continue;

            try {
                LocalDate date = LocalDate.parse(booking.getDate(), formatter);
                double cost = booking.getCost();
                int month = date.getMonthValue() - 1;

                if (date.getYear() == currentYear) {
                    current[month] += cost;
                } else if (date.getYear() == currentYear - 1) {
                    previous[month] += cost;
                }

            } catch (Exception ignored) {}
        }

        double max = 0.0;
        for (int i = 0; i < 12; i++) {
            max = Math.max(max, Math.max(current[i], previous[i]));
        }

        String[] monthLabels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 0; i < 12; i++) {
            int currBars = (int) ((max == 0 ? 0 : (current[i] / max)) * 20);
            int prevBars = (int) ((max == 0 ? 0 : (previous[i] / max)) * 20);
            String currBar = "█".repeat(currBars);
            String prevBar = "▒".repeat(prevBars);

            chartArea.append(String.format("%-3s %s | %s £%.2f / £%.2f\n",
                    monthLabels[i], currBar, prevBar, current[i], previous[i]));
        }

        JScrollPane scrollPane = new JScrollPane(chartArea);
        chartPanel.add(scrollPane, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }


    private void generateInvoices() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle("Select Folder to Save Invoices");

        int userSelection = dirChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File folder = dirChooser.getSelectedFile();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int invoiceID = 1;
        boolean anySaved = false;

        for (BookingEntry booking : calendarPanel.getAllBookings()) {
            if (!booking.isConfirmed()) continue;

            try {
                StringBuilder invoiceText = new StringBuilder();
                invoiceText.append("Lancaster Music Hall - Invoice\n");
                invoiceText.append("==============================\n\n");
                invoiceText.append("Invoice ID: ").append(String.format("%03d", invoiceID)).append("\n");
                invoiceText.append("Client Name: ").append(booking.getClient()).append("\n");
                invoiceText.append("Booking Date: ").append(booking.getDate()).append("\n");
                invoiceText.append("Event Description: ").append(booking.getDescription()).append("\n");
                invoiceText.append(String.format("Total Amount: £%.2f\n", booking.getCost()));
                invoiceText.append(String.format("Amount Paid: £%.2f\n", booking.getCost()));
              //  double baseCost = booking.getCost();
               // double discount = booking.getDiscount();
                //double finalCost = baseCost * (1.0 - discount);

               //invoiceText.append(String.format("Original Amount: £%.2f\n", baseCost));
                //invoiceText.append(String.format("Discount: %.0f%%\n", discount * 100));
               // invoiceText.append(String.format("Total Amount Due: £%.2f\n", finalCost));
               // invoiceText.append(String.format("Amount Paid: £%.2f\n", finalCost));

                invoiceText.append("Due Date: ").append(booking.getDate()).append("\n");
                invoiceText.append("Payment Status: Paid\n");

                // Show preview
                JTextArea previewArea = new JTextArea(invoiceText.toString());
                previewArea.setEditable(false);
                previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(previewArea);
                scrollPane.setPreferredSize(new Dimension(450, 250));

                int option = JOptionPane.showConfirmDialog(this, scrollPane,
                        "Preview Invoice " + String.format("%03d", invoiceID),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String fileName = String.format("Invoice_%03d_%s.txt", invoiceID, booking.getClient().replaceAll("\\s+", "_"));
                    File invoiceFile = new File(folder, fileName);
                    FileWriter writer = new FileWriter(invoiceFile);
                    writer.write(invoiceText.toString());
                    writer.close();

                    invoiceID++;
                    anySaved = true;
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error writing invoice: " + ex.getMessage());
            }
        }

        if (anySaved) {
            JOptionPane.showMessageDialog(this, "Invoices generated and saved.");
        } else {
            JOptionPane.showMessageDialog(this, "No confirmed bookings found or no invoices saved.");
        }
    }





    public void refresh() {
            String selected = financeTable.getColumnName(0);
            if (selected.equals("Event Name")) {
                // See which panel is active based on column header
                if (financeTable.getColumnCount() == 3) {
                    loadTicketSalesData();
                } else if (financeTable.getColumnCount() == 5) {
                    loadRevenueData();
                } else {
                    loadContractData();
                }
            } else {
                loadTicketSalesData(); // Default
            }
            updateRevenueChart(); // Ensure chart is refreshed
        }
}