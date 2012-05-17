package anomaly.analysis.extra;
public class IpUrl implements Comparable<IpUrl> {
    private String ip;
    private String url;
    
    public IpUrl(String ip, String url) {
        super();
        this.ip = ip;
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        IpUrl other = (IpUrl) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public int compareTo(IpUrl o) {
        int result = url.compareTo(o.url);
        if (result != 0) {
            return result;
        }
        return ip.compareTo(o.ip);
    }
    
    @Override
    public String toString() {
        return url + "|" + ip;
    }
    
}
