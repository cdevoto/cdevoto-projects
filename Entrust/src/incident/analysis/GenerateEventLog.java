package incident.analysis;

import static util.AppUtil.collapseWhitespace;
import static util.AppUtil.formatDate;
import incident.analyzer.EventTargetType;
import incident.analyzer.IncidentAnalyzer;
import incident.analyzer.IncidentVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import main.Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import util.AppUtil;

public class GenerateEventLog implements IncidentVisitor {
    
    private PrintWriter out;
    private String inputDir;

    public static void main(String[] args) throws IOException {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new GenerateEventLog(appProperties);
    }
    
    public GenerateEventLog (Properties appProperties) throws IOException {
        this.inputDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getClassifierOutputDir(inputDir);
        IncidentAnalyzer analyzer = new IncidentAnalyzer();
        analyzer.configureLogFiles(new File(inputDir, "incidents.log"));
        
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "events.txt")));
            analyzer.processIncidents(this);
        } finally {
            if (out != null) {
                out.close();
            }
        }    
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onIncident(String observed, String incident) throws IOException {
        JSONObject jsonIncident = (JSONObject) JSONValue.parse(incident);
        Long queueTime = (Long) jsonIncident.get("eventTime");
        JSONArray events = (JSONArray) jsonIncident.get("events");
        for (int i = 0; i < events.size(); i++) {
            JSONObject event = (JSONObject) events.get(i);
            String target = (String) event.get("target");
            event.put("eventTime", formatDate(queueTime));
            event.put("targetType", EventTargetType.getEventTargetType(target).toString());
            out.println(collapseWhitespace(event.toJSONString().replaceAll("\\\\\\/", "/")));
            out.flush();
        }
        
    }

}
