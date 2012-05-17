package anomaly.inferencing;

import static util.AppUtil.*;

public class ConcreteInferredNode extends ConcreteNode implements InferredDataNode {
    
    private double numChildren = 0;
    private double numActivatedChildren = 0;

    public ConcreteInferredNode(NodeType type, String id, NodeConfig config) {
        super(type, id, config);
    }

    @Override
    public void addChild(ObservableNode child) {
        numChildren++;
        child.addObserver(this);
    }
    
    @Override
    public void startTimeChanged (String startTime) {
        getCurrentIncident().setStartTime(startTime);
    }

    @Override
    public void childActivated(String startTime) {
        numActivatedChildren++;
        if (config.isInferencing() && !activated && thresholdExceeded()) {
            handleIncidentStarted(startTime);
        } else if (activated && parseDate(startTime).compareTo(parseDate(getCurrentIncident().getStartTime())) < 0) {
            startTimeChanged(startTime);
        }
    }

    @Override
    public void childDeactivated(String endTime, String resolutionType) {
        numActivatedChildren--;
        if (activated && !thresholdExceeded() && checkDectivationCondition()) {
            handleIncidentEnded(endTime, resolutionType);
        }
    }

    protected boolean thresholdExceeded() {
        return numActivatedChildren / numChildren >= config.getActivationThreshold();
    }

    protected void handleIncidentStarted(String startTime) {
        incidentStarted(startTime);
    }

    protected void handleIncidentEnded(String endTime, String resolutionType) {
        incidentEnded(endTime, resolutionType);
    }

    protected boolean checkDectivationCondition() {
        return true;
    }
    
    

}
