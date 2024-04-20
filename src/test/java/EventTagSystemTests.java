import command.AddEventTagCommand;
import controller.Controller;
import model.EventTag;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class EventTagSystemTests extends ConsoleTest {

    //Fail Cases:

    //Test to create event tag when User is not logged in
    @Test
    void userNotLoggedInForAddEventTag(){
        Controller controller = createController();
        startOutputCapture();
        AddEventTagCommand addTagCmd = new AddEventTagCommand("Option", Set.of("true", "false"), "true");
        controller.runCommand(addTagCmd);
        assertNull(addTagCmd.getResult());
        stopOutputCaptureAndCompare(
                "ADD_EVENT_TAG_USER_NOT_STAFF"
        );
    }

    //Test to create event tag when User is not staff
    @Test
    void userNotStaffForAddEventTag() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        AddEventTagCommand addTagCmd = new AddEventTagCommand("Option", Set.of("true", "false"), "true");
        controller.runCommand(addTagCmd);
        assertNull(addTagCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_USER_NOT_STAFF"
        );
    }

    //Test to create event tag which clashes with existing tags
    @Test
    void addExistingTag() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd = new AddEventTagCommand("hasSocialDistancing", Set.of("true", "false"), "true");
        controller.runCommand(addTagCmd);
        assertNull(addTagCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_CLASHES_WITH_EXISTING_TAGS"
        );
    }

    //Test to create event tag with a default value that is not in the list of possible tag values
    @Test
    void defaultValueNotInTagValues() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd = new AddEventTagCommand("tag1", Set.of("1", "2"), "3");
        controller.runCommand(addTagCmd);
        assertNull(addTagCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_DEFAULT_TAG_VALUES_NOT_IN_THE_LIST_OF_POSSIBLE_TAGS"
        );
    }

    //Test to create event tag with only one possible tag value
    @Test
    void notEnoughTagValues() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd = new AddEventTagCommand("tag1", Set.of("1"), "1");
        controller.runCommand(addTagCmd);
        assertNull(addTagCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_NOT_ENOUGH_TAG_VALUES"
        );
    }

    //Test to create event tag, one success, then one faliure after
    @Test
    void oneSuccessOneFailiure(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd1 = new AddEventTagCommand("tag1", Set.of("true", "false"), "true");
        AddEventTagCommand addTagCmd2 = new AddEventTagCommand("hasSocialDistancing", Set.of("true", "false"), "true");
        controller.runCommand(addTagCmd1);
        controller.runCommand(addTagCmd2);
        assertTrue(addTagCmd1.getResult() instanceof EventTag);
        assertNull(addTagCmd2.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "ADD_EVENT_TAG_CLASHES_WITH_EXISTING_TAGS"
        );
    }

    //Success Cases:

    //Test to create event tag
    @Test
    void userStaffForAddEventTag() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd = new AddEventTagCommand("tag1", Set.of("true", "false"), "true");
        controller.runCommand(addTagCmd);
        assertTrue(addTagCmd.getResult() instanceof EventTag);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS"
        );
    }

    //Test to create 2 event tags
    @Test
    void userStaffForAddTwoEventTags(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd1 = new AddEventTagCommand("tag1", Set.of("true", "false"), "true");
        AddEventTagCommand addTagCmd2 = new AddEventTagCommand("tag2", Set.of("true", "false"), "true");
        controller.runCommand(addTagCmd1);
        controller.runCommand(addTagCmd2);
        assertTrue(addTagCmd1.getResult() instanceof EventTag);
        assertTrue(addTagCmd2.getResult() instanceof EventTag);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS"
        );
    }

    //Test to create event tag with lots of possible tag values
    @Test
    void addEventTagLotsOfValues(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        AddEventTagCommand addTagCmd = new AddEventTagCommand("tag1", Set.of("true", "false", "maybe", "idk", "maybe not", "next time", "another tag val"), "true");
        controller.runCommand(addTagCmd);
        assertTrue(addTagCmd.getResult() instanceof EventTag);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS"
        );
    }
}
