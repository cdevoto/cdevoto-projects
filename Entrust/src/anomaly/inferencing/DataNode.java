package anomaly.inferencing;

import java.util.List;


public interface DataNode {
    
    NodeType getNodeType ();
    String getNodeId ();
    List<Incident> getIncidents ();
    boolean isActivated ();

}
