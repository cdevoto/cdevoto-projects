package anomaly.analysis;

import static util.AppUtil.DATE_FORMAT;
import static util.AppUtil.EVENT_TIME_PATTERN;
import static util.AppUtil.EVENT_TYPE_PATTERN;
import static util.AppUtil.QUEUE_TIME_PATTERN;
import static util.AppUtil.getMatch;
import static util.AppUtil.parseDate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import main.Main;
import util.AppUtil;
import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;

public class SortAnomalies extends AnomalyVisitorAdapter {
    
    private static Map<String, Integer> eventTypeIndeces = new HashMap<String, Integer>();
    
    private Set<Entry> anomalies = new TreeSet<Entry>();
    private PrintWriter out;
    private String inputDir;
    
    
    static {
        eventTypeIndeces.put("UPDATE", 1);
        eventTypeIndeces.put("DECAY", 2);
        eventTypeIndeces.put("RTN", 3);
        eventTypeIndeces.put("TOUT", 4);
    }
    
    public static void main(String[] args) throws Exception {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new SortAnomalies(appProperties);
        
    }
    
    public SortAnomalies (Properties appProperties) throws IOException {
        this.inputDir = AppUtil.getInputDirPath(appProperties);
        File outputDir = AppUtil.getAnomalyDetectorOutputDir(inputDir);
        AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
        analyzer.configureEventLogs(AnomalyAnalyzer.KeyType.NONE,
                new File [] { new File(outputDir, "IP.txt"),
                              new File(outputDir, "URL.txt"),
                              new File(outputDir, "IPURL.txt") });
        try {
            out = new PrintWriter(new FileWriter(new File(outputDir, "ALL-SORTED.txt")));
            analyzer.visitEvents(this);
            for (Entry entry : anomalies) {
                String line = entry.toString();
                System.out.println(line);
                out.println(line);
                out.flush();
            }
        } finally {
            if (out == null) {
                out.close();
            }
        }
    }
    
    @Override
    public void onEvent(String line) {
        System.out.println(line);
        long queueTime = parseDate(getMatch(QUEUE_TIME_PATTERN, line)).getTime();
        Date eventTimeDate = parseDate(getMatch(EVENT_TIME_PATTERN, line));
        long eventTime = eventTimeDate.getTime();
        String eventType = getMatch(EVENT_TYPE_PATTERN, line);
        if (queueTime != eventTime) {
            line = AppUtil.replace(QUEUE_TIME_PATTERN, line, "\"onQueueTime\"\\:\"" + DATE_FORMAT.format(eventTimeDate) + "\"");
        }    
        anomalies.add(new Entry(queueTime, eventTime, eventType, line));
    }
    
    private static class Entry implements Comparable<Entry> {
        private long queueTime;
        private long eventTime;
        private String eventType;
        private String line;
        
        public Entry(long queueTime, long eventTime, String eventType, String line) {
            super();
            this.queueTime = queueTime;
            this.eventTime = eventTime;
            this.eventType = eventType;
            this.line = line;
        }
        



        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (eventTime ^ (eventTime >>> 32));
            result = prime * result
                    + ((eventType == null) ? 0 : eventType.hashCode());
            result = prime * result + ((line == null) ? 0 : line.hashCode());
            result = prime * result + (int) (queueTime ^ (queueTime >>> 32));
            return result;
        }




        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Entry other = (Entry) obj;
            if (eventTime != other.eventTime)
                return false;
            if (eventType == null) {
                if (other.eventType != null)
                    return false;
            } else if (!eventType.equals(other.eventType))
                return false;
            if (line == null) {
                if (other.line != null)
                    return false;
            } else if (!line.equals(other.line))
                return false;
            if (queueTime != other.queueTime)
                return false;
            return true;
        }




        @Override
        public int compareTo(Entry o) {
            long result = eventTime - o.eventTime;
            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                result = SortAnomalies.eventTypeIndeces.get(eventType) - SortAnomalies.eventTypeIndeces.get(o.eventType);
                if (result < 0) {
                    return -1;
                } else if (result > 0) {
                    return 1;
                } else {
                    result = queueTime - o.queueTime;
                    if (result < 0) {
                        return -1;
                    } else if (result > 0) {
                        return 1;
                    }
                }
            }
            return line.compareTo(o.line);
        }
        
        public String toString () {
            return line;
        }
        
    }

}
