package anomaly.inferencing;

import static util.AppUtil.*;

public class ConcreteObservableNode extends ConcreteNode implements ObservableDataNode {
    
    private EventManager eventManager;

    public ConcreteObservableNode(NodeType type, String id, NodeConfig config) {
        super(type, id, config);
        this.eventManager = new EventManager(this);
    }

    @Override
    public void addObserver(InferredNode observer) {
        eventManager.addObserver(observer);
    }

    @Override
    public void onEvent(EventType eventType, String event) {
        switch (eventType) {
        case UPDATE:
            if (activated == false) {
                double certainty = parseDouble(getMatch(CERTAINTY_PATTERN, event));
                if (certainty >= config.getCertaintyThreshold()) {
                    String startTime = getMatch(EVENT_TIME_PATTERN, event);
                    eventManager.fireActivation(startTime);
                }
            }
            break;
        case RTN:
            if (activated == true) {
                String endTime = getMatch(EVENT_TIME_PATTERN, event);
                String resolutionType = EventType.RTN.toString();
                eventManager.fireDeactivation(endTime, resolutionType);
            }
            break;
        case TOUT:
            if (activated == true) {
                String endTime = getMatch(EVENT_TIME_PATTERN, event);
                String resolutionType = EventType.TOUT.toString();
                eventManager.fireDeactivation(endTime, resolutionType);
            }
            break;
        }
    }
    
}
