package untitled.src;

/**
 * Temporary file to test the connection on windows app
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DatabaseMetaData;

/**
 * Test class to verify the connection to the database using DatabaseManager
 */
public class DatabaseConnectionTest {

    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===");

        // First, check if the DB_URL is correctly formatted
        checkURLFormat();

        // Try to connect
        testConnection();
    }

    private static void checkURLFormat() {
        System.out.println("Checking database URL format...");

        // This is a reflection hack to get the private DB_URL field from DatabaseManager
        try {
            java.lang.reflect.Field urlField = DatabaseManager.class.getDeclaredField("DB_URL");
            urlField.setAccessible(true);
            String dbUrl = (String) urlField.get(null);

            System.out.println("Current URL: " + dbUrl);

            // Check the URL format
            if (dbUrl.contains("jdbc:mysql:///")) {
                System.out.println("WARNING: The URL format appears incorrect!");
                System.out.println("It should be: jdbc:mysql://hostname:port/database");
                System.out.println("Suggested fix: jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t05");
            }

        } catch (Exception e) {
            System.out.println("Could not access DB_URL field: " + e.getMessage());
        }
    }

    private static void testConnection() {
        System.out.println("\nAttempting to connect to the database...");

        Connection connection = null;

        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            connection = dbManager.getConnection();

            if (connection != null) {
                System.out.println("SUCCESS: Connected to the database!");

                // Get metadata to verify connection details
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("\nConnection Details:");
                System.out.println("------------------");
                System.out.println("Database: " + metaData.getDatabaseProductName() + " " +
                        metaData.getDatabaseProductVersion());
                System.out.println("Driver: " + metaData.getDriverName() + " " +
                        metaData.getDriverVersion());
                System.out.println("URL: " + metaData.getURL());
                System.out.println("User: " + metaData.getUserName());

                // Test a basic query
                testQuery(connection);
            }

        } catch (SQLException e) {
            System.out.println("ERROR: Failed to connect to the database!");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());

            // Provide more specific guidance based on the error
            provideTroubleshootingGuidance(e);

        } finally {
            if (connection != null) {
                try {
                    DatabaseManager.getInstance().closeConnection(connection);
                    System.out.println("\nConnection closed successfully.");
                } catch (Exception e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    private static void testQuery(Connection connection) {
        System.out.println("\nTesting a simple query...");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            System.out.println("Tables in database:");
            System.out.println("------------------");

            boolean hasTables = false;
            while (rs.next()) {
                hasTables = true;
                System.out.println(rs.getString(1));
            }

            if (!hasTables) {
                System.out.println("No tables found in the database.");
            }

            System.out.println("------------------");
            System.out.println("Query executed successfully!");

        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }

    private static void provideTroubleshootingGuidance(SQLException e) {
        System.out.println("\nTroubleshooting Guidance:");
        System.out.println("------------------------");

        if (e.getMessage().contains("Communications link failure")) {
            System.out.println("1. Check if the hostname is correct (sst-stuproj.city.ac.uk)");
            System.out.println("2. Make sure you're on the university network or VPN");
            System.out.println("3. Verify the port (3306) is not blocked by a firewall");
        }
        else if (e.getMessage().contains("Access denied")) {
            System.out.println("1. Verify username and password");
            System.out.println("2. Check that the user has access to the database");
            System.out.println("3. Ensure you're using the correct database name");
        }
        else if (e.getMessage().contains("Unknown database")) {
            System.out.println("1. Verify the database name (in2033t05)");
            System.out.println("2. Check if the database exists on the server");
        }
        else if (e.getMessage().contains("No suitable driver")) {
            System.out.println("1. Make sure MySQL JDBC driver is in your classpath");
            System.out.println("2. Check if the URL format is correct");
        }

        System.out.println("\nSuggested fixes for common issues:");
        System.out.println("1. Fix URL format: jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t05");
        System.out.println("2. Add connection parameters: ?useSSL=false&serverTimezone=UTC");
        System.out.println("3. Double-check username: in2033t05_a (note the '_a' suffix)");
    }
}