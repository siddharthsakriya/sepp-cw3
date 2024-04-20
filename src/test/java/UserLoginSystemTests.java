import command.LoginCommand;
import command.LogoutCommand;
import command.RegisterConsumerCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.Consumer;
import model.Staff;
import model.User;
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserLoginSystemTests extends ConsoleTest {

    //Testing Consumer creation
    @Test
    void createConsumer() {
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "",
                "McMuffin"
        );
        controller.runCommand(registerConsumerCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );

        Consumer consumer = registerConsumerCmd.getResult();
        assertNotNull(consumer);
        assertEquals(consumer.getName(), "Ronald McDonald");
        assertEquals(consumer.getEmail(), "always@lovin.it");
        assertEquals(consumer.getPhoneNumber(), "000");
        assertEquals(consumer.getAddress(), "");
        assertTrue(consumer.checkPasswordMatch("McMuffin"));
    }

    //Testing Consumer creation, logging out then relogging in
    @Test
    void createConsumerAndRelogin() {
        Controller controller = createController();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "",
                "McMuffin"
        );

        LoginCommand loginCmd = new LoginCommand("always@lovin.it", "McMuffin");

        startOutputCapture();
        controller.runCommand(registerConsumerCmd);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(loginCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );

        assertNotNull(registerConsumerCmd.getResult());
        assertNotNull(loginCmd.getResult());
        assertEquals(registerConsumerCmd.getResult(),loginCmd.getResult());
    }

    //Testing Consumer creation, logging out then relogging in with wrong email
    @Test
    void createConsumerThenLoginWrongEmail() {
        Controller controller = createController();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "",
                "McMuffin"
        );
        startOutputCapture();
        controller.runCommand(registerConsumerCmd);
        controller.runCommand(new LogoutCommand());
        LoginCommand logInCmd = new LoginCommand("admin@inf2sepp.app", "123456");
        controller.runCommand(logInCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_EMAIL_NOT_REGISTERED"
        );

        assertNotNull(registerConsumerCmd.getResult());
        assertNull(logInCmd.getResult());
    }

    //Testing Consumer creation, logging out then relogging in with wrong password
    @Test
    void createConsumerThenLoginWrongPassword() {
        Controller controller = createController();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "",
                "McMuffin"
        );
        startOutputCapture();
        controller.runCommand(registerConsumerCmd);
        controller.runCommand(new LogoutCommand());
        LoginCommand logInCmd = new LoginCommand("always@lovin.it", "123456");
        controller.runCommand(logInCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_WRONG_PASSWORD"
        );

        assertNotNull(registerConsumerCmd.getResult());
        assertNull(logInCmd.getResult());
    }

    //Testing Staff creation
    @Test
    void createStaff() {
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand(
                "always@lovin.it",
                "Ronald McDonald",
                "Nec temere nec timide"
        );
        controller.runCommand(registerStaffCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );

        assertNotNull(registerStaffCmd.getResult());
        assertEquals(registerStaffCmd.getResult().getEmail(), "always@lovin.it");
        assertTrue(registerStaffCmd.getResult().checkPasswordMatch("Ronald McDonald"));
    }

    //Testing Staff creation, logging out then relogging in
    @Test
    void createStaffAndRelogin() {
        Controller controller = createController();
        startOutputCapture();
        RegisterStaffCommand registerStaffCmd = new RegisterStaffCommand(
                "always@lovin.it",
                "McMuffin",
                "Nec temere nec timide"
        );
        controller.runCommand(registerStaffCmd);
        controller.runCommand(new LogoutCommand());
        LoginCommand loginCmd = new LoginCommand("always@lovin.it", "McMuffin");
        controller.runCommand(loginCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );

        assertNotNull(registerStaffCmd.getResult());
        assertNotNull(loginCmd.getResult());
        assertEquals(registerStaffCmd.getResult(),loginCmd.getResult());

    }
}
