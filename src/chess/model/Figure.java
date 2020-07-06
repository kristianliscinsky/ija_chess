package chess.model;

import java.util.List;
import java.util.Map;


/**
 * The type Figure.
 */
public class Figure {

    private final Figures type;
    private final ChessColor chessColor;

    /**
     * Instantiates a new Figure.
     *
     * @param type  the type
     * @param color the color
     */
    public Figure(Figures type, ChessColor color) {
        this.type = type;
        this.chessColor = color;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Figures getType() {
        return type;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public ChessColor getColor() {
        return chessColor;
    }

    /**
     * Gets moves.
     *
     * @param from  the from
     * @param board the board
     * @return the moves
     */
    public List<Coord> getMoves(Coord from, Map<Coord, Figure> board) {
        return type.validMoves(from, board, true);
    }

    /**
     * Gets moves no filter.
     *
     * @param from  the from
     * @param board the board
     * @return the moves no filter
     */
    public List<Coord> getMovesNoFilter(Coord from, Map<Coord, Figure> board) {
        return type.validMoves(from, board, false);
    }

    public Figure clone() {
        return new Figure(type, chessColor);
    }

    @Override
    public String toString() {
        switch (chessColor) {
            case WHITE:
                if (type == Figures.PAWN)
                    return "P";
                return type.getC().toUpperCase();
            case BLACK:
                if (type == Figures.PAWN)
                    return "p";
                return type.getC().toLowerCase();
        }
        return "ERR";
    }

}
