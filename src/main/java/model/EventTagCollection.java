package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link EventTagCollection} represents an {@link Event}'s set of tags using hashmaps.
 */
public class EventTagCollection implements Serializable {
    private final Map<String,String> tags;

    /**
     * Initializing an empty collection of event tags
     */
    public EventTagCollection(){
        tags = new HashMap<>();
    }

    /**
     * Create a new collection of event tags using tagString
     *
     * @param tagString is a string containing multiple key value assignments
     */
    public EventTagCollection(String tagString){
        tags = new HashMap<>();
        String[] pairs = tagString.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            tags.put(keyValue[0], keyValue[1]);
        }
    }
    public String getValueFor(String name) {
        return tags.get(name);
}
    public Set<String> getTagNames() { return tags.keySet();}

    @Override
    public String toString() {
        return "EventTagCollection{" +
                "tags=" + tags +
                '}';
    }
}