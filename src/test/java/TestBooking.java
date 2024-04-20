import model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Booking class
 */
public class TestBooking {
    @Test
    void testCancelByConsumer(){
        Consumer testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzen, China",
                "123456"
        );

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

        Event newEvent2 = new Event(
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

        Booking newBooking = new Booking(
                10292,
                testUser,
                newEvent,
                2,
                LocalDateTime.now()
        );

        Booking newBooking2 = new Booking(
                10292,
                testUser,
                newEvent2,
                2,
                LocalDateTime.now()
        );

        testUser.addBooking(newBooking);
        testUser.addBooking(newBooking2);
        assertNotEquals(BookingStatus.CancelledByConsumer, newBooking.getStatus());
        assertNotEquals(BookingStatus.CancelledByConsumer, newBooking2.getStatus());
        newBooking.cancelByConsumer();
        newBooking2.cancelByConsumer();
        assertEquals(BookingStatus.CancelledByConsumer, newBooking.getStatus());
        assertEquals(BookingStatus.CancelledByConsumer, newBooking2.getStatus());
    }

    @Test
    void testCancelByProvider(){
        Consumer testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzen, China",
                "123456"
        );

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

        Event newEvent2 = new Event(
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

        Booking newBooking = new Booking(
                10292,
                testUser,
                newEvent,
                2,
                LocalDateTime.now()
        );

        Booking newBooking2 = new Booking(
                10292,
                testUser,
                newEvent2,
                2,
                LocalDateTime.now()
        );

        testUser.addBooking(newBooking);
        testUser.addBooking(newBooking2);
        assertNotEquals(BookingStatus.CancelledByProvider, newBooking.getStatus());
        assertNotEquals(BookingStatus.CancelledByProvider, newBooking2.getStatus());
        newBooking.cancelByProvider();
        newBooking2.cancelByProvider();
        assertEquals(BookingStatus.CancelledByProvider, newBooking.getStatus());
        assertEquals(BookingStatus.CancelledByProvider, newBooking2.getStatus());
    }

    @Test
    void toStringBooking(){
        Consumer testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzen, China",
                "123456"
        );

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

        Booking newBooking = new Booking(
                10292,
                testUser,
                newEvent,
                2,
                LocalDateTime.now()
        );

        assertEquals("Booking{" +
                "status=" + newBooking.getStatus() +
                ", bookingNumber=" + newBooking.getBookingNumber() +
                ", booker=" + newBooking.getBooker().getName() +
                ", event=" + newBooking.getEvent() +
                ", numTickets=" + newBooking.getNumTickets() +
                ", bookingDateTime=" + newBooking.getBookingDateTime() +
                '}',
                newBooking.toString());
        assertNotEquals("random text", newBooking.toString());
        assertNotEquals(192737, newBooking.toString());
        assertNotEquals(false, newBooking.toString());

    }
}
