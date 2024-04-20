package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import model.*;
import view.IView;

import java.util.Map;
/**
 * {@link UpdateConsumerProfileCommand} allows {@link Consumer}s to update their account details and change the preferences
 * associated with each tag
 */
public class UpdateConsumerProfileCommand extends UpdateProfileCommand {
    private final String oldPassword;
    private final String newName;
    private final String newEmail;
    private final String newPhoneNumber;
    private final String newAddress;
    private final String newPassword;
    private final EventTagCollection newPreferences;

    /**
     * @param oldPassword    account password before the change, required for extra security verification. Must not be null
     * @param newName        full name of the person holding this account. Must not be null
     * @param newEmail       email address to use for this account. Must not be null
     * @param newPhoneNumber phone number to use for this account (used for notifying the {@link Consumer} of any
     *                       {@link Event} cancellations that they have bookings for). Must not be null
     * @param newAddress     updated Consumer address. Optional and may be null
     * @param newPassword    password to use for this account. Must not be null
     * @param newPreferences a {@link EventTagCollection} object of preferences for tags, used for filtering events
     *                       in the {@link ListEventsCommand}. If null, default preferences are applied
     */
    public UpdateConsumerProfileCommand(String oldPassword,
                                        String newName,
                                        String newEmail,
                                        String newPhoneNumber,
                                        String newAddress,
                                        String newPassword,
                                        EventTagCollection newPreferences) {
        this.oldPassword = oldPassword;
        this.newName = newName;
        this.newEmail = newEmail;
        this.newPhoneNumber = newPhoneNumber;
        this.newAddress = newAddress;
        this.newPassword = newPassword;
        this.newPreferences = newPreferences == null ? new EventTagCollection() : newPreferences;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that oldPassword, newName, newEmail, newPhoneNumber, and newPassword are all not null
     * @verifies.that current user is logged in
     * @verifies.that oldPassword matches the current user's password
     * @verifies.that there is no other user already registered with the same email address as newEmail
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that verifies that new preferences only include existing tags
     */
    @Override
    public void execute(Context context, IView view) {
        if (oldPassword == null || newName == null || newEmail == null || newPhoneNumber == null || newPassword == null || newAddress == null) {
            view.displayFailure(
                    "UpdateConsumerProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL,
                    Map.of("oldPassword", "***",
                            "newName", String.valueOf(newName),
                            "newEmail", String.valueOf(newEmail),
                            "newPhoneNumber", String.valueOf(newPhoneNumber),
                            "newPassword", "***",
                            "newPreferences", newPreferences,
                            "newAddress", newAddress
                    ));
            successResult = false;
            return;
        }


        if (isProfileUpdateInvalid(context, view, oldPassword, newEmail)) {
            successResult = false;
            return;
        }

        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Consumer)) {
            view.displayFailure(
                    "UpdateConsumerProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_NOT_CONSUMER);
            successResult = false;
            return;
        }
        if (newAddress != null && !newAddress.isBlank())
        {
            boolean isAddrValid = true;
            String[] addressSplit = newAddress.split("\\s+");

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
            if(!isAddrValid) { // could be !context.getMapSystem().isValidAddress(newAddress)
                view.displayFailure(
                        "UpdateConsumerProfileCommand",
                        LogStatus.USER_UPDATE_PROFILE_INVALID_ADDRESS,
                        Map.of("address", newAddress)
                );
                successResult = false;
                return;
            }

            GHPoint coords = context.getMapSystem().convertToCoordinates(newAddress);
            if(!context.getMapSystem().isPointWithinMapBounds(coords))
            {
                view.displayFailure(
                        "UpdateConsumerProfileCommand",
                        UpdateConsumerProfileCommand.LogStatus.USER_UPDATE_PROFILE_POINT_OUT_OF_BOUNDS,
                        Map.of("address", newAddress)
                );
                successResult = false;
                return;
            }
        }



        Map<String, EventTag> possibleTags = context.getEventState().getPossibleTags();
        boolean tagsValid = true;
        for ( String tagName : newPreferences.getTagNames()) {
            if (!(possibleTags.containsKey(tagName))) {
                tagsValid = false;
            }
            else if (!(possibleTags.get(tagName).values.contains(newPreferences.getValueFor(tagName)))) {
                tagsValid = false;
            }
        }

        if (!tagsValid){
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.NEW_TAGS_DO_NOT_MATCH,
                    Map.of("tags", newPreferences)
            );
            successResult = false;
            return;
        }



        changeUserEmail(context, newEmail);
        currentUser.updatePassword(newPassword);
        Consumer consumer = (Consumer) currentUser;
        consumer.setAddress(newAddress);
        consumer.setName(newName);
        consumer.setPhoneNumber(newPhoneNumber);
        consumer.setAddress(newAddress);
        consumer.setPreferences(newPreferences);

        view.displaySuccess(
                "UpdateConsumerProfileCommand",
                LogStatus.USER_UPDATE_PROFILE_SUCCESS,
                Map.of("newName", newName,
                        "newEmail", newEmail,
                        "newPhoneNumber", newPhoneNumber,
                        "newAddress", newAddress,
                        "newPassword", "***",
                        "preferences", newPreferences)
        );
        successResult = true;
    }



    private enum LogStatus {
        USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL,
        USER_UPDATE_PROFILE_NOT_CONSUMER,
        USER_UPDATE_PROFILE_INVALID_ADDRESS,
        USER_UPDATE_PROFILE_POINT_OUT_OF_BOUNDS,
        USER_UPDATE_PROFILE_SUCCESS,
        NEW_TAGS_DO_NOT_MATCH

    }
}
