package anomaly.analyzer;

import java.io.File;
import java.io.IOException;

public interface IpUrlEventVisitor {

    void onIpUrlEvent(String ip, String url, String line) throws IOException;
    void onBeforeIpUrlEventsProcessed() throws IOException;
    void onBeforeIpUrlFileProcessed(File file) throws IOException;
    void onAfterIpUrlFileProcessed(File file) throws IOException;
    void onAfterIpUrlEventsProcessed() throws IOException;

}
