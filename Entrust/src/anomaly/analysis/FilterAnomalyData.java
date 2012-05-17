package anomaly.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import main.Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import util.AppUtil;


public class FilterAnomalyData {
    
    private static Pattern FILE_NAME_PATTERN = Pattern.compile("Avail([^\\-]+)\\-");
    private static Pattern URL_PATTERN = Pattern.compile("\"url\"\\:\"([^\"]*)\"");
    private static Pattern IP_PATTERN = Pattern.compile("\"ipAddress\"\\:\"([^\"]*)\"");
    
    private String rootDir;
    private Set<Pattern> urlFilters = new LinkedHashSet<Pattern>();
    private Set<String> ipFilters = new TreeSet<String>();
    
    
    public static void main(String[] args) throws Exception {
        Properties appProperties = AppUtil.getApplicationProperties(Main.class);
        new FilterAnomalyData(appProperties);
    }
    
    public FilterAnomalyData (Properties appProperties) throws IOException {
        initializeProperties(appProperties);
        

        Map<KeyType, List<File>> inputFilesByKeyType = getInputFilesByKeyType();
        processFilesOfType(inputFilesByKeyType, KeyType.IP_URL);        
        processFilesOfType(inputFilesByKeyType, KeyType.URL);        
        processFilesOfType(inputFilesByKeyType, KeyType.IP);        

        
    }
    
    private void initializeProperties(Properties appProperties) {
        this.rootDir = appProperties.getProperty(AppUtil.PROP_ROOT_DIRECTORY);
        if (this.rootDir == null) {
            throw new RuntimeException("Expected a 'rootDirectory' property");
        }
        String urlFilters = appProperties.getProperty(AppUtil.PROP_URL_FILTERS);
        if (urlFilters != null) {
            JSONArray jsonFilters = (JSONArray) JSONValue.parse(urlFilters);
            for (int i = 0; i < jsonFilters.size(); i++) {
                Pattern pattern = Pattern.compile((String) jsonFilters.get(i));
                this.urlFilters.add(pattern);
            }
        }
    }

    private Map<KeyType, List<File>> getInputFilesByKeyType() {
        File inputDir = new File(rootDir);
        Map<KeyType, List<File>> inputFilesByKeyType = new HashMap<KeyType, List<File>>();
        for (File child : inputDir.listFiles()) {
            KeyType keyType = getKeyType(child);
            if (keyType == null) {
                continue;
            }
            List<File> inputFiles = inputFilesByKeyType.get(keyType);
            if (inputFiles == null) {
                inputFiles = new LinkedList<File>();
                inputFilesByKeyType.put(keyType, inputFiles);
            }
            inputFiles.add(child);
        }
        return inputFilesByKeyType;
    }

    private void processFilesOfType (Map<KeyType, List<File>> inputFilesByKeyType, KeyType keyType) throws IOException {
        File anomalyOutputDir = AppUtil.getAnomalyDetectorOutputDir(rootDir);
        anomalyOutputDir.mkdirs();

        List<File> ipUrlFiles = inputFilesByKeyType.get(keyType);
        if (ipUrlFiles != null && !ipUrlFiles.isEmpty()) {
            PrintWriter out = null;
            try {
                out = new PrintWriter(new FileWriter(new File(anomalyOutputDir, keyType.toString() + ".txt")));
                for (File file : ipUrlFiles) {
                    processFile(file, out, keyType);
                }
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
        
    }

    private void processFile(File file, PrintWriter out, KeyType keyType) throws IOException {
        System.out.println("Processing file " + file.getName() + "...");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                if (keyType.url) {
                    String url = AppUtil.getMatch(URL_PATTERN, line);
                    boolean matchesFilter = matchesUrlFilter(url);
                    if (matchesFilter) {
                        out.println(line);
                        out.flush();
                        if (keyType.ip) {
                            String ip = AppUtil.getMatch(IP_PATTERN, line);
                            if (!ip.trim().isEmpty() && !ip.startsWith("0")) {
                                ipFilters.add(ip);
                            }    
                        }
                    }
                } else {
                    String ip = AppUtil.getMatch(IP_PATTERN, line);
                    if (matchesIpFilter(ip)) {
                        out.println(line);
                        out.flush();
                    }
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private KeyType getKeyType(File child) {
        try {
            String type = AppUtil.getMatch(FILE_NAME_PATTERN, child.getName());
            if (type == null) {
                return null;
            } else if ("IP".equals(type)) {
                return KeyType.IP;
            } else if ("IPURL".equals(type)) {
                return KeyType.IP_URL;
            } else if ("URL".equals(type)) {
                return KeyType.URL;
            }
        } catch (Exception ex) {}
        return null;
    }

    private boolean matchesUrlFilter(String url) {
        if (urlFilters.isEmpty()) {
            return true;
        }
        boolean matchesFilter = false;
        for (Pattern filter : urlFilters) {
            if (filter.matcher(url).find()) {
                matchesFilter = true;
                break;
            }
        }
        return matchesFilter;
    }

    private boolean matchesIpFilter(String ip) {
        if (urlFilters.isEmpty()) {
            return true;
        }
        return ipFilters.contains(ip);
    }

    private static class KeyType {
        public static final KeyType IP = new KeyType("IP", false, true);
        public static final KeyType URL = new KeyType("URL", true, false);
        public static final KeyType IP_URL = new KeyType("IPURL", true, true);
        
        public String name;
        public boolean url;
        public boolean ip;
        
        public KeyType(String name, boolean url, boolean ip) {
            this.name = name;
            this.url = url;
            this.ip = ip;
        }
    
        @Override
        public String toString() {
            return name;
        }
    }

}
