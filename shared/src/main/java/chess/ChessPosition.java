package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    final private int currentRow;
    final private int currentCol;

    public ChessPosition(int row, int col) {
        if (row > 8 || col > 8 || row < 1 || col < 1) {
            throw new IllegalArgumentException("Position must be between 0 and 9");
        }
        this.currentRow = row;
        this.currentCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return currentRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return currentCol;
    }

    @Override
    public int hashCode() {
        return "%s%s".formatted(currentRow, currentCol).hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        ChessPosition pos = (ChessPosition) obj;
        return pos.getColumn() == this.getColumn() && pos.getRow() == this.getRow();
    }
    @Override
    public String toString() {
        return "Row: " + currentRow + ", Col: " + currentCol;
    }
}
