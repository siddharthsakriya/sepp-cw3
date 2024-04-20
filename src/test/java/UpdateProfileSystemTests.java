import command.*;
import controller.Controller;
import model.EventTagCollection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateProfileSystemTests extends ConsoleTest {

    //Fail Cases (Consumer):

    //Test to update consumer profile when user is not logged in
    @Test
    void updateConsumerNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_UPDATE_PROFILE_NOT_LOGGED_IN"
        );
    }

    //Test to update Consumer profile when user is Staff
    @Test
    void updateConsumerAsStaff() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                STAFF_PASSWORD,
                "Alice",
                STAFF_EMAIL,
                "000",
                "",
                STAFF_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_NOT_CONSUMER"
        );
    }

    //Test to update Consumer profile when name is null
    @Test
    void updateConsumerNullName() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                null,
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Consumer profile when email is null
    @Test
    void updateConsumerNullEmail() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                null,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Consumer profile when phone number is null
    @Test
    void updateConsumerNullPhone() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                null,
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Consumer profile when old password is null
    @Test
    void updateConsumerNullOldPassword() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                null,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Consumer profile when new password is null
    @Test
    void updateConsumerNullNewPassword() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                null,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Consumer profile when old password is wrong
    @Test
    void updateConsumerWrongPassword() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD + "test",
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_WRONG_PASSWORD"
        );
    }

    //Test to update Consumer profile when new email is in use
    @Test
    void updateConsumerTakenEmail() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Domain Parker",
                "peter@parker.com",
                "01324456897",
                "55.94872684464941 -3.199892044473183", // Edinburgh Castle
                "park before it's popular!"
        ));
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE"
        );
    }

    //Test to update Consumer profile when address provided is invalid
    @Test
    void updateConsumerInvalidAddress(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "an invalid address", // Edinburgh Castle Coords
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_INVALID_ADDRESS"
        );
    }

    //Test to update Consumer profile when LatLong provided for address is out of bounds
    @Test
    void updateConsumerLatLongOutOfBounds(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "100.94872684464941 -33737.199892044473183",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_POINT_OUT_OF_BOUNDS"
        );
    }

    //Test to update Consumer profile when new tags are not included
    @Test
    void updateConsumerTagsNotIncluded(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                CONSUMER_EMAIL,
                "000",
                "",
                CONSUMER_PASSWORD,
                new EventTagCollection("catered=pizza")
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "NEW_TAGS_DO_NOT_MATCH"
        );
    }

    //Success Case (Consumer):

    //Test to update Consumer email when all fields are valid
    @Test
    void updateConsumerNewEmailAndRelogin() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "55.94872684464941 -3.199892044473183", // Edinburgh Castle Coords
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());

        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                "peter@parker.com",
                CONSUMER_PASSWORD
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }

    //Test to update Consumer email when all fields are valid, and check this change has been applied
    @Test
    void updateConsumerEmailInvalidatesOldEmail() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Alice",
                "peter@parker.com",
                "000",
                "55.94872684464941 -3.199892044473183",
                CONSUMER_PASSWORD,
                new EventTagCollection()
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());

        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                CONSUMER_EMAIL,
                CONSUMER_PASSWORD
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_EMAIL_NOT_REGISTERED"
        );
    }

    //Fail Cases (Staff):

    //Test to update Staff profile when User not logged in
    @Test
    void updateStaffNotLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                STAFF_EMAIL,
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_UPDATE_PROFILE_NOT_LOGGED_IN"
        );
    }

    //Test to update Staff profile when User is a Consumer
    @Test
    void updateProviderAsConsumer() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                CONSUMER_PASSWORD,
                CONSUMER_EMAIL,
                CONSUMER_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_NOT_STAFF"
        );
    }

    //Test to update Staff profile when email is null
    @Test
    void updateStaffNullEmail(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                null,
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Staff profile when new password is null
    @Test
    void updateStaffNullNewPassword(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                STAFF_EMAIL,
                null
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Staff profile when old password is null
    @Test
    void updateStaffNullOldPassword(){
            Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                null,
                STAFF_EMAIL,
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL"
        );
    }

    //Test to update Staff profile when old password is incorrect
    @Test
    void updateStaffPasswordsDontMatch(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                "wrong password",
                STAFF_EMAIL,
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_WRONG_PASSWORD"
        );
    }

    //Test to update Staff profile when email is already in use
    @Test
    void updateStaffClashingEmails(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterStaffCommand(
                "Peter@mail.com",
                STAFF_PASSWORD,
                "Nec temere nec timide"
        ));
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                STAFF_EMAIL,
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE"
        );
    }

    //Success Cases(Staff):

    //Test to update Staff email with valid inputs
    @Test
    void updateStaffUpdateMailRelogin(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                "newmail@mail.com",
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                "newmail@mail.com",
                STAFF_PASSWORD
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }

    //Test to update Staff email with valid inputs and check to ensure change has been made
    @Test
    void updateStaffUpdateMailReloginOldMail(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        UpdateStaffProfileCommand updateCmd = new UpdateStaffProfileCommand(
                STAFF_PASSWORD,
                "newmail@mail.com",
                STAFF_PASSWORD
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(
                "bring-in-the-cash@pawsforawwws.org",
                STAFF_PASSWORD
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_EMAIL_NOT_REGISTERED"
        );
    }

}
