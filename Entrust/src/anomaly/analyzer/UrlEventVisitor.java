package anomaly.analyzer;

import java.io.File;
import java.io.IOException;

public interface UrlEventVisitor {

    void onUrlEvent(String url, String line) throws IOException;
    void onBeforeUrlEventsProcessed() throws IOException;
    void onBeforeUrlFileProcessed(File file) throws IOException;
    void onAfterUrlFileProcessed(File file) throws IOException;
    void onAfterUrlEventsProcessed() throws IOException;

}
