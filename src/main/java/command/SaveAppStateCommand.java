package command;

import controller.Context;
import model.Staff;
import model.User;
import view.IView;

import java.io.*;
import java.util.Map;

/**
 * {@link LoadAppStateCommand} allows staff users to save current app state by providing the file name they wish to save
 */
public class SaveAppStateCommand implements ICommand<Boolean> {
    private final String filename;
    private Boolean successResult;

    /**
     * @param filename file name of the .ser file to be saved
     */
    public SaveAppStateCommand(String filename) {
        this.filename = filename;
        this.successResult = null;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current user logged in is a staff member.
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "SaveAppStateCommand",
                    LogStatus.SAVE_APP_USER_NOT_STAFF,
                    Map.of("currentUser", currentUser,
                            "filename", filename,
                            "successResult", successResult = false
                    )
            );
            successResult = false;
            return;
        }


        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(fileOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            out.writeObject(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        view.displaySuccess(
                "SaveAppStateCommand",
                LogStatus.SAVE_APP_STATE_SUCCESS,
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
        SAVE_APP_USER_NOT_STAFF,
        SAVE_APP_STATE_SUCCESS


    }
}