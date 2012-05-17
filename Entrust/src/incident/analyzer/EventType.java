package incident.analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

public class EventType {
    public static final EventType CREATE = new EventType("Create");
    public static final EventType UPDATE = new EventType("Update");
    public static final EventType ESCALATE = new EventType("Escalate");
    public static final EventType DE_ESCALATE = new EventType("DeEscalate");
    public static final EventType CHANGE_HYPOTHESIS = new EventType("ChangeHypothesis");
    public static final EventType CLOSE = new EventType("Close");
    
    private static final Map<String, EventType> eventTypes = new LinkedHashMap<String, EventType>();

    private String name;
    
    static {
        EventType [] types = { CREATE, UPDATE, ESCALATE, DE_ESCALATE, CHANGE_HYPOTHESIS, CLOSE };
        for (EventType type : types) {
            eventTypes.put(type.toString(), type);
        }
    }
    
    public static EventType getInstance (String name) {
        return eventTypes.get(name);
    }
    
    private EventType (String name) {
        this.name = name;
    }
    
    public String toString () {
        return this.name;
    }
 
}
