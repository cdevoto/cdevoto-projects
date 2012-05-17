package incident.analyzer;

import java.io.IOException;

public interface IncidentVisitor {

    void onIncident (String observed, String incident) throws IOException;
    
}
