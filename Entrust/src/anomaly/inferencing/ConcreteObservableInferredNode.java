package anomaly.inferencing;

import static util.AppUtil.parseDate;


public class ConcreteObservableInferredNode extends ConcreteInferredNode
        implements ObservableInferredDataNode {

    private EventManager eventManager;
    private ConcreteObservableNode delegate;
    
    public ConcreteObservableInferredNode(NodeType type, String id, NodeConfig config) {
        super(type, id, config);
        this.eventManager = new EventManager(this);
        this.delegate = new ConcreteObservableNode(type, id, config);
        this.delegate.addObserver(new InferredNode () {

            @Override
            public void addChild(ObservableNode child) {
            }
            
            @Override
            public void startTimeChanged (String startTime) {
                ConcreteObservableInferredNode.this.startTimeChanged(startTime);                
            }

            @Override
            public void childActivated(String startTime) {
                if (!activated) {
                    eventManager.fireActivation(startTime);
                } else if (parseDate(startTime).compareTo(parseDate(getCurrentIncident().getStartTime())) < 0) {
                    startTimeChanged(startTime);
                }
            }

            @Override
            public void childDeactivated(String endTime, String resolutionType) {
                if (!thresholdExceeded()) {
                    eventManager.fireDeactivation(endTime, resolutionType);
                }
            }
            
        });
    }

    @Override
    public void addObserver(InferredNode observer) {
        eventManager.addObserver(observer);
    }

    @Override
    public void onEvent(EventType type, String event) {
        this.delegate.onEvent(type, event);
    }
   
    @Override 
    public void startTimeChanged (String startTime) {
        super.startTimeChanged(startTime);
        eventManager.fireStartTimeChanged(startTime);
    }
    
    @Override
    protected void handleIncidentStarted(String startTime) {
        eventManager.fireActivation(startTime);
    }
    
    @Override
    protected void handleIncidentEnded(String endTime, String resolutionType) {
        eventManager.fireDeactivation(endTime, resolutionType);
    }
    
    @Override
    protected boolean checkDectivationCondition() {
        return !this.delegate.isActivated();
    }

}
