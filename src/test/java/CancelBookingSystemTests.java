import command.*;
import controller.Controller;
import model.Booking;
import model.Event;
import model.EventTagCollection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CancelBookingSystemTests extends ConsoleTest {
    //Fail Cases:

    //Test for cancelling a booking within 24 hours of an event starting
    @Test
    void cancelBookingWithin24H() {
        Controller controller = createStaffAndEvent(5, 12, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookCmd = new BookEventCommand(firstEventNumber, 2);
        controller.runCommand(bookCmd);
        long bookingNumber = bookCmd.getResult().getBookingNumber();
        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(bookingNumber);
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_NO_CANCELLATIONS_WITHIN_24H"
        );
    }

    //Test for cancelling a booking which doesn't exist
    @Test
    void cancelNonExistingBooking() {
        Controller controller = createStaffAndEvent(5, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(103945);
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_BOOKING_NOT_FOUND"
        );
    }

    //Test for cancelling a booking of another user
    @Test
    void cancelAnotherUsersBooking() {
        Controller controller = createStaffAndEvent(5, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller, 2);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Evil",
                "clever@hacks.net",
                "999",
                null,
                "password"));
        CancelBookingCommand cancelCmd = new CancelBookingCommand(1);
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CANCEL_BOOKING_USER_IS_NOT_BOOKER"
        );
    }

    //Test for cancelling a booking twice
    @Test
    void cancelBookingTwice() {
        Controller controller = createStaffAndEvent(1, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);

        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_SUCCESS",
                "CANCEL_BOOKING_BOOKING_NOT_ACTIVE"
        );
    }

    //Test for cancelling a booking when not logged in
    @Test
    void cancelNotLoggedIn() {
        Controller controller = createStaffAndEvent(1, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);

        startOutputCapture();
        controller.runCommand(new LogoutCommand());
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_LOGOUT_SUCCESS",
                "CANCEL_BOOKING_USER_NOT_CONSUMER"
        );
    }

    //Test for cancelling a booking when User is Staff
    @Test
    void cancelNotConsumer() {
        Controller controller = createStaffAndEvent(1, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));

        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_USER_NOT_CONSUMER"
        );
    }

    //Success Cases:

    //Test for successfully cancel a booking
    @Test
    void bookEventThenCancelBooking() {
        Controller controller = createStaffAndEvent(1, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);
        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        assertTrue(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_SUCCESS"
        );
    }
}
