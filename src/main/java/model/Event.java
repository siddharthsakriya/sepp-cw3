package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * {@link Event} represents an event that can be booked by {@link Consumer}s. Tickets can be free, but they are
 * required to attend, and there is a maximum cap on the number of tickets that can be booked.
 */
public class Event implements Serializable {
    private final long eventNumber;
    private final String title;
    private final EventType type;
    private final int numTicketsCap;
    private final int ticketPriceInPence;
    private final String venueAddress;
    private final String description;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final EventTagCollection tags;
    private final List<Review> reviews;
    private EventStatus status;
    private int numTicketsLeft;

    /**
     * Create a new Event with status = {@link EventStatus#ACTIVE}
     *
     * @param eventNumber         unique event identifier
     * @param title               name of the event
     * @param type                type of the event
     * @param numTicketsCap       maximum number of tickets, initially all available for booking
     * @param ticketPriceInPence  price of each ticket in GBP pence
     * @param venueAddress        address where the performance will be taking place
     * @param description         additional details about the event, e.g., who the performers in a concert will be
     *                            or if payment is required on entry in addition to ticket booking
     * @param startDateTime       date and time when the performance will begin
     * @param endDateTime         date and time when the performance will end
     * @param tags                tags for event, of type EventCollection
     */
    public Event(long eventNumber,
                 String title,
                 EventType type,
                 int numTicketsCap,
                 int ticketPriceInPence,
                 String venueAddress,
                 String description,
                 LocalDateTime startDateTime,
                 LocalDateTime endDateTime,
                 EventTagCollection tags) {
        this.eventNumber = eventNumber;
        this.title = title;
        this.type = type;
        this.numTicketsCap = numTicketsCap;
        this.ticketPriceInPence = ticketPriceInPence;
        this.venueAddress = venueAddress;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.tags = tags;
        this.reviews = new ArrayList<Review>();
        this.status = EventStatus.ACTIVE;
        this.numTicketsLeft = numTicketsCap;
    }

    /**
     * @return Number of the maximum cap of tickets which were initially available
     */
    public int getNumTicketsCap() {
        return numTicketsCap;
    }

    public int getNumTicketsLeft() {
        return numTicketsLeft;
    }

    public void setNumTicketsLeft(int numTicketsLeft) {
        this.numTicketsLeft = numTicketsLeft;
    }

    public int getTicketPriceInPence() {
        return ticketPriceInPence;
    }

    public String getVenueAddress() { return venueAddress; }

    public long getEventNumber() {
        return eventNumber;
    }

    public String getTitle() {
        return title;
    }

    public EventType getType() {
        return type;
    }

    public String getDescription() {return description;}

    public EventStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public EventTagCollection getTags() { return tags; }

    public  List<Review> getReviews() { return reviews;}

    /**
     * Set {@link #status} to {@link EventStatus#CANCELLED}
     */
    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }

    /**
     * Add a review to reviews
     * @param review
     */
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventNumber=" + eventNumber +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", numTicketsCap=" + numTicketsCap +
                ", ticketPriceInPence=" + ticketPriceInPence +
                ", venueAddress='" + venueAddress + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", tags=" + tags +
                ", reviews=" + reviews +
                ", status=" + status +
                ", numTicketsLeft=" + numTicketsLeft +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventNumber == event.eventNumber && numTicketsCap == event.numTicketsCap && ticketPriceInPence == event.ticketPriceInPence && numTicketsLeft == event.numTicketsLeft && Objects.equals(title, event.title) && type == event.type && Objects.equals(venueAddress, event.venueAddress) && Objects.equals(description, event.description) && Objects.equals(startDateTime, event.startDateTime) && Objects.equals(endDateTime, event.endDateTime) && Objects.equals(tags.toString(), event.tags.toString()) && Objects.equals(reviews, event.reviews) && status == event.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventNumber, title, type, numTicketsCap, ticketPriceInPence, venueAddress, description, startDateTime, endDateTime, tags, reviews, status, numTicketsLeft);
    }
}
