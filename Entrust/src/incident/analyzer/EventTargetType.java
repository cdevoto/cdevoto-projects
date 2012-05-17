package incident.analyzer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EventTargetType implements Comparable<EventTargetType> {
    
    public static final EventTargetType DOMAIN = new EventTargetType("DOMAIN", 1) {
        
        @Override
        protected final String getPattern () {
            return "^http\\:\\/\\/ot\\#rootDomain_";
        }
    };
    
    public static final EventTargetType SERVICE = new EventTargetType("SERVICE", 2) {
        
        @Override
        protected final String getPattern () {
            return "*";
        }
    };

    public static final EventTargetType LOCATED_SERVICE = new EventTargetType("LOCATED_SERVICE", 3) {
        
        @Override
        protected final String getPattern () {
            return "_NortheastCanada$";
        }
    };
    
    
    public static final EventTargetType DEPLOYED_SERVICE = new EventTargetType("DEPLOYED_SERVICE", 4) {
        
        @Override
        protected final String getPattern () {
            return "entrust\\.net\\:\\d";
        }
    };
    
    public static final EventTargetType HOST = new EventTargetType("HOST", 5) {
        
        @Override
        protected final String getPattern () {
            return "^http\\:\\/\\/ip\\#";
        }
    };
    
    private static final EventTargetType [] TYPES = { DOMAIN, HOST, LOCATED_SERVICE, DEPLOYED_SERVICE, SERVICE }; 
    private static final Map<String, EventTargetType> TYPE_MAP = new LinkedHashMap<String, EventTargetType>(); 

    static {
        EventTargetType [] types = { DOMAIN, SERVICE, LOCATED_SERVICE, DEPLOYED_SERVICE, HOST };
        for (EventTargetType type : types) {
            TYPE_MAP.put(type.name, type);
        }
    }

    public static EventTargetType getByName (String name) {
        return TYPE_MAP.get(name);
    }

    public static EventTargetType getEventTargetType (String target) {
        for (EventTargetType type : TYPES) {
            if (type.matches(target)) {
                return type;
            }
        }
        return null;
    }
    
    public static Iterable<EventTargetType> getTypes () {
        return new Iterable<EventTargetType> () {

            @Override
            public Iterator<EventTargetType> iterator() {
                return TYPE_MAP.values().iterator();
            }
        };
    }
    
    private int index;
    private String name;
    private Pattern pattern;

    private EventTargetType (String name, int index) {
        this.index = index;
        this.name = name;
        String pattern = getPattern();
        if (!pattern.equals("*")) {
            this.pattern = Pattern.compile(pattern);
        }
    }
    
    protected abstract String getPattern (); 
    
    private boolean matches (String target) {
        if (pattern == null) {
            return true;
        }
        Matcher m = this.pattern.matcher(target);
        return m.find();
    }

    public String toString () {
        return this.name;
    }
    
    @Override
    public int compareTo(EventTargetType o) {
        return this.index = o.index;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventTargetType other = (EventTargetType) obj;
        if (index != other.index)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals(other.pattern))
            return false;
        return true;
    }

    public static void main(String[] args) {
        String [] targets = {
            "https://seal.entrust.net",
            "http://ip#216.191.247.146",
            "http://ot#rootDomain_entrust.net",
            "https://evupdater.entrust.net:216.191.247.147",
            "https://seal.entrust.net_NortheastCanada"
        };   
        
        for (String target : targets) {
            System.out.println(EventTargetType.getEventTargetType(target));
        }
    }
}
