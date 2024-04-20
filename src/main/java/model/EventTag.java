
package model;

import java.io.Serializable;
import java.util.Set;
/**
 * {@link EventTag} represents an EventTag made by a {@link Staff} for an {@link Event}.
 */
public class EventTag implements Serializable {
    public Set<String> values;
    public String defaultValue;

    /**
     * @param values   the possible values the event tag can take
     * @param defaultValue          the event tag's default value
     */
    public EventTag(Set<String> values, String defaultValue){
        this.values = values;
        this.defaultValue = defaultValue;
    }
}