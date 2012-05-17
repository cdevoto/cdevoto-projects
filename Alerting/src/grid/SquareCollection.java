package grid;

public interface SquareCollection {

    public void accept (SquareVisitor visitor);
    
    public boolean contains (int value);
    
}
