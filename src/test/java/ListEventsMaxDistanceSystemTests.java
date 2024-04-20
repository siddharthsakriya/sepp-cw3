import command.*;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ListEventsMaxDistanceSystemTests extends ConsoleTest{
    //Fail Cases:

    //Test to list User events by max distance when User has not added their address
    @Test
    void listEventsByMaxDistanceNoConsumerAddress(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        createMultipleEvents(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new UpdateConsumerProfileCommand(CONSUMER_PASSWORD, "jssks", CONSUMER_EMAIL, "9292929292", "", CONSUMER_PASSWORD, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        ListEventMaxDistanceCommand listEventsCmd = new ListEventMaxDistanceCommand(true, true, LocalDate.now().plusDays(5), TransportMode.car, 10000);
        controller.runCommand(listEventsCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "LIST_EVENT_DISTANCE_CONSUMER_NO_ADDRESS"
        );
    }

    //Test to list User events by max distance when User is Staff
    @Test
    void listEventsByMaxDistanceNotConsumer(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        ListEventMaxDistanceCommand eventMaxDistanceCmd = new ListEventMaxDistanceCommand(true,true, LocalDate.now().plusDays(5), TransportMode.car, 10000);
        controller.runCommand(eventMaxDistanceCmd);
        assertNull(eventMaxDistanceCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_DISTANCE_USER_NOT_CONSUMER"
        );
    }

    //Test to list User events when User is not logged in
    @Test
    void listEventsByMaxDistanceNotLoggedIn(){
        Controller controller = createController();
        startOutputCapture();
        ListEventMaxDistanceCommand eventMaxDistanceCmd = new ListEventMaxDistanceCommand(true,true, LocalDate.now().plusDays(5), TransportMode.car, 10000);
        controller.runCommand(eventMaxDistanceCmd);
        assertNull(eventMaxDistanceCmd.getResult());
        stopOutputCaptureAndCompare(
                "LIST_EVENT_DISTANCE_USER_NOT_CONSUMER"
        );
    }

    //Success Cases:

    //Test to list User events by max distance when no events have been added yet
    @Test
    void listEventByMaxDistanceEmpty(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        createMultipleEvents(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new UpdateConsumerProfileCommand(CONSUMER_PASSWORD, "jssks", CONSUMER_EMAIL, "9292929292", "55.94872684464941 -3.199892044473183", CONSUMER_PASSWORD, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        ListEventMaxDistanceCommand listEventsCmd = new ListEventMaxDistanceCommand(true, true, LocalDate.now().plusDays(5), TransportMode.car, 0);
        controller.runCommand(listEventsCmd);
        assertTrue(listEventsCmd.getResult().isEmpty());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "LIST_EVENT_DISTANCE_SUCCESS"
        );
    }

    //Test to list User events by max distance when there are two events in the system
    @Test
    void listEventByMaxDistanceTwoEvents(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        createMultipleEvents(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new UpdateConsumerProfileCommand(CONSUMER_PASSWORD, "jssks", CONSUMER_EMAIL, "9292929292", "55.94872684464941 -3.199892044473183", CONSUMER_PASSWORD, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        ListEventMaxDistanceCommand listEventsCmd = new ListEventMaxDistanceCommand(true, true, LocalDate.now().plusDays(5), TransportMode.car, 10000);
        controller.runCommand(listEventsCmd);
        assertTrue(listEventsCmd.getResult().size() == 2);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "LIST_EVENT_DISTANCE_SUCCESS"
        );
    }

    //Test to list User events by max distance when the events in the system have no address
    @Test
    void ListEventsMaxDistanceNoUserEventsEventsNoAddress(){
        Controller controller = createController();
        createStaff(controller);

        Event event = createEventNoAddress(
                controller,
                2,
                10,
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        );
        controller.runCommand(new LogoutCommand());

        startOutputCapture();
        createConsumer(controller);
        ListEventMaxDistanceCommand eventMaxDistanceCmd = new ListEventMaxDistanceCommand(true,true, LocalDate.now().plusDays(5), TransportMode.car, 10000);
        controller.runCommand(eventMaxDistanceCmd);
        assertTrue(eventMaxDistanceCmd.getResult().isEmpty());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_DISTANCE_SUCCESS"
        );
    }
}
