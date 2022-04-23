package puzzles.hoppers.gui;

import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /**
     * The resources directory is located directly underneath the gui package
     */
    private final static String RESOURCES_DIR = "resources/";

    /**
     * Title of the GUI
     */
    private final static String TITLE = "Hoppers GUI";

    /**
     * Number of rows
     */
    private int ROWS = 0;
    /**
     * Number of columns
     */
    private int COLS = 0;
    /**
     * Whether the completion animation has been run yet
     * This is reset to false whenever the configuraiton is reset or loaded from file
     */
    private boolean hasAnimated = false;

    /**
     * RNG for randomly rotating the lily pads
     */
    private final Random rand = new Random();
    /**
     * Stage given by start()
     */
    private Stage stage;
    /**
     * 2D array of buttons representing cells in HoppersConfig
     */
    private Button[][] buttons;
    /**
     * Label that updates with information from HoppersModel
     */
    private Label infoLabel;

    // for demonstration purposes
    // no, heck you I'm stealing this
    private Image redFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "red_frog.png"));
    private Image greenFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "green_frog.png"));
    private Image lilyPad = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "lily_pad.png"));
    private Image water = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "water.png"));

    /**
     * The current model instance
     */
    HoppersModel model;

    /**
     * Called when the Application is first initalized
     */
    public void init() {
    }

    /**
     * Called when the stage is started. Shows the GUI
     *
     * @param stage Stage object of this application
     * @throws Exception because of threading, any exceptions will be printed out by Application in a stack trace
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        String filename = getParameters().getRaw().get(0);
        HoppersModel m = new HoppersModel();
        m.addObserver(this);
        m.load(filename);

        stage.setTitle(TITLE);
        stage.show();
    }

    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        model = hoppersModel;
        checkResize();
        infoLabel.setText(msg);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Button b = buttons[row][col];
                HoppersConfig.cellContents cell = hoppersModel.getConfig().get(row, col);
                setButtonBg(b, cell);
                if (cell == HoppersConfig.cellContents.EMPTY) {
                    //do nothing, so lily pad rotation is reset to normal when jumped on and left that way
                    //even when the frog leaves
                } else {
                    b.setRotate(0);
                }
            }
        }
        runAnimation();
    }

    /**
     * main method, launches the GUI application
     *
     * @param args filename
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
            System.out.println();
        }
    }

    /**
     * Checks if the grid has been resized by a file load and re-creates the button grid and resizes the stage
     */
    public void checkResize() {
        if (ROWS != HoppersConfig.ROWS || COLS != HoppersConfig.COLS) {
            ROWS = HoppersConfig.ROWS;
            COLS = HoppersConfig.COLS;
            initalizeMainPane();
            stage.sizeToScene();
        }
    }

    /**
     * Creates all static and dynamic aspects of the GUI, some of which are stored in variables to be updated later
     */
    private void initalizeMainPane() {
        BorderPane main = new BorderPane();
        main.setBackground(new Background(new BackgroundFill(Color.rgb(18, 145, 227), null, null)));
        infoLabel = new Label();
        infoLabel.textFillProperty().set(Color.LAWNGREEN);
        infoLabel.setStyle("-fx-font: 18px Comic-Sans; -fx-padding : 0 0 12 0;");
        HBox infoBox = new HBox();
        infoBox.alignmentProperty().set(Pos.CENTER);
        infoBox.getChildren().add(infoLabel);

        buttons = new Button[ROWS][COLS];
        GridPane froggyGrid = new GridPane();
        froggyGrid.setHgap(0);
        froggyGrid.setVgap(0);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Button button = new Button();
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(event -> model.select(finalRow, finalCol));
                setButtonBg(button, HoppersConfig.cellContents.INVALID);
                froggyGrid.add(button, col, row); //backwards for layout reasons
                button.setStyle("""
                        -fx-background-color: transparent;
                        -fx-padding: -0.45;""");
                buttons[row][col] = button;
            }
        }
        Button hint = new Button("hint");
        Button load = new Button("load");
        Button reset = new Button("reset");

        styleButton(hint);
        styleButton(load);
        styleButton(reset);

        hint.setOnAction(event -> model.getHint());
        load.setOnAction(event -> loadFile());
        reset.setOnAction(event -> reset());
        HBox box = new HBox();
        box.getChildren().addAll(hint, load, reset);
        box.alignmentProperty().set(Pos.CENTER);

        HBox thankBox = new HBox();
        Label thankLabel = new Label("Thanks for playing!");
        thankLabel.setStyle("""
                -fx-font: 10px Comic-Sans""");
        //ugly
        //thankLabel.textFillProperty().set(Color.LAWNGREEN);
        thankBox.getChildren().add(thankLabel);
        //thankBox.alignmentProperty().set(Pos.CENTER);

        Scene scene = new Scene(main);
        main.setTop(infoBox);
        FlowPane fp = new FlowPane();
        fp.getChildren().addAll(froggyGrid, box);
        main.setCenter(fp);
        main.setBottom(thankBox);


        stage.setScene(scene);
        newPlay();
    }

    /**
     * Styles the given button
     * This is currently only used on the buttons at the bottom of the GUI
     *
     * @param b the provided button instance
     */
    private void styleButton(Button b) {
        b.setStyle("-fx-font: 14px Comic-Sans; -fx-border-style: solid inside;");
        b.setBackground(new Background(new BackgroundFill(Color.rgb(18, 145, 227), null, null)));
    }

    /**
     * Resets the configuration to its starting state
     */
    private void reset() {
        newPlay();
        model.reset();
    }

    /**
     * Loads from a given file
     */
    private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            newPlay();
            model.load(f.getPath());

        }
    }

    /**
     * Sets the background image of a given button given its cellContents
     *
     * @param b Button instance
     * @param cell Contents of the corresponding cell in the current configuration
     */
    private void setButtonBg(Button b, HoppersConfig.cellContents cell) {
        Image newBg = null;
        switch (cell) {
            case RED -> newBg = redFrog;
            case GREEN -> newBg = greenFrog;
            case EMPTY -> newBg = lilyPad;
            case INVALID -> newBg = water;
        }
        b.setGraphic(new ImageView(newBg));
    }

    /**
     * Resets the orientation of the lilypads
     * This rotates empty cells randomly, and resets rotation on all other cells to 0 degrees
     */
    private void newPlay() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                HoppersConfig.cellContents cell = model.getConfig().get(row, col);
                Button b = buttons[row][col];
                if (cell == HoppersConfig.cellContents.EMPTY) {
                    b.setRotate(rand.nextInt() % 360);
                } else {
                    b.toBack();
                    b.setRotate(0);
                }
            }
        }
        hasAnimated = false;
    }

    /**
     * Run a short animation where all empty cells spin around and then align in the same direction
     */
    private void runAnimation() {
        if (!hasAnimated && model.getConfig().isSolution()) {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    HoppersConfig.cellContents cell = model.getConfig().get(row, col);
                    Button b = buttons[row][col];
                    if (cell == HoppersConfig.cellContents.EMPTY) {
                        animateButton(b, rand.nextInt() % 2 == 0 ? 2 : -2);
                    }
                }
            }
            hasAnimated = true;
        }
    }

    /**
     * Runs the animation on a specific button
     * Called by runAnimation()
     *
     * @param b Provided button instance
     * @param mult Number of times to rotate
     */
    private void animateButton(Button b, int mult) {
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(b);
        rotateTransition.setDuration(Duration.millis(2000));
        rotateTransition.setToAngle(360 * mult);
        rotateTransition.play();
    }
}
