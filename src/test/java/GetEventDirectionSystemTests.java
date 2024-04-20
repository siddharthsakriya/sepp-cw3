import command.*;
import controller.Controller;
import model.EventTagCollection;
import model.EventType;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GetEventDirectionSystemTests extends ConsoleTest{
    //Fail Cases:

    //Test to get directions for an event which doesn't exist
    @Test
    void eventDoesNotExist(){
       Controller controller = createStaffAndEvent(1,1, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
       controller.runCommand(new LogoutCommand());
       createConsumer(controller);
       startOutputCapture();
       GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(3, TransportMode.car);
       controller.runCommand(eventDirectionsCmd);
       assertNull(eventDirectionsCmd.getResult());
       stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_NO_SUCH_EVENT");
    }

    //Test to get directions for an event which has no address
    @Test
    void eventHasNoAddress(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(eventDirectionsCmd);
        assertNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS");
    }

    //Test to get directions for an event when the user is Staff
    @Test
    void userNotConsumer(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "55.94368888764689 -3.1888246174917114", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(eventDirectionsCmd);
        assertNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER");
    }

    //Test to get directions for an event when the user has no address
    @Test
    void getDirectionsConsumerHasNoAddress(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "55.94368888764689 -3.1888246174917114", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand("Shoo", "Sho@gmail.com","122341344","","123"));
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(eventDirectionsCmd);
        assertNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS");
    }

    //Success Cases:

    //Test to get directions for an event when the transport mode is a bike
    @Test
    void getDirectionsSuccessBike(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "55.94368888764689 -3.1888246174917114", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.bike);
        controller.runCommand(eventDirectionsCmd);
        assertNotNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_SUCCESS");
    }

    //Test to get directions for an event when the transport mode is a car
    @Test
    void getDirectionsSuccessCar(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "55.94368888764689 -3.1888246174917114", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.car);
        controller.runCommand(eventDirectionsCmd);
        assertNotNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_SUCCESS");
    }

    //Test to get directions for an event when the transport mode is by foot
    @Test
    void getDirectionsSuccessFoot(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "55.94368888764689 -3.1888246174917114", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.foot);
        controller.runCommand(eventDirectionsCmd);
        assertNotNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_SUCCESS");
    }

    //Test to get directions for an event when the transport mode is by wheelchair
    @Test
    void getDirectionsSuccessWheelchair(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Meteor Shower", EventType.Theatre,  100, 0, "55.94368888764689 -3.1888246174917114", "Watch the stars", LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(3),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        GetEventDirectionsCommand eventDirectionsCmd = new GetEventDirectionsCommand(1, TransportMode.wheelchair);
        controller.runCommand(eventDirectionsCmd);
        assertNotNull(eventDirectionsCmd.getResult());
        stopOutputCaptureAndCompare("GET_EVENT_DIRECTIONS_SUCCESS");
    }

}
