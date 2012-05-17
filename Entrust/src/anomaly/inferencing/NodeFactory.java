package anomaly.inferencing;

public class NodeFactory {
    
    private NodeConfig config;
    
    public NodeFactory (NodeConfig config) {
        if (config == null) {
            throw new NullPointerException();
        }
        this.config = config;
    }
    
    public InferredDataNode createInferredNode (NodeType type, String id) {
        if (type == null || id == null) {
            throw new NullPointerException();
        }
        return new ConcreteInferredNode(type, id, config);
    }

    public ObservableDataNode createObservableNode (NodeType type, String id) {
        if (type == null || id == null) {
            throw new NullPointerException();
        }
        return new ConcreteObservableNode(type, id, config);
    }
    
    public ObservableInferredDataNode createObservableInferredNode (NodeType type, String id) {
        if (type == null || id == null) {
            throw new NullPointerException();
        }
        return new ConcreteObservableInferredNode(type, id, config);
    }
    
}
