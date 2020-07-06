package chess.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The enum Figures.
 */
public enum Figures {
    /**
     * The Pawn.
     */
    PAWN("") {
        @Override
        public List<Coord> validMoves(Coord c, Map<Coord, Figure> board, boolean f) {
            List<Coord> tmp = new ArrayList<>();

            //ugly ik
            if ((c.getRow() == 2 && board.get(c).getColor().equals(ChessColor.WHITE)) ||
                    c.getRow() == 7 && board.get(c).getColor().equals(ChessColor.BLACK)) {
                if (board.get(Coord.coordinate(c.getCol(), c.getRow() + 2 * board.get(c).getColor().getDirection())) == null &&
                        board.get(Coord.coordinate(c.getCol(), c.getRow() + board.get(c).getColor().getDirection())) == null) {
                    tmp.add(Coord.coordinate(c.getCol(), c.getRow() + 2 * board.get(c).getColor().getDirection()));
                }
            }

            Coord cTmp1 = Coord.coordinate(c.getCol() + 1, c.getRow() + board.get(c).getColor().getDirection());
            Coord cTmp2 = Coord.coordinate(c.getCol(), c.getRow() + board.get(c).getColor().getDirection());
            Coord cTmp3 = Coord.coordinate(c.getCol() - 1, c.getRow() + board.get(c).getColor().getDirection());

            if (cTmp2 != null && board.get(cTmp2) == null) {
                tmp.add(cTmp2);
            }

            if (cTmp1 != null && board.get(cTmp1) != null && !board.get(cTmp1).getColor().equals(board.get(c).getColor())) {
                tmp.add(cTmp1);
            }

            if (cTmp3 != null && board.get(cTmp3) != null && !board.get(cTmp3).getColor().equals(board.get(c).getColor())) {
                tmp.add(cTmp3);
            }

            return tmp;
        }
    },
    /**
     * The Rook.
     */
    ROOK("V") {
        @Override
        public List<Coord> validMoves(Coord coord, Map<Coord, Figure> board, boolean f) {
            List<Coord> tmp = new ArrayList<>();
            Coord cTmp;

            for (int i = coord.getCol() + 1; i < 9; i++) {
                cTmp = Coord.coordinate(i, coord.getRow());

                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            for (int i = coord.getCol() - 1; i > 0; i--) {
                cTmp = Coord.coordinate(i, coord.getRow());
                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            for (int i = coord.getRow() + 1; i < 9; i++) {
                cTmp = Coord.coordinate(coord.getCol(), i);
                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            for (int i = coord.getRow() - 1; i > 0; i--) {
                cTmp = Coord.coordinate(coord.getCol(), i);
                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }
            return tmp;
        }
    },
    /**
     * The Knight.
     */
    KNIGHT("J") {
        @Override
        public List<Coord> validMoves(Coord coord, Map<Coord, Figure> board, boolean f) {
            List<Coord> tmp = new ArrayList<>();
            Coord cTmp;

            int[][] offsets = {
                    {-2, 1},
                    {-1, 2},
                    {1, 2},
                    {2, 1},
                    {2, -1},
                    {1, -2},
                    {-1, -2},
                    {-2, -1}
            };

            Figures.addOffests(coord, board, tmp, offsets);
            return tmp;

        }
    },
    /**
     * The Bishop.
     */
    BISHOP("S") {
        @Override
        public List<Coord> validMoves(Coord coord, Map<Coord, Figure> board, boolean f) {
            List<Coord> tmp = new ArrayList<>();
            Coord cTmp;

            for (int i = coord.getCol() + 1, j = coord.getRow() + 1; i < 9 && j < 9; i++, j++) {
                cTmp = Coord.coordinate(i, j);
                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            for (int i = coord.getCol() + 1, j = coord.getRow() - 1; i < 9 && j > 0; i++, j--) {
                cTmp = Coord.coordinate(i, j);
                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            for (int i = coord.getCol() - 1, j = coord.getRow() + 1; j < 9 && i > 0; i--, j++) {
                cTmp = Coord.coordinate(i, j);
                // adds if empty
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            for (int i = coord.getCol() - 1, j = coord.getRow() - 1; i > 0 && j > 0; i--, j--) {
                cTmp = Coord.coordinate(i, j);
                if (add(cTmp, coord, board, tmp, f))
                    break;
            }

            return tmp;
        }
    },
    /**
     * The Queen.
     */
    QUEEN("D") {
        @Override
        public List<Coord> validMoves(Coord coord, Map<Coord, Figure> board, boolean f) {
            List<Coord> tmp = new ArrayList<>();
            tmp.addAll(BISHOP.validMoves(coord, board, f));
            tmp.addAll(ROOK.validMoves(coord, board, f));
            return tmp;
        }
    },
    /**
     * The King.
     */
    KING("K") {
        @Override
        public List<Coord> validMoves(Coord coord, Map<Coord, Figure> board, boolean f) {
            List<Coord> tmp = new ArrayList<>();

            int[][] offsets = {
                    {1, 0},
                    {0, 1},
                    {-1, 0},
                    {0, -1},
                    {1, 1},
                    {-1, 1},
                    {-1, -1},
                    {1, -1}
            };

            Figures.addOffests(coord, board, tmp, offsets);
            return tmp;
        }
    };

    /**
     * The S.
     */
    String s;

    Figures(String s) {
        this.s = s;
    }

    private static boolean add(Coord cTmp, Coord coord, Map<Coord, Figure> board, List<Coord> tmp, boolean f) {
        // adds if empty
        if (board.get(cTmp) == null) {
            tmp.add(cTmp);
        } else if (board.get(cTmp) != null &&
                !board.get(cTmp).getColor().equals(board.get(coord).getColor())) {
            // adds if enemy
            tmp.add(cTmp);
            if (f)
                return true;
        } else {
            // my piece
            if (f)
                return true;
        }
        return false;
    }

    private static void addOffests(Coord coord, Map<Coord, Figure> board, List<Coord> tmp, int[][] offsets) {
        Coord cTmp;
        for (int[] o : offsets) {
            cTmp = Coord.coordinate(coord.getCol() + o[0], coord.getRow() + o[1]);
            if (cTmp != null &&
                    board.get(cTmp) != null &&
                    !board.get(cTmp).getColor().equals(board.get(coord).getColor())) {
                tmp.add(cTmp);
            } else if (board.get(cTmp) == null) {
                if (coord.getCol() + o[0] > 8 || coord.getCol() + o[0] < 1 || coord.getRow() + o[1] > 8 || coord.getRow() + o[1] < 1) {
                    continue;
                }
                tmp.add(cTmp);
            }
        }
    }

    /**
     * Promotions list.
     *
     * @return the list
     */
    public static List<Figures> promotions() {
        return Arrays.stream(Figures.values()).skip(1).collect(Collectors.toList());
    }

    /**
     * Gets c.
     *
     * @return the c
     */
    public String getC() {
        return s;
    }

    /**
     * Valid moves list.
     *
     * @param c      the c
     * @param b      the b
     * @param filter the filter
     * @return the list
     */
    public abstract List<Coord> validMoves(Coord c, Map<Coord, Figure> b, boolean filter);

}
