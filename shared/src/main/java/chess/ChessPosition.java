package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        if (row < 1
                || row > 8
                || col < 1
                || col > 8) {
            throw new RuntimeException("Tried to access ChessPosition that was out of bounds! Row: %s, Col: %s".formatted(row, col));
        }
        this.row = row;
        this.col = col;
    }

    public static boolean isValid(ChessPosition pos) {
        return pos.getRow() >= 1
                && pos.getRow() <= 8
                && pos.getCol() >= 1
                && pos.getCol() <= 8;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getCol() {
        return this.col;
    }

    @Override
    public String toString() {
        return "Row" + row + "Col" + col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition newPosition = (ChessPosition) obj;
        return this.row == newPosition.getRow() && this.col == newPosition.getCol();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
