package incident.analysis.extra;

import static util.AppUtil.HR1;
import static util.AppUtil.HR2;
import static util.AppUtil.collapseWhitespace;
import static util.AppUtil.getMatch;
import incident.analyzer.EventTargetType;
import incident.analyzer.EventVisitor;
import incident.analyzer.IncidentAnalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PartitionEventsByTarget implements EventVisitor {
    
    private Map<String, Map<String, List<String>>> eventTargetTypeMap = new TreeMap<String, Map<String, List<String>>>();

    public static void main(String[] args) throws IOException {
        new PartitionEventsByTarget();
    }
    
    public PartitionEventsByTarget () throws IOException {
        for (EventTargetType targetType : EventTargetType.getTypes()) {
            eventTargetTypeMap.put(targetType.toString(), new TreeMap<String, List<String>>());
        }
        
        IncidentAnalyzer analyzer = new IncidentAnalyzer();
        analyzer.configureLogFiles("G:/dec23_anomalies/entrust-anomalies/4_24_2012/events.log");
        analyzer.processEvents(this);
        
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("G:/dec23_anomalies/entrust-anomalies/4_24_2012/partitioned-events.log"));
            for (String targetType : eventTargetTypeMap.keySet()) {
                Map<String, List<String>> eventMap = eventTargetTypeMap.get(targetType);
                if (eventMap.isEmpty()) {
                    continue;
                }
                out.println(HR1);
                out.println(targetType + " INCIDENTS:");
                out.println(HR1);
                out.flush();
                for (String target : eventMap.keySet()) {
                    List<String> events = eventMap.get(target);
                    out.println(HR2);
                    out.println(target + " [Total Events: " + events.size() + "]");
                    out.println(HR2);
                    for (String event : events) {
                        out.println(collapseWhitespace(event));
                        //out.println(HR2);
                    }
                    out.flush();
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }    
    }

    @Override
    public void onEvent(String target, String event) throws IOException {
        String targetType = getMatch(IncidentAnalyzer.TARGET_TYPE_PATTERN, event);
        Map<String, List<String>> eventMap = eventTargetTypeMap.get(targetType);
        List<String> events = eventMap.get(target);
        if (events == null) {
            events = new LinkedList<String>();
            eventMap.put(target, events);
        }
        events.add(event);
    }

}
