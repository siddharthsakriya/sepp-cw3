package command;

import controller.Context;
import model.*;
import view.IView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * {@link LoadAppStateCommand} allows staff users to load in an app state by providing the file name they wish to load
 */
public class LoadAppStateCommand implements ICommand<Boolean> {
    private final String filename;
    private Boolean successResult;
    FileInputStream fileIn;
    ObjectInputStream in;
    Context importContext;

    /**
     * @param filename        file name of the .ser file to be loaded
     */
    public LoadAppStateCommand(String filename) {
        this.filename = filename;
        this.successResult = null;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that staff is currently logged in
     * @verifies.that file exists
     * @verifies.that there are no tags with clashing names and varying tagValues
     * @verifies.that there are no Users with clashing emails and varying User details
     * @verifies.that there are no Event with clashing emails, clashing StartDate, clashing EndDate, and varying Event details
     * @verifies.that there are no Bookings with clashing Booker, clashing Event, and clashing BookingDate
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LogStatus.LOAD_APP_STATE_USER_NOT_STAFF,
                    Map.of("currentUser", currentUser,
                            "filename", filename,
                            "successResult", successResult = false
                    )
            );
            successResult = false;
            return;
        }

        //Deserializing file
        try {
            fileIn = new FileInputStream(filename);
            in = new ObjectInputStream(fileIn);

            importContext = (Context) in.readObject();
        }
        catch (FileNotFoundException e) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LogStatus.LOAD_APP_STATE_FILE_NOT_FOUND,
                    Map.of("filename", filename,
                            "successResult", successResult = false
                    )
            );
            return;
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            in.close();
            fileIn.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Verification Stage of the execute method.
        Map<String, EventTag> importTags = importContext.getEventState().getPossibleTags();
        Map<String, EventTag> currentTags = context.getEventState().getPossibleTags();

        for ( String tagName : importTags.keySet()) {
            if (currentTags.containsKey(tagName)){
                if (!(Objects.equals(currentTags.get(tagName).values, importTags.get(tagName).values) && Objects.equals(currentTags.get(tagName).defaultValue, importTags.get(tagName).defaultValue))){
                    view.displayFailure(
                            "LoadAppStateCommand",
                            LogStatus.LOAD_APP_STATE_CLASHING_TAGS,
                            Map.of("filename", filename,
                                    "successResult", successResult = false
                            )
                    );
                    return;
                }
            }
        }

        Map<String, User> importUsers = importContext.getUserState().getAllUsers();
        Map<String, User> currUsers = context.getUserState().getAllUsers();
        for (String email : importUsers.keySet()) {
            if (context.getUserState().getAllUsers().containsKey(email) ){

                if (currUsers.get(email) instanceof Consumer && importUsers.get(email)instanceof Consumer ){
                    Consumer currUser = (Consumer) currUsers.get(email);
                    Consumer impUser = (Consumer)  importUsers.get(email);

                    if (!currUser.equals(impUser)){
                        view.displayFailure(
                                "LoadAppStateCommand",
                                LogStatus.LOAD_APP_STATE_CLASHING_EMAIL,
                                Map.of("filename", filename,
                                        "successResult", successResult = false
                                )
                        );
                        return;
                    }
                }

                else if (!(currUsers.get(email) instanceof Staff && importUsers.get(email)instanceof Staff)){
                    view.displayFailure(
                            "LoadAppStateCommand",
                            LogStatus.LOAD_APP_STATE_CLASHING_EMAIL,
                            Map.of("filename", filename,
                                    "successResult", successResult = false
                            )
                    );
                    return;
                }
            }
        }

        List<Event> importEvents = importContext.getEventState().getAllEvents();
        List<Event> currentEvents = context.getEventState().getAllEvents();
        for ( Event event : importEvents) {

            Stream<Event> filteredEvents = currentEvents.stream();
            filteredEvents = filteredEvents.filter(currentEvent -> currentEvent.getTitle().equals(event.getTitle())
                    && currentEvent.getStartDateTime().equals(event.getStartDateTime())
                    && currentEvent.getEndDateTime().equals(event.getEndDateTime()));

            boolean isEventTitleAndTimeClash = filteredEvents.anyMatch(filteredEvent -> !filteredEvent.equals(event));

            if (isEventTitleAndTimeClash)
            {
                view.displayFailure(
                        "LoadAppStateCommand",
                        LogStatus.LOAD_APP_STATE_CLASHING_EVENTS,
                        Map.of("filename", filename,
                                "successResult", successResult = false
                        )
                );
                return;
            }
        }

        List<Booking> importBookings = importContext.getBookingState().getAllBookings();
        List<Booking> currentBookings = context.getBookingState().getAllBookings();
        for (Booking booking : importBookings) {
            boolean clashingBookings = currentBookings.stream().anyMatch(currentBooking -> currentBooking.getEvent().equals(booking.getEvent())
                    && currentBooking.getBooker().equals(booking.getBooker())
                    && currentBooking.getBookingDateTime().equals(booking.getBookingDateTime())
                    );

            if (clashingBookings) {
                view.displayFailure(
                        "LoadAppStateCommand",
                        LogStatus.LOAD_APP_STATE_CLASHING_BOOKINGS,
                        Map.of("filename", filename,
                                "successResult", successResult = false
                        )
                );
                return;
            }
        }

        //Verification has passed at this point. Execute now loads imported variables to file.
        for ( String tag : importTags.keySet()) {
            if (!currentTags.containsKey(tag)){
                EventTag eventTag = importTags.get(tag);
                context.getEventState().createEventTag(tag,eventTag.values, eventTag.defaultValue);
            }

        }

        for (String userEmail : importUsers.keySet()) {
            if (!context.getUserState().getAllUsers().containsKey(userEmail)) {
                User user = importUsers.get(userEmail);
                context.getUserState().addUser(user);
            }
        }

        for (Event event : importEvents) {
            if (!context.getEventState().getAllEvents().contains(event))
            context.getEventState().createEvent(
                    event.getTitle(),
                    event.getType(),
                    event.getNumTicketsCap(),
                    event.getTicketPriceInPence(),
                    event.getVenueAddress(),
                    event.getDescription(),
                    event.getStartDateTime(),
                    event.getEndDateTime(),
                    event.getTags());
        }

        for (Booking booking : importBookings) {
            context.getBookingState().addBooking(booking);
        }

        view.displaySuccess(
                "SaveAppStateCommand",
                LogStatus.LOAD_APP_STATE_SUCCESS,
                Map.of("currentUser", currentUser,
                        "filename", filename,
                        "successResult", successResult = true
                )
        );

    }


    @Override
    public Boolean getResult() {
        return successResult;
    }

    private enum LogStatus {
        LOAD_APP_STATE_USER_NOT_STAFF,
        LOAD_APP_STATE_FILE_NOT_FOUND,
        LOAD_APP_STATE_CLASHING_TAGS,
        LOAD_APP_STATE_CLASHING_EMAIL,
        LOAD_APP_STATE_CLASHING_EVENTS,
        LOAD_APP_STATE_CLASHING_BOOKINGS,
        LOAD_APP_STATE_SUCCESS

    }
}