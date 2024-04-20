import command.*;
import controller.Context;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ConsoleTest {
    private static PrintStream downstream;
    private final Pattern commandPattern = Pattern.compile("(?<callerName>[A-Za-z]+)(?<additionalInfo>.*?) => (?<result>[A-Z0-9_]+)");
    private ByteArrayOutputStream out;

    @BeforeAll
    static void saveDownstream() {
        downstream = System.out;
    }

    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    protected void startOutputCapture() {
        // NOTE: be careful, if the captured output exceeds 8192 bytes, the remainder will be lost!
        out = new ByteArrayOutputStream(8192);
        System.setOut(new PrintStream(out));
    }

    protected void stopOutputCaptureAndCompare(String... expected) {
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        try {
            String line;
            int idx;
            for (line = br.readLine(), idx = 0;
                 line != null && idx < expected.length;
                 line = br.readLine(), ++idx) {
                downstream.println(line);
                Matcher m = commandPattern.matcher(line);
                if (m.find()) {
                    assertEquals(expected[idx], m.group("result"));
                }
                // otherwise output includes a line that is not in command format
                // this happens when consumers are notified about cancellations
                // which is safe to ignore
            }

            assertEquals(expected.length, idx);
            assertNull(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void restoreDownstream() {
        System.setOut(downstream);
        System.out.println("---");
    }

    protected static Controller createController() {
        return new Controller(
                new Context(
                        "The University of Edinburgh",
                        "55.94747223411703 -3.187300017491497", // Old College, South Bridge, Edinburgh
                        "epay@ed.ac.uk",
                        "Nec temere nec timide"
                ),
                new TestView()
        );
    }

    protected static final String STAFF_EMAIL = "bring-in-the-cash@pawsforawwws.org";
    protected static final String STAFF_PASSWORD = "very insecure password 123";

    protected static void createStaff(Controller controller) {
        controller.runCommand(new RegisterStaffCommand(
                "bring-in-the-cash@pawsforawwws.org",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
    }
    protected static Event createEventNoAddress(Controller controller, int numTickets, int eventDelayHours, EventTagCollection tags) {
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                numTickets,
                0,
                "", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(eventDelayHours),
                LocalDateTime.now().plusHours(eventDelayHours + 1),
                tags
        );
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }
    protected static Event createEvent(Controller controller, int numTickets, int eventDelayHours, EventTagCollection tags) {
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                numTickets,
                0,
                "55.94368888764689 -3.1888246174917114", // George Square Gardens, Edinburgh
                "Please be prepared to pay 2.50 pounds on entry",
                LocalDateTime.now().plusHours(eventDelayHours),
                LocalDateTime.now().plusHours(eventDelayHours + 1),
                tags
        );
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }
    protected static Event createEventInPast(Context testContext, EventTagCollection tagCollection,LocalDateTime startTime,LocalDateTime endTime){

        return(testContext.getEventState().createEvent(
                "World Tour",
                EventType.Music,
                10000,
                200,
                "55.86440964478519 -4.252880444477458", // Glasgow Royal Concert Hall
                "Lady Gaga and Ariana Grande will be performing in a duet",
                startTime,
                endTime,
                tagCollection
                ));
    }
    protected static Controller createStaffAndEvent(int numTickets, int eventDelayHours, EventTagCollection tags) {
        Controller controller = createController();
        createStaff(controller);
        createEvent(controller, numTickets, eventDelayHours, tags);
        return controller;
    }

    protected static Controller createStaffAndTag(String tagName, Set<String> tagValues, String defaultValue) {
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new AddEventTagCommand(tagName, tagValues, defaultValue));
        return controller;
    }
    protected static final String CONSUMER_EMAIL = "i-would -never-steal-a@dog.xd";
    protected static final String CONSUMER_PASSWORD = "123456";

    protected static void createConsumer(Controller controller) {
        controller.runCommand(new RegisterConsumerCommand(
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                "55.94872684464941 -3.199892044473183", // Edinburgh Castle
                CONSUMER_PASSWORD
        ));
    }



    protected static Booking createConsumerAndBookFirstEvent(Controller controller, int numTicketsRequested) {
        createConsumer(controller);

        ListEventsCommand eventsCmd = new ListEventsCommand(false, false, null);
        controller.runCommand(eventsCmd);
        List<Event> events = eventsCmd.getResult();

        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookCmd = new BookEventCommand(
                firstEventNumber, numTicketsRequested
        );
        controller.runCommand(bookCmd);
        return bookCmd.getResult();
    }
    public static void createConsumerAndBookEvent(Context testContext,Controller controller,String name,String email,Event event){
        Consumer consumer = new Consumer(name,email,
                "09999999999","55.94872684464941 -3.199892044473183", // Edinburgh Castle
                "secure password");
        testContext.getUserState().addUser(consumer);
        controller.runCommand(new LoginCommand(consumer.getEmail(), "secure password"));
        Booking booking = testContext.getBookingState().createBooking(consumer,event,2);
        consumer.addBooking(booking);
    }

    protected static List<Event> getAllEvents(Controller controller) {
        ListEventsCommand eventsCmd = new ListEventsCommand(false, false, null);
        controller.runCommand(eventsCmd);
        return eventsCmd.getResult();
    }

    protected static List<Event> getUserEvents(Controller controller) {
        ListEventsCommand eventsCmd = new ListEventsCommand(true, false, null);
        controller.runCommand(eventsCmd);
        return eventsCmd.getResult();
    }

    protected static void createMultipleEvents(Controller controller){
        controller.runCommand(new CreateEventCommand(
                "Event1",
                EventType.Music,
                100,
                200,
                "55.94747223411703 -3.187300017491497",
                "Event1 is a music event",
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(11),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        ));

        controller.runCommand(new CreateEventCommand(
                "Event2",
                EventType.Music,
                100,
                200,
                "55.94747223411703 -3.187300017491497",
                "Event1 is a music event",
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(11),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        ));
    }

    protected static void listEventsCommand(Controller controller, boolean userEventsOnly, boolean activeEventsOnly, LocalDate searchDate) {
        controller.runCommand(new ListEventsCommand(
                userEventsOnly,
                activeEventsOnly,
                searchDate
        ));
    }

    protected static void registerPawsForAwwws(Controller controller) {
        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
    }

    protected static Event createEvent2(Controller controller,
                                      LocalDateTime startDateTime,
                                      LocalDateTime endDateTime) {
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.94368888764689 -3.1888246174917114",
                "Come and enjoy some pets for pets",
                startDateTime,
                endDateTime,
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")
        );
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    protected static void addEventTag(Controller controller, String tagName, Set<String> tagValues, String defaultValue) {
        controller.runCommand(new AddEventTagCommand(
                tagName,
                tagValues,
                defaultValue
        ));
    }

}
