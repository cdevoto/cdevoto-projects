package anomaly.analyzer;

import java.io.File;
import java.io.IOException;

public interface IpEventVisitor {

    void onIpEvent(String ip, String line) throws IOException;
    void onBeforeIpEventsProcessed() throws IOException;
    void onBeforeIpFileProcessed(File file) throws IOException;
    void onAfterIpFileProcessed(File file) throws IOException;
    void onAfterIpEventsProcessed() throws IOException;

}
