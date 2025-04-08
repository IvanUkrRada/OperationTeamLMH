package untitled.src;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private DatabaseManagment() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create the connection URL correctly
            String url = "jdbc:mysql://" + SERVER + ":" + PORT + "/" + DATABASE +
                    "?useSSL=false&serverTimezone=UTC";

            // Create connection properties
            Properties props = new Properties();
            props.setProperty("user", USERNAME);
            props.setProperty("password", PASSWORD);
            props.setProperty("connectTimeout", "5000");

            // Attempt to connect
            connection = DriverManager.getConnection(url, props);
            logger.log(Level.INFO, "Database connection established successfully");

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found: " + e.getMessage(), e);
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to database: " + e.getMessage(), e);
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
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

    public boolean addBooking (BookingEntry newEntry){

        PreparedStatement pstmt = null;
        try{
            String query = "INSERT INTO Bookings (BookingID, Date, Start_End_Time, Room_ID, Details, Confirmed, Client_ID " +
                    ") VALUES (?,?,?,?,?,?,?)";

            pstmt = connection.prepareStatement(query);

            pstmt.setInt (1, newEntry.getBookingID());
            pstmt.setString (2, newEntry.getDate());
            pstmt.setString (3, newEntry.getStartEndTime());
            pstmt.setString (4, newEntry.getRoomID());
            pstmt.setString (5, newEntry.getDetails());
            pstmt.setBoolean (6, newEntry.getConfirmed());
            pstmt.setString (7, newEntry.getClientID());

            int affectedRows = pstmt.executeUpdate();
            return true;

        }catch(SQLException e){
            logger.log(Level.SEVERE, "Error fetching clients: " + e.getMessage(), e);
            displayError("Database Error", "Failed to fetch clients: " + e.getMessage());
            return false;
        }
    }

    // Helper method to create a new client
    private int createClient(String clientName) {
        try {
            String query = "INSERT INTO Clients (Company_Name, Contact_Name) VALUES (?, 'Unknown')";

            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, clientName);

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating client: " + e.getMessage(), e);
            System.err.println("Error creating client: " + e.getMessage());
        }

        return -1;
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
// Update removeBooking in DatabaseManagment.java
    public boolean removeBooking(int bookingId) {
        try {
            // Make sure we have a valid connection
            if (getConnection() == null) {
                System.err.println("Cannot remove booking: No database connection");
                return false;
            }

            String query = "DELETE FROM Bookings WHERE Booking_ID = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, bookingId);

                int rowsDeleted = pstmt.executeUpdate();
                return rowsDeleted > 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error removing booking: " + e.getMessage(), e);
            System.err.println("Error removing booking: " + e.getMessage());
            return false;
        }

//        return false;
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

    //Bookings import methods
    public Map<String, ArrayList<BookingEntry>> loadBookingsFromDatabase() {
        Map<String, ArrayList<BookingEntry>> bookingsMap = new HashMap<>();

        try {
            // Ensure we have a valid connection
            if (getConnection() == null) {
                System.err.println("Cannot load bookings: No database connection");
                return bookingsMap;
            }

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
                    int bookingId = rs.getInt("Booking_ID");
                    String date = rs.getString("Date");
                    String venueSpace = rs.getString("Name");
                    String timeSlot = rs.getString("Start_End_Time");
                    String client = rs.getString("Company_Name");
                    String description = rs.getString("Details");
                    boolean confirmed = rs.getBoolean("Confirmed");

                    // Determine venue type (hall or room) based on room name
                    String venueType = determineVenueType(venueSpace);

                    // Calculate cost based on venue, type, timeSlot and date
                    double cost = calculateBookingCost(venueSpace, venueType, timeSlot, date);

                    // Create booking entry
                    BookingEntry booking = new BookingEntry(date, venueSpace, venueType, timeSlot,
                            client, description, cost);
                    booking.setConfirmed(confirmed);
                    booking.setBookingId(bookingId);

                    // Add to the map
                    if (!bookingsMap.containsKey(date)) {
                        bookingsMap.put(date, new ArrayList<>());
                    }
                    bookingsMap.get(date).add(booking);
                }

                System.out.println("Loaded " + countAllBookings(bookingsMap) + " bookings from database");

            } catch (SQLException e) {
                System.err.println("Error executing query: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return bookingsMap;
    }

    private int countAllBookings(Map<String, ArrayList<BookingEntry>> bookingsMap) {
        int count = 0;
        for (ArrayList<BookingEntry> bookings : bookingsMap.values()) {
            count += bookings.size();
        }
        return count;
    }


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