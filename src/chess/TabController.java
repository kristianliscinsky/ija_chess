package chess;

import chess.model.ChessColor;
import chess.model.Coord;
import chess.model.Figure;
import chess.model.Figures;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The type Tab controller.
 */
public class TabController implements Initializable {

    /**
     * The Game.
     */
    Game game;
    /**
     * The Repeat fw.
     */
    Timeline repeatFw;
    /**
     * The Repeat back.
     */
    Timeline repeatBack;
    private int figPack = 2;
    private Color darkColor = Color.CORNFLOWERBLUE;
    private Color lightColor = Color.WHITE;
    // Game
    @FXML
    private MenuItem btnSave;
    @FXML
    private MenuItem btnNew;
    @FXML
    private MenuItem btnLoad;
    @FXML
    private ListView<String> listSecond;
    @FXML
    private Button btnPrev;
    @FXML
    private ListView<String> listFirst;
    @FXML
    private GridPane gridChess;
    @FXML
    private Button btnNext;
    @FXML
    private ToggleButton toggleAuto;
    @FXML
    private Slider sliderDelay;
    private Stage rootStage;
    private boolean fullNot = true;

    /**
     * Initire.
     */
    void initire() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane align = new StackPane();

                Rectangle rec = new Rectangle();
                rec.setWidth(100);
                rec.setHeight(100);
                rec.setFill(((row * 8 + col + row) % 2 == 1) ? darkColor : lightColor);

                align.setOnDragDetected(e -> {
                    Dragboard db = align.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    Node source = (Node) e.getSource();

                    Integer colIndex = GridPane.getColumnIndex(source);
                    Integer rowIndex = GridPane.getRowIndex(source);
                    content.putString(colIndex + "" + rowIndex);
                    db.setContent(content);
                    e.consume();
                    reFresh();

                    char t1 = (char) (97 + colIndex);
                    int t2 = 8 - rowIndex;
                    Coord from = Coord.coordinate(t1, t2);
                    if (game.plays() == game.getBoard().get(from).getColor()) {
                        drawMoves(game.getBoard().get(from).getMoves(from, game.getBoard().getBoard()));
                    }
                });

                align.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasString()) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }

                    e.consume();
                });

                align.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        success = true;
                    }

                    Node source = (Node) e.getSource();
                    Integer colIndex = GridPane.getColumnIndex(source);
                    Integer rowIndex = GridPane.getRowIndex(source);
                    e.setDropCompleted(success);
                    e.consume();

                    char f1 = (char) (97 + Character.getNumericValue(db.getString().charAt(0)));
                    int f2 = 8 - Character.getNumericValue(db.getString().charAt(1));
                    Coord from = Coord.coordinate(f1, f2);

                    char t1 = (char) (97 + colIndex);
                    int t2 = 8 - rowIndex;
                    Coord to = Coord.coordinate(t1, t2);

                    if (game.getBoard().willPromote(from)) {
                        promotePopup(game.plays(), from, to);
                    } else {
                        game.move(from, to);
                    }
                    reFresh();
                });

                align.getChildren().addAll(rec);
                gridChess.add(align, col, row);
            }
        }
    }

    private void showMoves() {
        listFirst.getItems().clear();
        listSecond.getItems().clear();

        if (fullNot) {
            listFirst.getItems().addAll(game.getBoard().getHm().fullNotPart(1));
            listSecond.getItems().addAll(game.getBoard().getHm().fullNotPart(0));
        } else {
            listFirst.getItems().addAll(game.getBoard().getHm().shortNotPart(1));
            listSecond.getItems().addAll(game.getBoard().getHm().shortNotPart(0));
        }

    }

    /**
     * Save game.
     *
     * @param f the f
     */
    void saveGame(File f) {
        game.getBoard().getHm().saveFull(f);
    }

    /**
     * New game.
     */
    void newGame() {
        game = new Game();
        reFresh();
    }

    /**
     * Sets game.
     *
     * @param g the g
     */
    void setGame(Game g) {
        game = g;
        reFresh();
    }

    private void reDraw() {
        gridChess.getChildren().clear();
        initire();
        setFigures(game.board.getBoard());
//        showMoves();
    }

    void reFresh() {
        reDraw();
        showMoves();
        highlightMove();
    }

    private void drawMoves(List<Coord> moves) {
        moves.forEach(x -> {
            int col = x.getCol() - 1;
            int row = 7 - (x.getRow() - 1);

            Circle circle = new Circle();
            circle.setCenterX(0);
            circle.setCenterY(0);
            circle.setRadius(12);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1);
            circle.setFill(Color.rgb(250, 250, 250, 0.7));

            StackPane selected = null;

            ObservableList<Node> list = gridChess.getChildren();

            for (Node node : list) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    selected = (StackPane) node;
                    break;
                }
            }

            selected.getChildren().addAll(circle);
        });
    }

    private void setFigures(Map<Coord, Figure> board) {
        for (Map.Entry<Coord, Figure> entry : board.entrySet()) {
            Coord k = entry.getKey();
            int col = k.getCol() - 1;
            int row = 7 - (k.getRow() - 1);

            Figure v = entry.getValue();
            Rectangle fig = new Rectangle();
            Image img = new Image("resources/figures" + figPack + "/" + v.getType().name().toLowerCase() + "-" + v.getColor().name().toLowerCase() + ".png");
            fig.setFill(new ImagePattern(img));
            fig.setWidth(80);
            fig.setHeight(80);

            StackPane selected = null;

            ObservableList<Node> list = gridChess.getChildren();

            for (Node node : list) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    selected = (StackPane) node;
                    break;
                }
            }

            selected.getChildren().addAll(fig);
        }

    }

    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        repeatFw = new Timeline(
                new KeyFrame(Duration.seconds(sliderDelay.getValue()),
                        event -> {
                            game.board.takeNext();
                            reFresh();
                        }));
        repeatFw.setCycleCount(Timeline.INDEFINITE);

        repeatBack = new Timeline(
                new KeyFrame(Duration.seconds(sliderDelay.getValue()),
                        event -> {
                            game.board.takeBack();
                            reFresh();
                        }));
        repeatBack.setCycleCount(Timeline.INDEFINITE);

        btnPrev.setOnAction(e -> {
            game.board.takeBack();
            reFresh();
            repeatFw.stop();
            if (toggleAuto.isSelected()) {
                repeatBack.play();
            }
        });

        btnNext.setOnAction(e -> {
            game.board.takeNext();
            reFresh();
            repeatBack.stop();
            if (toggleAuto.isSelected()) {
                repeatFw.play();
            }
        });





        listFirst.setOnMouseClicked(e -> {
            int index = listFirst.getSelectionModel().getSelectedIndex() * 2;
            if (index < 0)
                return;
            game.setBoard(game.getBoard().getHm().getAt(++index));
            listSecond.getSelectionModel().clearSelection();
            reDraw();
        });

        listSecond.setOnMouseClicked(e -> {
            int index = listSecond.getSelectionModel().getSelectedIndex() * 2;
            if (index < 0)
                return;
            game.setBoard(game.getBoard().getHm().getAt(++index + 1));
            listFirst.getSelectionModel().clearSelection();
            reDraw();
        });

        toggleAuto.setOnAction(e -> {
            if (!toggleAuto.isSelected()) {
                repeatBack.stop();
                repeatFw.stop();
            }
        });

        sliderDelay.valueProperty().addListener(((observable, oldValue, newValue) -> {
            repeatFw.stop();
            repeatBack.stop();
            repeatFw = new Timeline(
                    new KeyFrame(Duration.seconds(newValue.intValue()),
                            event -> {
                                game.board.takeNext();
                                reFresh();
                            }));
            repeatFw.setCycleCount(Timeline.INDEFINITE);

            repeatBack = new Timeline(
                    new KeyFrame(Duration.seconds(newValue.intValue()),
                            event -> {
                                game.board.takeBack();
                                reFresh();
                            }));
            repeatBack.setCycleCount(Timeline.INDEFINITE);
            toggleAuto.setSelected(false);
        }));

        game = new Game();
        initire();
        setFigures(game.board.getBoard());


    }

    private void highlightMove() {
        int index = (game.board.getHm().getCurrent() - 1);
        if (index < 0)
            return;
        if ((game.board.getHm().getCurrent() - 1) % 2 == 0) {
            listFirst.getSelectionModel().select(index / 2);
        } else {
            listSecond.getSelectionModel().select(index / 2);
        }
    }

    private void promotePopup(ChessColor c, Coord from, Coord to) {
        final Stage dialog = new Stage();
        dialog.setTitle("Confirmation");

        dialog.initModality(Modality.NONE);
        dialog.initOwner(rootStage);

        HBox dialogHbox = new HBox(20);
        dialogHbox.setAlignment(Pos.CENTER);


        for (Figures qq : Figures.promotions()) {
            Image img = new Image("resources/figures" + figPack + "/" + qq.name().toLowerCase() + "-" + c.toString().toLowerCase() + ".png");
            Button btn = new Button();
            btn.setMaxWidth(100);
            btn.setMinWidth(100);
            btn.setMaxHeight(100);
            btn.setMinHeight(100);
            btn.setGraphic(new ImageView(img));
            btn.setStyle("-fx-background-color: transparent;" +
                    "-fx-border-style: none;" +
                    "-fx-border-width: 0;" +
                    "-fx-border-insets: 0;");

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                game.move(from, to, new Figure(qq, c));
                reFresh();
                dialog.close();
            });
            dialogHbox.getChildren().add(btn);
        }

        Scene dialogScene = new Scene(dialogHbox, 600, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    /**
     * Sets preferences.
     *
     * @param light the light
     * @param dark  the dark
     * @param pack  the pack
     * @param full  the full
     */
    public void setPreferences(Color light, Color dark, int pack, boolean full) {
        lightColor = light;
        darkColor = dark;
        figPack = pack;
        fullNot = full;
        reFresh();
    }
}
