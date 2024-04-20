import command.*;
import controller.Controller;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

public class SaveAppStateSystemTests extends ConsoleTest{
    //Fail Case:

    //Test to save app state when user is Consumer
    @Test
    void saveAsConsumer(){
        Controller controller = createController();
        createConsumer(controller);
        startOutputCapture();
        controller.runCommand(new SaveAppStateCommand("Save"));
        stopOutputCaptureAndCompare("SAVE_APP_USER_NOT_STAFF");
    }


    //Success Case:

    //Test to save app state when user is Staff
    @Test
    void saveAsStaff(){
        Controller controller = createController();
        createStaff(controller);
        controller.runCommand(new CreateEventCommand("Chilli", EventType.Music, 100, 0, "", "chilli", LocalDateTime.of(2025, Month.MARCH, 10,0,0), LocalDateTime.of(2025, Month.MARCH, 20,0,0),new EventTagCollection("hasSocialDistancing=true,venueCapacity=200")));
        controller.runCommand(new AddEventTagCommand("hasGreg", Set.of("yes", "no"), "no"));
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller, 1);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(STAFF_EMAIL,STAFF_PASSWORD));
        startOutputCapture();
        controller.runCommand(new SaveAppStateCommand("Save.ser"));
        stopOutputCaptureAndCompare("SAVE_APP_STATE_SUCCESS");
    }
}
