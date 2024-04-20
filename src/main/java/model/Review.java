package model;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * {@link Review} is an implementation of a system for {@link Consumer}s to leave reviews on {@link Event}s
 * that they have been to.
 */
public class Review implements Serializable {
    private final Consumer author;
    private final Event event;
    private final LocalDateTime creationDateTime;
    private final String content;

    /**
     *
     * @param author the {@link Consumer} who has created review
     * @param event the {@link Event} this review is for
     * @param creationDateTime the date and time when this review was made
     * @param content the content of the review
     */
    public Review(Consumer author, Event event, LocalDateTime creationDateTime, String content) {
        this.author = author;
        this.event = event;
        this.creationDateTime = creationDateTime;
        this.content = content;
    }

    public Consumer getAuthor() {
        return author;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public String getContent() {
        return content;
    }

}
