package untitled.src;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MusicHallGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}

class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private CalendarPanel calendarPanel;
    private BookingsPanel bookingsPanel;
    private ReviewPanel reviewPanel;
    private FinancePanel financePanel;
    private ClientPanel clientPanel;
    private JLabel statusLabel;

    public MainFrame() {
        setTitle("Music Hall Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Navigation Panel (Sidebar)
        JPanel navPanel = createNavigationPanel();

        // Status bar
        statusLabel = new JLabel(" Ready");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Main Content Panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize panels
        calendarPanel = new CalendarPanel(this);
        bookingsPanel = new BookingsPanel(this, calendarPanel);
        reviewPanel = new ReviewPanel(this, calendarPanel);
        financePanel = new FinancePanel(this, calendarPanel);
        clientPanel = new ClientPanel (this);

        contentPanel.add(calendarPanel, "Calendar");
        contentPanel.add(bookingsPanel, "Bookings");
        contentPanel.add(reviewPanel, "Review");
        contentPanel.add(financePanel, "Finance");
        contentPanel.add (clientPanel,"Clients");

        // Add panels to the main frame
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set initial panel
        cardLayout.show(contentPanel, "Calendar");

        setVisible(true);
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(7, 1, 0, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setPreferredSize(new Dimension(150, 0));
        ImageIcon calendarIcon = new ImageIcon("untitled/imageCalendar2.png");
        Image img = calendarIcon.getImage();
        Image newImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Resize
        calendarIcon = new ImageIcon(newImg);
        JButton calendarButton = createNavButton("Calendar");
        calendarButton.setBorderPainted(true);
        calendarButton.setContentAreaFilled(false);
        calendarButton.setFocusPainted(false);
        calendarButton.setOpaque(false);
        JButton bookingsButton = createNavButton("Bookings");
        JButton reviewButton = createNavButton("Reviews");
        JButton financeButton = createNavButton("Finance");
        JButton clientButton = createNavButton("Clients");
        JButton helpButton = createNavButton("Help");
        JButton exitButton = createNavButton("Exit");

        navPanel.add(calendarButton);
        navPanel.add(bookingsButton);
        navPanel.add(clientButton);
        navPanel.add(reviewButton);
        navPanel.add(financeButton);
        navPanel.add(helpButton);
        navPanel.add(exitButton);

        // TO SWITCH BETWEEN PANELS
        calendarButton.addActionListener(e -> cardLayout.show(contentPanel, "Calendar"));
        bookingsButton.addActionListener(e -> cardLayout.show(contentPanel, "Bookings"));
        reviewButton.addActionListener(e -> cardLayout.show(contentPanel, "Review"));
        financeButton.addActionListener(e -> cardLayout.show(contentPanel, "Finance"));
        clientButton.addActionListener(e -> cardLayout.show(contentPanel, "Clients"));
        helpButton.addActionListener(e -> showHelp());
        exitButton.addActionListener(e -> confirmExit());

        return navPanel;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(130, 50));
        return button;
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Music Hall Management System\n\n" +
                        "This application helps manage bookings, reviews, and finances for the Music Hall venues.\n" +
                        "For assistance, contact the system administrator.",
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmExit() {//In case user accidentaly pressed exit button, confirm
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setStatus(String message) {
        statusLabel.setText(" " + message);
    }

    public void refreshAllPanels() {
        calendarPanel.refresh();
        bookingsPanel.refresh();
        reviewPanel.refresh();
        financePanel.refresh();
        clientPanel.refresh ();
    }
}
class VenueSpace {
    private String name;
    private Map<String, Integer> capacities;
    private Map<String, Double> rates;
    private String type; // "hall" or "room"

    public VenueSpace(String name, String type) {
        this.name = name;
        this.type = type;
        this.capacities = new HashMap<>();
        this.rates = new HashMap<>();

        initializeDefaultRates();


    }
    public int getDefaultCapacity() {
        if (this.type.equals("room")) {
            return this.getCapacity("classroom");
        } else {
            return this.getCapacity("standard");
        }
    }

    private void initializeDefaultRates() {
        if ("hall".equals(type)) {
            rates.put("hourly", 0.0);
            rates.put("evening_weekday", 0.0);
            rates.put("evening_weekend", 0.0);
            rates.put("daily_weekday", 0.0);
            rates.put("daily_weekend", 0.0);
            rates.put("weekly_day", 0.0);
            rates.put("weekly_full", 0.0);
        } else { // room
            rates.put("hourly", 0.0);
            rates.put("half_day", 0.0);
            rates.put("full_day", 0.0);
            rates.put("weekly", 0.0);
        }
    }

    public void addCapacity(String configType, int capacity) {
        capacities.put(configType, capacity);
    }

    public void addRate(String rateType, double price) {
        rates.put(rateType, price);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCapacity(String configType) {
        return capacities.getOrDefault(configType, 0);
    }

    public double getRate(String rateType) {
        return rates.getOrDefault(rateType, 0.0);
    }

    @Override
    public String toString() {
        if ("hall".equals(type)) {
            int maxCapacity = capacities.getOrDefault("standard", 0);
            return name + " (Capacity: " + maxCapacity + ")";
        } else {
            return name + " (Meeting Room)";
        }
    }
}

class BookingEntry {
    private String date;
    private String venueSpace;
    private String venueType;  // "hall" or "room"
    private String timeSlot;
    private String client;
    private String description;
    private double cost;
    private boolean confirmed;
    private String configuration; // For rooms: "classroom", "boardroom", "presentation" (Capacity)

    public BookingEntry(String date, String venueSpace, String venueType, String timeSlot,
                        String client, String description, double cost) {
        this.date = date;
        this.venueSpace = venueSpace;
        this.venueType = venueType;
        this.timeSlot = timeSlot;
        this.client = client;
        this.description = description;
        this.cost = cost;
        this.confirmed = false;
        this.configuration = "standard";
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getConfiguration() {
        return configuration;
    }
    public String getDate() {
        return date;
    }

    public String getVenueSpace() {
        return venueSpace;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getClient() {
        return client;
    }

    public String getDescription() {
        return description;
    }

    public double getCost() {
        return cost;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return date + " - " + venueSpace + " - " + timeSlot + " - " + client +
                (confirmed ? " (Confirmed)" : " (Pending)");
    }
}





class BookingsTableModel extends AbstractTableModel {
    private String[] columnNames = {"Date", "Venue", "Time Slot", "Client", "Cost", "Confirmed"};
    private ArrayList<BookingEntry> bookings;

    public BookingsTableModel(ArrayList<BookingEntry> bookings) {
        this.bookings = bookings;
    }

    public void updateData(ArrayList<BookingEntry> bookings) {
        this.bookings = bookings;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return bookings.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= bookings.size()) {
            return null;
        }

        BookingEntry booking = bookings.get(rowIndex);

        switch (columnIndex) {
            case 0: return booking.getDate();
            case 1: return booking.getVenueSpace();
            case 2: return booking.getTimeSlot();
            case 3: return booking.getClient();
            case 4: return String.format("£%.2f", booking.getCost());
            case 5: return booking.isConfirmed();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 5) {
            return Boolean.class;
        }
        return String.class;
    }
}

class ClientTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ClientID" ,"Company_Name", "Contact_Name", "Contact_Email", "Phone_Number", "Street_Address", "City", "Postcode", "Billing_Name", "Billing_Email"};
    private ArrayList<ClientEntry> clients;
    ClientTableModel (ArrayList<ClientEntry> clients){
        this.clients = clients;
    }

    public void addClient (String clientID, String companyName, String contactName, String contactEmail, String phoneNumber, String streetAddress, String city, String postcode, String billingName, String billingEmail){
        ClientEntry newEntry = new ClientEntry( clientID,  companyName,  contactName,  contactEmail,  phoneNumber,  streetAddress,  city,  postcode,  billingName,  billingEmail);
        clients.add (newEntry);
        fireTableDataChanged();
    }

    public int getRowCount() {
        return clients.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getNumberClients ()
    {
        return clients.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= clients.size()) {
            return null;
        }

        ClientEntry client = clients.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> client.getClientID();
            case 1 -> client.getCompanyName();
            case 2 -> client.getContactName();
            case 3 -> client.getContactEmail();
            case 4 -> client.getPhoneNumber ();
            case 5 -> client.getStreetAddress ();
            case 6 -> client.getCity ();
            case 7 -> client.getPostcode ();
            case 8 -> client.getBillingName ();
            case 9 -> client.getBillingEmail ();
            default -> null;
        };
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}

class ClientEntry{

    private String clientID;
    private String companyName;
    private String contactName;
    private  String contactEmail;
    private  String phoneNumber;
    private  String streetAddress;
    private  String city;
    private  String postcode;
    private  String billingName;
    private String billingEmail;
    ClientEntry (String clientID, String companyName, String contactName, String contactEmail, String phoneNumber, String streetAddress, String city, String postcode, String billingName, String billingEmail){
        this.clientID = clientID;
        this.companyName =companyName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.postcode = postcode;
        this.billingName = billingName;
        this.billingEmail = billingEmail;
    }

    public String getClientID() {
        return clientID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getBillingName() {
        return billingName;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    @Override
    public String toString() {
        return "ClientID: " + clientID + " | Company: " + companyName + " | Contact: " + contactName +
                " | Email: " + contactEmail + " | Phone: " + phoneNumber + " | Address: " + streetAddress +
                ", " + city + " " + postcode + " | Billing Name: " + billingName + " | Billing Email: " + billingEmail;
    }
}


