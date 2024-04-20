package command;

import controller.Context;
import model.*;
import view.IView;

import java.util.Map;
import java.util.Set;

/**
 * {@link AddEventTagCommand} allows {@link model.Staff Staff} to create tags for an
 * {@link Event}.
 */
public class AddEventTagCommand implements ICommand<EventTag> {
    private EventTag eventTagResult;
    private final String tagName;
    private final Set<String> tagValues;
    private final String defaultValue;

    /**
     * @param tagName         Name of Tag to create
     * @param tagValues       set of all possible tags
     * @param defaultValue    defaultValue of new tag.
     */
    public AddEventTagCommand(String tagName, Set<String> tagValues,String defaultValue) {
        this.tagName = tagName;
        this.tagValues = tagValues;
        this.defaultValue = defaultValue;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Staff
     * @verifies.that the new tag name doesn't clash with any existing tags
     * @verifies.that there are at least 2 tag values.
     * @verifies.that the default tag value is in the list of possible tag values
     */
    @Override
    public void execute(Context context, IView view) {

        User currentUser = context.getUserState().getCurrentUser();
        if (!(currentUser instanceof Staff)) {
            view.displayFailure("AddEventTagCommand",
                    AddEventTagCommand.LogStatus.ADD_EVENT_TAG_USER_NOT_STAFF,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            eventTagResult = null;
            return;
        }

        if (context.getEventState().getPossibleTags().containsKey(tagName)) {
            view.displayFailure("AddEventTagCommand",
                    AddEventTagCommand.LogStatus.ADD_EVENT_TAG_CLASHES_WITH_EXISTING_TAGS,
                    Map.of("Tag name", String.valueOf(tagName))
            );
            eventTagResult = null;
            return;
        }

        if (tagValues.size()<2){
            view.displayFailure("AddEventTagCommand",
                    AddEventTagCommand.LogStatus.ADD_EVENT_TAG_NOT_ENOUGH_TAG_VALUES,
                    Map.of("Tag values", String.valueOf(tagValues))
            );
            eventTagResult = null;
            return;
        }

        if (!(tagValues.contains(defaultValue))){
            view.displayFailure("AddEventTagCommand",
                    LogStatus.ADD_EVENT_TAG_DEFAULT_TAG_VALUES_NOT_IN_THE_LIST_OF_POSSIBLE_TAGS,
                    Map.of("Tag values", String.valueOf(tagValues),
                            "Default Value", String.valueOf(defaultValue))
            );
            eventTagResult = null;
            return;
        }


        eventTagResult = context.getEventState().createEventTag(tagName, tagValues, defaultValue);
        view.displaySuccess(
                "AddEventTagCommand",
                AddEventTagCommand.LogStatus.ADD_EVENT_TAG_SUCCESS,
                Map.of("Tag name", String.valueOf(tagName),
                        "Tag values", String.valueOf(tagValues),
                        "Default Value", String.valueOf(defaultValue))
        );
    }

    @Override
    public EventTag getResult() {
        return eventTagResult;
    }

    private enum LogStatus {
        ADD_EVENT_TAG_USER_NOT_STAFF,
        ADD_EVENT_TAG_CLASHES_WITH_EXISTING_TAGS,
        ADD_EVENT_TAG_NOT_ENOUGH_TAG_VALUES,
        ADD_EVENT_TAG_DEFAULT_TAG_VALUES_NOT_IN_THE_LIST_OF_POSSIBLE_TAGS,
        ADD_EVENT_TAG_SUCCESS
    }
}
