import command.LogoutCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterStaffSystemTests extends ConsoleTest {

    //Fail Cases:

    //Test to register a Staff when a Staff is already logged in
    @Test
    void registerStaffStaffLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand("randommail@gmail.com", "password", "Nec temere nec timide");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_REGISTER_LOGGED_IN"
        );

        assertNull(registerStaffCmd.getResult());

    }

    //Test to register a Staff when a Consumer is already logged in
    @Test
    void registerStaffConsumerLoggedIn(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand("randommail@gmail.com", "password", "Nec temere nec timide");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_REGISTER_LOGGED_IN"
        );
        assertNull(registerStaffCmd.getResult());
    }

    //Test to register a Staff with the email field null
    @Test
    void registerStaffNoEmail() {
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd =new RegisterStaffCommand("", "password", "Nec temere nec timide");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
        assertNull(registerStaffCmd.getResult());
    }

    //Test to register a Staff with the password field null
    @Test
    void registerStaffNoPassword() {
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd =new RegisterStaffCommand("scoobydoo@gmail.com", "", "Nec temere nec timide");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
        assertNull(registerStaffCmd.getResult());
    }

    //Test to register a Staff with the secret field null
    @Test
    void registerStaffNoSecret() {
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand("scoobydoo@gmail.com", "password", "");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
        assertNull(registerStaffCmd.getResult());
    }

    //Test to register a Staff with clashing emails
    @Test
    void registerStaffWithSameEmail() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterStaffCommand("basedBadger@gmail.com", "password", "Nec temere nec timide"));
        controller.runCommand(new LogoutCommand());
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand("basedBadger@gmail.com", "password", "Nec temere nec timide");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_REGISTER_EMAIL_ALREADY_REGISTERED");
        assertNull(registerStaffCmd.getResult());
    }

    //Test to register a Staff with an incorrect secret
    @Test
    void registerStaffWithWrongSecret() {
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand("randommail@gmail.com", "password", "Incorrect Secret");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare(
                "USER_REGISTER_WRONG_STAFF_SECRET"
        );
        assertNull(registerStaffCmd.getResult());

    }

    //Success Cases:

    //Test to register a Staff with valid details
    @Test
    void registerValidStaff(){
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand("basedBadger@gmail.com", "password", "Nec temere nec timide");
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS");

        assertNotNull(registerStaffCmd.getResult());
        assertEquals(registerStaffCmd.getResult().getEmail(),"basedBadger@gmail.com");
        assertTrue(registerStaffCmd.getResult().checkPasswordMatch("password"));
        }


}
