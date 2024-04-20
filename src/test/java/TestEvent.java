import model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Event class
 */

public class TestEvent {

    @Test
    void eventCancelled()
    {
        Event newEvent = new Event(
                1181828,
                "Test Event",
                EventType.Music,
                818181,
                191991,
                "alalsls",
                "jjjsjs",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")


        );

        newEvent.cancel();
        assertEquals(EventStatus.CANCELLED, newEvent.getStatus());
    }

    @Test
    void eventReview()
    {
        Event newEvent = new Event(
                1181828,
                "Test Event",
                EventType.Music,
                818181,
                191991,
                "alalsls",
                "jjjsjs",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")


        );

        Consumer testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzen, China",
                "123456"
        );

        Review newReview = new Review(testUser, newEvent, LocalDateTime.now(), "mince");

        newEvent.addReview(newReview);
        boolean reviewFound = false;
        for (int j = 0; j < newEvent.getReviews().size(); j++)
        {
            if (newEvent.getReviews().get(j).equals(newReview)) {
                reviewFound = true;
                break;
            }
        }
        assertTrue(reviewFound);
    }

    @Test
    void toStringEvent() {

        Event newEvent = new Event(
                1181828,
                "Test Event",
                EventType.Music,
                818181,
                191991,
                "alalsls",
                "jjjsjs",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")


        );

        assertEquals("Event{" +
                        "eventNumber=" + newEvent.getEventNumber() +
                        ", title='" + newEvent.getTitle() + '\'' +
                        ", type=" + newEvent.getType() +
                        ", numTicketsCap=" + newEvent.getNumTicketsCap() +
                        ", ticketPriceInPence=" + newEvent.getTicketPriceInPence() +
                        ", venueAddress='" + newEvent.getVenueAddress() + '\'' +
                        ", description='" + newEvent.getDescription() + '\'' +
                        ", startDateTime=" + newEvent.getStartDateTime() +
                        ", endDateTime=" + newEvent.getEndDateTime() +
                        ", tags=" + newEvent.getTags() +
                        ", reviews=" + newEvent.getReviews() +
                        ", status=" + newEvent.getStatus() +
                        ", numTicketsLeft=" + newEvent.getNumTicketsLeft() +
                        '}',
                newEvent.toString());
        assertNotEquals("random text", newEvent.toString());
        assertNotEquals(192737, newEvent.toString());
        assertNotEquals(false, newEvent.toString());

    }


}
