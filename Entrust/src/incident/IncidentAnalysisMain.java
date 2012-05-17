package incident;

import incident.analysis.ConvertEventsIntoIncidents;
import incident.analysis.FilterImportantEvents;
import incident.analysis.GenerateEventLog;
import incident.analysis.GenerateIncidentGanttChart;
import incident.analysis.SortIncidents;

import java.util.Properties;

import main.Main;
import util.AppUtil;


public class IncidentAnalysisMain {
    
    public IncidentAnalysisMain (Properties appProperties) throws Exception {
        new GenerateEventLog(appProperties);
        new ConvertEventsIntoIncidents(appProperties);
        new GenerateIncidentGanttChart(appProperties);
        new FilterImportantEvents(appProperties);
        new SortIncidents(appProperties);
    }

    public static void main(String[] args) throws Exception {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new IncidentAnalysisMain(appProperties);
    }

}
