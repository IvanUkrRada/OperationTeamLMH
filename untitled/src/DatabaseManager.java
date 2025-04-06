package untitled.src;

/**
 * Temporary file to test the connection on windows app
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database manager with singleton pattern for connection management
 */
public class DatabaseManager {
    // Corrected URL format
    private static final String HOST = "sst-stuproj.city.ac.uk";
    private static final String PORT = "3306";
    private static final String DATABASE = "in2033t05";
    private static final String DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&serverTimezone=UTC";

    // Note: The username from your code was "in2033t05" but based on the DatabaseConnector
    // class in your earlier files, it might be "in2033t05_a". Check which is correct.
    private static final String USER = "in2033t05_d";
    private static final String PASSWORD = "Je7qFdd8Bfg";

    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver registered successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Get the singleton instance of the database manager
     * @return DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Get a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("Database connection established");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;  // Re-throw to allow caller to handle
        }
    }

    /**
     * Close the database connection
     * @param connection Connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Test database connection and print result
     * @return true if connection successful, false otherwise
     */
    public boolean testConnection() {
        Connection conn = null;
        boolean success = false;

        try {
            System.out.println("Testing connection to " + HOST + "...");
            conn = getConnection();

            if (conn != null) {
                System.out.println("Connection test successful!");
                System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName());
                success = true;
            }

        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
        } finally {
            if (conn != null) {
                closeConnection(conn);
            }
        }

        return success;
    }

    // Simple test method that can be run directly
    public static void main(String[] args) {
        DatabaseManager.getInstance().testConnection();
    }
}