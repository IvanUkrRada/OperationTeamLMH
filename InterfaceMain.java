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
        void viewCalendar(String startDate, String endDate);
        //Displays calendar --- separate from update calendar.
        void updateCalendar(int eventID, String spaceName, String eventName, String date,  String startTime, String endTime);
        //this is main method to interact with the calendar which allows to set te relevant details in calendar
        // first Name of the space, Name of the Event (depends on what will occur : tour, show, film), date and time.
        //We offer reduced price tickets to the students studying theatre or film and the staff of these
        //institutions.

        boolean isVenueAvailable(int eventID, String date, String startTime, String endTime);
        //to check if the venue/room available during that time period
        void releaseSpace(int eventID, String date,  String startTime, String endTime);
        //if the booking is cancelled

        void setupDay(int eventID, String date, String startTime, String endTime );
        //We also arrange tours of the venue, so that students can see the venue in actions
        //while shows are setting up. No fee is charged for the tours and these are based on the setup days of
        //bookings with operations 

        int getMaxOccupancy(String spaceName);
        // This method allow you to get the number of seats/occupancy available for each space.
        int getSeatsLeft(int eventID, String date,  String startTime, String endTime);
        //To check the amount of seats on the specific date

        // Ticket Sales & Performance Tracking
        int getTotalTicketsSoldPerEvent(String eventName);
        //we get information about tickets sales from event/ we get data about tickets from Box Office
        //If the space in not booked and the revenue is produced of the ticket sales

        double getEventRevenue(String eventName);
        //to get the total revenue of specific event
        double getClientRevenueShare(String eventName);
        //As part of the agreement with the client, none, part or all of the ticket sales
        //revenue is payable to the client (and is thus offset against their bill)

        // Space & Booking Information
        void setRoomSetup(String spaceName, String setupType);
        // basic information about necessities for booked space

        String getAnnualBookingHistory(int year);
        //Annual booking history

        String[] getPositiveReviews();
        //In order for Marketing team to receive a list of positive reviews we will use 5star rating
        //Positive will be considered under the 4th and 5th star rating.
        String[] getNegativeReviews();
        //All bellow 4 stars rating will be considered negative.

        void displaySeatingConfiguration (String eventName);
        // Allows BO team to view seating config and specify if a seat 
        // needs to be removed/discounted
        void removeOrDiscount (String eventName, String seatNumber, int discount);
        // Removes seats that need to be removed and discounts the rest by specified value.
    }


}

// restrictedView is monitored by the Booking Office
// some information about the revenue(specifically tickets sales) is calculated using data received from other team