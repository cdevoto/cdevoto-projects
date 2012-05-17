package incident.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import json.JsonLogAnalyzer;
import json.JsonLogVisitor;
import main.Main;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import util.AppUtil;

public class FilterImportantEvents implements JsonLogVisitor {
    
    private Map<String, Boolean> incidents = new HashMap<String, Boolean>();
    private List<String> events = new LinkedList<String>();
    
    public static void main(String[] args) throws IOException {
        Properties props = AppUtil.getApplicationProperties(Main.class);
        new FilterImportantEvents(props);
    }
    
    public FilterImportantEvents (Properties appProperties) throws IOException {
        File outputDir = AppUtil.getClassifierOutputDir(appProperties);
        JsonLogAnalyzer analyzer = new JsonLogAnalyzer();
        analyzer.configureLogFiles(new File(outputDir, "events.txt"));
        analyzer.processJsonLogs(this);
        
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "filtered-events.txt")));
            for (String event : events) {
                out.println(event);
            }
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void onJsonObject(String event) {
        JSONObject json = (JSONObject) JSONValue.parse(event);
        String target = (String) json.get("target");
        String root = (String) json.get("root");
        String type = (String) json.get("type");
        if ("Create".equals(type)) {
            events.add(event);
            incidentStarted(target);
            if (!hasOngoingIncident(root)) {
                incidentStarted(root);
            }
        } else if ("Update".equals(type)) {
            if (!hasOngoingIncident(root)) {
                events.add(event);
                incidentStarted(root);
            }
        } else if ("Close".equals(type)) {
            events.add(event);
            incidentEnded(target);
        } else {
            events.add(event);
            if (!hasOngoingIncident(root)) {
                incidentStarted(root);
            }
        }
        
    }

    private boolean hasOngoingIncident(String target) {
        Boolean status = incidents.get(target);
        if (status == null || status == false) {
            return false;
        }
        return true;
    }

    private void incidentStarted(String target) {
        incidents.put(target, true);
    }

    private void incidentEnded(String target) {
        incidents.put(target, true);
    }
}
