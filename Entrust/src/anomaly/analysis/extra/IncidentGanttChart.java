package anomaly.analysis.extra;
import static util.AppUtil.DEFAULT_END_TIME;
import static util.AppUtil.DEFAULT_START_TIME;
import static util.AppUtil.EVENT_TIME_PATTERN;
import static util.AppUtil.EVENT_TYPE_PATTERN;
import static util.AppUtil.getElapsedIncrements;
import static util.AppUtil.getRoundedTime;
import static util.AppUtil.pad;
import static util.AppUtil.parseDate;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;


public class IncidentGanttChart extends AnomalyVisitorAdapter {
    

    private static Pattern URL_PATTERN = Pattern.compile("^([^\\.]+)\\.log$");
    private static Pattern IP_PATTERN = Pattern.compile("^([^\\.]+)\\.log$");
    private static Pattern IP_URL_PATTERN = Pattern.compile("^([^_]+)__([^\\.]*)\\.log$");

    private static final String HR1 = "****************************************************************************************************************************************************************************************************************************************************************************************************************************************";
    private static final String SECTION_GAP = "\n\n\n";
    
    private List<String> ipFilter = new LinkedList<String>();
    private List<String> urlFilter = new LinkedList<String>();
    private boolean fileProcessed = false;
    boolean anomalyStarted = false;
    long startTime = DEFAULT_START_TIME;
    long endTime = startTime;

    public static void main(String[] args) throws IOException {
        String [] urlFilter = null, ipFilter = null;

        urlFilter = new String [] { "https://evupdater\\.entrust\\.net" };
        ipFilter = new String [] { "216\\.191\\.247\\.147", "216\\.191\\.247\\.211" };

        new IncidentGanttChart(urlFilter, ipFilter);
    }
    
    public IncidentGanttChart (String [] urlFilter, String [] ipFilter) throws IOException {

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
        
        AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
        File parentDir = new File("G:/dec23_anomalies/entrust-anomalies");

        File ipUrlDir = new File(parentDir, "IPURL");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.IP_URL, ipUrlDir.listFiles());
        
        File ipDir = new File(parentDir, "IP");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.IP, ipDir.listFiles());

        File urlDir = new File(parentDir, "URL");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.URL, urlDir.listFiles());
        
        analyzer.visitIpEvents(this);
        System.out.println(SECTION_GAP);
        analyzer.visitUrlEvents(this);
        System.out.println(SECTION_GAP);
        analyzer.visitIpUrlEvents(this);
        
    }
    
    @Override
    public void onBeforeIpUrlEventsProcessed() throws IOException {
        System.out.println(HR1);
        System.out.println("IP_URL Incidents");
        System.out.println(HR1);
        System.out.print(pad(""));
        printTimeline();
    }
    
    @Override
    public void onBeforeIpUrlFileProcessed(File file) throws IOException {
        String ipUrl = extractIpUrlFromFileName(file.getName());
        String [] fields = ipUrl.split("\\|");
        String url = fields[0];
        if (!urlMatchesFilter(url, true)) {
            fileProcessed = false;
            return;
        }
        String ip = fields[1];
        if (!ipMatchesFilter(ip)) {
            fileProcessed = false;
            return;
        }
        fileProcessed = true;
        System.out.print(pad(ipUrl));
    }

    @Override
    public void onAfterIpUrlFileProcessed(File file) throws IOException {
        onAfterFileProcessed();
    }

    @Override
    public void onBeforeIpEventsProcessed() throws IOException {
        System.out.println(HR1);
        System.out.println("IP Incidents");
        System.out.println(HR1);
        System.out.print(pad(""));
        printTimeline();
    }

    @Override
    public void onBeforeIpFileProcessed(File file) throws IOException {
        String ip = extractIpFromFileName(file.getName());
        if (!ipMatchesFilter(ip)) {
            fileProcessed = false;
            return;
        }
        fileProcessed = true;
        System.out.print(pad(ip));
    }

    @Override
    public void onAfterIpFileProcessed(File file) throws IOException {
        onAfterFileProcessed();
    }

    @Override
    public void onBeforeUrlEventsProcessed() throws IOException {
        System.out.println(HR1);
        System.out.println("URL Incidents");
        System.out.println(HR1);
        System.out.print(pad(""));
        printTimeline();
    }

    @Override
    public void onBeforeUrlFileProcessed(File file) throws IOException {
        String url = extractUrlFromFileName(file.getName());
        if (!urlMatchesFilter(url, true)) {
            fileProcessed = false;
            return;
        }
        fileProcessed = true;
        System.out.print(pad(url));
    }
    
    @Override
    public void onAfterUrlFileProcessed(File file) throws IOException {
        onAfterFileProcessed();
    }
    
    @Override
    public void onIpUrlEvent(String ip, String url, String line) {
        if (!urlMatchesFilter(url, false) || !ipMatchesFilter(ip)) {
            return;
        }
        processEvent(line);
    }

    @Override
    public void onIpEvent(String ip, String line) {
        if (!ipMatchesFilter(ip)) {
            return;
        }
        processEvent(line);
    }

    @Override
    public void onUrlEvent(String url, String line) {
        if (!urlMatchesFilter(url, false)) {
            return;
        }
        processEvent(line);
    }
    
    private void processEvent(String line) {
        String eventType = getMatch(EVENT_TYPE_PATTERN, line);
        String eventTime = getMatch(EVENT_TIME_PATTERN, line);
        if ("UPDATE".equals(eventType) && !anomalyStarted) {
            anomalyStarted = true;
            startTime = getRoundedTime(parseDate(eventTime).getTime());
            int elapsedIncrements = getElapsedIncrements(endTime, startTime);
            for (int i = 0; i < elapsedIncrements; i++) {
                System.out.print(" ");
            }
        } else if ("RTN".equals(eventType)) {
            anomalyStarted = false;
            long anomalyEnd = getRoundedTime(parseDate(eventTime).getTime());
            int elapsedIncrements = getElapsedIncrements(startTime, anomalyEnd) - 1;
            for (int i = 0; i < elapsedIncrements; i++) {
                System.out.print("*");
            }
            System.out.print("R");
            endTime = anomalyEnd;
        } else if ("TOUT".equals(eventType)) {
            anomalyStarted = false;
            long anomalyEnd = getRoundedTime(parseDate(eventTime).getTime());
            int elapsedIncrements = getElapsedIncrements(startTime, anomalyEnd) - 1;
            for (int i = 0; i < elapsedIncrements; i++) {
                System.out.print("*");
            }
            System.out.print("T");
            endTime = anomalyEnd;
        }
    }

    private boolean ipMatchesFilter(String ip) {
        if (!ipFilter.isEmpty()) {
            boolean matchFound = false;
            for (String expression : ipFilter) {
                try {
                    if (ip.matches(expression)) {
                        matchFound = true;
                        break;
                    }
                } catch (Exception e) {}
            }
            return matchFound;
        }
        return true;
    }

    private boolean urlMatchesFilter(String url, boolean domainOnly) {
        if (!urlFilter.isEmpty()) {
            boolean matchFound = false;
            for (String expression : urlFilter) {
                if (domainOnly) {
                    expression = expression.substring(expression.lastIndexOf("/") + 1);
                }
                try {
                    if (url.matches(expression)) {
                        matchFound = true;
                        break;
                    }
                } catch (Exception e) {}
            }
            return matchFound;
        }
        return true;
    }
    
    private void onAfterFileProcessed () {
        if (!fileProcessed) {
            return;
        }
        if (anomalyStarted) {
            anomalyStarted = false;
            long anomalyEnd = getRoundedTime(DEFAULT_END_TIME);
            int elapsedIncrements = getElapsedIncrements(startTime, anomalyEnd) - 1;
            for (int i = 0; i < elapsedIncrements; i++) {
                System.out.print("*");
            }
            System.out.print("N");
        }
        System.out.println();
        initTimes();
    }
    
    private String getMatch(Pattern pattern, String line) {
        Matcher m = pattern.matcher(line);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return m.group(1);
        
    }
    
    private String extractIpUrlFromFileName (String fileName) {
        Matcher m = IP_URL_PATTERN.matcher(fileName);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return m.group(1).replace("-", ".").toLowerCase() + "|" + m.group(2).replace("-", ".");
    }

    private String extractUrlFromFileName (String fileName) {
        Matcher m = URL_PATTERN.matcher(fileName);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return m.group(1).replace("-", ".").toLowerCase();
    }
    
    private String extractIpFromFileName (String fileName) {
        Matcher m = IP_PATTERN.matcher(fileName);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return m.group(1).replace("-", ".");
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
