import command.*;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import view.IView;

public class LoadAppStateSystemTests extends ConsoleTest {
    public void createSave() {
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Chilli", EventType.Music, 100, 0, "", "chilli", LocalDateTime.of(2025, Month.MARCH, 10,0,0), LocalDateTime.of(2025, Month.MARCH, 20,0,0),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new AddEventTagCommand("hasGreg", Set.of("yes", "no"), "no"));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller, 1);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(STAFF_EMAIL,STAFF_PASSWORD));
        startOutputCapture();
        controller.runCommand(new SaveAppStateCommand("LoadTest.ser"));
    }

    //Fail Cases:

    //Test to load a saved state as a Consumer
    @Test
    void loadAsConsumer(){
        createSave();
        Controller controller = createController();
        createConsumer(controller);
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("LoadTest.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_USER_NOT_STAFF");
    }

    //Test to load a saved state file which doesn't exist
    @Test
    void loadFileDoesNotExist() {
        createSave();
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("HELPME.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_FILE_NOT_FOUND");
    }

    //Test to load a saved state file with clashing tags
    @Test
    void loadAppClashingTags (){
        createSave();
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new AddEventTagCommand("hasGreg", Set.of("true", "false"), "false"));
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("LoadTest.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_CLASHING_TAGS");
    }

    //Test to load a saved state file with clashing emails
    @Test
    void loadAppClashingEmail () {
        createSave();
        Controller controller = createController();
        controller.runCommand(new RegisterConsumerCommand("che", "i-would -never-steal-a@dog.xd","121313123123123","55.94872684464941 -3.199892044473183","123456"));
        controller.runCommand(new LogoutCommand());
        createStaff(controller);
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("LoadTest.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_CLASHING_EMAIL");
    }

    //Test to load a saved state file with clashing events
    @Test
    void loadAppClashingEvents(){
        createSave();
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Chilli", EventType.Music, 100, 0, "", "chilli chilli", LocalDateTime.of(2025, Month.MARCH, 10,0,0), LocalDateTime.of(2025, Month.MARCH, 20,0,0),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("LoadTest.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_CLASHING_EVENTS");
    }

    //Test to load a saved state file with clashing bookings
    @Test
    void loadAppClashingBooking(){
        Context context= new Context(
                "The University of Edinburgh",
                "55.94747223411703 -3.187300017491497", // Old College, South Bridge, Edinburgh
                "epay@ed.ac.uk",
                "Nec temere nec timide"
        );
        IView view = new TestView();
        Event event = context.getEventState().createEvent("HueHueHue", EventType.Theatre, 1,0,"55.94368888764689 -3.1888246174917114", "Bleh", LocalDateTime.now().plusHours(-2), LocalDateTime.now().plusHours(-1),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        Consumer consumer = new Consumer("Chihuahua Fan", CONSUMER_EMAIL, "01324456897", "55.94872684464941 -3.199892044473183", CONSUMER_PASSWORD);
        context.getUserState().addUser(consumer);
        Booking booking = context.getBookingState().createBooking(consumer,event,1);
        consumer.addBooking(booking);
        event.setNumTicketsLeft(event.getNumTicketsLeft()-1);
        Controller controller = new Controller(context, view);
        Controller controller1 = new Controller(context, view);
        createStaff(controller);
        controller.runCommand(new SaveAppStateCommand("ClashingBooking.ser"));
        startOutputCapture();
        controller1.runCommand(new LoadAppStateCommand("ClashingBooking.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_CLASHING_BOOKINGS");
    }

    //Success Cases:

    //Test to load a saved state when user is Staff
    @Test
    void loadAsStaffSuccess(){
        createSave();
        Controller controller = createController();
        createStaff(controller);
        startOutputCapture();
        controller.runCommand(new LoadAppStateCommand("LoadTest.ser"));
        stopOutputCaptureAndCompare("LOAD_APP_STATE_SUCCESS");
    }
}
