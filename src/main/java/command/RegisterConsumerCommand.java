package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import model.Consumer;
import model.User;
import view.IView;
import java.util.Map;

/**
 * {@link RegisterConsumerCommand} allows users to register a new {@link Consumer} account on the system.
 * After registration, they are automatically logged in.
 */
public class RegisterConsumerCommand implements ICommand<Consumer> {
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String address;
    private final String password;
    private Consumer newConsumerResult;

    /**
     * @param name        full name of the consumer
     * @param email       personal email address (which will be used as the account email)
     * @param phoneNumber phone number (to allow notification of {@link model.Event} cancellations)
     * @param address     Consumer address (for filtering events by distance away), optional
     * @param password    password to log in to the system in the future
     */
    public RegisterConsumerCommand(String name,
                                   String email,
                                   String phoneNumber,
                                   String address,
                                   String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that no user is currently logged in
     * @verifies.that name, email, phoneNumber, and password are all not null
     * @verifies.that there is no user with the same email address already registered
     * @verifies.that if an address is provided, it is a valid lat-long format and falls within map system boundaries
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        if (currentUser != null) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_LOGGED_IN,
                    Map.of("currentUser", currentUser)
            );
            newConsumerResult = null;
            return;
        }

        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL,
                    Map.of("name", name,
                            "email", String.valueOf(email),
                            "phoneNumber", String.valueOf(phoneNumber),
                            "address", String.valueOf(address),
                            "password", "***")
            );
            newConsumerResult = null;
            return;
        }

        if (context.getUserState().getAllUsers().containsKey(email)) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED,
                    Map.of("email", email)
            );
            newConsumerResult = null;
            return;
        }

        if (address != null && !address.isBlank())
        {
            boolean isAddrValid = true;
            String[] addressSplit = address.split("\\s+");

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
            if(!isAddrValid) { //could be !context.getMapSystem().isValidAddress(address)
                view.displayFailure(
                        "RegisterConsumerCommand",
                        LogStatus.USER_REGISTER_INVALID_ADDRESS_FORMAT,
                        Map.of("address", address)
                );
                newConsumerResult = null;
                return;
            }

            GHPoint coords = context.getMapSystem().convertToCoordinates(address);
            if(!context.getMapSystem().isPointWithinMapBounds(coords))
            {
                view.displayFailure(
                        "RegisterConsumerCommand",
                        LogStatus.USER_REGISTER_ADDRESS_OUT_OF_BOUNDS,
                        Map.of("address", address)
                );
                newConsumerResult = null;
                return;
            }
        }

        Consumer consumer = new Consumer(name, email, phoneNumber, address, password);
        context.getUserState().addUser(consumer);
        view.displaySuccess(
                "RegisterConsumerCommand",
                LogStatus.REGISTER_CONSUMER_SUCCESS,
                Map.of("name", name,
                        "email", email,
                        "phoneNumber", phoneNumber,
                        "address", String.valueOf(address),
                        "password", "***")
        );
        newConsumerResult = consumer;
        context.getUserState().setCurrentUser(consumer);
        view.displaySuccess(
                "RegisterConsumerCommand",
                LogStatus.USER_LOGIN_SUCCESS,
                Map.of("email", email,
                        "password", "***")
        );
    }

    /**
     * @return Instance of the newly registered {@link Consumer} if successful and null otherwise
     */
    @Override
    public Consumer getResult() {
        return newConsumerResult;
    }

    private enum LogStatus {
        REGISTER_CONSUMER_SUCCESS,
        USER_REGISTER_LOGGED_IN,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_EMAIL_ALREADY_REGISTERED,
        USER_REGISTER_INVALID_ADDRESS_FORMAT,
        USER_REGISTER_ADDRESS_OUT_OF_BOUNDS,
        USER_LOGIN_SUCCESS,
    }
}
