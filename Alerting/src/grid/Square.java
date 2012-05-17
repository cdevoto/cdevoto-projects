package grid;

import java.util.Set;
import java.util.TreeSet;

public class Square {
    
    public Group row;
    public Group col;
    public Grid group;
    public Group groupRow;
    public Group groupCol;
    
    public int rowIdx;
    public int colIdx;
    public Set<Integer> candidates = new TreeSet<Integer>();
    public Square nextHoriz = null;
    public Square prevHoriz = null;
    public Square nextVert = null;
    public Square prevVert = null;

    private int value = 0;

    public Square() {
    }
    
    public boolean hasValue () {
        return value != 0;
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((candidates == null) ? 0 : candidates.hashCode());
        result = prime * result + colIdx;
        result = prime * result
                + ((nextHoriz == null) ? 0 : nextHoriz.hashCode());
        result = prime * result
                + ((nextVert == null) ? 0 : nextVert.hashCode());
        result = prime * result
                + ((prevHoriz == null) ? 0 : prevHoriz.hashCode());
        result = prime * result
                + ((prevVert == null) ? 0 : prevVert.hashCode());
        result = prime * result + rowIdx;
        result = prime * result + value;
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
        Square other = (Square) obj;
        if (candidates == null) {
            if (other.candidates != null)
                return false;
        } else if (!candidates.equals(other.candidates))
            return false;
        if (colIdx != other.colIdx)
            return false;
        if (nextHoriz == null) {
            if (other.nextHoriz != null)
                return false;
        } else if (!(nextHoriz.rowIdx == other.nextHoriz.rowIdx && nextHoriz.colIdx == other.nextHoriz.colIdx))
            return false;
        if (nextVert == null) {
            if (other.nextVert != null)
                return false;
        } else if (!(nextVert.rowIdx == other.nextVert.rowIdx && nextVert.colIdx == other.nextVert.colIdx))
            return false;
        if (prevHoriz == null) {
            if (other.prevHoriz != null)
                return false;
        } else if (!(prevHoriz.rowIdx == other.prevHoriz.rowIdx && prevHoriz.colIdx == other.prevHoriz.colIdx))
            return false;
        if (prevVert == null) {
            if (other.prevVert != null)
                return false;
        } else if (!(prevVert.rowIdx == other.prevVert.rowIdx && prevVert.colIdx == other.prevVert.colIdx))
            return false;
        if (rowIdx != other.rowIdx)
            return false;
        if (value != other.value)
            return false;
        return true;
    }

    public void setValue (int value) {
        this.value = value;
        this.candidates.clear();
    }
    
    public int getValue () {
        return this.value;
    }

    @Override
    public String toString() {
        return "Square [rowIdx=" + rowIdx + ", colIdx=" + colIdx
                + ", candidates=" + candidates + ", nextHoriz=" + (nextHoriz == null ? "null" : "[" + nextHoriz.rowIdx + "," + nextHoriz.colIdx + "]")
                + ", prevHoriz=" + (prevHoriz == null ? "null" : "[" + prevHoriz.rowIdx + "," + prevHoriz.colIdx + "]") + ", nextVert=" + (nextVert == null ? "null" : "[" + nextVert.rowIdx + "," + nextVert.colIdx + "]")
                + ", prevVert=" + (prevVert == null ? "null" : "[" + prevVert.rowIdx + "," + prevVert.colIdx + "]") + ", value=" + value + "]";
    }
    

}
