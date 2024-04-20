import command.LogoutCommand;
import command.RegisterConsumerCommand;
import controller.Controller;
import model.Consumer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterConsumerSystemTests extends ConsoleTest{
    //Fail Cases:

    //Test to register a Consumer when a Consumer is already logged in
    @Test
    void registerConsumerConsumerLoggedIn(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_REGISTER_LOGGED_IN");
    }

    //Test to register a Consumer when a Staff is already logged in
    @Test
    void registerConsumerStaffLoggedIn(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_REGISTER_LOGGED_IN");
    }

    //Test to register a consumer with the name field null
    @Test
    void registerConsumerNoName(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("","basedBadger@gmail.com","01231313123", "","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
    }

    //Test to register a consumer with the email field null
    @Test
    void registerConsumerNoEmail(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","","01231313123", "","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
    }

    //Test to register a consumer with the phone number field null
    @Test
    void registerConsumerNoPhoneNumber(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","", "","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
    }

    //Test to register a consumer with the password field null
    @Test
    void registerConsumerNoPassword(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "","");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
    }

    //Test to register a consumer with a clashing email address
    @Test
    void registerConsumerWithSameEmail(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd1 = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "","honey");
        controller.runCommand(registerConsumerCmd1);
        assertTrue(registerConsumerCmd1.getResult() instanceof Consumer);
        controller.runCommand(new LogoutCommand());
        RegisterConsumerCommand registerConsumerCmd2 = new RegisterConsumerCommand("Bee","basedBadger@gmail.com","1413223423", "","hive");
        controller.runCommand(registerConsumerCmd2);
        assertNull(registerConsumerCmd2.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_REGISTER_EMAIL_ALREADY_REGISTERED");
    }

    //Test to register a consumer with String address
    @Test
    void registerConsumerStringAddress(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "Edinburgh, Scotland","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare("USER_REGISTER_INVALID_ADDRESS_FORMAT");
    }

    //Test to register a consumer with a latitude out of bounds
    @Test
    void registerConsumerLatLongOutOfBounds(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "1122.4234324324242 333333.1212312414","honey");
        controller.runCommand(registerConsumerCmd);
        assertNull(registerConsumerCmd.getResult());
        stopOutputCaptureAndCompare("USER_REGISTER_ADDRESS_OUT_OF_BOUNDS");
    }

    //Success Cases:

    //Test to register a valid consumer with no address
    @Test
    void registerValidConsumerNoAddress(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "","honey");
        controller.runCommand(registerConsumerCmd);
        assertTrue(registerConsumerCmd.getResult() instanceof Consumer);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS");
    }

    //Test to register a valid consumer with an address
    @Test
    void registerValidConsumerWithAddress(){
        Controller controller = createController();
        startOutputCapture();
        RegisterConsumerCommand registerConsumerCmd = new RegisterConsumerCommand("Badger","basedBadger@gmail.com","01231313123", "55.94747223411703 -3.187300017491497","honey");
        controller.runCommand(registerConsumerCmd);
        assertTrue(registerConsumerCmd.getResult() instanceof Consumer);
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS");
    }
}
