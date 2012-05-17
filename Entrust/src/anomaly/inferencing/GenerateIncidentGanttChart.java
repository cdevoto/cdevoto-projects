package anomaly.inferencing;

import static util.AppUtil.DEFAULT_START_TIME;
import static util.AppUtil.EVENT_TYPE_PATTERN;
import static util.AppUtil.HR1;
import static util.AppUtil.IP_PATTERN;
import static util.AppUtil.URL_PATTERN;
import static util.AppUtil.getElapsedIncrements;
import static util.AppUtil.getMatch;
import static util.AppUtil.getRoundedTime;
import static util.AppUtil.getTimeline;
import static util.AppUtil.pad;
import static util.AppUtil.parseDate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import main.Main;
import util.AppUtil;
import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;

public class GenerateIncidentGanttChart extends AnomalyVisitorAdapter {
    
    private List<String> ipFilter = new LinkedList<String>();
    private List<String> urlFilter = new LinkedList<String>();
    private boolean verbose = false;
    private Set<DataNode> dataNodes = new TreeSet<DataNode>(new DataNodeComparator());
    private Map<String, ObservableNode> observableNodes = new HashMap<String, ObservableNode>();
    private String inputDir;
    
    public static void main(String[] args) throws Exception {
        String [] urlFilter = null, ipFilter = null;
        //urlFilter = new String [] { "https\\:\\/\\/evupdater\\.entrust\\.net", "https\\:\\/\\/buy\\.entrust\\.net" };
        //ipFilter = new String [] { "216\\.191\\.247\\.147", "216\\.191\\.247\\.211",  "216\\.191\\.247\\.141", "216\\.191\\.247\\.205"};

        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new GenerateIncidentGanttChart(appProperties, urlFilter, ipFilter);
    }
    
    public GenerateIncidentGanttChart (Properties appProperties, String [] urlFilter, String [] ipFilter) throws Exception {
        this.inputDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getAnomalyDetectorOutputDir(inputDir);
        
        File logFile = new File(outputDir, "ALL-SORTED.txt");
        AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.NONE, logFile);
        if (urlFilter != null) {
            for (String expression : urlFilter) {
                this.urlFilter.add(expression);
            }
        }    
        if (ipFilter != null) {
            for (String expression : ipFilter) {
                this.ipFilter.add(expression);
            }
        }    
        
        NodeConfig config = new NodeConfig();
        config.setActivationThreshold(1.0 /3.0);
        config.setCertaintyThreshold(0.2);
        config.setInferencing(true);
        
        creatDataNodes(config);

        analyzer.visitEvents(this);
        
        PrintWriter chartOut = null;
        try {
            chartOut = new PrintWriter(new FileWriter(new File(outputDir, "gantt-chart.txt")));
            printGanttChart(chartOut);
        } finally {
            if (chartOut != null) {
                chartOut.close();
            }
        }
        PrintWriter listOut = null;
        try {
            listOut = new PrintWriter(new FileWriter(new File(outputDir, "incident-list.txt")));
            printIncidents(listOut);
        } finally {
            if (listOut != null) {
                listOut.close();
            }
        }
    }

    private void printGanttChart(PrintWriter out) {
        NodeType currentType = null;
        String currentId = null;
        long startTime = DEFAULT_START_TIME;
        boolean firstLoop = true;
        for (DataNode node : dataNodes) {
            List<Incident> incidents = node.getIncidents();
            if (incidents.isEmpty()) {
                continue;
            }
            if (node.getNodeType() != currentType) {
                currentType = node.getNodeType();
                if (firstLoop) {
                    firstLoop = false;
                } else {
                    out.println();
                }    
                out.println(HR1);
                out.println(currentType + " Incidents:");
                out.println(HR1);
                out.println(getTimeline(60));
            }    
            if (!node.getNodeId().equals(currentId)) {
                currentId = node.getNodeId().replace("|", ":");
                startTime = DEFAULT_START_TIME;
                out.print(pad(currentId, 60));
            }
            for (Incident incident : incidents) {
                long iStartTime = getRoundedTime(parseDate(incident.getStartTime()).getTime());
                long iEndTime = getRoundedTime(parseDate(incident.getEndTime()).getTime());
                for (int i = 0; i < getElapsedIncrements(startTime, iStartTime); i++) {
                    out.print(" ");
                }
                for (int i = 0; i < getElapsedIncrements(iStartTime, iEndTime) - 1; i++) {
                    out.print("*");
                }
                out.print(incident.getResolutionType().charAt(0));
                startTime = iEndTime;
            }
            out.println();
        }
        out.println();
        out.flush();
    }

    private void printIncidents(PrintWriter out) {
        for (DataNode node : dataNodes) {
            if (verbose) {
                out.println(HR1);
                out.println("Incidents for " + node.getNodeId());
                out.println(HR1);
            }
            for (Incident incident : node.getIncidents()) {
                out.println(incident);
            }
            if (verbose) {
                out.println();
            }
        }
        out.flush();
    }
    
    @Override
    public void onEvent(String line) throws IOException {
        if (!urlFilter.isEmpty()) {
            boolean matchFound = false;
            for (String expression : urlFilter) {
                try {
                    String url = getMatch(URL_PATTERN, line);
                    if (url.matches(expression)) {
                        matchFound = true;
                        break;
                    }
                } catch (Exception e) {
                    matchFound = true;
                }
            }
            if (!matchFound) {
                return;
            }
        }
        if (!ipFilter.isEmpty()) {
            boolean matchFound = false;
            for (String expression : ipFilter) {
                try {
                    String ip = getMatch(IP_PATTERN, line);
                    if (ip.matches(expression)) {
                        matchFound = true;
                        break;
                    }
                } catch (Exception e) {
                    matchFound = true;
                }
            }
            if (!matchFound) {
                return;
            }
        }
        String type = getMatch(EVENT_TYPE_PATTERN, line);
        EventType eventType = EventType.getInstance(type);
        if (eventType != null) {
            String id = "";
            try {
                String url = getMatch(URL_PATTERN, line);
                if (url != null) {
                    id += url;
                }
            } catch (RuntimeException ex) {}
            try {
                String ip = getMatch(IP_PATTERN, line);
                if ("ip" != null) {
                    if (!id.isEmpty()) {
                        id += "|";
                    }
                    id += ip;
                }
            } catch (RuntimeException ex) {}
            ObservableNode node = observableNodes.get(id);
            if (node != null) {
                node.onEvent(eventType, line);
            }
            
        }
    }

    private void creatDataNodes(NodeConfig config) {
        
        NodeFactory factory = new NodeFactory(config);
        
        InferredDataNode inferred = factory.createInferredNode(NodeType.DOMAIN, "http://ot#rootDomain_entrust.net");
        dataNodes.add(inferred);
        
        ObservableInferredDataNode observableInferred = null;
        ObservableDataNode observable = null;
        
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "https://buy.entrust.net");
        registerObservableNode(observableInferred, inferred);
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "http://crl.entrust.net");
        registerObservableNode(observableInferred, inferred);
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "https://evupdater.entrust.net");
        registerObservableNode(observableInferred, inferred);        
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "http://ocsp.entrust.net");
        registerObservableNode(observableInferred, inferred);        
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "https://seal.entrust.net");
        registerObservableNode(observableInferred, inferred);        
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "https://managed.entrust.net");
        registerObservableNode(observableInferred, inferred);        
        observableInferred = factory.createObservableInferredNode(NodeType.SERVICE, "https://www.entrust.net");
        registerObservableNode(observableInferred, inferred);        
        
        observableInferred = factory.createObservableInferredNode(NodeType.LOCATED_SERVICE, "https://buy.entrust.net_NortheastCanada");
        registerObservableNode(observableInferred, "https://buy.entrust.net");
        observableInferred = factory.createObservableInferredNode(NodeType.LOCATED_SERVICE, "https://evupdater.entrust.net_NortheastCanada");
        registerObservableNode(observableInferred, "https://evupdater.entrust.net");
        observableInferred = factory.createObservableInferredNode(NodeType.LOCATED_SERVICE, "http://ocsp.entrust.net_NortheastCanada");
        registerObservableNode(observableInferred, "http://ocsp.entrust.net");
        observableInferred = factory.createObservableInferredNode(NodeType.LOCATED_SERVICE, "https://seal.entrust.net_NortheastCanada");
        registerObservableNode(observableInferred, "https://seal.entrust.net");
        observableInferred = factory.createObservableInferredNode(NodeType.LOCATED_SERVICE, "https://managed.entrust.net_NortheastCanada");
        registerObservableNode(observableInferred, "https://managed.entrust.net");
        observableInferred = factory.createObservableInferredNode(NodeType.LOCATED_SERVICE, "https://www.entrust.net_NortheastCanada");
        registerObservableNode(observableInferred, "https://www.entrust.net");


        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.141");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.205");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.145");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.163");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.209");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.227");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.147");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.211");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.139");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.203");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "69.164.70.230");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.146");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.210");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.142");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.206");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.140");
        registerObservableNode(observableInferred);
        observableInferred = factory.createObservableInferredNode(NodeType.HOST, "216.191.247.204");
        registerObservableNode(observableInferred);

        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://buy.entrust.net|216.191.247.141");
        registerObservableNode(observable, "https://buy.entrust.net_NortheastCanada", "216.191.247.141");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://buy.entrust.net|216.191.247.205");
        registerObservableNode(observable, "https://buy.entrust.net_NortheastCanada", "216.191.247.205");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://crl.entrust.net|216.191.247.145");
        registerObservableNode(observable, "http://crl.entrust.net", "216.191.247.145");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://crl.entrust.net|216.191.247.163");
        registerObservableNode(observable, "http://crl.entrust.net", "216.191.247.163");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://crl.entrust.net|216.191.247.209");
        registerObservableNode(observable, "http://crl.entrust.net", "216.191.247.209");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://crl.entrust.net|216.191.247.227");
        registerObservableNode(observable, "http://crl.entrust.net", "216.191.247.227");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://evupdater.entrust.net|216.191.247.147");
        registerObservableNode(observable, "https://evupdater.entrust.net_NortheastCanada", "216.191.247.147");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://evupdater.entrust.net|216.191.247.211");
        registerObservableNode(observable, "https://evupdater.entrust.net_NortheastCanada", "216.191.247.211");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://ocsp.entrust.net|216.191.247.139");
        registerObservableNode(observable, "http://ocsp.entrust.net_NortheastCanada", "216.191.247.139");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://ocsp.entrust.net|216.191.247.203");
        registerObservableNode(observable, "http://ocsp.entrust.net_NortheastCanada", "216.191.247.203");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "http://ocsp.entrust.net|69.164.70.230");
        registerObservableNode(observable, "http://ocsp.entrust.net_NortheastCanada", "69.164.70.230");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://seal.entrust.net|216.191.247.146");
        registerObservableNode(observable, "https://seal.entrust.net_NortheastCanada", "216.191.247.146");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://seal.entrust.net|216.191.247.210");
        registerObservableNode(observable, "https://seal.entrust.net_NortheastCanada", "216.191.247.210");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://managed.entrust.net|216.191.247.142");
        registerObservableNode(observable, "https://managed.entrust.net_NortheastCanada", "216.191.247.142");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://managed.entrust.net|216.191.247.206");
        registerObservableNode(observable, "https://managed.entrust.net_NortheastCanada", "216.191.247.206");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://www.entrust.net|216.191.247.140");
        registerObservableNode(observable, "https://www.entrust.net_NortheastCanada", "216.191.247.140");
        observable = factory.createObservableNode(NodeType.DEPLOYED_SERVICE, "https://www.entrust.net|216.191.247.204");
        registerObservableNode(observable, "https://www.entrust.net_NortheastCanada", "216.191.247.204");
    }

    private void registerObservableNode(ObservableDataNode observable) {
        registerObservableNode(observable, (InferredDataNode) null);
    }

    private void registerObservableNode(ObservableDataNode observable, String ... parents) {
        InferredDataNode [] parentArray = new InferredDataNode [parents.length];
        for (int i = 0; i < parents.length; i++) {
            parentArray[i] = (InferredDataNode) observableNodes.get(parents[i]);
        }
        registerObservableNode(observable, parentArray);
    }
    

    private void registerObservableNode(ObservableDataNode observable, InferredDataNode ... parents) {
        dataNodes.add(observable);
        observableNodes.put(observable.getNodeId(), observable);
        if (parents != null && parents[0] != null) {
           for (InferredDataNode parent : parents) { 
               parent.addChild(observable);
           }    
        }   
    }


    private static class DataNodeComparator implements Comparator<DataNode> {
    
        @Override
        public int compare(DataNode o1, DataNode o2) {
            int result = o1.getNodeType().compareTo(o2.getNodeType());
            if (result != 0) {
                return result;
            }
            return o1.getNodeId().compareTo(o2.getNodeId());
        }
        
    }


}
