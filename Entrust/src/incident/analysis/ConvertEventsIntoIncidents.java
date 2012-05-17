package incident.analysis;

import static util.AppUtil.HR1;
import static util.AppUtil.HR2;
import static util.AppUtil.getMatch;
import incident.analyzer.EventTargetType;
import incident.analyzer.EventType;
import incident.analyzer.EventVisitor;
import incident.analyzer.IncidentAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import main.Main;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import util.AppUtil;
import anomaly.inferencing.Incident;

public class ConvertEventsIntoIncidents implements EventVisitor {
    
    private Map<String, Map<String, IncidentList>> incidentTargetTypeMap = new TreeMap<String, Map<String, IncidentList>>();
    private String inputDir;

    public static void main(String[] args) throws IOException {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new ConvertEventsIntoIncidents(appProperties);
    }
    
    public ConvertEventsIntoIncidents (Properties appProperties) throws IOException {
        this.inputDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getClassifierOutputDir(inputDir);
        for (EventTargetType targetType : EventTargetType.getTypes()) {
            incidentTargetTypeMap.put(targetType.toString(), new TreeMap<String, IncidentList>());
        }
        
        IncidentAnalyzer analyzer = new IncidentAnalyzer();
        analyzer.configureLogFiles(new File(outputDir, "events.txt"));
        analyzer.processEvents(this);
        
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "event-incidents.txt")));
            for (String targetType : incidentTargetTypeMap.keySet()) {
                Map<String, IncidentList> incidentMap = incidentTargetTypeMap.get(targetType);
                if (incidentMap.isEmpty()) {
                    continue;
                }
                out.println(HR1);
                out.println(targetType + " INCIDENTS:");
                out.println(HR1);
                out.flush();
                for (String target : incidentMap.keySet()) {
                    IncidentList incidents = incidentMap.get(target);
                    if (incidents.size() == 0) {
                        continue;
                    }
                    out.println(HR2);
                    out.println(target + " [Total Incidents: " + incidents.size() + "]");
                    out.println(HR2);
                    for (Incident incident : incidents) {
                        if (incident.getEndTime() == null) {
                            System.out.println("ERROR - An incident was opened but never closed: " + incident);
                        }    
                        out.println(incident);
                        
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
    public void onEvent(String target, String eventString) throws IOException {
        String targetType = getMatch(IncidentAnalyzer.TARGET_TYPE_PATTERN, eventString);
        IncidentList incidents = getIncidentList(target, targetType);
        JSONObject event  = (JSONObject) JSONValue.parse(eventString);
        String eventTypeString = (String) event.get("type");
        EventType eventType = EventType.getInstance(eventTypeString);
        if (eventType == EventType.CREATE) {
            String startTime = (String) event.get("eventTime");
            incidents.startIncident(targetType, target, startTime);
            createRootIfNecessary(target, eventString, targetType, incidents, event);
        } else if (eventType == EventType.UPDATE) {
            createRootIfNecessary(target, eventString, targetType, incidents, event);
        } else if (eventType == EventType.CLOSE) {
            String reason = (String) event.get("reason");
            String endTime = (String) event.get("eventTime");
            incidents.endIncident(endTime, reason);
        }
    }

    private void createRootIfNecessary(String target, String eventString,
            String targetType, IncidentList incidents, JSONObject event) {
        String root = getMatch(IncidentAnalyzer.ROOT_PATTERN, eventString);
        IncidentList rootIncidents = null;
        String rootTargetType = null;
        if (!root.equals(target)) {
            rootTargetType = EventTargetType.getEventTargetType(root).toString();
            rootIncidents = getIncidentList(root, rootTargetType);
        } else {
            rootTargetType = targetType;
            rootIncidents = incidents;
        }
        if (!rootIncidents.hasCurrentIncident()) {
            //System.out.println("Incident created on Update event: " + eventString);
            String startTime = (String) event.get("eventTime");
            rootIncidents.startIncident(rootTargetType, root, startTime);
        }
    }

    private IncidentList getIncidentList(String target, String targetType) {
        Map<String, IncidentList> incidentMap = incidentTargetTypeMap.get(targetType);
        IncidentList incidents = incidentMap.get(target);
        if (incidents == null) {
            incidents = new IncidentList(target);
            incidentMap.put(target, incidents);
        }
        return incidents;
    }
    

}
