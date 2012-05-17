package anomaly.analysis.extra;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;


public class GenerateKeyFiles extends AnomalyVisitorAdapter {
    
    private static final Pattern HOST_NAME = Pattern.compile("[^\\/]+\\.entrust\\.net");
    
    private Map<IpUrl, PrintWriter> ipUrlWriters = new LinkedHashMap<IpUrl, PrintWriter>();
    private Map<String, PrintWriter> ipWriters = new LinkedHashMap<String, PrintWriter>();
    private Map<String, PrintWriter> urlWriters = new LinkedHashMap<String, PrintWriter>();
    
    
    public static void main(String[] args) throws IOException {
        new GenerateKeyFiles();
    }

    public GenerateKeyFiles () throws IOException {
        ReferencedKeys keys = new ReferencedKeys();
        
        try {
            File parentDir = new File("G:/dec23_anomalies/entrust-anomalies");
            File ipUrlDir = new File(parentDir, "IPURL");
            if (!ipUrlDir.exists()) {
                ipUrlDir.mkdir();
            } else {
                for (File f : ipUrlDir.listFiles()) {
                    f.delete();
                }
            }
            File ipDir = new File(parentDir, "IP");
            if (!ipDir.exists()) {
                ipDir.mkdir();
            } else {
                for (File f : ipDir.listFiles()) {
                    f.delete();
                }
            }
            File urlDir = new File(parentDir, "URL");
            if (!urlDir.exists()) {
                urlDir.mkdir();
            } else {
                for (File f : urlDir.listFiles()) {
                    f.delete();
                }
            }
            
            for (IpUrl ipUrl : keys.getIpUrls()) {
                PrintWriter out = new PrintWriter(new FileWriter("G:/dec23_anomalies/entrust-anomalies/IPURL/" + extractFileNameFromIpUrl(ipUrl) + ".log"));
                ipUrlWriters.put(ipUrl, out);
            }
            
            for (String ip : keys.getIps()) {
                PrintWriter out = new PrintWriter(new FileWriter("G:/dec23_anomalies/entrust-anomalies/IP/" + extractFileNameFromIp(ip) + ".log"));
                ipWriters.put(ip, out);
            }

            for (String url : keys.getUrls()) {
                PrintWriter out = new PrintWriter(new FileWriter("G:/dec23_anomalies/entrust-anomalies/URL/" + extractFileNameFromUrl(url) + ".log"));
                urlWriters.put(url, out);
            }

            AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
            analyzer.configureEventLogsByPath(AnomalyAnalyzer.KeyType.IP, "G:/dec23_anomalies/entrust-anomalies/IP.txt");
            analyzer.configureEventLogsByPath(AnomalyAnalyzer.KeyType.URL, "G:/dec23_anomalies/entrust-anomalies/URL.txt");
            analyzer.configureEventLogsByPath(AnomalyAnalyzer.KeyType.IP_URL, "G:/dec23_anomalies/entrust-anomalies/IPURL.txt");
            
            analyzer.visitIpEvents(this);
            analyzer.visitUrlEvents(this);
            analyzer.visitIpUrlEvents(this);
        } finally {
            for (PrintWriter out : ipUrlWriters.values()) {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (PrintWriter out : ipWriters.values()) {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (PrintWriter out : urlWriters.values()) {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    public void onIpUrlEvent(String ip, String url, String line) {
        IpUrl ipUrl = new IpUrl(ip, url);
        PrintWriter out = ipUrlWriters.get(ipUrl);
        out.println(line);
        out.flush();
    }

    @Override
    public void onIpEvent(String ip, String line) {
        PrintWriter out = ipWriters.get(ip);
        out.println(line);
        out.flush();
    }

    @Override
    public void onUrlEvent(String url, String line) {
        PrintWriter out = urlWriters.get(url);
        out.println(line);
        out.flush();
    }

    private String extractFileNameFromIpUrl (IpUrl ipUrl) {
        return extractFileNameFromUrl(ipUrl.getUrl()) + "__" + extractFileNameFromIp(ipUrl.getIp());
    }

    private String extractFileNameFromUrl (String url) {
        Matcher m = HOST_NAME.matcher(url);
        if (!m.find()) {
            throw new IllegalArgumentException();
        }
        return m.group(0).replace(".", "-").toUpperCase();    
    }
    
    private String extractFileNameFromIp (String ip) {
        return ip.replace(".", "-");
    }
}
