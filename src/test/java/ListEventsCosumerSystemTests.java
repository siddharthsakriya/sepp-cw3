import command.*;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;
import view.IView;

import javax.swing.text.View;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListEventsCosumerSystemTests extends ConsoleTest{
    //Fail Cases:

    //Test to list User events when User is not logged in
    @Test
    void listEventNotLoggedIn(){
        Controller controller = createController();
        startOutputCapture();
        ListEventsCommand listEventsCmd = new ListEventsCommand(true, false, LocalDate.now());
        controller.runCommand(listEventsCmd);
        assertNull(listEventsCmd.getResult());
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_NOT_LOGGED_IN"
        );
    }

    //Success Cases:

    //Test to list two User events as a Consumer
    @Test
    void listEventConsumerTwoEvents(){
        Controller controller = createController();
        createStaff(controller);
        createMultipleEvents(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new UpdateConsumerProfileCommand(CONSUMER_PASSWORD, "jssks", CONSUMER_EMAIL, "9292929292", "55.94872684464941 -3.199892044473183", CONSUMER_PASSWORD, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        startOutputCapture();
        ListEventsCommand listEventsCmd = new ListEventsCommand(true, false, LocalDate.now().plusDays(2));
        controller.runCommand(listEventsCmd);
        assertTrue(listEventsCmd.getResult().size() == 2);
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS"
        );
    }

    //Test to list User events as a Consumer based on a max distance
    @Test
    void listEventByMaxDistanceEmpty(){
        Controller controller = createController();
        createStaff(controller);
        createMultipleEvents(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new UpdateConsumerProfileCommand(CONSUMER_PASSWORD, "jssks", CONSUMER_EMAIL, "9292929292", "55.94872684464941 -3.199892044473183", CONSUMER_PASSWORD, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        startOutputCapture();
        controller.runCommand(new ListEventMaxDistanceCommand(true,true, LocalDate.now().plusDays(5), TransportMode.car, 9828282 ));
        stopOutputCaptureAndCompare(
                "LIST_EVENT_DISTANCE_SUCCESS"
        );
    }

    //Test to list an empty list of User events as a Consumer
    @Test
    void listEventsConsumerEmpty(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        ListEventsCommand listEventsCmd = new ListEventsCommand(true, false, LocalDate.now());
        controller.runCommand(listEventsCmd);
        assertTrue(listEventsCmd.getResult().isEmpty());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS"
        );
    }

}
