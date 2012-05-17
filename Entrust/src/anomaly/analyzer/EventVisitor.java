package anomaly.analyzer;

import java.io.File;
import java.io.IOException;

public interface EventVisitor {

    void onEvent(String line) throws IOException;
    void onBeforeEventsProcessed() throws IOException;
    void onBeforeFileProcessed(File file) throws IOException;
    void onAfterFileProcessed(File file) throws IOException;
    void onAfterEventsProcessed() throws IOException;

}
