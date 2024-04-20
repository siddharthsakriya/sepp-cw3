import command.CreateEventCommand;
import command.LogoutCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEventSystemTests extends ConsoleTest {
    //Fail Cases:

    //Test for creating an event when User is not Staff
    @Test
    void createEventNotStaff(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand(
                "event_title",
                EventType.Theatre,
                5,
                100,
                "",
                "a event description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2).plusHours(1),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        );
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_USER_NOT_STAFF"
        );
    }

    //Test for creating an event when User is not logged in
    @Test
    void createEventNotLoggedIn(){
        Controller controller = createController();
        startOutputCapture();
        CreateEventCommand createEventCmd = new CreateEventCommand(
                "event_title",
                EventType.Theatre,
                5,
                100,
                "",
                "a event description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2).plusHours(1),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        );
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "CREATE_EVENT_USER_NOT_STAFF"
        );
    }

    //Test for creating an event with a start date in the past
    @Test
    void createEventInThePast() {
        Controller controller = createController();
        startOutputCapture();
        registerPawsForAwwws(controller);
        Event event = createEvent2(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).plusHours(2)
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_IN_THE_PAST"
        );
    }

    //Test for creating an event with a start date after the end date
    @Test
    void createEventWithEndBeforeStart() {
        Controller controller = createController();
        startOutputCapture();
        registerPawsForAwwws(controller);
        Event event = createEvent2(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).minusHours(2)
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_START_AFTER_END"
        );
    }

    //Test for creating an 2 of the same events with the same start time
    @Test
    void createSameEventStartingSameTime() {
        LocalDateTime startDateTime = LocalDateTime.of(2026,1,10,0,0,0);
        LocalDateTime endDateTime = LocalDateTime.of(2026,1,11,0,0,0);
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd1 = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94368888764689 -3.1888246174917114",
                "Come and enjoy some pets for pets",
                startDateTime,
                endDateTime,
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));

        CreateEventCommand createEventCmd2 = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94368888764689 -3.1888246174917114",
                "Come and enjoy some pets for pets",
                startDateTime,
                endDateTime,
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));

        controller.runCommand(createEventCmd1);
        controller.runCommand(createEventCmd2);
        assertTrue(createEventCmd1.getResult() instanceof Event);
        assertFalse(createEventCmd2.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "CREATE_EVENT_TITLE_AND_TIME_CLASH"
        );
    }

    //Test for creating an event with a word address format
    @Test
    void createEventWithWordAddressFormat () {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "Edinburgh Castle",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_NOT_LAT_LONG"
        );
    }

    //Another Test for creating an event with a word address format
    @Test
    void createEventWithWordAddressFormatTwo () {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "Edinburgh Castle",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_NOT_LAT_LONG"
        );
    }

    //Test for creating an event with a wrong lat long format
    @Test
    void createEventWithWrongLatLongFormat () {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "155.94368888764689 -3222.1888246174917114",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_LAT_LONG_NOT_BOUNDS"
        );
    }

    //Test for creating an event with a negative ticket price
    @Test
    void createEventWithNegativePrice() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                -100,
                "55.94368888764689 -3.1888246174917114",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_NEGATIVE_TICKET_PRICE"
        );
    }

    //Test for creating an event with tags that don't match
    @Test
    void createEventWithNonMatchingTags() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand("Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94368888764689 -3.1888246174917114",
                "Come and enjoy some pets for pets",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=500"));
        controller.runCommand(createEventCmd);
        assertFalse(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_TAGS_DO_NOT_MATCH"
        );
    }

    //Success Cases:

    //Test for creating an event with unlimited tickets and a valid address
    @Test
    void createUnlimitedTicketedEventValidAddress() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        Event result = createEvent(controller,1000092930, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        assertTrue(result instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );
    }

    //Test for creating an event with limited tickets and a valid address
    @Test
    void createNormalEventValidAddress() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        Event result = createEvent(controller,100, 1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        assertTrue(result instanceof Event);
        controller.runCommand(new LogoutCommand());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS"
        );
    }

    //Test for creating an event with unlimited tickets and no address
    @Test
    void createUnlimitedTicketedEventNoAddress(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand(
                "event_title",
                EventType.Theatre,
                18282820,
                0,
                "",
                "a event description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2).plusHours(1),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertTrue(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );
    }

    //Test for creating an event with limited tickets and no address
    @Test
    void createNormalEventNoAddress(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand(
                "event_title",
                EventType.Theatre,
                5,
                100,
                "",
                "a event description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2).plusHours(1),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertTrue(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );
    }

    //Test for creating a free event with no address
    @Test
    void createFreeEventNoAddress(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand(
                "event_title",
                EventType.Theatre,
                100,
                0,
                "",
                "a event description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2).plusHours(1),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertTrue(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );
    }

    //Test for creating a free event with an address
    @Test
    void createFreeEventWithAddress(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        CreateEventCommand createEventCmd = new CreateEventCommand(
                "event_title",
                EventType.Theatre,
                100,
                0,
                "55.94368888764689 -3.1888246174917114",
                "a event description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2).plusHours(1),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        controller.runCommand(createEventCmd);
        assertTrue(createEventCmd.getResult() instanceof Event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );
    }
}
