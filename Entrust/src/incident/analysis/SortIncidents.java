package incident.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import main.Main;

import json.JsonLogAnalyzer;
import json.JsonLogVisitor;
import util.AppUtil;

public class SortIncidents implements JsonLogVisitor {
    
    public static final Pattern START_TIME_PATTERN = Pattern.compile("\"startTime\"\\: ?\"([^\"]+)\"");
    
    Map<Date, String> sortedIncidents = new TreeMap<Date, String>();
    
    public static void main(String[] args) throws IOException {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new SortIncidents(appProperties);
    }
    
    public SortIncidents (Properties appProperties) throws IOException {
        String rootDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getClassifierOutputDir(rootDir);
        JsonLogAnalyzer analyzer = new JsonLogAnalyzer();
        analyzer.configureLogFiles(new File(outputDir, "incident-list.txt"));
        analyzer.processJsonLogs(this);
        
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "sorted-incident-list.txt")));
            for (String incident : sortedIncidents.values()) {
                out.println(incident);
            }
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void onJsonObject(String incident) {
        String startTime = AppUtil.getMatch(START_TIME_PATTERN, incident);
        Date startTimeDate = AppUtil.parseDate(startTime);
        sortedIncidents.put(startTimeDate, incident);
    }
    
    

}
