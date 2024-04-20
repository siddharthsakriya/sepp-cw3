package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * {@link ReviewEventCommand} allows logged-in {@link Consumer}s to leave reviews on events that they have been to.
 */

public class ReviewEventCommand implements ICommand<Review> {
    private Review reviewResult;
    private final Long eventNumber;
    private final String content;

    /**
     *
     * @param eventNumber the identifier of the event to be reviewed
     * @param content the user's review
     */

    public ReviewEventCommand(Long eventNumber, String content){
        this.eventNumber = eventNumber;
        this.content = content;
    }

    /**
     *
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that an event exists with the corresponding eventNumber
     * @verifies.that the event is already over
     * @verifies.that the current user is a logged-in {@link Consumer}
     * @verifies.that the {@link Consumer} had at least 1 valid booking (not cancelled by the consumer)
     *                at the event
     */
    @Override
    public void execute(Context context, IView view) {
        //an event exists with the corresponding eventNumber
        Event event = context.getEventState().findEventByNumber(eventNumber);
        if (event == null){
            view.displayFailure(
                    "ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_EVENT_NOT_FOUND,
                    Map.of("eventNumber", eventNumber)
            );
            reviewResult = null;
            return;
        }

        //the event has not ended
        if (LocalDateTime.now().isBefore(event.getEndDateTime())){
            view.displayFailure(
                    "ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_EVENT_NOT_OVER,
                    Map.of("eventNumber", eventNumber,
                            "endDateTime", event.getEndDateTime()
                    )
            );
            reviewResult = null;
            return;
        }

        //the current user is a logged-in Consumer
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_USER_NOT_CONSUMER,
                    Map.of("currentUser", String.valueOf(currentUser))
            );
            reviewResult = null;
            return;
        }
        //the consumer had at least 1 valid booking (not cancelled by the consumer) at the event
        Consumer consumer = (Consumer) currentUser;
        List<Booking> Bookings = consumer.getBookings();
        boolean hasValidBooking = false;
        for (Booking booking: Bookings){
            if (booking.getEvent().equals(event) && (booking.getStatus() != BookingStatus.CancelledByConsumer)){
                hasValidBooking = true;
                break;
            }

        }

        if (!hasValidBooking){ //could be !consumer.hasActiveBooking(event)
            view.displayFailure("ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_NO_VALID_BOOKING,
                    Map.of("booking",consumer.getBookings())
            );
            reviewResult = null;
            return;
        }

        reviewResult = new Review((Consumer) currentUser, event, LocalDateTime.now(), content);
        event.addReview(reviewResult);
        view.displaySuccess(
                "ReviewEventCommand",
                LogStatus.REVIEW_EVENT_SUCCESS,
                Map.of("review", reviewResult)
        );
    }

    @Override
    public Review getResult() {
        return reviewResult;
    }

    private enum LogStatus {
        REVIEW_EVENT_EVENT_NOT_FOUND,
        REVIEW_EVENT_EVENT_NOT_OVER,
        REVIEW_EVENT_USER_NOT_CONSUMER,
        REVIEW_EVENT_NO_VALID_BOOKING,
        REVIEW_EVENT_SUCCESS

    }
}
