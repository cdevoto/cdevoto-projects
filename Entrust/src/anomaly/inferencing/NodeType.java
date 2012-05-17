package anomaly.inferencing;

public enum NodeType implements Comparable<NodeType> {
    DOMAIN,
    SERVICE,
    LOCATED_SERVICE,
    HOST,
    DEPLOYED_SERVICE
}
