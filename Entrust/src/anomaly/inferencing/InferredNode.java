package anomaly.inferencing;

public interface InferredNode {
    
    void addChild (ObservableNode child);
    void startTimeChanged (String startTime);
    void childActivated (String startTime);
    void childDeactivated(String endTime, String resolutionType);

}
