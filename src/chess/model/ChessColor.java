package chess.model;

/**
 * The enum Chess color.
 */
public enum ChessColor {
    /**
     * White chess color.
     */
    WHITE(1),
    /**
     * Black chess color.
     */
    BLACK(-1);

    /**
     * The Direction.
     */
    int direction;

    ChessColor(int direction) {
        this.direction = direction;
    }

    /**
     * Gets direction.
     *
     * @return the direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Opposite chess color.
     *
     * @return the chess color
     */
    public ChessColor opposite() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                throw new IllegalStateException("Hafla kafla");
        }
    }
}
