package chess;

import chess.model.HistoryManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The type Main controller.
 */
public class MainController extends Application implements Initializable {

    /**
     * The File chooser.
     */
    FileChooser fileChooser;
    private Color darkColor = Color.CORNFLOWERBLUE;
    private Color lightColor = Color.WHITE;

    @FXML
    private MenuItem menuNew;
    @FXML
    private MenuItem menuQuit;
    @FXML
    private MenuItem menuRenew;
    @FXML
    private MenuItem menuLoad;
    @FXML
    private MenuItem menuSave;
    @FXML
    private MenuItem menuClose;
    @FXML
    private MenuItem menuAbout;
    @FXML
    private MenuItem menuPreferences;
    @FXML
    private TabPane tabPane;


    private List<TabController> gameControllers = new ArrayList<>();
    private FXMLLoader fXMLLoader;


    private Stage rootStage;
    private Scene rootScene;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        primaryStage.setTitle("Chess v1.0");
        rootScene = new Scene(root, 1100, 870);
        primaryStage.setScene(rootScene);
        primaryStage.show();

        rootStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        menuQuit.setOnAction(e -> Platform.exit());

        menuNew.setOnAction(e -> {
            tabPane.getTabs().add(new Tab("ChessTab"));
            tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
        });

        tabPane.getTabs().add(new Tab("ChessTab"));
        tabPane.getSelectionModel().clearSelection();
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if (newValue.getContent() == null) {
                    try {
                        fXMLLoader = new FXMLLoader(getClass().getResource("game.fxml"));
                        Parent root = (Parent) fXMLLoader.load();
                        newValue.setContent(root);
                        gameControllers.add(fXMLLoader.getController());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        tabPane.getSelectionModel().selectFirst();

        menuRenew.setOnAction(e -> {
            gameControllers.get(tabPane.getSelectionModel().getSelectedIndex()).newGame();
        });

        menuLoad.setOnAction(e -> {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file");
            File file = fileChooser.showOpenDialog(rootStage);
            if (file != null) {
                gameControllers.get(tabPane.getSelectionModel().getSelectedIndex())
                        .setGame(HistoryManager.loadFull(file.getAbsolutePath()));
            }
        });

        menuSave.setOnAction(e -> {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file");
            File file = fileChooser.showSaveDialog(rootStage);
            if (file != null) {
                gameControllers.get(tabPane.getSelectionModel().getSelectedIndex())
                        .saveGame(file);
            }
        });

        menuClose.setOnAction(e -> {
            if (tabPane.getTabs().size() > 1) {
                gameControllers.remove(tabPane.getSelectionModel().getSelectedIndex()).newGame();
                tabPane.getTabs().removeAll(tabPane.getSelectionModel().getSelectedItem());
            }
        });

        menuAbout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About IJA Chess");
            alert.setHeaderText("Chess - IJA 2018/2019");
            alert.setContentText("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nullam at arcu a est sollicitudin euismod. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus.");

            alert.showAndWait();
        });

        menuPreferences.setOnAction(e -> {
            preferencesPopup();
        });


    }

    private void preferencesPopup() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change preferences");

        ButtonType loginButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ColorPicker lightPicker = new ColorPicker(lightColor);
        ColorPicker darkPicker = new ColorPicker(darkColor);

        final ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("1 - modern");
        rb1.setToggleGroup(group);
        rb1.setGraphic(new ImageView(new Image("resources/figures1/king-black.png")));

        RadioButton rb2 = new RadioButton("2 - classic");
        rb2.setGraphic(new ImageView(new Image("resources/figures2/king-black.png")));
        rb2.setSelected(true);
        rb2.setToggleGroup(group);

        CheckBox cb = new CheckBox("Use short notation");

        grid.add(new Label("Moves history:"), 0, 0);
        grid.add(cb, 1, 0);
        grid.add(new Label("Light color:"), 0, 1);
        grid.add(lightPicker, 1, 1);
        grid.add(new Label("Dark color:"), 0, 2);
        grid.add(darkPicker, 1, 2);
        grid.add(rb1, 0, 3);
        grid.add(rb2, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            Integer selectedRadioButton = Integer.valueOf(((RadioButton) group.getSelectedToggle()).getText().substring(0, 1));
            if (dialogButton == loginButtonType) {
                gameControllers.get(tabPane.getSelectionModel().getSelectedIndex())
                        .setPreferences(lightPicker.getValue(), darkPicker.getValue(), selectedRadioButton, !cb.isSelected());
            }
            return null;
        });

        dialog.showAndWait();

    }
}
