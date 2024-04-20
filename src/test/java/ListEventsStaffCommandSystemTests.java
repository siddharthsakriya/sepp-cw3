import command.*;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;
import view.IView;

import javax.swing.text.View;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ListEventsStaffCommandSystemTests extends ConsoleTest{
    //Fail Cases:

    //Test to list User events based on max distance when User is staff
    @Test
    void listEventsByMaxDistanceNotConsumer(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        ListEventsCommand listEventsCmd = new ListEventMaxDistanceCommand(true,true, LocalDate.now().plusDays(5), TransportMode.car, 10000);
        controller.runCommand(listEventsCmd);
        assertNull(listEventsCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_DISTANCE_USER_NOT_CONSUMER"
        );
    }

    //Success Cases:

    //Test to list User events when User is Staff
    @Test
    void listEventStaffEmpty(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        ListEventsCommand listEventCmd = new ListEventsCommand(true, true, LocalDate.now());
        controller.runCommand(listEventCmd);
        assertTrue(listEventCmd.getResult().isEmpty());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS"
        );
    }

    //Test to list two User events in the system when User is Staff
    @Test
    void listEventStaffTwoEvents(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        createMultipleEvents(controller);
        ListEventsCommand listEventCmd = new ListEventsCommand(true, true, LocalDate.now().plusDays(2));
        controller.runCommand(listEventCmd);
        assertTrue(listEventCmd.getResult().size() == 2);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "LIST_EVENTS_SUCCESS"

        );
    }
}
