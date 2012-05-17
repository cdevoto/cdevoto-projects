package anomaly.inferencing;


public interface ObservableNode {
    
    void addObserver (InferredNode observer);
    void onEvent (EventType type, String event);

}
