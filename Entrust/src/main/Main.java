package main;

import incident.IncidentAnalysisMain;

import java.util.Properties;

import util.AppUtil;
import anomaly.AnomalyAnalysisMain;

public class Main {
    
    public static void main(String[] args) throws Exception {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new AnomalyAnalysisMain(appProperties);
        new IncidentAnalysisMain(appProperties);
        new GenerateCombinedIncidentGanttChart(appProperties);
        
    }

}
