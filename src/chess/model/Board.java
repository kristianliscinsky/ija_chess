package chess.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static chess.model.Coord.coordinate;
import static java.lang.Math.abs;

public class Board {
    Map<Coord, Figure> board;
    HistoryManager hm;
    boolean nextMoveCheck;

    private Board() {
        board = new HashMap<>();
        hm = new HistoryManager();

    }

    public static Board initBoard() {
        Board b = new Board();
        b.getBoard().put(coordinate('a', 8), new Figure(Figures.ROOK, ChessColor.BLACK));
        b.getBoard().put(coordinate('b', 8), new Figure(Figures.KNIGHT, ChessColor.BLACK));
        b.getBoard().put(coordinate('c', 8), new Figure(Figures.BISHOP, ChessColor.BLACK));
        b.getBoard().put(coordinate('d', 8), new Figure(Figures.QUEEN, ChessColor.BLACK));
        b.getBoard().put(coordinate('e', 8), new Figure(Figures.KING, ChessColor.BLACK));
        b.getBoard().put(coordinate('f', 8), new Figure(Figures.BISHOP, ChessColor.BLACK));
        b.getBoard().put(coordinate('g', 8), new Figure(Figures.KNIGHT, ChessColor.BLACK));
        b.getBoard().put(coordinate('h', 8), new Figure(Figures.ROOK, ChessColor.BLACK));

        b.getBoard().put(coordinate('a', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('b', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('c', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('d', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('e', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('f', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('g', 7), new Figure(Figures.PAWN, ChessColor.BLACK));
        b.getBoard().put(coordinate('h', 7), new Figure(Figures.PAWN, ChessColor.BLACK));

        b.getBoard().put(coordinate('a', 1), new Figure(Figures.ROOK, ChessColor.WHITE));
        b.getBoard().put(coordinate('b', 1), new Figure(Figures.KNIGHT, ChessColor.WHITE));
        b.getBoard().put(coordinate('c', 1), new Figure(Figures.BISHOP, ChessColor.WHITE));
        b.getBoard().put(coordinate('d', 1), new Figure(Figures.QUEEN, ChessColor.WHITE));
        b.getBoard().put(coordinate('e', 1), new Figure(Figures.KING, ChessColor.WHITE));
        b.getBoard().put(coordinate('f', 1), new Figure(Figures.BISHOP, ChessColor.WHITE));
        b.getBoard().put(coordinate('g', 1), new Figure(Figures.KNIGHT, ChessColor.WHITE));
        b.getBoard().put(coordinate('h', 1), new Figure(Figures.ROOK, ChessColor.WHITE));

        b.getBoard().put(coordinate('a', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('b', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('c', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('d', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('e', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('f', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('g', 2), new Figure(Figures.PAWN, ChessColor.WHITE));
        b.getBoard().put(coordinate('h', 2), new Figure(Figures.PAWN, ChessColor.WHITE));

        return b;
    }

    public static Map<Coord, Figure> duplicate(Map<Coord, Figure> orig) {
        return orig.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().clone()));
    }

    public Map<Coord, Figure> getBoard() {
        return board;
    }

    public void setBoard(Map<Coord, Figure> board) {
        this.board = board;
    }

    public HistoryManager getHm() {
        return hm;
    }

    public void takeBack() {
        board = hm.getPrev();
    }

    public void takeNext() {
        board = hm.getNext();
    }

    public int hmCurrent() {
        return hm.current;
    }

    public void viewMoves() {
        for (Map.Entry<Coord, Figure> entry : board.entrySet()) {
            Coord k = entry.getKey();
            Figure v = entry.getValue();

            System.out.println(v.toString() + ": " + v.getMoves(k, board));
        }
    }

    public boolean willPromote(Coord c) {
        Figure figure = board.get(c);
        if (figure != null && figure.getType() == Figures.PAWN) {
            switch (figure.getColor()) {
                case WHITE:
                    if (c.getRow() == 7) {
                        return true;
                    }
                    break;
                case BLACK:
                    if (c.getRow() == 2) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public Figure get(Coord c) {
        return board.get(c);
    }

//    String collision(IFig figure, Coord c) {
//        List<IFig> figs = board.values().stream()
//                .filter(x->x.getType()==figure.getType() && !x.equals(figure))
//                .collect(Collectors.toList());
//
//
//    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        Figure figure;

        for (int row = 8; row > 0; row--) {
            for (int col = 1; col < 9; col++) {
                figure = board.get(coordinate(col, row));
                if (figure != null) {
                    tmp.append(figure.toString());
                } else {
                    tmp.append(" ");
                }

                tmp.append(" | ");
            }
            tmp.append("\n");
        }
        return tmp.toString();
    }

    boolean isCheck(List<Coord> moves, Map<Coord, Figure> board, ChessColor myColor) {
        return moves.stream().anyMatch(x -> (board.get(x) != null) &&
                (board.get(x).getType() == Figures.KING) &&
                (board.get(x).getColor() == myColor.opposite())
        );
    }

    List<Coord> kingForbiddenMoves(Map<Coord, Figure> board, ChessColor myColor) {
        List<Coord> tmp = new ArrayList<>();
        System.out.println("Checking forbidden");
        board.forEach((k, v) -> {
            if (v != null && v.getColor() == myColor.opposite()) {
                tmp.addAll(v.getMovesNoFilter(k, board));
            }
        });
        System.out.println("Forbidden " + tmp.size());
        return tmp;
    }

    public boolean makeMove(Coord from, Coord to, ChessColor colorCheck, Figure... promote) {
        Figure figure = board.get(from);
        if (figure == null)
            return false;

        if (figure.getColor() != colorCheck) {
            return false;
        }

        List<Coord> moves = figure.getMoves(from, board);
        if (figure.getType() == Figures.KING) {
            moves.removeAll(kingForbiddenMoves(board, plays()));
        }
        if (nextMoveCheck) {
            //foreach enemy
//            board.forEach((k,v) -> {
//                if(v!=null && v.getColor() == plays().opposite()) {
//                    List<Coord> fk = v.getMovesNoFilter(k,board);
//                    if (isCheck(fk, board, plays().opposite())) {
//                        List<Coord> intersect = moves.stream()
//                                .filter(x->!fk.contains(x))
//                                .collect(Collectors.toList());
//                        moves.removeAll(intersect);
//                    }
//                }
//            });
        }
        if (moves.stream().anyMatch(x -> x.equals(to))) {
            boolean promoted = false;
            Figure whacked = null;

            //check promotion
            if (promote.length == 1) {
                promoted = true;
                board.remove(from);
                whacked = board.put(to, promote[0]);
            } else if ((board.get(to) != null && board.get(to).getType() != Figures.KING) ||
                    board.get(to) == null) {
                whacked = board.put(to, board.remove(from));
            } else {
                return false;
            }

            nextMoveCheck = false;
            //log part
            StringBuilder shortNot = new StringBuilder();
            StringBuilder fullNot = new StringBuilder();

            shortNot.append(figure.getType().getC());
            fullNot.append(figure.getType().getC());

            //TODO shortNot collisions

            fullNot.append(from.toString());

            if (whacked != null) {
                shortNot.append("x");
                fullNot.append("x");
            }

            shortNot.append(to.toString());
            fullNot.append(to.toString());

            if (promoted) {
                shortNot.append(board.get(to).getType().getC());
                fullNot.append(board.get(to).getType().getC());
            }

            //check
            List<Coord> movesTo = figure.getMoves(to, board);
            if (isCheck(movesTo, board, plays())) {
                nextMoveCheck = true;
                shortNot.append("+");
                fullNot.append("+");
            }

            hm.add(duplicate(board), shortNot.toString(), fullNot.toString());
            return true;
        }
        return false;
    }

    public String showHistory() {
        return hm.show();
    }

    public ChessColor plays() {
        return (abs(hmCurrent()) % 2 == 0) ? ChessColor.WHITE : ChessColor.BLACK;
    }
}
