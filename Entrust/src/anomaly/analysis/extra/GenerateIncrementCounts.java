package anomaly.analysis.extra;
import static util.AppUtil.DEFAULT_END_TIME;
import static util.AppUtil.DEFAULT_START_TIME;
import static util.AppUtil.EVENT_TIME_PATTERN;
import static util.AppUtil.EVENT_TYPE_PATTERN;
import static util.AppUtil.getElapsedIncrements;
import static util.AppUtil.getMatch;
import static util.AppUtil.getRoundedTime;
import static util.AppUtil.pad;
import static util.AppUtil.parseDate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;


public class GenerateIncrementCounts extends AnomalyVisitorAdapter {
    
    boolean anomalyStarted = false;
    long startTime = DEFAULT_START_TIME;
    long endTime = startTime;
    Map<AnomalyAnalyzer.KeyType, List<Integer>> incrementCounts = new HashMap<AnomalyAnalyzer.KeyType, List<Integer>>();

    public static void main(String[] args) throws IOException {
        new GenerateIncrementCounts();
    }
    
    public GenerateIncrementCounts () throws IOException {
        
        List<Integer> increments = new ArrayList<Integer>();
        int totalIncrements = getElapsedIncrements(DEFAULT_START_TIME, DEFAULT_END_TIME);
        for (int i = 0; i < totalIncrements; i++) {
            increments.add(0);
        }
        incrementCounts.put(AnomalyAnalyzer.KeyType.IP_URL, increments);
        incrementCounts.put(AnomalyAnalyzer.KeyType.IP, new ArrayList<Integer>(increments));
        incrementCounts.put(AnomalyAnalyzer.KeyType.URL, new ArrayList<Integer>(increments));

        AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
        File parentDir = new File("G:/dec23_anomalies/entrust-anomalies");

        File ipUrlDir = new File(parentDir, "IPURL");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.IP_URL, ipUrlDir.listFiles());
        
        File ipDir = new File(parentDir, "IP");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.IP, ipDir.listFiles());

        File urlDir = new File(parentDir, "URL");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.URL, urlDir.listFiles());
        
        analyzer.visitIpEvents(this);
        analyzer.visitUrlEvents(this);
        analyzer.visitIpUrlEvents(this);
        
        System.out.print(pad(""));
        printTimeline();
        System.out.print(pad("IP"));
        printIncrementCounts(incrementCounts.get(AnomalyAnalyzer.KeyType.IP));
        System.out.print(pad("URL"));
        printIncrementCounts(incrementCounts.get(AnomalyAnalyzer.KeyType.URL));
        System.out.print(pad("IP_URL"));
        printIncrementCounts(incrementCounts.get(AnomalyAnalyzer.KeyType.IP_URL));
    }

    private void printIncrementCounts(List<Integer> counts) {
        for (int count : counts) {
            if (count == 0) {
                System.out.print(" ");
            } else if (count > 9){
                System.out.print("+");
            } else {
                System.out.print(count);
            }
        }
        System.out.println();
    }
    
    public Map<AnomalyAnalyzer.KeyType, List<Integer>> getIncrementCounts() {
        return incrementCounts;
    }

    public void setIncrementCounts(
            Map<AnomalyAnalyzer.KeyType, List<Integer>> incrementCounts) {
        this.incrementCounts = incrementCounts;
    }

    @Override
    public void onAfterIpUrlFileProcessed(File file) throws IOException {
        onAfterFileProcessed(incrementCounts.get(AnomalyAnalyzer.KeyType.IP_URL));
    }

    @Override
    public void onAfterIpFileProcessed(File file) throws IOException {
        onAfterFileProcessed(incrementCounts.get(AnomalyAnalyzer.KeyType.IP));
    }

    @Override
    public void onAfterUrlFileProcessed(File file) throws IOException {
        onAfterFileProcessed(incrementCounts.get(AnomalyAnalyzer.KeyType.URL));
    }
    
    @Override
    public void onIpUrlEvent(String ip, String url, String line) {
        processEvent(line, incrementCounts.get(AnomalyAnalyzer.KeyType.IP_URL));
    }

    @Override
    public void onIpEvent(String ip, String line) {
        processEvent(line, incrementCounts.get(AnomalyAnalyzer.KeyType.IP));
    }

    @Override
    public void onUrlEvent(String url, String line) {
        processEvent(line, incrementCounts.get(AnomalyAnalyzer.KeyType.URL));
    }
    
    private void processEvent(String line, List<Integer> incrementCounts) {
        String eventType = getMatch(EVENT_TYPE_PATTERN, line);
        String eventTime = getMatch(EVENT_TIME_PATTERN, line);
        if ("UPDATE".equals(eventType) && !anomalyStarted) {
            anomalyStarted = true;
            startTime = getRoundedTime(parseDate(eventTime).getTime());
        } else if ("RTN".equals(eventType) || "TOUT".equals(eventType)) {
            anomalyStarted = false;
            long anomalyEnd = getRoundedTime(parseDate(eventTime).getTime());
            int startingIncrement = getElapsedIncrements(DEFAULT_START_TIME, startTime);
            int elapsedIncrements = getElapsedIncrements(startTime, anomalyEnd);
            for (int i = startingIncrement; i < startingIncrement + elapsedIncrements; i++) {
                incrementCounts.set(i, incrementCounts.get(i) + 1);
            }
            endTime = anomalyEnd;
        }
    }
    
    private void onAfterFileProcessed (List<Integer> incrementCounts) {
        if (anomalyStarted) {
            anomalyStarted = false;
            long anomalyEnd = getRoundedTime(DEFAULT_END_TIME);
            int startingIncrement = getElapsedIncrements(DEFAULT_START_TIME, startTime);
            int elapsedIncrements = getElapsedIncrements(startTime, anomalyEnd);
            for (int i = startingIncrement; i < startingIncrement + elapsedIncrements; i++) {
                incrementCounts.set(i, incrementCounts.get(i) + 1);
            }
        }
        initTimes();
    }
    
    private void printTimeline () {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 11; j++) {
                System.out.print("+");
            }
            System.out.print((i + 1) % 10);
        }
        System.out.println();
    }
    
    private void initTimes () {
        startTime = DEFAULT_START_TIME;
        endTime = startTime;
    }
}
