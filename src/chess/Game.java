package chess;

import chess.model.*;

import java.util.Map;

import static chess.model.Board.initBoard;

/**
 * The type Game.
 */
public class Game {
    /**
     * The Board.
     */
    Board board;

    /**
     * Instantiates a new Game.
     */
    public Game() {
        board = initBoard();
        board.getHm().add(Board.duplicate(board.getBoard()), "INIT", "INIT");
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets board.
     *
     * @param board the board
     */
    public void setBoard(Map<Coord, Figure> board) {
        this.board.setBoard(board);
    }

    /**
     * Plays chess color.
     *
     * @return the chess color
     */
    ChessColor plays() {
        return board.plays();
    }

    /**
     * Move.
     *
     * @param from the from
     * @param to   the to
     */
    public void move(Coord from, Coord to) {
        if (from == to)
            return;
        System.out.printf("[%c, %d] -> [%c, %d]\n", from.getColC(), from.getRow(), to.getColC(), to.getRow());

        ChessColor plays = plays();
        boolean moved;
        if (board.willPromote(from)) {
            moved = board.makeMove(from, to, plays, new Figure(Figures.QUEEN, plays));
        } else {
            moved = board.makeMove(from, to, plays);
        }

        if (!moved) {
            System.out.println("Invalid move"); //haha postih
        }
    }

    /**
     * Move.
     *
     * @param from    the from
     * @param to      the to
     * @param promote the promote
     */
    void move(Coord from, Coord to, Figure promote) {
        if (from == to)
            return;
        System.out.printf("[%c, %d] -> [%c, %d]\n", from.getColC(), from.getRow(), to.getColC(), to.getRow());

        ChessColor plays = plays();
        boolean moved = board.makeMove(from, to, plays, promote);

        if (!moved) {
            System.out.println("Invalid move"); //haha postih
        }
    }
}
