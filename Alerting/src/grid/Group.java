package grid;

import java.util.Arrays;

public class Group implements SquareCollection {
    public int idx;
    public Square [] squares;

    public Group(int idx, int size) {
        this.idx = idx;
        this.squares = new Square[size];
    }
    
    public void accept (SquareVisitor visitor) {
        for (int idx = 0; idx < squares.length; idx++) {
            visitor.visit(squares[idx]);
        }
    }
    
    public boolean contains (int value) {
        for (int idx = 0; idx < squares.length; idx++) {
            if (squares[idx].getValue() == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idx;
        result = prime * result + Arrays.hashCode(squares);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (idx != other.idx)
            return false;
        if (!Arrays.equals(squares, other.squares))
            return false;
        return true;
    }
    
    
}
