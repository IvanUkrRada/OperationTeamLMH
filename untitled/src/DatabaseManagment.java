package untitled.src;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connector class for the Music Hall Management System.
 * Provides methods to connect to the MySQL database and perform operations on bookings and events.
 */
public class DatabaseManagment {

    // JDBC URL parts
    private static final String SERVER = "sst-stuproj.city.ac.uk";
    private static final String PORT = "3306";
    private static final String DATABASE = "in2033t05";

    // Connection credentials
    private static final String USERNAME = "in2033t05_a";
    private static final String PASSWORD = "3WZj1sq7siU";

    // JDBC objects
    private Connection connection = null;
    private static final Logger logger = Logger.getLogger(DatabaseManagment.class.getName());

    // Singleton instance
    private static DatabaseManagment instance = null;

    /**
     * Private constructor for Singleton pattern
     */
    DatabaseManagment() {
        // Initialize connection on creation
        getConnection();
    }

    /**
     * Get the singleton instance of DatabaseConnector
     * @return DatabaseConnector instance
     */
    public static synchronized DatabaseManagment getInstance() {
        if (instance == null) {
            instance = new DatabaseManagment();
        }
        return instance;
    }

    /**
     * Establishes a connection to the database
     * @return Connection object
     */
    public Connection getConnection() {
        if (connection == null) {
            try {
                // Class.forName("com.mysql.cj.jdbc.Driver"); // Not needed for JDBC 4.0+

                Properties connectionProps = new Properties();
                connectionProps.put("user", USERNAME);
                connectionProps.put("password", PASSWORD);
                connectionProps.put("useSSL", "false");
                connectionProps.put("serverTimezone", "UTC");

                // Create connection URL
                String url = "jdbc:mysql://" + SERVER + ":" + PORT + "/" + DATABASE;

                connection = DriverManager.getConnection(url, connectionProps);
                logger.log(Level.INFO, "Database connection established successfully");

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to connect to database: " + e.getMessage(), e);
                displayError("Database Connection Error", "Failed to connect to database: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                logger.log(Level.INFO, "Database connection closed");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database connection: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Displays an error message
     * @param title Error title
     * @param message Error message
     */
    private void displayError(String title, String message) {
        javax.swing.JOptionPane.showMessageDialog(null,
                message,
                title,
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Gets all bookings from the database
     * @return ArrayList of BookingEntry objects
     */
    public ArrayList<BookingEntry> getAllBookings() {
        ArrayList<BookingEntry> bookings = new ArrayList<>();

        try {
            String query =
                    "SELECT b.Booking_ID, b.Date, b.Start_End_Time, r.Room_ID, " +
                    "r.Name, b.Details, b.Confirmed, b.Client_ID, c.Company_Name " +
                    "FROM Bookings b " +
                    "JOIN Room r ON b.Room_ID = r.Room_ID " +
                    "JOIN Clients c ON b.Client_ID = c.ClientID " +
                    "ORDER BY b.Date, b.Start_End_Time";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    String date = rs.getString("Date");
                    String venueSpace = rs.getString("Name");
                    String timeSlot = rs.getString("Start_End_Time");
                    String client = rs.getString("Company_Name");
                    String description = rs.getString("Details");
                    boolean confirmed = rs.getBoolean("Confirmed");

                    // Create booking object
                    // Determine venue type (hall or room) based on room name
                    String venueType = determineVenueType(venueSpace);

                    // For cost, you might want to calculate based on event type or fetch from invoices
                    // For simplicity, we're using a placeholder value
                    double cost = calculateBookingCost(venueSpace, venueType, timeSlot, date); //Tolu

                    BookingEntry booking = new BookingEntry(date, venueSpace, venueType, timeSlot,
                            client, description, cost);
                    booking.setConfirmed(confirmed);

                    bookings.add(booking);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching bookings: " + e.getMessage(), e);
            displayError("Database Error", "Failed to fetch bookings: " + e.getMessage());
        }

        return bookings;
    }

    public ArrayList<ClientEntry> getAllClients () {

        ArrayList <ClientEntry>clients = new ArrayList<>();

        try{
            String query = "SELECT c.ClientID, c.Company_Name, c.Contact_Name, c.Contact_Email, c.Phone_Number, " +
                    "c.Street_Address, c.City, c.Postcode, c.Billing_Name, c.Billing_Email " +
                    "FROM Clients c " +
                    "ORDER BY c.ClientID";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while(rs.next()) {
                    String clientID = rs.getString("ClientID");
                    String companyName = rs.getString ("Company_Name");
                    String contactName = rs.getString ("Contact_Name");
                    String contactEmail = rs.getString ("Contact_Email");
                    String phoneNumber = rs.getString ("Phone_Number");
                    String streetAddress = rs.getString ("Street_Address");
                    String city = rs.getString ("City");
                    String postcode = rs.getString ("Postcode");
                    String billingName = rs.getString ("Billing_Name");
                    String billingEmail = rs.getString ("Billing_Email");

                    ClientEntry client = new ClientEntry (clientID, companyName, contactName, contactEmail,
                            phoneNumber, streetAddress, city, postcode, billingName, billingEmail);

                    clients.add(client);
                }
            }

        }catch(SQLException e){
            logger.log(Level.SEVERE, "Error fetching clients: " + e.getMessage(), e);
            displayError("Database Error", "Failed to fetch clients: " + e.getMessage());
        }

        return clients;
    }

    public boolean addClient (ClientEntry newEntry){

        PreparedStatement pstmt = null;
        try{
            String query = "INSERT INTO Clients (ClientID, Company_Name, Contact_Name, Contact_Email, Phone_Number, " +
                    "Street_Address, City, Postcode, Billing_Name, Billing_Email) VALUES (?,?,?,?,?,?,?,?,?,?)";

            pstmt = connection.prepareStatement(query);

            pstmt.setString (1, newEntry.getClientID());
            pstmt.setString (2, newEntry.getCompanyName());
            pstmt.setString (3, newEntry.getContactName());
            pstmt.setString (4, newEntry.getContactEmail());
            pstmt.setString (5, newEntry.getPhoneNumber());
            pstmt.setString (6, newEntry.getStreetAddress());
            pstmt.setString (7, newEntry.getCity());
            pstmt.setString (8, newEntry.getPostcode());
            pstmt.setString (9, newEntry.getBillingName());
            pstmt.setString (10, newEntry.getBillingEmail());

            int affectedRows = pstmt.executeUpdate();
            return true;

        }catch(SQLException e){
            logger.log(Level.SEVERE, "Error fetching clients: " + e.getMessage(), e);
            displayError("Database Error", "Failed to fetch clients: " + e.getMessage());
            return false;
        }
    }

    private double calculateBookingCost(String venueName, String venueType, String timeSlot, String date) {
        // Find matching venue
        for (VenueSpace venue : getAllVenueSpaces()) {
            if (venue.getName().equals(venueName)) {
                // calculate cost based on timeSlot and venue rates
                if (timeSlot.contains("Morning") || timeSlot.contains("Afternoon")) {
                    return venue.getRate("hourly") * 3; // 3 hours
                } else if (timeSlot.contains("Evening")) {
                    boolean isWeekend = isWeekend(date);
                    return isWeekend ? venue.getRate("evening_weekend") : venue.getRate("evening_weekday");
                } else if (timeSlot.contains("Full Day")) {
                    boolean isWeekend = isWeekend(date);
                    return isWeekend ? venue.getRate("daily_weekend") : venue.getRate("daily_weekday");
                } else if (timeSlot.contains("Half Day")) {
                    return venue.getRate("half_day");
                } else if (timeSlot.contains("Weekly")) {
                    return venue.getRate("weekly");
                }
            }
        }
        return 0.0;
    }

    private boolean isWeekend(String dateString) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateString);
            int day = date.getDayOfWeek().getValue();
            return (day >= 5); // Friday, Saturday, Sunday are considered weekend
        } catch (Exception e) {
            return false;
        }
    }

    // You'll also need this method to get venue spaces:
    private ArrayList<VenueSpace> getAllVenueSpaces() {
        // This is a simplified version - you should implement it properly
        ArrayList<VenueSpace> spaces = new ArrayList<>();
        // Create the same venue spaces as in CalendarPanel.initializeVenueSpaces()
        // ... (copy the venue creation code)
        return spaces;
    }

    /**
     * Determines the venue type based on the venue name
     * @param venueName Name of the venue
     * @return "hall" or "room"
     */
    private String determineVenueType(String venueName) {
        // Logic to determine if venue is a hall or a room based on name
        if (venueName.contains("Hall") || venueName.contains("Rehearsal Space")) {
            return "hall";
        } else {
            return "room";
        }
    }

    /**
     * Adds a new booking to the database
     * @param booking BookingEntry object containing booking details
     * @return true if successful, false otherwise
     */
    public boolean addBooking(BookingEntry booking) {
        try {
            // First find the Room_ID based on venue space name
            int roomId = getRoomIdByName(booking.getVenueSpace());

            // Find Client_ID based on client name
            int clientId = getClientIdByName(booking.getClient());

            // If we couldn't find the room or client, return false
            if (roomId == -1 || clientId == -1) {
                return false;
            }

            String query = "INSERT INTO Bookings (Date, Start_End_Time, Room_ID, Details, Confirmed, Client_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, booking.getDate());
                pstmt.setString(2, booking.getTimeSlot());
                pstmt.setInt(3, roomId);
                pstmt.setString(4, booking.getDescription());
                pstmt.setBoolean(5, booking.isConfirmed());
                pstmt.setInt(6, clientId);

                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding booking: " + e.getMessage(), e);
            displayError("Database Error", "Failed to add booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing booking in the database
     * @param bookingId ID of the booking to update
     * @param booking Updated BookingEntry object
     * @return true if successful, false otherwise
     */
    public boolean updateBooking(int bookingId, BookingEntry booking) {
        try {
            // Find the Room_ID based on venue space name
            int roomId = getRoomIdByName(booking.getVenueSpace());

            // Find Client_ID based on client name
            int clientId = getClientIdByName(booking.getClient());

            // If we couldn't find the room or client, return false
            if (roomId == -1 || clientId == -1) {
                return false;
            }

            String query = "UPDATE Bookings SET Date = ?, Start_End_Time = ?, Room_ID = ?, " +
                    "Details = ?, Confirmed = ?, Client_ID = ? " +
                    "WHERE Booking_ID = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, booking.getDate());
                pstmt.setString(2, booking.getTimeSlot());
                pstmt.setInt(3, roomId);
                pstmt.setString(4, booking.getDescription());
                pstmt.setBoolean(5, booking.isConfirmed());
                pstmt.setInt(6, clientId);
                pstmt.setInt(7, bookingId);

                int rowsUpdated = pstmt.executeUpdate();
                return rowsUpdated > 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating booking: " + e.getMessage(), e);
            displayError("Database Error", "Failed to update booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a booking from the database
     * @param bookingId ID of the booking to remove
     * @return true if successful, false otherwise
     */
    public boolean removeBooking(int bookingId) {
        try {
            String query = "DELETE FROM Bookings WHERE Booking_ID = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, bookingId);

                int rowsDeleted = pstmt.executeUpdate();
                return rowsDeleted > 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error removing booking: " + e.getMessage(), e);
            displayError("Database Error", "Failed to remove booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the Room ID by room name
     * @param roomName Name of the room
     * @return Room ID or -1 if not found
     */
    private int getRoomIdByName(String roomName) {
        try {
            String query = "SELECT Room_ID FROM Room WHERE Name = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, roomName);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("Room_ID");
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting room ID: " + e.getMessage(), e);
        }

        return -1;
    }

    /**
     * Gets the Client ID by client name (company name)
     * @param clientName Name of the client
     * @return Client ID or -1 if not found
     */
    private int getClientIdByName(String clientName) {
        try {
            String query = "SELECT ClientID FROM Clients WHERE Company_Name = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, clientName);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("ClientID");
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting client ID: " + e.getMessage(), e);
        }

        return -1;
    }



//    /**
//     * Gets all events from the database
//     * @return ArrayList of Event objects (you'll need to create this class)
//     */
//    public ArrayList<Event> getAllEvents() {
//        ArrayList<Event> events = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Events ORDER BY Event_ID";
//
//            try (Statement stmt = connection.createStatement();
//                 ResultSet rs = stmt.executeQuery(query)) {
//
//                while (rs.next()) {
//                    int eventId = rs.getInt("Event_ID");
//                    String startEndDate = rs.getString("Start_End_Date");
//                    double maxDiscount = rs.getDouble("Max_Discount");
//                    String description = rs.getString("Description");
//
//                    Event event = new Event(eventId, startEndDate, maxDiscount, description);
//                    events.add(event);
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching events: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch events: " + e.getMessage());
//        }
//
//        return events;
//    }
//
//    /**
//     * Gets all rooms from the database
//     * @return ArrayList of Room objects (you'll need to create this class)
//     */
//    public ArrayList<Room> getAllRooms() {
//        ArrayList<Room> rooms = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Room ORDER BY Room_ID";
//
//            try (Statement stmt = connection.createStatement();
//                 ResultSet rs = stmt.executeQuery(query)) {
//
//                while (rs.next()) {
//                    int roomId = rs.getInt("Room_ID");
//                    String name = rs.getString("Name");
//                    int capacity = rs.getInt("Capacity");
//                    String layouts = rs.getString("Layouts");
//
//                    Room room = new Room(roomId, name, capacity, layouts);
//                    rooms.add(room);
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching rooms: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch rooms: " + e.getMessage());
//        }
//
//        return rooms;
//    }
//
//    /**
//     * Gets all clients from the database
//     * @return ArrayList of Client objects (you'll need to create this class)
//     */
//    public ArrayList<Client> getAllClients() {
//        ArrayList<Client> clients = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Clients ORDER BY ClientID";
//
//            try (Statement stmt = connection.createStatement();
//                 ResultSet rs = stmt.executeQuery(query)) {
//
//                while (rs.next()) {
//                    int clientId = rs.getInt("ClientID");
//                    String companyName = rs.getString("Company_Name");
//                    String contactName = rs.getString("Contact_Name");
//                    String contactEmail = rs.getString("Contact_Email");
//                    String phoneNumber = rs.getString("Phone_Number");
//
//                    Client client = new Client(clientId, companyName, contactName, contactEmail, phoneNumber);
//                    clients.add(client);
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching clients: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch clients: " + e.getMessage());
//        }
//
//        return clients;
//    }
//
//    /**
//     * Gets ticket sales for a specific event
//     * @param eventId ID of the event
//     * @return ArrayList of TicketSale objects (you'll need to create this class)
//     */
//    public ArrayList<TicketSale> getTicketSalesForEvent(int eventId) {
//        ArrayList<TicketSale> ticketSales = new ArrayList<>();
//
//        try {
//            String query = "SELECT ts.* FROM Ticket_Sales ts " +
//                    "JOIN Event_Tickets et ON ts.Ticketing_ID = et.Ticketing_ID " +
//                    "WHERE et.Event_ID = ?";
//
//            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//                pstmt.setInt(1, eventId);
//
//                try (ResultSet rs = pstmt.executeQuery()) {
//                    while (rs.next()) {
//                        int ticketingId = rs.getInt("Ticketing_ID");
//                        String date = rs.getString("Date");
//                        int totalSeats = rs.getInt("Total_Seats");
//                        double value = rs.getDouble("Value");
//
//                        TicketSale ticketSale = new TicketSale(ticketingId, date, totalSeats, value);
//                        ticketSales.add(ticketSale);
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching ticket sales: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch ticket sales: " + e.getMessage());
//        }
//
//        return ticketSales;
//    }
//
//    /**
//     * Gets all reviews for a specific client
//     * @param clientId ID of the client
//     * @return ArrayList of Review objects (you'll need to create this class)
//     */
//    public ArrayList<Review> getReviewsForClient(int clientId) {
//        ArrayList<Review> reviews = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Reviews WHERE Client_ID = ?";
//
//            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//                pstmt.setInt(1, clientId);
//
//                try (ResultSet rs = pstmt.executeQuery()) {
//                    while (rs.next()) {
//                        int reviewId = rs.getInt("Review_ID");
//                        int rating = rs.getInt("Rating");
//
//                        Review review = new Review(reviewId, clientId, rating);
//                        reviews.add(review);
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching reviews: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch reviews: " + e.getMessage());
//        }
//
//        return reviews;
//    }
//
//    /**
//     * Gets all positive reviews (rating >= 4)
//     * @return ArrayList of Review objects
//     */
//    public ArrayList<Review> getPositiveReviews() {
//        ArrayList<Review> reviews = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Reviews WHERE Rating >= 4";
//
//            try (Statement stmt = connection.createStatement();
//                 ResultSet rs = stmt.executeQuery(query)) {
//
//                while (rs.next()) {
//                    int reviewId = rs.getInt("Review_ID");
//                    int clientId = rs.getInt("Client_ID");
//                    int rating = rs.getInt("Rating");
//
//                    Review review = new Review(reviewId, clientId, rating);
//                    reviews.add(review);
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching positive reviews: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch positive reviews: " + e.getMessage());
//        }
//
//        return reviews;
//    }
//
//    /**
//     * Gets all negative reviews (rating < 4)
//     * @return ArrayList of Review objects
//     */
//    public ArrayList<Review> getNegativeReviews() {
//        ArrayList<Review> reviews = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Reviews WHERE Rating < 4";
//
//            try (Statement stmt = connection.createStatement();
//                 ResultSet rs = stmt.executeQuery(query)) {
//
//                while (rs.next()) {
//                    int reviewId = rs.getInt("Review_ID");
//                    int clientId = rs.getInt("Client_ID");
//                    int rating = rs.getInt("Rating");
//
//                    Review review = new Review(reviewId, clientId, rating);
//                    reviews.add(review);
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching negative reviews: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch negative reviews: " + e.getMessage());
//        }
//
//        return reviews;
//    }
//
//    /**
//     * Gets invoices for a specific client
//     * @param clientId ID of the client
//     * @return ArrayList of Invoice objects (you'll need to create this class)
//     */
//    public ArrayList<Invoice> getInvoicesForClient(int clientId) {
//        ArrayList<Invoice> invoices = new ArrayList<>();
//
//        try {
//            String query = "SELECT * FROM Invoices WHERE Client_ID = ?";
//
//            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//                pstmt.setInt(1, clientId);
//
//                try (ResultSet rs = pstmt.executeQuery()) {
//                    while (rs.next()) {
//                        int invoiceId = rs.getInt("Invoice_ID");
//                        String date = rs.getString("Date");
//                        double costs = rs.getDouble("Costs");
//                        double total = rs.getDouble("Total");
//
//                        Invoice invoice = new Invoice(invoiceId, clientId, date, costs, total);
//                        invoices.add(invoice);
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching invoices: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch invoices: " + e.getMessage());
//        }
//
//        return invoices;
//    }
//
//    /**
//     * Gets the contract for a specific client and event
//     * @param clientId ID of the client
//     * @param eventId ID of the event
//     * @return Contract object (you'll need to create this class) or null if not found
//     */
//    public Contract getContractForClientEvent(int clientId, int eventId) {
//        try {
//            String query = "SELECT * FROM Contract WHERE Client_ID = ? AND Event_ID = ?";
//
//            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//                pstmt.setInt(1, clientId);
//                pstmt.setInt(2, eventId);
//
//                try (ResultSet rs = pstmt.executeQuery()) {
//                    if (rs.next()) {
//                        String details = rs.getString("Details");
//
//                        return new Contract(clientId, eventId, details);
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, "Error fetching contract: " + e.getMessage(), e);
//            displayError("Database Error", "Failed to fetch contract: " + e.getMessage());
//        }
//
//        return null;
//    }


    public ArrayList<ReviewPanel.Review> getAllReviews() {
        ArrayList<ReviewPanel.Review> reviews = new ArrayList<>();

        try {
            String query = "SELECT r.Review_ID, r.Client_ID, c.Company_Name, r.Rating, " +
                    "FROM Reviews r " +
                    "JOIN Clients c ON r.Client_ID = c.ClientID " +
                    "ORDER BY r.Date DESC";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    int reviewId = rs.getInt("Review_ID");
                    int clientId = rs.getInt("Client_ID");
                    String clientName = rs.getString("Company_Name");
                    int rating = rs.getInt("Rating");
                    String comments = rs.getString("Comments");
                    String date = rs.getString("Date");
                    String type = rs.getString("Type");

                    ReviewPanel.Review review = new ReviewPanel.Review(reviewId, clientId, clientName,
                            rating, comments, date, type);
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching reviews: " + e.getMessage(), e);
            displayError("Database Error", "Failed to fetch reviews: " + e.getMessage());
        }

        return reviews;
    }

    // Add methods to get filtered reviews
    public ArrayList<ReviewPanel.Review> getVenueReviews() {
        return getReviewsByType("venue");
    }

    public ArrayList<ReviewPanel.Review> getRoomReviews() {
        return getReviewsByType("room");
    }

    public ArrayList<ReviewPanel.Review> getShowReviews() {
        return getReviewsByType("show");
    }

    public ArrayList<ReviewPanel.Review> getRecentReviews(int days) {
        ArrayList<ReviewPanel.Review> reviews = new ArrayList<>();

        try {
            String query = "SELECT r.Review_ID, r.Client_ID, c.Company_Name, r.Rating, " +
                    "FROM Reviews r " +
                    "JOIN Clients c ON r.Client_ID = c.ClientID " +
                    "ORDER BY r.Review_ID DESC";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, days);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        // Same code as in getAllReviews to create Review objects
                        // ...
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching recent reviews: " + e.getMessage(), e);
        }

        return reviews;
    }

    private ArrayList<ReviewPanel.Review> getReviewsByType(String type) {
        ArrayList<ReviewPanel.Review> reviews = new ArrayList<>();

        try {
            String query =
                    "SELECT r.Review_ID, r.Client_ID, c.Company_Name, r.Rating, " +
                    "FROM Reviews r " +
                    "JOIN Clients c ON r.Client_ID = c.ClientID " +
                    "ORDER BY r.Review_ID DESC";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, type);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        // Same code as in getAllReviews to create Review objects
                        // ...
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching reviews by type: " + e.getMessage(), e);
        }

        return reviews;
    }

    /**
     * Checks if a venue is available for the given date and time
     * @param roomId ID of the room
     * @param date Date to check
     * @param startEndTime Time period to check
     * @return true if available, false if already booked
     */
    public boolean isVenueAvailable(int roomId, String date, String startEndTime) {
        try {
            String query = "SELECT COUNT(*) AS count FROM Bookings " +
                    "WHERE Room_ID = ? AND Date = ? AND Start_End_Time = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, roomId);
                pstmt.setString(2, date);
                pstmt.setString(3, startEndTime);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        return count == 0; // Available if no bookings found
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking venue availability: " + e.getMessage(), e);
            displayError("Database Error", "Failed to check venue availability: " + e.getMessage());
        }

        return false; // Default to unavailable on error
    }
}