package anomaly;

import java.util.Properties;

import main.Main;
import util.AppUtil;
import anomaly.analysis.FilterAnomalyData;
import anomaly.analysis.SortAnomalies;
import anomaly.inferencing.GenerateIncidentGanttChart;

public class AnomalyAnalysisMain {
    
    public AnomalyAnalysisMain (Properties appProperties) throws Exception {
        new FilterAnomalyData(appProperties);
        new SortAnomalies(appProperties);
        
        String [] urlFilter = null, ipFilter = null;
        //urlFilter = new String [] { "https\\:\\/\\/evupdater\\.entrust\\.net", "https\\:\\/\\/buy\\.entrust\\.net" };
        //ipFilter = new String [] { "216\\.191\\.247\\.147", "216\\.191\\.247\\.211",  "216\\.191\\.247\\.141", "216\\.191\\.247\\.205"};
        new GenerateIncidentGanttChart(appProperties, urlFilter, ipFilter);
        
    }
    
    public static void main(String[] args) throws Exception {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new AnomalyAnalysisMain(appProperties);
        
    }

}
