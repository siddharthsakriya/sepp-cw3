package command;

import controller.Context;
import model.*;
import view.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link ListEventReviewsCommand} allows {@link User}s to read reviews of a given event.
 */

public class ListEventReviewsCommand implements ICommand<List<Review>> {

    private final List<Review> reviewsResult;
    private final String eventTitle;

    /**
     *
     * @param eventTitle title of the event to list the reviews of.
     */
    public ListEventReviewsCommand(String eventTitle) {
            this.reviewsResult = new ArrayList<>();
            this.eventTitle = eventTitle;
    }

    /**
     *
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     */
    @Override
    public void execute(Context context, IView view) {
        List<Event> events = context.getEventState()
                            .getAllEvents()
                            .stream()
                            .filter(event -> event.getTitle().equals(eventTitle))
                            .collect(Collectors.toList());

        for (Event event:events) {
            reviewsResult.addAll(event.getReviews());
        }

        view.displaySuccess("ListEventReviewsCommand",
                            LogStatus.LIST_EVENT_REVIEWS_SUCCESS,
                            Map.of("reviewsResult", reviewsResult));
    }



    @Override
    public List<Review> getResult() {
        return reviewsResult;
    }

    private enum LogStatus {
        LIST_EVENT_REVIEWS_SUCCESS

    }
}
