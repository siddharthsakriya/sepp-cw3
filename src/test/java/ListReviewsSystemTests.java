import command.*;
import controller.Context;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListReviewsSystemTests extends ConsoleTest{

    //Success Cases:

    //Test to list event reviews when there are no events
    @Test
    void listEventReviewNoEvents(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller, "newTag",new HashSet<>(Arrays.asList("true","false")),"false");
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        ListEventReviewsCommand listReviewsCommand = new ListEventReviewsCommand("test event");
        controller.runCommand(listReviewsCommand);
        assertEquals(listReviewsCommand.getResult().size(),0);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );

    }

    //Test to list event reviews when event title is not found
    @Test
    void listEventReviewsTitleNotFound(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext,  new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"), LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(event.getEventNumber(),"greatEvent");
        controller.runCommand(reviewCmd);
        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle()+"test");
        controller.runCommand(listEventReviewsCommand);
        assertEquals(listEventReviewsCommand.getResult().size(),0);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );
    }

    //Test to list event reviews when there are no reviews
    @Test
    void listEventReviewsNoReviews(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"),LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle());
        controller.runCommand(listEventReviewsCommand);
        assertEquals(0, listEventReviewsCommand.getResult().size());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );

    }

    //Test to list event reviews when there multiple reviews
    @Test
    void listEventReviewsMultipleReviews(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"),LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(event.getEventNumber(),"greatEvent");
        controller.runCommand(reviewCmd);
        ReviewEventCommand reviewCmd1 = new ReviewEventCommand(event.getEventNumber(),"superb!!");
        controller.runCommand(reviewCmd1);
        ReviewEventCommand reviewCmd2 = new ReviewEventCommand(event.getEventNumber(),"lovely event!!");
        controller.runCommand(reviewCmd2);
        ReviewEventCommand reviewCmd3 = new ReviewEventCommand(event.getEventNumber(),"best event of my life!");
        controller.runCommand(reviewCmd3);
        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle());
        controller.runCommand(listEventReviewsCommand);
        assertEquals(listEventReviewsCommand.getResult(),Arrays.asList(reviewCmd.getResult(),reviewCmd1.getResult(),reviewCmd2.getResult(),reviewCmd3.getResult()));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );
    }

    //Test to list event reviews when there are reviews from multiple users
    @Test
    void listEventReviewsWithReviewsFromMultipleUsers(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"),LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(event.getEventNumber(),"greatEvent");
        controller.runCommand(reviewCmd);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"james","james@gmail.com",event);
        ReviewEventCommand reviewCmd1 = new ReviewEventCommand(event.getEventNumber(),"superb!!");
        controller.runCommand(reviewCmd1);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"raj","raj@gmail.com",event);
        ReviewEventCommand reviewCmd2 = new ReviewEventCommand(event.getEventNumber(),"awesome event!!");
        controller.runCommand(reviewCmd2);

        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle());
        controller.runCommand(listEventReviewsCommand);
        assertEquals(listEventReviewsCommand.getResult(),Arrays.asList(reviewCmd.getResult(),reviewCmd1.getResult(),reviewCmd2.getResult()));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );


    }

    //Test to list event reviews when user is Staff
    @Test
    void listEventReviewsSuccessStaff(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"),LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(event.getEventNumber(),"greatEvent");
        controller.runCommand(reviewCmd);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"james","james@gmail.com",event);
        ReviewEventCommand reviewCmd1 = new ReviewEventCommand(event.getEventNumber(),"superb!!");
        controller.runCommand(reviewCmd1);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"raj","raj@gmail.com",event);
        ReviewEventCommand reviewCmd2 = new ReviewEventCommand(event.getEventNumber(),"awesome event!!");
        controller.runCommand(reviewCmd2);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterStaffCommand("amy123@gmail.com","password123","Nec temere nec timide"));
        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle());
        controller.runCommand(listEventReviewsCommand);
        assertEquals(listEventReviewsCommand.getResult(),Arrays.asList(reviewCmd.getResult(),reviewCmd1.getResult(),reviewCmd2.getResult()));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );
    }

    //Test to list event reviews as a Consumer
    @Test
    void listEventReviewsSuccessConsumer(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"),LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(event.getEventNumber(),"greatEvent");
        controller.runCommand(reviewCmd);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"james","james@gmail.com",event);
        ReviewEventCommand reviewCmd1 = new ReviewEventCommand(event.getEventNumber(),"superb!!");
        controller.runCommand(reviewCmd1);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"raj","raj@gmail.com",event);
        ReviewEventCommand reviewCmd2 = new ReviewEventCommand(event.getEventNumber(),"awesome event!!");
        controller.runCommand(reviewCmd2);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand("jamie", "mail@mail.co.uk", "01010011", "", "password123"));
        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle());
        controller.runCommand(listEventReviewsCommand);
        assertEquals(listEventReviewsCommand.getResult(),Arrays.asList(reviewCmd.getResult(),reviewCmd1.getResult(),reviewCmd2.getResult()));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );
    }

    //Test to list event reviews for events with a single review
    @Test
    void listEventReviewsSingleReviews(){
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        startOutputCapture();
        createStaff(controller);
        addEventTag(controller,"newTag",new HashSet<>(Arrays.asList("true","false")), "false");
        Event event = createEventInPast(testContext, new EventTagCollection("hasSocialDistancing=true,venueCapacity=200"),LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(2).plusHours(3));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookEvent(testContext,controller,"amy","amy@gmail.com",event);
        ReviewEventCommand reviewCmd = new ReviewEventCommand(event.getEventNumber(),"greatEvent");
        controller.runCommand(reviewCmd);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand("jamie", "mail@mail.co.uk", "01010011", "", "password123"));
        ListEventReviewsCommand listEventReviewsCommand = new ListEventReviewsCommand(event.getTitle());
        controller.runCommand(listEventReviewsCommand);
        assertEquals(listEventReviewsCommand.getResult(),Arrays.asList(reviewCmd.getResult()));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );
    }
}
