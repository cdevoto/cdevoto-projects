package anomaly.analysis.extra;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import anomaly.analyzer.AnomalyAnalyzer;
import anomaly.analyzer.AnomalyVisitorAdapter;


public class ReferencedKeys extends AnomalyVisitorAdapter {
    
    private Set<String> ips = new TreeSet<String>();
    private Set<String> urls = new TreeSet<String>();
    private Set<IpUrl> ipUrls = new TreeSet<IpUrl>();
    
    public static void main(String[] args) throws IOException {
        new ReferencedKeys();
    }

    public ReferencedKeys () throws IOException {
        AnomalyAnalyzer analyzer = new AnomalyAnalyzer();
        analyzer.configureEventLogsByPath(AnomalyAnalyzer.KeyType.IP, "G:/dec23_anomalies/entrust-anomalies/IP.txt");
        analyzer.configureEventLogsByPath(AnomalyAnalyzer.KeyType.URL, "G:/dec23_anomalies/entrust-anomalies/URL.txt");
        analyzer.configureEventLogsByPath(AnomalyAnalyzer.KeyType.IP_URL, "G:/dec23_anomalies/entrust-anomalies/IPURL.txt");
        
        analyzer.visitIpEvents(this);
        analyzer.visitUrlEvents(this);
        analyzer.visitIpUrlEvents(this);
    }
    
    public Set<String> getIps() {
        return ips;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public Set<IpUrl> getIpUrls() {
        return ipUrls;
    }

    @Override
    public void onIpUrlEvent(String ip, String url, String line) {
        ipUrls.add(new IpUrl(ip, url));
    }

    @Override
    public void onIpEvent(String ip, String line) {
        ips.add(ip);
    }

    @Override
    public void onUrlEvent(String url, String line) {
        urls.add(url);
    }

    @Override
    public void onAfterIpUrlEventsProcessed() {
        System.out.println("---------------------------------------------------------");
        for (IpUrl ipUrl : ipUrls) {
            System.out.println(ipUrl);
        }
        System.out.println("\nTotal IP_URLs: " + ipUrls.size());
    }

    @Override
    public void onAfterIpEventsProcessed() {
        System.out.println("---------------------------------------------------------");
        for (String ip : ips) {
            System.out.println(ip);
        }
        System.out.println("\nTotal IPs: " + ips.size());
    }

    @Override
    public void onAfterUrlEventsProcessed() {
        System.out.println("---------------------------------------------------------");
        for (String url : urls) {
            System.out.println(url);
        }
        System.out.println("\nTotal URLs: " + urls.size());
    }
}
