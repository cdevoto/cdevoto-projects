package anomaly.inferencing;

import java.util.LinkedList;
import java.util.List;

class EventManager {
    
    private ConcreteNode observable;
    private List<InferredNode> observers = new LinkedList<InferredNode>();
    
    public EventManager (ConcreteNode observable) {
        this.observable = observable;
    }
   
    public void addObserver(InferredNode observer) {
        this.observers.add(observer);
    }
    
    public void fireActivation (String startTime) {
        observable.incidentStarted(startTime);
        for (InferredNode observer : observers) {
            observer.childActivated(startTime);
        }
    }

    public void fireDeactivation (String endTime, String resolutionType) {
        observable.incidentEnded(endTime, resolutionType);
        for (InferredNode observer : observers) {
            observer.childDeactivated(endTime, resolutionType);
        }
    }

    public void fireStartTimeChanged(String startTime) {
        for (InferredNode observer : observers) {
            observer.startTimeChanged(startTime);
        }
    }
    

}
