import model.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the EventTagCollection class
 */
public class TestEventTagCollection {
    @Test
    void getValueTest(){
        EventTagCollection testTagColl = new EventTagCollection("hasSocialDistancing=true,venueCapacity=200");
        assertEquals(testTagColl.getValueFor("hasSocialDistancing"), "true");
    }

    @Test
    void getNamesTest(){
        EventTagCollection testTagColl = new EventTagCollection("hasSocialDistancing=true,venueCapacity=200");
        Set<String> tagSet = testTagColl.getTagNames();
        assertTrue(tagSet.contains("hasSocialDistancing"));
        assertTrue(tagSet.contains("venueCapacity"));
    }

}
