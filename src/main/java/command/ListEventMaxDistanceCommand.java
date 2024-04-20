package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.graphhopper.ResponsePath;


/**
 * {@link ListEventMaxDistanceCommand} allows logged-in {@link Consumer}s to list events with a limit on distance
 * which they are willing to travel in a given mode of transport.
 */

public class ListEventMaxDistanceCommand extends ListEventsCommand{

    private final TransportMode transportMode;
    private final double maxDistance;
    /**
     * @param userEventsOnly   if true, the returned events will be filtered depending on the logged-in user:
     *                         for {@link Staff}s only the {@link Event}s they have created,
     *                         and for {@link Consumer}s only the {@link Event}s that match their {@link EventTagCollection}
     * @param activeEventsOnly if true, returned {@link Event}s will be filtered to contain only {@link Event}s with
     *                         {@link EventStatus#ACTIVE}
     * @param searchDate       chosen date to look for events. Can be null. If not null, only {@link Event}s that are
     *                         happening on {@link #searchDate} (i.e., starting, ending, or in between) will be included
     * @param transportMode    user selected choice of transport
     * @param maxDistance      furthest distance that the user is willing to travel
     */

    public ListEventMaxDistanceCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDate searchDate, TransportMode transportMode, double maxDistance) {
        super(userEventsOnly, activeEventsOnly, searchDate);
        this.transportMode = transportMode;
        this.maxDistance = maxDistance;
    }

    //Checks whether a specific event is within the range the user has inputted
    private Double eventDistance(Context context, Event event, TransportMode transportMode){
        Consumer consumer = (Consumer) context.getUserState().getCurrentUser();
        String eventAddress = event.getVenueAddress();
        if (!(eventAddress == null || eventAddress.isEmpty())) {
            ResponsePath path = context.getMapSystem().routeBetweenPoints(
                    transportMode,
                    context.getMapSystem().convertToCoordinates(consumer.getAddress()),
                    context.getMapSystem().convertToCoordinates(eventAddress)
            );

            return path.getDistance();
        }
        else{
            return maxDistance + 1;
        }
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a {@link Consumer}
     * @verifies.that current user has an address set up in their profile
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Consumer)) {
            view.displayFailure(
                    "CreateEventCommand",
                    ListEventMaxDistanceCommand.LogStatus.LIST_EVENT_DISTANCE_USER_NOT_CONSUMER,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            eventListResult = null;
            return;
        }
        Consumer consumer = (Consumer) currentUser;
        String consumerAddress = consumer.getAddress();
        if(consumerAddress == null || consumerAddress.isBlank()){
            view.displayFailure(
                    "ListEventMaxDistanceCommand",
                    LogStatus.LIST_EVENT_DISTANCE_CONSUMER_NO_ADDRESS,
                    Map.of("consumerAddress", consumerAddress)

            );
            eventListResult = null;
            return;
        }


        List<Event> eventsFittingPreferences = context.getEventState().getAllEvents().stream()
                .filter(event -> eventSatisfiesPreferences(context.getEventState().getPossibleTags(), consumer.getPreferences(), event))
                .filter(event -> eventDistance(context, event, transportMode)<= maxDistance)
                .sorted(Comparator.comparingDouble(event -> eventDistance(context, event, transportMode)))
                .collect(Collectors.toList());

        eventListResult = filterEvents(eventsFittingPreferences, activeEventsOnly, searchDate);
        view.displaySuccess(
                "ListEventMaxDistanceCommand",
                LogStatus.LIST_EVENT_DISTANCE_SUCCESS,
                Map.of("userEventsOnly", userEventsOnly,
                        "activeEventsOnly", activeEventsOnly,
                        "searchDate", searchDate,
                        "transportMode", transportMode,
                        "maxDistance", maxDistance,
                        "eventListResult", eventListResult
                )
        );
        }

    public List<Event> getResult() {
        return eventListResult;
    }

    private enum LogStatus {
        LIST_EVENT_DISTANCE_USER_NOT_CONSUMER,
        LIST_EVENT_DISTANCE_CONSUMER_NO_ADDRESS,
        LIST_EVENT_DISTANCE_SUCCESS

    }
}
