package chess.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Coord.
 */
public class Coord {

    private static Map<String, Coord> cache = new HashMap<>();
    private final int col;
    private final int row;

    private Coord(int col, int row) {
        this.row = row;
        this.col = col;
    }


    /**
     * Coordinate coord.
     *
     * @param col the col
     * @param row the row
     * @return the coord
     */
    static Coord coordinate(int col, int row) {
        if (col > 8 || col < 1 || row > 8 || row < 1) {
            return null;
        }
        String key = (char) (col + 96) + "" + row;
        if (!cache.containsKey(key)) {
            Coord coordinate = new Coord(col, row);
            cache.put(key, coordinate);
            return coordinate;
        }

        return cache.get(key);
    }

    /**
     * Coordinate coord.
     *
     * @param col the col
     * @param row the row
     * @return the coord
     */
    public static Coord coordinate(char col, int row) {
        return coordinate(col - 96, row);
    }


    /**
     * Gets row.
     *
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets col.
     *
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets col c.
     *
     * @return the col c
     */
    public char getColC() {
        return (char) (col + 96);
    }

    @Override
    public String toString() {
        return (char) (col + 96) + "" + row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coord coord = (Coord) o;

        if (col != coord.col) return false;
        return row == coord.row;
    }

    @Override
    public int hashCode() {
        int result = col;
        result = 31 * result + row;
        return result;
    }
}
