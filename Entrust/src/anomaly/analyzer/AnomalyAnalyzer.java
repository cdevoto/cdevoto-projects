package anomaly.analyzer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnomalyAnalyzer {
    public static enum KeyType {
        IP,
        URL,
        IP_URL,
        NONE
    };

    private static Pattern URL_PATTERN = Pattern.compile("\"url\"\\:\"([^\"]*)\"");
    private static Pattern IP_PATTERN = Pattern.compile("\"ipAddress\"\\:\"([^\"]*)\"");
    
    private Map<KeyType, Set<File>> logFiles = new HashMap<KeyType, Set<File>>();
    
    public AnomalyAnalyzer () {
        logFiles.put(KeyType.IP, new LinkedHashSet<File>());
        logFiles.put(KeyType.URL, new LinkedHashSet<File>());
        logFiles.put(KeyType.IP_URL, new LinkedHashSet<File>());
        logFiles.put(KeyType.NONE, new LinkedHashSet<File>());
    }
    
    
    public void configureEventLogsByPath(KeyType keyType, Set<String> paths) {
        Set<File> fileSet = new LinkedHashSet<File>();
        for (String path : paths) {
            fileSet.add(new File(path));
        }
        configureEventLogs(keyType, fileSet);
    }
    
    public void configureEventLogsByPath(KeyType keyType, String ... paths) {
        Set<File> fileSet = new LinkedHashSet<File>();
        for (String path : paths) {
            fileSet.add(new File(path));
        }
        configureEventLogs(keyType, fileSet);
    }
    
    public void configureEventLogs(KeyType keyType, Set<File> files) {
        if (keyType == null || logFiles == null) {
            throw new NullPointerException();
        }
        Set<File> fileSet = logFiles.get(keyType);
        fileSet.addAll(files);
    }
    
    public void configureEventLogs(KeyType keyType, File ... files) {
        if (keyType == null || logFiles == null) {
            throw new NullPointerException();
        }
        Set<File> fileSet = logFiles.get(keyType);
        for (File file : files) {
            fileSet.add(file);
        }
    }

    public void visitEvents(EventVisitor visitor)
            throws FileNotFoundException, IOException {
        visitor.onBeforeEventsProcessed();
        Set<File> fileSet = logFiles.get(KeyType.NONE);
        if (fileSet.isEmpty()) {
            throw new IllegalStateException(
                    "You must first configure the event logs for the NONE key type.");
        }
        for (File f : fileSet) {
            visitor.onBeforeFileProcessed(f);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(f));
                for (String line = in.readLine(); line != null; line = in
                        .readLine()) {
                    visitor.onEvent(line);
                }
                visitor.onAfterFileProcessed(f);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        visitor.onAfterEventsProcessed();
    }

    public void visitIpUrlEvents(IpUrlEventVisitor visitor) throws FileNotFoundException,
            IOException {
        visitor.onBeforeIpUrlEventsProcessed();
        Set<File> fileSet = logFiles.get(KeyType.IP_URL);
        if (fileSet.isEmpty()) {
            throw new IllegalStateException("You must first configure the event logs for the IP_URL key type.");
        }
        for (File f : fileSet) {
            visitor.onBeforeIpUrlFileProcessed(f);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(f));
                for (String line = in.readLine(); line != null; line = in
                        .readLine()) {
                    Matcher m1 = URL_PATTERN.matcher(line);
                    if (!m1.find()) {
                        throw new RuntimeException("Expected a URL:\n\t" + line);
                    }
                    String url = m1.group(1);
                    m1 = IP_PATTERN.matcher(line);
                    if (!m1.find()) {
                        throw new RuntimeException("Expected an IP:\n\t" + line);
                    }
                    String ip = m1.group(1);
                    visitor.onIpUrlEvent(ip, url, line);
                }
                visitor.onAfterIpUrlFileProcessed(f);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        visitor.onAfterIpUrlEventsProcessed();
    }

    public void visitIpEvents(IpEventVisitor visitor) throws FileNotFoundException, IOException {
        visitor.onBeforeIpEventsProcessed();
        Set<File> fileSet = logFiles.get(KeyType.IP);
        if (fileSet.isEmpty()) {
            throw new IllegalStateException("You must first configure the log files for the IP key type.");
        }
        for (File f : fileSet) {
            visitor.onBeforeIpFileProcessed(f);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(f));
                for (String line = in.readLine(); line != null; line = in
                        .readLine()) {
                    Matcher m1 = IP_PATTERN.matcher(line);
                    if (!m1.find()) {
                        throw new RuntimeException("Expected an IP:\n\t" + line);
                    }
                    String ip = m1.group(1);
                    visitor.onIpEvent(ip, line);
                }
                visitor.onAfterIpFileProcessed(f);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        visitor.onAfterIpEventsProcessed();
    }

    public void visitUrlEvents(UrlEventVisitor visitor) throws FileNotFoundException,
            IOException {
        visitor.onBeforeUrlEventsProcessed();
        Set<File> fileSet = logFiles.get(KeyType.URL);
        if (fileSet.isEmpty()) {
            throw new IllegalStateException("You must first configure the log files for the URL key type.");
        }
        for (File f : fileSet) {
            visitor.onBeforeUrlFileProcessed(f);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(f));
                for (String line = in.readLine(); line != null; line = in
                        .readLine()) {
                    Matcher m1 = URL_PATTERN.matcher(line);
                    if (!m1.find()) {
                        throw new RuntimeException("Expected an URL:\n\t" + line);
                    }
                    String url = m1.group(1);
                    visitor.onUrlEvent(url, line);
                }
                visitor.onAfterUrlFileProcessed(f);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        visitor.onAfterUrlEventsProcessed();
    }

}
