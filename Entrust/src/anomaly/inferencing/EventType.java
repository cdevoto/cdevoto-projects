package anomaly.inferencing;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    UPDATE,
    RTN,
    TOUT;
    
    private static EventType [] eventTypes = { UPDATE, RTN, TOUT }; 
    private static Map<String, EventType> eventTypeMap = new HashMap<String, EventType>();

    static {
        for (EventType type : eventTypes) {
            eventTypeMap.put(type.toString(), type);
        }
    }
    
    public static EventType getInstance (String type) {
        return eventTypeMap.get(type);
    }
}
