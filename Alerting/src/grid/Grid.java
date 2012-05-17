package grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.devoware.util.FileUtil;
import org.devoware.util.LineVisitor;

public class Grid implements SquareCollection {
    public static final int SIZE = 9;
    public static final int SIZE_SQRT = (int) Math.sqrt(SIZE);
    
    public int idx;
    public int size;
    public int sizeSqrt;
    public Square [] [] squares;
    public Group [] rows;
    public Group [] cols;
    public Grid [] groups = new Grid [0];
    
    public static void main(String[] args) throws IOException {
        /*
        grid.setValues(0, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        grid.setValues(1, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        grid.setValues(2, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
    
        grid.setValues(3, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        grid.setValues(4, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        grid.setValues(5, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        
        grid.setValues(6, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        grid.setValues(7, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        grid.setValues(8, new int [] {0, 0, 0,  0, 0, 0,  0, 0, 0});
        */

        final StringBuilder buf = new StringBuilder();
        FileUtil.readFile("puzzle.txt", new LineVisitor() {
            
            @Override
            public void processLine(String line) throws IOException {
                buf.append(line + "\n");
            }
        });
        
        Grid grid = new Grid(buf.toString());
        
    
        printGrid(grid);
        //grid.printCandidateMoves();
        //System.out.println();
        //Grid clone = new Grid(grid);
        //printGrid(clone);
        printGrid(grid.solve());
        
     }

    public Grid () {
        this(SIZE, true);
    }
    
    public Grid (String puzzle) {
        String [] initRows = puzzle.split("[\\n\\r]");
        List<String> rowList = new ArrayList<String>();
        for (String row : initRows) {
            if (row.trim().length() > 0) {
                rowList.add(row);
            }
        }
        String [] rows = rowList.toArray(new String [rowList.size()]);
        init(rows.length, true);
        for (int row = 0; row < rows.length; row++) {
            String cols = rows[row];
            int col = 0;
            
            for (int idx = 0; idx < cols.length(); idx++) {
                char c = cols.charAt(idx);
                if (c == ' ') {
                    continue;
                }
                int value = Integer.parseInt(String.valueOf(c));
                if (value != 0) {
                    setValue(row, col, value);
                }
                col++;
            }
            if (col != rows.length) {
                throw new IllegalArgumentException(puzzle);
            }
        }
    }
    
    public Grid (int size) {
        this(size, true);
    }
    
    public Grid (Grid grid) {
        this(grid.size, true);
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                if (grid.squares[row][col].hasValue()) {
                    setValue(row, col, grid.squares[row][col].getValue());
                }
            }
        }
    }
    
    private Grid (int size, boolean createSquares) {
        init(size, createSquares);
    }

    private void init(int size, boolean createSquares) {
        this.size = size;
        this.sizeSqrt = (int) Math.sqrt(size);
        this.squares = new Square [size] [size];
        this.rows = new Group [size];
        this.cols = new Group [size];
        if (this.sizeSqrt > 1) {
            this.groups = new Grid[size];
        }
        if (createSquares) {
            createSquares();
        }
    }

    private void createSquares() {
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                squares[row][col] = new Square();
                squares[row][col].rowIdx = row;
                squares[row][col].colIdx = col;
                for (int i = 1; i <= size; i++) {
                    squares[row][col].candidates.add(i);
                }
                if (col > 0) {
                    squares[row][col].prevHoriz = squares[row][col - 1]; 
                    squares[row][col - 1].nextHoriz = squares[row][col];
                }
                if (row > 0) {
                    squares[row][col].prevVert = squares[row - 1][col]; 
                    squares[row - 1][col].prevVert = squares[row][col]; 
                }
            }
        }

        createGroups();
        
    }

    private void createGroups() {
        for (int row = 0; row < size; row++) {
            rows[row] = new Group(row, size);
            for (int col = 0; col < size; col++) {
                rows[row].squares[col] = squares[row][col];
                squares[row][col].row = rows[row];
            }
        }
        
        for (int col = 0; col < size; col++) {
            cols[col] = new Group(col, size);
            for (int row = 0; row < size; row++) {
                cols[col].squares[row] = squares[row][col];
                squares[row][col].col = cols[col];
            }
        }
        
        for (int group = 0; group < size; group++) {
            groups[group] = new Grid(sizeSqrt, false);
            groups[group].idx = group;
            int lowRow = group / sizeSqrt * sizeSqrt;
            int highRow = lowRow + sizeSqrt - 1;
            int lowCol = group % sizeSqrt * sizeSqrt;
            int highCol = lowCol + sizeSqrt - 1;
            
            for (int row = lowRow; row <= highRow; row++) {
                for (int col = lowCol; col <= highCol; col++) {
                    groups[group].squares[row - lowRow][col - lowCol] = squares[row][col];
                    squares[row][col].group = groups[group];
                }
            }
            
            for (int row = 0; row < groups[group].size; row++) {
                groups[group].rows[row] = new Group(row, groups[group].size);
                for (int col = 0; col < groups[group].size; col++) {
                    groups[group].rows[row].squares[col] = groups[group].squares[row][col];
                    groups[group].squares[row][col].groupRow = groups[group].rows[row];
                }
            }
            
            for (int col = 0; col < groups[group].size; col++) {
                groups[group].cols[col] = new Group(col, groups[group].size);
                for (int row = 0; row < groups[group].size; row++) {
                    groups[group].cols[col].squares[row] = groups[group].squares[row][col];
                    groups[group].squares[row][col].groupCol = groups[group].cols[col];
                }
            }
        }
    }
    
    public void acceptEmpty (SquareVisitor visitor) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!squares[row][col].hasValue()) {
                    visitor.visit(squares[row][col]);
                }    
            }
        }
    }

    public void accept (SquareVisitor visitor) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                visitor.visit(squares[row][col]);
            }
        }
    }
    
    public void accept (SquareCollectionVisitor visitor) {
        for (int idx = 0; idx < size; idx++) {
            visitor.visit(rows[idx]);
        }
        for (int idx = 0; idx < size; idx++) {
            visitor.visit(cols[idx]);
        }
        for (int idx = 0; idx < size; idx++) {
            visitor.visit(rows[idx]);
        }
    }
    public void printPuzzleFromCurrentState () {
        for (int row = 0; row < size; row++) {
            if (row > 0 && row % sizeSqrt == 0) {
                System.out.println();
            }
            System.out.print("grid.setValues(" + row + ", new int [] {");
            boolean first = true;
            for (int col = 0; col < size; col++) {
                if (first) {
                    first = false;
                } else {
                    System.out.print(", ");
                }
                if (col > 0 && col % sizeSqrt == 0) {
                    System.out.print(" ");
                }
                System.out.print(squares[row][col].getValue());
            }
            System.out.println("});");
        }    
    }
    
    public void print () {
        System.out.println(toString());
    }

    public String toString () {
        StringBuilder buf = new StringBuilder();
        for (int row = 0; row < size; row++) {
            if (row > 0 && row % sizeSqrt == 0) {
                buf.append("\n");
            }
            for (int col = 0; col < size; col++) {
                if (col > 0 && col % sizeSqrt == 0) {
                    buf.append(" ");
                }
                if (squares[row][col].candidates.isEmpty()) {
                    buf.append(squares[row][col].getValue());
                } else {
                    buf.append("*");
                    System.out.print("*");
                }
            }
            buf.append("\n");
        }    
        return buf.toString();
    }
    
    public void printCandidates () {
        
        char [] [] candidates = new char [size * sizeSqrt] [size * sizeSqrt]; 
        for (int row = 0; row < candidates.length; row++) {
            for (int col = 0; col < candidates.length; col++) {
                candidates[row][col] = ' ';
            }
            
        }
        
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int startRow = row * sizeSqrt, currRow = startRow;
                int startCol = col * sizeSqrt, currCol = startCol;
                if (!squares[row][col].candidates.isEmpty()) {
                    for (int candidate : squares[row][col].candidates) {
                        candidates[currRow][currCol++] = ("" + candidate).charAt(0);
                        if (currCol == startCol + sizeSqrt) {
                            currCol = startCol;
                            currRow++;
                        }
                    }
                    
                } else {
                    for (int i = 0; i < size; i++) {
                        candidates[currRow][currCol++] = 'F';
                        if (currCol == startCol + sizeSqrt) {
                            currCol = startCol;
                            currRow++;
                        }
                    }
                }
            }
        }
        
        for (int row = 0; row < candidates.length; row++) {
            if (row > 0 && row % sizeSqrt == 0) {
                for (int i = 0; i < candidates.length + 3 * (size - 1); i++) {
                    System.out.print("*");
                }
                System.out.println();
            }
            
            for (int col = 0; col < candidates.length; col++) {
                if (col > 0 && col % sizeSqrt == 0) {
                    System.out.print(" * ");
                }
                System.out.print(candidates[row][col]);
            }
            System.out.println();
        }
    }

    public void setValues (int row, int [] values) {
        for (int col = 0; col < size; col++) {
            if (values[col] != 0) {
                setValue(row, col, values[col]);
            }
        }
    }

    public void setValue (int row, int col, int value) {
        if (value < 1 || value > this.size) {
            throw new IllegalArgumentException("Illegal value: " + value);
        }
        setValue(this.squares[row][col], value);
    }
    
    private void setValue (Square square, int value) {
        if (square.hasValue()) {
            if (square.getValue() != value) {
                throw new IllegalArgumentException("Illegal move!\n" + new Move(square.rowIdx, square.colIdx, value) + "\n" + this);
            }
            return;
        }
        if (square.row.contains(value) || square.col.contains(value) || square.group.contains(value)) {
            throw new IllegalArgumentException("Illegal move!:\n" + new Move(square.rowIdx, square.colIdx, value) + "\n" + this);
        }
        square.setValue(value);
        eliminateCandidateValue(square.row, value);
        eliminateCandidateValue(square.col, value);
        eliminateCandidateValue(square.group, value);
        
        executeOnlyChoiceRule();
    }

    public boolean contains (int value) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (squares[row][col].getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Grid solve () {
        return solve(this);
    }
    
    
    private Grid solve(Grid grid) {
        if (grid.getRemainingSquares() == 0 && grid.getRemainingCandidates() == 0) {
            return grid;
        }
        Set<Move> moves = grid.findCandidateMoves();
        if (moves.isEmpty()) {
            return null;
        }
        Move bestMove = moves.iterator().next();
        if (bestMove.remainingSquares == 0 && bestMove.remainingCandidates == 0) {
            grid.printCandidates();
            System.out.println("\n" + bestMove + "\n");
            bestMove.executeMove(grid);
            return grid;
        }
        for (Move move : moves) {
            System.out.println("\n" + move + "\n");
            Grid clone = new Grid(grid);
            move.executeMove(clone);
            Grid result = clone.solve();
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public Move findBestMove () {
        final Set<Move> potentialMoves = findCandidateMoves();
        if (potentialMoves.isEmpty()) {
            return null;
        }
        return potentialMoves.iterator().next();
    }
    
    public Set<Move> findCandidateMoves () {
        final Set<Move> potentialMoves = new TreeSet<Move>();
        acceptEmpty(new SquareVisitor () {

            @Override
            public void visit(Square square) {
                for (int value : square.candidates) {
                    Move move = new Move(square.rowIdx, square.colIdx, value);
                    try {
                        Grid grid = new Grid(Grid.this);
                        move.executeMove(grid);
                        move.remainingSquares = grid.getRemainingSquares();
                        move.remainingCandidates = grid.getRemainingCandidates();
                        if (move.remainingCandidates > 0 && move.remainingSquares == 0 || move.remainingCandidates < move.remainingSquares) {
                            throw new IllegalArgumentException("Illegal move!\n" + new Move(square.rowIdx, square.colIdx, value) + "\n" + this);
                        }
                        potentialMoves.add(move);
                    } catch (IllegalArgumentException ex) {}    
                }
            }
        });
        return potentialMoves;
    }

    private void executeOnlyChoiceRule() {
        accept(new SquareVisitor () {
            @Override
            public void visit(Square square) {
                if (square.candidates.size() == 1) {
                    setValue(square, square.candidates.iterator().next());
                }
            }
        });
        boolean changeMade;
        do {
            changeMade = false;
            for (int i = 1; i <= size; i++) {
                changeMade = executeRuleTwo(i) || changeMade;
            }
        } while (changeMade);
    }

    private boolean executeRuleTwo(final int value) {
        final boolean [] result = new boolean [1];
        result[0] = false;
        accept(new SquareCollectionVisitor () {

            @Override
            public void visit(SquareCollection collection) {
                RuleTwoVisitor visitor = new RuleTwoVisitor(value);
                collection.accept(visitor);
                Square candidate = visitor.getCandidate();
                if (candidate != null) {
                    result[0] = true;
                    setValue(candidate, value);
                }
            }
            
        }) ;
        return result[0];
    }

    private void eliminateCandidateValue (SquareCollection collection, final int value) {
        collection.accept(new SquareVisitor () {

            @Override
            public void visit(Square square) {
                square.candidates.remove(value);
            }
            
        });
    }
    
    public int getRemainingSquares () {
        RemainingSquaresVisitor visitor = new RemainingSquaresVisitor();
        accept(visitor);
        return visitor.getRemainingSquares();
    }
    
    public int getRemainingCandidates () {
        RemainingCandidatesVisitor visitor = new RemainingCandidatesVisitor();
        accept(visitor);
        return visitor.getRemainingCandidates();
    }
    
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(cols);
        result = prime * result + Arrays.hashCode(groups);
        result = prime * result + idx;
        result = prime * result + Arrays.hashCode(rows);
        result = prime * result + size;
        result = prime * result + sizeSqrt;
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
        Grid other = (Grid) obj;
        if (!Arrays.equals(cols, other.cols))
            return false;
        if (!Arrays.equals(groups, other.groups))
            return false;
        if (idx != other.idx)
            return false;
        if (!Arrays.equals(rows, other.rows))
            return false;
        if (size != other.size)
            return false;
        if (sizeSqrt != other.sizeSqrt)
            return false;
        if (!Arrays.equals(squares, other.squares))
            return false;
        return true;
    }

    public static void printGrid(Grid grid) {
        System.out.println("\n\n" + grid);
        
        //System.out.println();
        //System.out.println("CANDIDATES:");
        //grid.printCandidates();
        
        //System.out.println();
        //grid.printPuzzleFromCurrentState();
        //grid.printCandidateMoves();
    }
    
    public void printCandidateMoves() {
        Set<Move> candidateMoves = findCandidateMoves();
        for (Move move : candidateMoves) {
            System.out.println(move);
        }
    }

    public static Set<Move> findLegalMoves (Grid grid, int depth) {
        if (grid.getRemainingSquares() == 0 || depth == 0) {
            return null;
        } else {
            Set<Move> candidateMoves = grid.findCandidateMoves();
            Set<Move> legalMoves = new LinkedHashSet<Move>(candidateMoves);
            for (Move move : candidateMoves) {
                Grid clone = new Grid(grid);
                move.executeMove(clone);
                Set<Move> nextMoves = findLegalMoves(grid, depth - 1);
                if (nextMoves != null && nextMoves.isEmpty()) {
                    legalMoves.remove(candidateMoves);
                } 
            }
            return candidateMoves;
        }
    }
    
    private static class RuleTwoVisitor implements SquareVisitor {
        private int candidateCount = 0;
        private Square candidate = null;
        private int value;
        
        public RuleTwoVisitor (int value) {
            this.value = value;
        }

        @Override
        public void visit(Square square) {
            if (square.candidates.contains(value)) {
                candidateCount++;
                if (candidateCount == 1) {
                    candidate = square;
                }
            }
        }
        
        public Square getCandidate () {
            return candidateCount == 1 ? candidate : null;
        }
        
    }
    
    private static class Move implements Comparable<Move> {
        public int row;
        public int col;
        public int value;
        public int remainingSquares;
        public int remainingCandidates;
        
        public Move(int row, int col, int value) {
            super();
            this.row = row;
            this.col = col;
            this.value = value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + col;
            result = prime * result + remainingCandidates;
            result = prime * result + remainingSquares;
            result = prime * result + row;
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
            Move other = (Move) obj;
            if (col != other.col)
                return false;
            if (remainingCandidates != other.remainingCandidates)
                return false;
            if (remainingSquares != other.remainingSquares)
                return false;
            if (row != other.row)
                return false;
            if (value != other.value)
                return false;
            return true;
        }

        @Override
        public int compareTo(Move o) {
            int result = remainingSquares - o.remainingSquares;
            if (result != 0) {
                return result;
            }
            return remainingCandidates - o.remainingCandidates;
        }
        
        public void executeMove (Grid grid) {
            grid.setValue(this.row, this.col, this.value);
            remainingSquares = grid.getRemainingSquares();
            remainingCandidates = grid.getRemainingCandidates();            
        }

        @Override
        public String toString() {
            return "Move [row=" + row + ", col=" + col + ", value=" + value
                    + ", remainingSquares=" + remainingSquares
                    + ", remainingCandidates=" + remainingCandidates + "]";
        }
        
    }
    
    private static class RemainingSquaresVisitor implements SquareVisitor {
        private int remainingSquares;

        @Override
        public void visit(Square square) {
            if (!square.hasValue()) {
                remainingSquares++;
            }
        }
        
        public int getRemainingSquares() {
            return remainingSquares;
        }
    }

    private static class RemainingCandidatesVisitor implements SquareVisitor {
        private int remainingCandidates;

        @Override
        public void visit(Square square) {
            if (!square.candidates.isEmpty()) {
                remainingCandidates += square.candidates.size();
            }
        }
        
        public int getRemainingCandidates() {
            return remainingCandidates;
        }
    }
}
