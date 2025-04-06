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

public class ClientPanel extends JPanel {
    private MainFrame parentFrame;
    private JTable clientsTable;
    private ClientTableModel tableModel;
    private JButton addButton;

    public ClientPanel(MainFrame parent) {
        this.parentFrame = parent;

        setLayout(new BorderLayout());
        ArrayList<ClientEntry> clients = new ArrayList<>();
        tableModel = new ClientTableModel(clients);
        clientsTable = new JTable(tableModel);

        for (int i = 0; i < 10; i++) {
            clientsTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        JScrollPane scrollPane = new JScrollPane(clientsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add Client");
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addClient());
    }

    public void refresh (){
        clientsTable.repaint ();
    }

    private void addClient() {
        JDialog selectDialog = new JDialog(parentFrame, "Enter client's details", true);
        selectDialog.setSize(400, 350);
        selectDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lCompanyName = new JLabel("Company Name:");
        JTextField tCompanyName = new JTextField(30);
        JLabel lContactName = new JLabel("Contact Name:");
        JTextField tContactName = new JTextField(30);
        JLabel lContactEmail = new JLabel("Contact Email:");
        JTextField tContactEmail = new JTextField(30);
        JLabel lPhoneNumber = new JLabel("Phone Number:");
        JTextField tPhoneNumber = new JTextField(30);
        JLabel lStreetAddress = new JLabel("Street Address:");
        JTextField tStreetAddress = new JTextField(30);
        JLabel lCity = new JLabel("City:");
        JTextField tCity = new JTextField(30);
        JLabel lPostCode = new JLabel("Postcode:");
        JTextField tPostCode = new JTextField(30);
        JLabel lBillingName = new JLabel("Billing Name:");
        JTextField tBillingName = new JTextField(30);
        JLabel lBillingEmail = new JLabel("Billing Email:");
        JTextField tBillingEmail = new JTextField(30);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lCompanyName, gbc);

        gbc.gridx = 1;
        panel.add(tCompanyName, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lContactName, gbc);

        gbc.gridx = 1;
        panel.add(tContactName, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lContactEmail, gbc);

        gbc.gridx = 1;
        panel.add(tContactEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lPhoneNumber, gbc);

        gbc.gridx = 1;
        panel.add(tPhoneNumber, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lStreetAddress, gbc);

        gbc.gridx = 1;
        panel.add(tStreetAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lCity, gbc);

        gbc.gridx = 1;
        panel.add(tCity, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lPostCode, gbc);

        gbc.gridx = 1;
        panel.add(tPostCode, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lBillingName, gbc);

        gbc.gridx = 1;
        panel.add(tBillingName, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lBillingEmail, gbc);

        gbc.gridx = 1;
        panel.add(tBillingEmail, gbc);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        okButton.addActionListener(e -> {
            String companyName = tCompanyName.getText();
            String contactName = tContactName.getText();
            String contactEmail = tContactEmail.getText();
            String phoneNumber = tPhoneNumber.getText();
            String streetAddress = tStreetAddress.getText();
            String city = tCity.getText();
            String postcode = tPostCode.getText();
            String billingName = tBillingName.getText();
            String billingEmail = tBillingEmail.getText();

            if (areFieldsValid(companyName, contactName, contactEmail, phoneNumber, streetAddress, city, postcode, billingName, billingEmail)) {
                String clientID = tableModel.getNumberClients() + "";

                tableModel.addClient(clientID, companyName, contactName, contactEmail, phoneNumber, streetAddress, city, postcode, billingName, billingEmail);

                selectDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(selectDialog, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> selectDialog.dispose());

        selectDialog.add(panel, BorderLayout.CENTER);
        selectDialog.add(buttonPanel, BorderLayout.SOUTH);

        selectDialog.setVisible(true);
    }
    private boolean areFieldsValid(String companyName, String contactName, String contactEmail, String phoneNumber,
                                   String streetAddress, String city, String postcode, String billingName, String billingEmail) {
        return !companyName.isEmpty() && !contactName.isEmpty() && !contactEmail.isEmpty() && !phoneNumber.isEmpty() &&
                !streetAddress.isEmpty() && !city.isEmpty() && !postcode.isEmpty() && !billingName.isEmpty() && !billingEmail.isEmpty();
    }
}