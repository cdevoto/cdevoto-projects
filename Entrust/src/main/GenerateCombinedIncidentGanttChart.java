package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import util.AppUtil;

public class GenerateCombinedIncidentGanttChart {
    
    private static final Pattern TARGET_PATTERN = Pattern.compile("^(\\S+) ?");
    private String timeLine;
    private String inputDir;
    
    
    public static void main(String[] args) throws Exception {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new GenerateCombinedIncidentGanttChart(appProperties);
    }

    public GenerateCombinedIncidentGanttChart (Properties appProperties) throws IOException {
        this.inputDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getOutputDir(inputDir);
        File anomalyOutputDir = AppUtil.getAnomalyDetectorOutputDir(inputDir);
        File classifierOutputDir = AppUtil.getClassifierOutputDir(inputDir);
        
        Map<String, IncidentPair> incidentPairs = new LinkedHashMap<String, IncidentPair>();
        
        BufferedReader adIn = null;
        try {
            adIn = new BufferedReader(new FileReader(new File(anomalyOutputDir, "gantt-chart.txt")));
            processGanttChart(OutputType.AD, adIn, incidentPairs);
        } finally {
            if (adIn != null) {
                adIn.close();
            }
        }
        
        BufferedReader clIn = null;
        try {
            clIn = new BufferedReader(new FileReader(new File(classifierOutputDir, "gantt-chart.txt")));
            processGanttChart(OutputType.CL, clIn, incidentPairs);
        } finally {
            if (clIn != null) {
                clIn.close();
            }
        }
        
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "gantt-chart.txt")));
            String targetType = null;
            for (IncidentPair pair : incidentPairs.values()) {
                if (!pair.targetType.equals(targetType)) {
                    targetType = pair.targetType;
                    out.println(AppUtil.HR1);
                    out.println(targetType);
                    out.println(AppUtil.HR1);
                    out.println(timeLine);
                }
                out.println(pair.toString());
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    private void processGanttChart(OutputType type, BufferedReader in, Map<String, IncidentPair> incidentPairs) throws IOException {
        String targetType = null;
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            if (line.trim().length() == 0) {
                continue;
            } else if (line.startsWith("*")) {
                line = in.readLine();
                targetType = line.trim();
                line = in.readLine();
                continue;
            } else if (line.trim().length() > 10 && line.startsWith(" ")) {
                if (timeLine == null) {
                    timeLine = "     " + line;
                }
            } else {
                String target = AppUtil.getMatch(TARGET_PATTERN, line);
                IncidentPair pair = incidentPairs.get(target);
                if (pair == null) {
                    pair = new IncidentPair(target, targetType);
                    incidentPairs.put(target, pair);
                }
                pair.setOutput(line, type);
            }
            
        }
    }

    private static enum OutputType {
        AD,
        CL
    }
    
    private static class IncidentPair {
        private String target;
        private String targetType;
        private String adOutput;
        private String clOutput;
        
        public IncidentPair (String target, String targetType) {
            this.target = target;
            this.targetType = targetType;
        }
        
        public void setOutput (String output, OutputType outputType) {
            if (outputType == OutputType.AD) {
                this.adOutput = output;
            } else if (outputType == OutputType.CL) {
                this.clOutput = output;
            }
        }
        
        @Override
        public String toString() {
            return "[AD] " + (adOutput != null ? adOutput : target) + "\n" +
                   "[CL] " + (clOutput != null ? clOutput : target) + "\n" +
                    AppUtil.HR2;
        }
    }

}
