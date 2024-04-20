package state;

import model.Event;
import model.EventTag;
import model.EventTagCollection;
import model.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


/**
 * {@link EventState} is a concrete implementation of {@link IEventState}.
 */
public class EventState implements IEventState, Serializable {
    private final List<Event> events;
    private long nextEventNumber;
    private final Map<String, EventTag> possibleTags ;

    /**
     * Create a new EventState with an empty list of events, which keeps track of the next event and performance numbers
     * it will generate, starting from 1 and incrementing by 1 each time when requested
     */
    public EventState() {
        events = new LinkedList<>();
        nextEventNumber = 1;
        possibleTags = new HashMap<String, EventTag>();
        Set<String> trueFalseSet = new HashSet<String>() ;
        trueFalseSet.add("true");
        trueFalseSet.add("false");
        Set<String> capacitySet = new HashSet<String>() ;
        capacitySet.add("<20");
        capacitySet.add("20-100");
        capacitySet.add("100-200");
        capacitySet.add("200");
        createEventTag("hasSocialDistancing", trueFalseSet, "false");
        createEventTag("hasAirFiltration", trueFalseSet, "false");
        createEventTag("venueCapacity", capacitySet, "<20");


    }

    /**
     * Copy constructor to make a deep copy of another EventState instance
     *
     * @param other instance to copy
     */
    public EventState(IEventState other) {
        EventState otherImpl = (EventState) other;
        events = new LinkedList<>(otherImpl.events);
        nextEventNumber = otherImpl.nextEventNumber;
        possibleTags = new HashMap<String, EventTag>(otherImpl.possibleTags);

    }

    @Override
    public List<Event> getAllEvents() {
        return events;
    }

    @Override
    public Event findEventByNumber(long eventNumber) {
        return events.stream()
                .filter(event -> event.getEventNumber() == eventNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Event createEvent(String title,
                             EventType type,
                             int numTickets,
                             int ticketPriceInPence,
                             String venueAddress,
                             String description,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             EventTagCollection tags) {
        long eventNumber = nextEventNumber;
        nextEventNumber++;

        Event event = new Event(eventNumber, title, type, numTickets,
                ticketPriceInPence, venueAddress, description, startDateTime,
                endDateTime, tags);
        events.add(event);
        return event;
    }

    public Map<String, EventTag> getPossibleTags() { return possibleTags; }

    /**
     *
     * @param tagName is the name of the tag
     * @param tagValues are possible values that the tag can take
     * @param defaultTagValue is the default value of the tag
     * @return
     */
    public EventTag createEventTag(String tagName, Set<String> tagValues, String defaultTagValue){
        EventTag newEventTag = new EventTag(tagValues, defaultTagValue);
        //added an if condition into event state?
        possibleTags.put(tagName, newEventTag);
        return newEventTag;
    }

    /*@Override
    public Boolean areTagsValid(EventTagCollection tags) {
        for ( String tagName : tags.getTagNames()) {
            if (!(possibleTags.containsKey(tagName))) {
                return false;
            }
            else if (!(possibleTags.get(tagName).values.contains(tags.getValueFor(tagName)))) {
                return false;
            }
        }
        return true;
    }*/ //could be useful in CreateEventCommand
}
