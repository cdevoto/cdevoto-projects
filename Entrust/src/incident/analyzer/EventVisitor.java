package incident.analyzer;

import java.io.IOException;

public interface EventVisitor {

    void onEvent (String observed, String incident) throws IOException;
    
}
