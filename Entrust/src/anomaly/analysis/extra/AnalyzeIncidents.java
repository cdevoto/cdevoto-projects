package anomaly.analysis.extra;
import static util.AppUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;


public class AnalyzeIncidents extends AnomalyVisitorAdapter {
    
    private static Pattern URL_PATTERN = Pattern.compile("^([^\\.]+)\\.log$");
    private static Pattern IP_PATTERN = Pattern.compile("^([^\\.]+)\\.log$");
    private static Pattern IP_URL_PATTERN = Pattern.compile("^([^_]+)__([^\\.]*)\\.log$");
    private static Pattern EVENT_TYPE_PATTERN = Pattern.compile("\"eventType\"\\:\"GOMEZ\\.[^\\.]+\\.REFERENCE\\.AVAILABILITY\\.ANOMALY\\.([^\"]*)\"");
    private static Pattern EVENT_TIME_PATTERN = Pattern.compile("\"eventTime\"\\:\"([^\"]*)\"");

    private static final String HR1 = "***************************************************************************************************************************************************";
    private static final String HR2 = "---------------------------------------------------------------------------------------------------------------------------------------------------";
    private static final String SECTION_GAP = "\n\n\n";
    
    boolean verbose = false;
    boolean anomalyStarted = false;
    long startTime = 0L;

    public static void main(String[] args) throws IOException {
        new AnalyzeIncidents();
    }

    public AnalyzeIncidents () throws IOException {
        this(false);
    }
    
    public AnalyzeIncidents (boolean verbose) throws IOException {
        AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
        File parentDir = new File("G:/dec23_anomalies/entrust-anomalies");

        File ipUrlDir = new File(parentDir, "IPURL");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.IP_URL, ipUrlDir.listFiles());
        
        File ipDir = new File(parentDir, "IP");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.IP, ipDir.listFiles());

        File urlDir = new File(parentDir, "URL");
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.URL, urlDir.listFiles());
        
        analyzer.visitIpEvents(this);
        if (verbose) {
            System.out.println(SECTION_GAP);
        }
        analyzer.visitUrlEvents(this);
        if (verbose) {
            System.out.println(SECTION_GAP);
        }
        analyzer.visitIpUrlEvents(this);
    }
    
    @Override
    public void onBeforeIpUrlEventsProcessed() throws IOException {
        if (verbose) {
            System.out.println(HR1);
            System.out.println("IP_URL Incidents");
            System.out.println(HR1);
        }
    }
    
    @Override
    public void onBeforeIpUrlFileProcessed(File file) throws IOException {
        if (verbose) {
            System.out.println(HR2);
            System.out.println(extractIpUrlFromFileName(file.getName()));
            System.out.println(HR2);
        }
    }

    @Override
    public void onAfterIpUrlFileProcessed(File file) throws IOException {
        if (anomalyStarted) {
            System.out.println("\"endTime\"=\"-\" \"resolutionType\"=\"NONE\" }");
            anomalyStarted = false;
            startTime = 0L;
        }
        if (verbose) {
            System.out.println(HR2);
        }
    }

    @Override
    public void onBeforeIpEventsProcessed() throws IOException {
        if (verbose) {
            System.out.println(HR1);
            System.out.println("IP Incidents");
            System.out.println(HR1);
        }
    }

    @Override
    public void onBeforeIpFileProcessed(File file) throws IOException {
        if (verbose) {
            System.out.println(HR2);
            System.out.println(extractIpFromFileName(file.getName()));
            System.out.println(HR2);
        }
    }

    @Override
    public void onAfterIpFileProcessed(File file) throws IOException {
        if (anomalyStarted) {
            System.out.println("\"endTime\"=\"-\" \"resolutionType\"=\"NONE\" }");
            anomalyStarted = false;
            startTime = 0L;
        }
        if (verbose) {
            System.out.println(HR2);
        }
    }

    @Override
    public void onBeforeUrlEventsProcessed() throws IOException {
        if (verbose) {
            System.out.println(HR1);
            System.out.println("URL Incidents");
            System.out.println(HR1);
        }
    }

    @Override
    public void onBeforeUrlFileProcessed(File file) throws IOException {
        if (verbose) {
            System.out.println(HR2);
            System.out.println(extractUrlFromFileName(file.getName()));
            System.out.println(HR2);
        }
    }
    
    @Override
    public void onAfterUrlFileProcessed(File file) throws IOException {
        if (anomalyStarted) {
            System.out.println("\"endTime\"=\"-\", \"duration\"=\"-\", \"resolutionType\"=\"NONE\" }");
            anomalyStarted = false;
            startTime = 0L;
        }
        if (verbose) {
            System.out.println(HR2);
        }
    }
    
    @Override
    public void onIpUrlEvent(String ip, String url, String line) {
        processEvent(line, "\"objType\"=\"IP_URL\", \"ip\"=\"" + ip + "\", \"url\"=\"" + url + "\", ");
    }

    @Override
    public void onIpEvent(String ip, String line) {
        processEvent(line, "\"objType\"=\"IP\", \"ip\"=\"" + ip + "\", ");
    }

    @Override
    public void onUrlEvent(String url, String line) {
        processEvent(line, "\"objType\"=\"URL\", \"url\"=\"" + url + "\", ");
    }
    
    private void processEvent(String line, String idString) {
        String eventType = getMatch(EVENT_TYPE_PATTERN, line);
        String eventTime = getMatch(EVENT_TIME_PATTERN, line);
        if ("UPDATE".equals(eventType) && !anomalyStarted) {
            anomalyStarted = true;
            startTime = parseDate(eventTime).getTime();
            System.out.print("{ \"type\"=\"incident\", " + idString + "\"startTime\"=\"" + eventTime + "\", ");
        } else if ("RTN".equals(eventType)) {
            anomalyStarted = false;
            long endTime = parseDate(eventTime).getTime();
            System.out.println("\"endTime\"=\"" + eventTime + "\", \"duration\"=\"" + formatElapsedTime(endTime - startTime) + "\", \"resolutionType\"=\"RTN\" }");
            startTime = 0L;
        } else if ("TOUT".equals(eventType)) {
            anomalyStarted = false;
            long endTime = parseDate(eventTime).getTime();
            System.out.println("\"endTime\"=\"" + eventTime + "\", \"duration\"=\"" + formatElapsedTime(endTime - startTime) + "\", \"resolutionType\"=\"TOUT\" }");
            startTime = 0L;
        }
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
        return "IP_URL: " + m.group(1).replace("-", ".").toLowerCase() + "|" + m.group(2).replace("-", ".");
    }

    private String extractUrlFromFileName (String fileName) {
        Matcher m = URL_PATTERN.matcher(fileName);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return "URL: " + m.group(1).replace("-", ".").toLowerCase();
    }
    
    private String extractIpFromFileName (String fileName) {
        Matcher m = IP_PATTERN.matcher(fileName);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return "IP: " + m.group(1).replace("-", ".");
    }
}
