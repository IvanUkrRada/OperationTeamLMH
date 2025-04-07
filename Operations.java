package Interface;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

interface OperationsInterface {
    //Retrieves venue bookings for a given date and time
    List<String> getVenueBookings(LocalDate date, LocalTime time) throws SQLException;

    //Retrieves meeting room bookings for a given date and time
    List<String> getMeetingRoomBookings(LocalDate date, LocalTime time) throws SQLException;

    //Gets ticket sales for a specific ticketID
    double getTicketSales(int ticketID) throws SQLException;

    //Gets meeting room sales with a specific roomID
    double getMeetingRoomSales(int roomID) throws SQLException;

    //Retrieves community members; School, University etc. With a booking ID
    List<String> getCommunity(int bookingID) throws SQLException;

    //Retrieves held seats for a booking using a booking ID
    String getSeatsHeld(int bookingID) throws SQLException;

    //Retrieves campaign performance using a campaign ID
    double getCampaignPerformance(int campaignID) throws SQLException;

    //Retrieves preferred advertising methods
    List<String> getPreferredAdvertising() throws SQLException;

}

public class Operations implements OperationsInterface {


    //Retrieves venue bookings for a given date and time
    @Override
    public List<String> getVenueBookings(LocalDate date, LocalTime time) throws SQLException {

        return null;
    }


    //Retrieves meeting room bookings for a given date and time
    @Override
    public List<String> getMeetingRoomBookings(LocalDate date, LocalTime time) throws SQLException {

        return null;
    }

    //Gets ticket sales for a specific ticketID
    @Override
    public double getTicketSales(int ticketID) throws SQLException {
        return 0;
    }

    //Gets meeting room sales with a specific roomID
    @Override
    public double getMeetingRoomSales(int roomID) throws SQLException {
        return 0;
    }

    //Retrieves community members; School, University etc. With a booking ID
    @Override
    public List<String> getCommunity(int bookingID) throws SQLException {
        return null;
    }

    //Retrieves held seats for a booking using a booking ID
    @Override
    public String getSeatsHeld(int bookingID) throws SQLException {
        return null;
    }

    //Retrieves campaign performance using a campaign ID
    @Override
    public double getCampaignPerformance(int campaignID) throws SQLException {
        return 0;
    }


    //Retrieves preferred advertising methods
    @Override
    public List<String> getPreferredAdvertising() throws SQLException {
        return null;
    }

}
