package anomaly.inferencing;

import static util.AppUtil.formatElapsedTime;
import static util.AppUtil.parseDate;

import java.util.LinkedList;
import java.util.List;

public abstract class ConcreteNode implements DataNode {
    
    protected NodeConfig config;
    protected NodeType type;
    protected String id;
    protected boolean activated = false;
    protected List<Incident> incidents = new LinkedList<Incident>();
    protected Incident tail = null;
    
    public ConcreteNode (NodeType type, String id, NodeConfig config) {
        this.type = type;
        this.id = id;
        this.config = config;
    }

    @Override
    public NodeType getNodeType() {
        return this.type;
    }

    @Override
    public String getNodeId() {
        return this.id;
    }

    @Override
    public List<Incident> getIncidents() {
        return new LinkedList<Incident>(incidents);
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }
    
    protected void incidentStarted (String startTime) {
        Incident incident = new Incident(type.toString(), id, startTime);
        this.activated = true;
        incidents.add(incident);
        tail = incident;
    }
    
    protected Incident getCurrentIncident () {
        return tail;
    }
    
    protected void incidentEnded (String endTime, String resolutionType) {
        Incident incident = tail;
        tail.setEndTime(endTime);
        tail.setResolutionType(resolutionType);
        String duration = formatElapsedTime(parseDate(endTime).getTime() - parseDate(incident.getStartTime()).getTime());
        tail.setDuration(duration);
        this.activated = false;
    }
}
