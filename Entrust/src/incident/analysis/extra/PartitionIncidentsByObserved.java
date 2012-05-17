package incident.analysis.extra;

import static util.AppUtil.HR1;
import static util.AppUtil.HR2;
import static util.AppUtil.collapseWhitespace;
import incident.analyzer.IncidentAnalyzer;
import incident.analyzer.IncidentVisitor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PartitionIncidentsByObserved implements IncidentVisitor {
    
    
    private Map<String, List<String>> incidentMap = new TreeMap<String, List<String>>();

    public static void main(String[] args) throws IOException {
        new PartitionIncidentsByObserved();
    }
    
    public PartitionIncidentsByObserved () throws IOException {
        IncidentAnalyzer analyzer = new IncidentAnalyzer();
        analyzer.configureLogFiles("G:/dec23_anomalies/incidents.log");
        analyzer.processIncidents(this);
        
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("G:/dec23_anomalies/partitioned-incidents2.log"));
            for (String observed : incidentMap.keySet()) {
                List<String> incidents = incidentMap.get(observed);
                out.println(HR1);
                out.println(observed + " [Total Incidents: " + incidents.size() + "]");
                out.println(HR1);
                for (String incident : incidents) {
                    out.println(collapseWhitespace(incident));
                    out.println(HR2);
                }
                out.flush();
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }    
    }

    @Override
    public void onIncident(String observed, String incident) throws IOException {
        
        List<String> incidents = incidentMap.get(observed);
        if (incidents == null) {
            incidents = new LinkedList<String>();
            incidentMap.put(observed, incidents);
        }
        incidents.add(incident);
    }

}
