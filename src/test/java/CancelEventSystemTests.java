import command.*;
import controller.Context;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CancelEventSystemTests extends ConsoleTest {

    //Fail Cases:

    //Test for cancelling an event when User is Consumer
    @Test
    void cancelEventAsConsumer() {
        Controller controller = createStaffAndEvent(10, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        CancelEventCommand cancelEventCmd = new CancelEventCommand(events.get(0).getEventNumber(), "Let's try messing with this event");
        controller.runCommand(cancelEventCmd);
        assertFalse(cancelEventCmd.getResult());
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_USER_NOT_STAFF"
        );
    }

    //Test for cancelling an event when User is not logged in
    @Test
    void cancelEventNotLoggedIn(){
        Controller controller = createStaffAndEvent(10, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        CancelEventCommand cancelEventCmd = new CancelEventCommand(events.get(0).getEventNumber(), "Let's try messing with this event");
        controller.runCommand(cancelEventCmd);
        assertFalse(cancelEventCmd.getResult());
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_USER_NOT_STAFF"
        );
    }

    //Test for cancelling an event which has already passed
    @Test
    void cancelEndedEvent() {
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        createStaff(controller);
        // Need to manually insert event into state, because CreateEventCommand does not allow
        // creating events in the past
        Event event = testContext.getEventState().createEvent(
            "World Tour",
                EventType.Music,
                10000,
                200,
                "55.86440964478519 -4.252880444477458", // Glasgow Royal Concert Hall
                "Lady Gaga and Ariana Grande will be performing in a duet",
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().minusDays(6).plusHours(3),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        );
        long eventNumber = event.getEventNumber();

        startOutputCapture();
        CancelEventCommand cancelEventCmd = new CancelEventCommand(
                eventNumber,
                "How do I clear this off the event list?!?!"
        );
        controller.runCommand(cancelEventCmd);
        assertFalse(cancelEventCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_EVENT_ALREADY_STARTED"
        );
    }

    //Test for cancelling an event which is currently ongoing
    @Test
    void cancelOngoingEvent() {
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        createStaff(controller);
        // Need to manually insert event into state, because CreateEventCommand does not allow
        // creating events in the past
        Event event = testContext.getEventState().createEvent(
                "World Tour",
                EventType.Music,
                10000,
                200,
                "55.86440964478519 -4.252880444477458", // Glasgow Royal Concert Hall
                "Lady Gaga and Ariana Grande will be performing in a duet",
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusHours(3),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        );
        long eventNumber = event.getEventNumber();

        startOutputCapture();
        CancelEventCommand cancelEventCmd = new CancelEventCommand(
                eventNumber,
                "Earthquake emergency"
        );
        controller.runCommand(cancelEventCmd);
        assertFalse(cancelEventCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_EVENT_ALREADY_STARTED"
        );
    }

    //Success Cases

    //Test for cancelling an event which has no bookings
    @Test
    void cancelEventWithoutBookings() {
        Controller controller = createStaffAndEvent(10, 48, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        startOutputCapture();
        List<Event> events = getUserEvents(controller);
        controller.runCommand(new CancelEventCommand(events.get(0).getEventNumber(), "Too few bookings"));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    //Test for cancelling an event which has 1 booking
    @Test
    void cancelCurrentEventWith1Booking() {
        Controller controller = createStaffAndEvent(10, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller, 4);
        controller.runCommand(new LogoutCommand());

        startOutputCapture();
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));
        List<Event> events = getUserEvents(controller);
        CancelEventCommand cancelEventCommand = new CancelEventCommand(events.get(0).getEventNumber(), "I guess we're just not bothered to run this after all");
        controller.runCommand(cancelEventCommand);
        assertTrue(cancelEventCommand.getResult());
        stopOutputCaptureAndCompare(
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_REFUND_BOOKING_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    //Test for cancelling an event which has 2 bookings
    @Test
    void cancelFutureEventWith2Bookings() {
        Controller controller = createController();
        createStaff(controller);
        long eventNumber = createEvent(controller, 1000, 60000, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")).getEventNumber();
        controller.runCommand(new LogoutCommand());

        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Dora The Explorer",
                "dora@explorer.com",
                "78945623",
                "55.944853077240545 -3.1873034598188967", // Informatics Forum
                "I <3 travelling"
        ));
        controller.runCommand(new BookEventCommand(eventNumber, 20));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand(
                "Metal Fan",
                "literally@a.fan",
                "like the kind that you put on a desk",
                "55.94458227461727 -3.1853257484630726", // The Pear Tree, Edinburgh
                "to cool down the room"
        ));
        controller.runCommand(new BookEventCommand(eventNumber, 1));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));

        CancelEventCommand cancelCmd = new CancelEventCommand(eventNumber, "Sorry!");
        controller.runCommand(cancelCmd);
        assertTrue(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CANCEL_EVENT_REFUND_BOOKING_SUCCESS",
                "CANCEL_EVENT_REFUND_BOOKING_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    //Test for cancelling an event which has 1 booking which has been cancelled
    @Test
    void cancelFutureEventWithCancelledBooking() {
        Controller controller = createController();
        createStaff(controller);
        long eventNumber = createEvent(controller, 10000, 6000, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")).getEventNumber();
        controller.runCommand(new LogoutCommand());

        createConsumer(controller);
        BookEventCommand bookEventCmd = new BookEventCommand(eventNumber, 20);
        controller.runCommand(bookEventCmd);
        long bookingNumber = bookEventCmd.getResult().getBookingNumber();

        CancelBookingCommand cancelBookingCmd = new CancelBookingCommand(bookingNumber);
        controller.runCommand(cancelBookingCmd);
        assertTrue(cancelBookingCmd.getResult());

        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));

        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventNumber, "Sorry!");
        controller.runCommand(cancelEventCmd);
        assertTrue(cancelEventCmd.getResult());

        stopOutputCaptureAndCompare(
                "USER_LOGIN_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }
}
