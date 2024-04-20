package command;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import controller.Context;
import model.Consumer;
import model.Event;
import model.TransportMode;
import model.User;
import view.IView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * {@link GetEventDirectionsCommand} allows users to find the fastest set of directions to arrive at a given event.
 * the first {@param directionsResult} element indicates total path distance; the remaining elements give step-by-step
 * directions, including distance for each step.
 */

public class GetEventDirectionsCommand implements ICommand<String[]> {
    private String[] directionsResult;
    private final long eventNumber;
    private final TransportMode transportMode;


    /**
     *
     * @param eventNumber identifier of a given event
     * @param transportMode method of arrival to event
     */
    public GetEventDirectionsCommand(long eventNumber, TransportMode transportMode) {
        this.eventNumber = eventNumber;
        this.transportMode = transportMode;
    }

    /**
     *
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that there is an event corresponding to the provided eventNumber.
     * @verifies.that the event includes a venueAddress.
     * @verifies.that the current {@link User} is a {@link Consumer}
     * @verifies.that the {@link Consumer}'s profile includes an address
     */
    public void execute(Context context, IView view){
        Event event = context.getEventState().findEventByNumber(eventNumber);
        // verify event not null
        if (event == null){
            view.displayFailure(
                 "GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
                    Map.of("eventNumber",eventNumber)
            );
            directionsResult = null;
            return;
        }

        if (event.getVenueAddress() == null || event.getVenueAddress().isBlank()){
            view.displayFailure(
                    "GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS,
                    Map.of("event",event)
            );
            directionsResult = null;
            return;
        }

        // venue address check
        // user is consumer
        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Consumer)){
            view.displayFailure(
                    "GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER,
                    Map.of("currentUser", String.valueOf(currentUser))
            );
            directionsResult = null;
            return;
        }

        Consumer consumer = (Consumer) currentUser;
        if (consumer.getAddress() == null || consumer.getAddress().isBlank()){
            view.displayFailure(
                    "GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS,
                    Map.of("consumer", consumer)
            );
            directionsResult = null;
            return;
        }
        
        ResponsePath path = context.getMapSystem().routeBetweenPoints(
                transportMode,
                context.getMapSystem().convertToCoordinates(consumer.getAddress()),
                context.getMapSystem().convertToCoordinates(event.getVenueAddress()));

        InstructionList il = path.getInstructions();
        Translation tr = context.getMapSystem().getTranslation();

        ArrayList<String> directionsArray = new ArrayList<>();
        for (Instruction instruction : il) {
            directionsArray.add("distance " + instruction.getDistance() + " for instruction: " + instruction.getTurnDescription(tr));
        }
        directionsResult = directionsArray.toArray(new String[0]);
        view.displaySuccess(
                "GetEventDirectionsCommand",
                LogStatus.GET_EVENT_DIRECTIONS_SUCCESS,
                Map.of("event", event,
                "consumer", consumer,
                "directions", Arrays.toString(directionsResult)
                )
        );

        // iterate over all turn instructions
        // correct the loading into string array along with formatting and checks
    }

    public String[] getResult(){
        return this.directionsResult;
    }

    private enum LogStatus {
        GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
        GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS,
        GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER,
        GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS,
        GET_EVENT_DIRECTIONS_SUCCESS
    }
}
