public class InterfaceMain {
    public interface VenueManagement {
        /*inorder to get/set data with/for specific space there are a list of names for each of them
        provided bellow:
    • The Main Hall = MainHall
    • The Small Hall = SmallHall
    • A Rehearsal space = RehSpace
    • Five other rooms of various sizes for meetings etc.  = Room1, Room2 and so on.
    by using the same naming/coding practices it will make development more effective.

         */
        // Calendar Updates
        void updateCalendar(String spaceName, String eventName, String date,  String startTime, String endTime);
        //this is main method to interact with the calendar which allows to set te relevant details in calendar
        // first Name of the space, Name of the Event (depends on what will occur : tour, show, film), date and time.
        boolean isVenueAvailable(String spaceName, String date, String startTime, String endTime);
        //to check if the venue/room available during that time period
        void reserveSpace(String spaceName, String date, String startTime, String endTime);
        //..
        void releaseSpace(String spaceName, String date,  String startTime, String endTime);
        //
        // ?void markSetupDays(String eventName, String setupDate);

        // Venue Capacities & Restrictions


        int getMaxOccupancy(String spaceName);
        // This method allow you to get the number of seats/occupancy available for each space.
        int getSeatsLeft(int spaceName, String date,  String startTime, String endTime);
        //T
        boolean isAccessible(String spaceName);
        boolean hasRestrictedViewSeats(String spaceName);

        // Ticket Sales & Performance Tracking
        int getTotalTicketsSold(String event);
        //we get information about tickets sales from the
        double getEventRevenue(String eventName);
        //
        double getClientRevenueShare(String eventName);
        //de

        // Space & Booking Information
        void setRoomSetup(String spaceName, String setupType);
        void getCalendar(String startDate, String endDate);
        void releaseHeldSeat(String seatId);
        String getAnnualBookingHistory(int year);

        // Customer Reviews

        String[] getPositiveReviews();
        //In order for Marketing team to receive a list of positive reviews we will use 5star rating
        //Positive will be considered under the 4th and 5th star rating.
        String[] getNegativeReviews();
        //All bellow 4 stars rating will be considered negative.
    }


}

// restrictedView is monitored by the Booking Office
// some information about the revenue(specifically tickets sales) is calculated using data received from other team