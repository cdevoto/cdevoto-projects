package anomaly.inferencing;

public class NodeConfig {
    
    private double activationThreshold = 1.0 / 3.0;
    private double certaintyThreshold = 0.2;
    private boolean inferencing = true;
    
    public NodeConfig() {
    }
    
    public double getActivationThreshold() {
        return activationThreshold;
    }

    public void setActivationThreshold(double activationThreshold) {
        this.activationThreshold = activationThreshold;
    }

    public double getCertaintyThreshold() {
        return certaintyThreshold;
    }
    public void setCertaintyThreshold(double certaintyThreshold) {
        this.certaintyThreshold = certaintyThreshold;
    }
    public boolean isInferencing() {
        return inferencing;
    }
    public void setInferencing(boolean inferencing) {
        this.inferencing = inferencing;
    }

}
