import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceOperation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OperationsGUI());
    }
}

class OperationsGUI {
    private JFrame frame;
    private JPanel panel;
    private JButton btnManageBookings, btnViewCalendar, btnSeatingConfig, btnGenerateReports;

    public OperationsGUI() {
        frame = new JFrame("Operations Team - Lancaster Music Hall");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        btnManageBookings = new JButton("Manage Bookings");
        btnViewCalendar = new JButton("View Calendar");
        btnSeatingConfig = new JButton("Seating Configuration");
        btnGenerateReports = new JButton("Generate Reports");

        panel.add(btnManageBookings);
        panel.add(btnViewCalendar);
        panel.add(btnSeatingConfig);
        panel.add(btnGenerateReports);

        btnManageBookings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Opening Booking Management...");
            }
        });

        btnViewCalendar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Displaying Venue Calendar...");
            }
        });

        btnSeatingConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Modifying Seating Configuration...");
            }
        });

        btnGenerateReports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Generating Revenue and Usage Reports...");
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void displayBookings() {
        JOptionPane.showMessageDialog(frame, "Displaying all current bookings...");
    }

    public void updateSeating() {
        JOptionPane.showMessageDialog(frame, "Updating seating arrangements...");
    }

    public void generateFinancialReport() {
        JOptionPane.showMessageDialog(frame, "Generating financial report for venue usage...");
    }
}
