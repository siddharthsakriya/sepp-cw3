package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * {@link CreateEventCommand} allows {@link Staff}s to create new {@link Event}s.
 * The command applies for the currently logged-in user.
 */
public class CreateEventCommand implements ICommand<Event> {
    private final String title;
    private final EventType type;
    private final int numTickets;
    private final int ticketPriceInPence;
    private final String venueAddress;
    private final String description;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final EventTagCollection tags;
    private Event eventResult;

    /**
     * @param title               title of the event
     * @param type                type of the event
     * @param numTickets          number of initially available tickets for the event. This can be 0 if the event does
     *                            not need booking.
     * @param ticketPriceInPence  price in GBP pence per event ticket. This can be 0 if the event is free.
     * @param venueAddress        indicates where this performance will take place, would be displayed to users in app
     * @param description         additional details about the event
     * @param startDateTime       indicates the date and time when this performance is due to start
     * @param endDateTime         indicates the date and time when this performance is due to end
     * @param tags                indicates tags associated to the events

     */
    public CreateEventCommand(String title,
                              EventType type,
                              int numTickets,
                              int ticketPriceInPence,
                              String venueAddress,
                              String description,
                              LocalDateTime startDateTime,
                              LocalDateTime endDateTime,
                              EventTagCollection tags) {
        this.title = title;
        this.type = type;
        this.numTickets = numTickets;
        this.ticketPriceInPence = ticketPriceInPence;
        this.venueAddress = venueAddress;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.tags = tags;
    }

    /**
     * @return event number corresponding to the created event if successful and null otherwise
     */
    @Override
    public Event getResult() {
        return eventResult;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Staff member
     * @verifies.that event startDateTime is not after endDateTime
     * @verifies.that event startDateTime is in the future
     * @verifies.that no other event with the same title has the same startDateTime and endDateTime
     * @verifies.that venueAddress is acceptable
     * @verifies.that the event ticket price is non-negative
     * @verifies.that event tags, if provided, only include known tag names and values
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "CreateEventCommand",
                    CreateEventCommand.LogStatus.CREATE_EVENT_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            eventResult = null;
            return;
        }

        if (startDateTime.isAfter(endDateTime)) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_START_AFTER_END,
                    Map.of("startDateTime", startDateTime,
                            "endDateTime", endDateTime)
            );
            eventResult = null;
            return;
        }

        if (startDateTime.isBefore(LocalDateTime.now())) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_IN_THE_PAST,
                    Map.of("startDateTime", startDateTime)
            );
            eventResult = null;
            return;
        }

        // Use case 3.6:
        // If all the fields were correctly provided and an event with the
        // same name did not already exist for some or all of the same dates and times
        boolean isEventTitleAndTimeClash = context.getEventState().getAllEvents().stream()
                .anyMatch(otherEvent -> otherEvent.getTitle().equals(title)
                        && otherEvent.getStartDateTime().equals(startDateTime)
                        && otherEvent.getEndDateTime().equals(endDateTime)
                );
        if (isEventTitleAndTimeClash) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_TITLE_AND_TIME_CLASH,
                    Map.of("title", title,
                            "startDateTime", startDateTime,
                            "endDateTime", endDateTime)
            );
            eventResult = null;
            return;
        }

        if (!venueAddress.isBlank()) {
            boolean isAddrValid = true;
            String[] addressSplit = venueAddress.split("\\s+");

            if (addressSplit.length != 2) {
                isAddrValid = false;
            }
            try {
                double lat = Double.parseDouble(addressSplit[0]);
                double lng = Double.parseDouble(addressSplit[1]);
            }
            catch (NumberFormatException e) {
                // Input string contains non-numeric values
                isAddrValid = false;
            }
            if (!isAddrValid) { //could be !context.getMapSystem().isValidAddress(venueAddress)
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_NOT_LAT_LONG,
                        Map.of("Venue address", venueAddress)

                );
                return;
            }
            GHPoint coords = context.getMapSystem().convertToCoordinates(venueAddress);
            if (!context.getMapSystem().isPointWithinMapBounds(coords)) {
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_LAT_LONG_NOT_BOUNDS,
                        Map.of("address", venueAddress)
                );
                return;
            }
        }

        if (ticketPriceInPence < 0) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_NEGATIVE_TICKET_PRICE,
                    Map.of("ticketPriceInPence", ticketPriceInPence)
            );
            eventResult = null;
            return;
        }
        boolean areTagsValid = true;
        for ( String tagName : tags.getTagNames()) {
            if (!(context.getEventState().getPossibleTags().containsKey(tagName))) {
                areTagsValid = false;
            }
            else if (!(context.getEventState().getPossibleTags().get(tagName).values.contains(tags.getValueFor(tagName)))) {
                areTagsValid = false;
            }
        }

        if (!areTagsValid){ //could be !context.getEventState().areTagsValid(tags)
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_TAGS_DO_NOT_MATCH,
                    Map.of("tags", tags)
            );
            return;
        }

        Event event = context.getEventState().createEvent(title, type, numTickets,
                ticketPriceInPence, venueAddress, description,
                startDateTime, endDateTime, tags);
        view.displaySuccess(
                "CreateEventCommand",
                LogStatus.CREATE_EVENT_SUCCESS,
                Map.of("eventNumber", event.getEventNumber(),
                        "organiser", currentUser,
                        "title", title)
        );
        eventResult = event;
    }

    private enum LogStatus {
        CREATE_EVENT_USER_NOT_STAFF,
        CREATE_EVENT_START_AFTER_END,
        CREATE_EVENT_IN_THE_PAST,
        CREATE_EVENT_TITLE_AND_TIME_CLASH,
        CREATE_EVENT_NOT_LAT_LONG,
        CREATE_EVENT_LAT_LONG_NOT_BOUNDS,
        CREATE_EVENT_NEGATIVE_TICKET_PRICE,
        CREATE_EVENT_SUCCESS,
        CREATE_EVENT_TAGS_DO_NOT_MATCH
    }
}
