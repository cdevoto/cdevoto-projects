package incident.analysis;

import static util.AppUtil.DEFAULT_START_TIME;
import static util.AppUtil.HR1;
import static util.AppUtil.getElapsedIncrements;
import static util.AppUtil.getRoundedTime;
import static util.AppUtil.getTimeline;
import static util.AppUtil.pad;
import static util.AppUtil.parseDate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import json.JsonLogAnalyzer;
import json.JsonLogVisitor;
import main.Main;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import util.AppUtil;

public class GenerateIncidentGanttChart implements JsonLogVisitor {

    private String currentTargetType;
    private String currentTarget;
    private long endTime = DEFAULT_START_TIME;
    private List<String> incidents = new LinkedList<String>();
    private PrintWriter out;
    private String inputDir;
    
    public static void main(String[] args) throws IOException {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new GenerateIncidentGanttChart(appProperties);
    }
    
    public GenerateIncidentGanttChart(Properties appProperties) throws IOException {
        this.inputDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getClassifierOutputDir(inputDir);
        JsonLogAnalyzer analyzer = new JsonLogAnalyzer();
        analyzer.configureLogFiles(new File(outputDir, "event-incidents.txt"));
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "gantt-chart.txt")));
            analyzer.processJsonLogs(this);
            out.println();
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        
        PrintWriter out2 = null;
        try {
            out2 = new PrintWriter(new FileWriter(new File(outputDir, "incident-list.txt")));
            for (String incident : incidents) {
                out2.println(incident);
            }
        } finally {
            if (out2 != null) {
                out2.close();
            }
        }
    }

    @Override
    public void onJsonObject(String jsonString) {
        incidents.add(jsonString);
        JSONObject incident = (JSONObject) JSONValue.parse(jsonString);
        String targetType = (String) incident.get("objType");
        String target = (String) incident.get("id");
        boolean firstIncidentOfType = false;
        if (!targetType.equals(currentTargetType)) {
            out.println();
            out.println(HR1);
            out.println(targetType + " Incidents:");
            out.println(HR1);
            out.println(getTimeline(60));
            currentTargetType = targetType;
            firstIncidentOfType = true;
        }
        if (!target.equals(currentTarget)) {
            if (!firstIncidentOfType) {
                out.println();
            }
            out.print(pad(target, 60));
            currentTarget = target;
            endTime = DEFAULT_START_TIME;
        }
        Date startTimeDate = parseDate((String) incident.get("startTime"));
        long roundedStartTime = getRoundedTime(startTimeDate.getTime());
        int blankIncrements = getElapsedIncrements(endTime, roundedStartTime);
        for (int i = 0; i < blankIncrements; i++) {
            out.print(" ");
        }
        Date endTimeDate = parseDate((String) incident.get("endTime"));
        long roundedEndTime = getRoundedTime(endTimeDate.getTime());
        int fullIncrements = getElapsedIncrements(roundedStartTime, roundedEndTime);
        for (int i = 1; i < fullIncrements; i++) {
            out.print("*");
        }
        out.print(((String) incident.get("resolutionType")).charAt(0));
        endTime = roundedEndTime;
    }

}
