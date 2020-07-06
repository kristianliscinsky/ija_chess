package chess.model;

import chess.Game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The type History manager.
 */
public class HistoryManager {

    private List<Map<Coord, Figure>> history;
    private List<String> shortNot;
    private List<String> fullNot;

    /**
     * The Current.
     */
    int current;

    /**
     * Instantiates a new History manager.
     */
    public HistoryManager() {
        history = new ArrayList<>();
        current = -1;

        shortNot = new ArrayList<>();
        fullNot = new ArrayList<>();
    }

    /**
     * Load full game.
     *
     * @param fileName the file name
     * @return the game
     */
    public static Game loadFull(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            List<String> load = stream.collect(Collectors.toList());

//            Pattern p = Pattern.compile("\\d+\\. [K|V|J|S|D]?[a-e]x?\\d[a-e]\\d\\+? [K|V|J|S|D]?[a-e]x?\\d[a-e]\\d\\+?");
//            boolean all = load.stream().allMatch(st -> p.matcher(st).matches());
//            if(!all)
//                return null;

            List<String> list = load.stream()
                    // this magical fuckery solves outofbounds ex
                    .map(x -> Arrays.asList(x.split("\\s+")).subList(1, Arrays.asList(x.split("\\s+")).size()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            list.forEach(System.out::println);
            Game g = new Game();

            list.forEach(x -> {
                int pos = 0;

                if (Character.isUpperCase(x.charAt(pos))) {
                    //skip if its just fig definition
                    pos++;
                }
                Coord from = Coord.coordinate(x.charAt(pos++), Integer.parseInt(String.valueOf(x.charAt(pos++))));
                if ('x' == (x.charAt(pos))) {
                    //skip if its "x"
                    pos++;
                }

                Coord to = Coord.coordinate(x.charAt(pos++), Integer.parseInt(String.valueOf(x.charAt(pos++))));

                g.move(from, to);
            });
            System.out.println("Game loaded");

            g.setBoard(g.getBoard().getHm().getAt(0));
            return g;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save full.
     *
     * @param f the f
     */
    public void saveFull(File f) {
        List<String> first = fullNotPart(1);
        List<String> second = fullNotPart(0);

        String out = IntStream
                .range(0, first.size())
                .mapToObj(x -> (x + 1) + ". " + first.get(x) + " " + ((second.size() > x) ? second.get(x) : ""))
                .collect(Collectors.joining("\r\n"));

        Path path = Paths.get(f.getAbsolutePath());
        byte[] strToBytes = out.getBytes();

        try {
            Files.write(path, strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Full not part list.
     *
     * @param mod the mod
     * @return the list
     */
    public List<String> fullNotPart(int mod) {
        return IntStream
                .range(1, fullNot.size())
                .filter(i -> i % 2 == mod)
                .mapToObj(fullNot::get)
                .collect(Collectors.toList());
    }

    /**
     * Short not part list.
     *
     * @param mod the mod
     * @return the list
     */
    public List<String> shortNotPart(int mod) {
        return IntStream
                .range(1, shortNot.size())
                .filter(i -> i % 2 == mod)
                .mapToObj(shortNot::get)
                .collect(Collectors.toList());
    }

    /**
     * Gets prev.
     *
     * @return the prev
     */
    public Map<Coord, Figure> getPrev() {
        if (current > 0) {
            current--;
        }
        return history.get(current);
    }

    /**
     * Gets next.
     *
     * @return the next
     */
    public Map<Coord, Figure> getNext() {
        if (current + 1 < history.size()) {
            current++;
        }
        return history.get(current);
    }

    /**
     * Gets at.
     *
     * @param i the
     * @return the at
     */
    public Map<Coord, Figure> getAt(int i) {
        if (i >= 0 && i < history.size()) {
            current = i;
        }
        return history.get(current);
    }

    /**
     * Show string.
     *
     * @return the string
     */
    public String show() {
        StringBuilder sb = new StringBuilder();
        sb.append("LOG -------------\n");
        for (int i = 0; i < history.size(); i++) {

            sb.append(" ");

            if (i == current)
                sb.append("[");

            sb.append(fullNot.get(i));
            if (i == current)
                sb.append("]");

            if (i % 2 == 1)
                sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets current.
     *
     * @return the current
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Add.
     *
     * @param board    the board
     * @param shortNot the short not
     * @param fullNot  the full not
     */
    public void add(Map<Coord, Figure> board, String shortNot, String fullNot) {
        current++;
        Map<Coord, Figure> temp = Board.duplicate(board);
        if (current < this.history.size()) {
            this.history.subList(current, this.history.size()).clear();
            this.shortNot.subList(current, this.shortNot.size()).clear();
            this.fullNot.subList(current, this.fullNot.size()).clear();
        }

        this.history.add(current, temp);
        this.shortNot.add(current, shortNot);
        this.fullNot.add(current, fullNot);
    }
}
