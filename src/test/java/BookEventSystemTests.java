import command.*;
import controller.Context;
import controller.Controller;
import model.Booking;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;
import view.IView;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookEventSystemTests extends ConsoleTest {

    //Fail Cases:

    //Test for booking an event when User is Staff
    @Test
    void bookEventUserNotConsumer() {
        Controller controller = createStaffAndEvent(100, 5, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        List<Event> events = getAllEvents(controller);
        long firstEvent = events.get(0).getEventNumber();
        startOutputCapture();
        BookEventCommand bookCmd = new BookEventCommand(firstEvent, 2);
        controller.runCommand(bookCmd);
        assertNull(bookCmd.getResult());
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_USER_NOT_CONSUMER"
        );
    }

    //Test for booking an event when User is not logged in
    @Test
    void bookEventUserNotLoggedIn() {
        Controller controller = createStaffAndEvent(100, 5, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        List<Event> events = getAllEvents(controller);
        long firstEvent = events.get(0).getEventNumber();
        startOutputCapture();
        controller.runCommand(new LogoutCommand());
        BookEventCommand bookCmd = new BookEventCommand(firstEvent, 2);
        controller.runCommand(bookCmd);
        assertNull(bookCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_LOGOUT_SUCCESS",
                "BOOK_EVENT_USER_NOT_CONSUMER"
        );
    }

    //Test for booking an event and requesting for a negative number of tickets
    @Test
    void bookNegativeTickets() {
        Controller controller = createStaffAndEvent(1, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        Booking result = createConsumerAndBookFirstEvent(controller, -11);
        assertNull(result);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_INVALID_NUM_TICKETS"
        );
    }

    //Test for booking an event and requesting more tickets than there is left
    @Test
    void overbookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        Booking result = createConsumerAndBookFirstEvent(controller, 2);
        assertNull(result);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );
    }

    //Test for booking an event and requesting more tickets than there is left on multiple occasions
    @Test
    void overbookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(2, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookCmd1 = new BookEventCommand(firstEventNumber, 1);
        BookEventCommand bookCmd2 = new BookEventCommand(firstEventNumber, 2);
        controller.runCommand(bookCmd1);
        controller.runCommand(bookCmd2);
        assertNotNull(bookCmd1.getResult());
        assertNull(bookCmd2.getResult());
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );
    }

    //Test for booking an event when the event is cancelled (not active)
    @Test
    void bookCancelledEvent() {
        Controller controller = createStaffAndEvent(100, 5, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        startOutputCapture();
        controller.runCommand(new CancelEventCommand(1, "test"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        BookEventCommand bookCmd = new BookEventCommand(1, 5);
        controller.runCommand(bookCmd);
        Booking result = bookCmd.getResult();
        assertNull(result);
        stopOutputCaptureAndCompare(
                "CANCEL_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_EVENT_NOT_ACTIVE"
        );
    }

    //Test for booking an event when User is not logged in
    @Test
    void bookEventNotLoggedIn() {
        Controller controller = createStaffAndEvent(5, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        startOutputCapture();
        controller.runCommand(new BookEventCommand(firstEventNumber, 5));
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_USER_NOT_CONSUMER"
        );
    }

    //Test for booking an event when the event does not exist
    @Test
    void bookNonExistingEvent() {
        Controller controller = createStaffAndEvent(5, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        BookEventCommand bookCmd = new BookEventCommand(1324, 1);
        startOutputCapture();
        controller.runCommand(bookCmd);
        assertNull(bookCmd.getResult());
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_EVENT_NOT_FOUND"
        );
    }

    //Test for booking an event which has ended
    @Test
    void bookEventWhichHasEnded(){
        Context context= new Context(
                "The University of Edinburgh",
                "55.94747223411703 -3.187300017491497", // Old College, South Bridge, Edinburgh
                "epay@ed.ac.uk",
                "Nec temere nec timide"
        );
        context.getEventState().createEvent("HueHueHue", EventType.Theatre, 100,0,"55.94368888764689 -3.1888246174917114", "Bleh", LocalDateTime.now().plusHours(-2), LocalDateTime.now().plusHours(-1),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        IView view = new TestView();
        Controller controller = new Controller(context, view);
        List<Event> events = getAllEvents(controller);
        long firstEvent = events.get(0).getEventNumber();
        startOutputCapture();
        createStaff(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        BookEventCommand bookCmd = new BookEventCommand(firstEvent, 5);
        controller.runCommand(bookCmd);
        Booking result = bookCmd.getResult();
        assertNull(result);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_ALREADY_OVER"
        );
    }

    //Success Cases:

    //Test for booking an event successfully
    @Test
    void bookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        Booking result = createConsumerAndBookFirstEvent(controller, 1);
        assertNotNull(result);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );
    }

    //Test for booking event successfully on multiple occasions
    @Test
    void bookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(5, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookCmd1 = new BookEventCommand(firstEventNumber, 3);
        BookEventCommand bookCmd2 = new BookEventCommand(firstEventNumber, 2);
        controller.runCommand(bookCmd1);
        controller.runCommand(bookCmd2);
        assertNotNull(bookCmd2.getResult());
        assertNotNull(bookCmd1.getResult());
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );
    }
}