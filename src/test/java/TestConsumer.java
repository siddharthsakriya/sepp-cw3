import model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Consumer class
 */
public class TestConsumer{

    @Test
    void samePasswordMatches() {
        User testUser = new Consumer(
                "Amy McDonald",
                "test@mail.com",
                "004499",
                "Stockbridge, Edinburgh",
                "123456"
        );
        assertTrue(testUser.checkPasswordMatch("123456"));
    }

    @Test
    void differentPasswordDoesNotMatch() {
        User testUser = new Consumer(
                "Super Woman",
                "test@mail.com",
                "004499",
                "Washington DC, USA",
                "123456"
        );
        assertFalse(testUser.checkPasswordMatch("12345"));
        assertFalse(testUser.checkPasswordMatch("1234567"));
        assertFalse(testUser.checkPasswordMatch(""));
        assertFalse(testUser.checkPasswordMatch("password"));
        assertFalse(testUser.checkPasswordMatch("admin"));
        assertFalse(testUser.checkPasswordMatch("123456789"));
    }

    @Test
    void passwordIsNotLoggedInPlaintext() {
        User testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzhen, China",
                "123456"
        );
        assertFalse(testUser.toString().contains("123456"));
    }

    @Test
    void passwordUpdateMatchSuccess(){
        User testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzhen, China",
                "123456"
        );
        testUser.updatePassword("newPassword");
        assertTrue(testUser.checkPasswordMatch("newPassword"));
        assertFalse(testUser.checkPasswordMatch("123456"));
        assertFalse(testUser.checkPasswordMatch("wrongpassword"));
        assertFalse(testUser.checkPasswordMatch("anotherwrongpassword"));
    }

    @Test
    void updatedPasswordIsNotLoggedInPlaintext() {
        User testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzhen, China",
                "123456"
        );

        testUser.updatePassword("newPassword");
        assertFalse(testUser.toString().contains("newPassword"));


    }
    @Test
    void hasActiveBookingCheck(){
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
                12772,
                LocalDateTime.now()
        );

        Booking newBooking2 = new Booking(
                10292,
                testUser,
                newEvent2,
                12772,
                LocalDateTime.now()
        );

        List<Booking> Bookings = testUser.getBookings();
        boolean hasValidBooking1 = false;
        for (Booking booking: Bookings){
            if (booking.getEvent().equals(newEvent) && (booking.getStatus() != BookingStatus.CancelledByConsumer)){
                hasValidBooking1 = true;
                break;
            }

        }
        boolean hasValidBooking2 = false;
        for (Booking booking: Bookings){
            if (booking.getEvent().equals(newEvent2) && (booking.getStatus() != BookingStatus.CancelledByConsumer)){
                hasValidBooking2 = true;
                break;
            }

        }

        assertFalse(hasValidBooking1);
        assertFalse(hasValidBooking2);
        testUser.addBooking(newBooking);
        testUser.addBooking(newBooking2);

        for (Booking booking: Bookings){
            if (booking.getEvent().equals(newEvent) && (booking.getStatus() != BookingStatus.CancelledByConsumer)){
                hasValidBooking1 = true;
                break;
            }

        }
        for (Booking booking: Bookings){
            if (booking.getEvent().equals(newEvent2) && (booking.getStatus() != BookingStatus.CancelledByConsumer)){
                hasValidBooking2 = true;
                break;
            }

        }
        assertTrue(hasValidBooking1);
        assertTrue(hasValidBooking2);

        //could use hasActiveBooking here

    }

    @Test
    void toStringConsumer(){
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
                12772,
                LocalDateTime.now()
        );

        Booking newBooking2 = new Booking(
                10292,
                testUser,
                newEvent2,
                12772,
                LocalDateTime.now()
        );
        testUser.addBooking(newBooking);
        testUser.addBooking(newBooking2);


        assertEquals("Consumer{" +
                "bookings=" + testUser.getBookings() +
                ", name='" + testUser.getName() + '\'' +
                ", phoneNumber='" + testUser.getPhoneNumber() + '\'' +
                ", address='" + testUser.getAddress() + '\'' +
                ", preferences=" + testUser.getPreferences() +
                '}',
                testUser.toString());

        assertNotEquals("Consumer{" +
                        "bookings=" + testUser.getBookings() +
                        ", name='" + testUser.getName() + '\'' +
                        ", phoneNumber='" + testUser.getPhoneNumber() + '\'' +
                        ", address='" + "188288282" + '\'' +
                        ", preferences=" + testUser.getPreferences() +
                        '}',
                testUser.toString());

        assertNotEquals("Consumer{" +
                        "bookings=" + testUser.getBookings() +
                        ", name='" + "wrongname" + '\'' +
                        ", phoneNumber='" + testUser.getPhoneNumber() + '\'' +
                        ", address='" + testUser.getAddress() + '\'' +
                        ", preferences=" + testUser.getPreferences() +
                        '}',
                testUser.toString());

        assertNotEquals(1233, testUser.toString());
        assertNotEquals(8.88811, testUser.toString());
    }
}
