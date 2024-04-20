import command.*;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;
import view.IView;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class ReviewEventSystemTests extends ConsoleTest {
    //Fail Cases:

    //Test to review an event which doesn't exist
    @Test
    void eventDoesNotExist(){
        Controller controller = createController();
        createConsumer(controller);
        controller.runCommand(new LoginCommand(CONSUMER_EMAIL,CONSUMER_PASSWORD));
        startOutputCapture();
        ReviewEventCommand reviewEventCmd = new ReviewEventCommand( (long) 10 , "I did not enjoy the event");
        controller.runCommand(reviewEventCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_EVENT_NOT_FOUND");
        assertNull(reviewEventCmd.getResult());
    }

    //Test to review an event which is ongoing
    @Test
    void eventOngoing(){
        Controller controller = createStaffAndEvent(1,1,new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        getAllEvents(controller);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        controller.runCommand(new LoginCommand(CONSUMER_EMAIL,CONSUMER_PASSWORD));
        Event event = getAllEvents(controller).get(0);
        controller.runCommand(new BookEventCommand(event.getEventNumber(), 1));
        startOutputCapture();
        ReviewEventCommand reviewEventCmd = new ReviewEventCommand( event.getEventNumber() , "I did not enjoy the event");
        controller.runCommand(reviewEventCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_EVENT_NOT_OVER");
        assertNull(reviewEventCmd.getResult());

    }

    //Test to review an event when user is Staff
    @Test
    void currentUserStaff(){
        Context context= new Context(
                "The University of Edinburgh",
                "55.94747223411703 -3.187300017491497", // Old College, South Bridge, Edinburgh
                "epay@ed.ac.uk",
                "Nec temere nec timide"
        );
        Event event = context.getEventState().createEvent("HueHueHue", EventType.Theatre, 1,0,"55.94368888764689 -3.1888246174917114", "Bleh", LocalDateTime.now().plusHours(-2), LocalDateTime.now().plusHours(-1),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        IView view = new TestView();
        Controller controller = new Controller(context, view);
        createStaff(controller);
        startOutputCapture();
        ReviewEventCommand reviewEventCmd = new ReviewEventCommand(event.getEventNumber(), "I did not enjoy the event");
        controller.runCommand(reviewEventCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_USER_NOT_CONSUMER");
        assertNull(reviewEventCmd.getResult());
    }

    //Test to review an event when Consumer doesn't have a booking
    @Test
    void consumerDoesNotHaveBooking(){
        Context context= new Context(
                "The University of Edinburgh",
                "55.94747223411703 -3.187300017491497", // Old College, South Bridge, Edinburgh
                "epay@ed.ac.uk",
                "Nec temere nec timide"
        );
        Event event = context.getEventState().createEvent("HueHueHue", EventType.Theatre, 1,0,"55.94368888764689 -3.1888246174917114", "Bleh", LocalDateTime.now().plusHours(-2), LocalDateTime.now().plusHours(-1),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"));
        IView view = new TestView();
        Controller controller = new Controller(context, view);
        createConsumer(controller);
        startOutputCapture();
        ReviewEventCommand reviewEventCmd = new ReviewEventCommand( event.getEventNumber() , "I did not enjoy the event");
        controller.runCommand(reviewEventCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_NO_VALID_BOOKING");
        assertNull(reviewEventCmd.getResult());
    }

    //Success Case:

    //Test to review an event when Consumer has a booking
    @Test
    void consumerHasValidBooking(){
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
        controller.runCommand(new LoginCommand(CONSUMER_EMAIL,CONSUMER_PASSWORD));
        startOutputCapture();
        ReviewEventCommand reviewEventCmd = new ReviewEventCommand( event.getEventNumber() , "I did not enjoy the event");
        controller.runCommand(reviewEventCmd);
        stopOutputCaptureAndCompare("REVIEW_EVENT_SUCCESS");

        assertNotNull(reviewEventCmd.getResult());
        assertEquals(reviewEventCmd.getResult().getEvent(), event);
        assertEquals(reviewEventCmd.getResult().getAuthor(),consumer);
        assertEquals(reviewEventCmd.getResult().getContent(), "I did not enjoy the event");
        assertEquals(reviewEventCmd.getResult().getCreationDateTime().toLocalDate(), LocalDate.now());
    }
}
