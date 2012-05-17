package incident.analyzer;

import static util.AppUtil.getMatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import json.JsonLogAnalyzer;
import json.JsonLogReader;

public class IncidentAnalyzer extends JsonLogAnalyzer {
    
    public static final Pattern OBSERVED_PATTERN = Pattern.compile("\"observed\"\\: ?\"([^\"]+)\"");
    public static final Pattern TARGET_PATTERN = Pattern.compile("\"target\"\\: ?\"([^\"]+)\"");
    public static final Pattern TARGET_TYPE_PATTERN = Pattern.compile("\"targetType\"\\: ?\"([^\"]+)\"");
    public static final Pattern TYPE_PATTERN = Pattern.compile("\"type\"\\: ?\"([^\"]+)\"");
    public static final Pattern ROOT_PATTERN = Pattern.compile("\"root\"\\: ?\"([^\"]+)\"");

    public IncidentAnalyzer () {
    }
    
    public void processIncidents (IncidentVisitor visitor) throws IOException {
        if (logFiles.isEmpty()) {
            throw new IllegalStateException("No log files configured for processing.");
        }
        for (File file : logFiles) {
            JsonLogReader in = null;
            try {
                in = new JsonLogReader(new BufferedReader(new FileReader(file)));
                for (String incident = in.readJsonObject(); incident != null; incident = in.readJsonObject()) {
                    String observed = getMatch(OBSERVED_PATTERN, incident);
                    visitor.onIncident(observed, incident);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        
    }

    public void processEvents (EventVisitor visitor) throws IOException {
        if (logFiles.isEmpty()) {
            throw new IllegalStateException("No log files configured for processing.");
        }
        for (File file : logFiles) {
            JsonLogReader in = null;
            try {
                in = new JsonLogReader(new BufferedReader(new FileReader(file)));
                for (String event = in.readJsonObject(); event != null; event = in.readJsonObject()) {
                    String target = getMatch(TARGET_PATTERN, event);
                    visitor.onEvent(target, event);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        
    }


} 
