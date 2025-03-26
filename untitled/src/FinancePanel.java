import javax.swing.*;
import java.awt.*;

class FinancePanel extends JPanel {
    private MainFrame parentFrame;
    private CalendarPanel calendarPanel;
    private JLabel totalRevenueLabel;
    private JTable financeTable;
    private JComboBox<String> periodCombo;
    private JButton exportButton;
    private JButton compareButton;

    public FinancePanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;
        this.calendarPanel = calendarPanel;

        setLayout(new BorderLayout());

        // Top panel with summary
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalRevenueLabel = new JLabel("Total Revenue: £0.00");
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryPanel.add(totalRevenueLabel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        periodCombo = new JComboBox<>(new String[]{"Current Month", "Current Quarter", "Current Year", "All Time"});
        controlPanel.add(new JLabel("Period:"));
        controlPanel.add(periodCombo);

        exportButton = new JButton("Export");
        compareButton = new JButton("Compare to Previous");

        controlPanel.add(exportButton);
        controlPanel.add(compareButton);

        summaryPanel.add(controlPanel, BorderLayout.EAST);
        add(summaryPanel, BorderLayout.NORTH);

        // Finance table
        String[] columnNames = {"Date", "Venue", "Client", "Event Type", "Revenue", "Status"};

        // Here data need to be displayed through Database
        Object[][] data = {};

        financeTable = new JTable(data, columnNames);
        add(new JScrollPane(financeTable), BorderLayout.CENTER);

        // Bottom panel with chart placeholder
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Revenue Trend"));
        chartPanel.setPreferredSize(new Dimension(0, 200));

        JLabel chartLabel = new JLabel("Revenue Chart Placeholder", SwingConstants.CENTER);
        chartPanel.add(chartLabel, BorderLayout.CENTER);
        add(chartPanel, BorderLayout.SOUTH);


        periodCombo.addActionListener(e -> refresh());

        compareButton.addActionListener(e -> showComparisonDialog());

        refresh();
    }

    public void refresh() {
        // Update total revenue//Need data to be transmitted through database
        double revenue = calendarPanel.calculateRevenue();
        totalRevenueLabel.setText(String.format("Total Revenue: £%.2f", revenue));

    }




    private void showComparisonDialog() {
        JDialog comparisonDialog = new JDialog(parentFrame, "Revenue Comparison", true);
        comparisonDialog.setSize(600, 400);
        comparisonDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel comparisonLabel = new JLabel("Revenue Comparison Chart Placeholder", SwingConstants.CENTER);
        panel.add(comparisonLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> comparisonDialog.dispose());

        comparisonDialog.add(panel);
        comparisonDialog.setVisible(true);
    }
}