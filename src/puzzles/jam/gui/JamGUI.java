package puzzles.jam.gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.util.HashMap;

public class JamGUI extends Application  implements Observer<JamModel, String>  {
    /**
     * Various variables to store things like the constants, a list of colors for cars, a map of cars and their colors...
     */
    private final static Color X_CAR_COLOR = Color.web("#DF0101", 1.0);
    private final static int BUTTON_FONT_SIZE = 20;
    private final static int ICON_SIZE = 75;
    private int carNum;
    private HashMap<Character, Color> carMap;
    private int ROWS;
    private int COLS;
    private Color[] colorList = new Color[]{
            Color.BLUE,
            Color.ORANGE,
            Color.YELLOW,
            Color.VIOLET,
            Color.GREEN,
            Color.PINK,
            Color.DARKGREY,
            Color.BROWN,
            Color.GREENYELLOW,
            Color.ORANGERED,
            Color.INDIGO,
            Color.TURQUOISE,
            Color.LIME,
            Color.FIREBRICK,
            Color.MEDIUMVIOLETRED,
            Color.DODGERBLUE,
    };
    private boolean initialized;

    private Label topText;
    private GridPane mainGrid;
    private JamModel model;
    private BorderPane mainbox;
    private Stage stage;

    /**
     * initializes the model, adds the ui to the observers, loads teh file into the modeld
     */
    public void init() {
        this.initialized = false;
        String filename = getParameters().getRaw().get(0);
        this.carNum = 0;
        this.model = new JamModel();
        this.model.addObserver(this);
        this.model.load(new File(filename));
    }

    /**
     * starts teh GUI
     * @param stage the stage to put the gui scene on
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.initialized = true;
        mainbox = new BorderPane();
        this.topText = new Label("Starting Game!");
        topText.setFont(Font.font(18));
        mainbox.setTop(topText);

        this.mainGrid = makeMainGrid(this.model);
        mainbox.setCenter(mainGrid);

        Button reset = new Button("Reset");
        reset.setOnAction((event) -> model.reset());
        Button load = new Button("Load");
        load.setOnAction((event) -> loadFile());
        Button hint = new Button("Hint");
        hint.setOnAction((event) -> model.getHint());
        reset.setStyle("-fx-font: 14px Comic-Sans; -fx-border-style: solid inside;");
        load.setStyle("-fx-font: 14px Comic-Sans; -fx-border-style: solid inside;");
        hint.setStyle("-fx-font: 14px Comic-Sans; -fx-border-style: solid inside;");

        HBox bottomButtons = new HBox(reset, load, hint);
        mainbox.setBottom(bottomButtons);

        Scene scene = new Scene(mainbox);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * loads a new file with the file chooser
     */
    public void loadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            model.load(f);
        }
    }

    /**
     * creates the i9nitial main gridPane of the GUI
     * @param model the model to base the initial state on
     * @return the GridPane of buttons
     */
    public GridPane makeMainGrid(JamModel model){
        GridPane grid = new GridPane();
        this.carMap = new HashMap<>();
        this.carNum = 0;
        carMap.put('X', X_CAR_COLOR);
        this.ROWS = model.getConfig().getNumRows();
        this.COLS = model.getConfig().getNumCols();
        for (int i = 0; i < this.model.getConfig().getNumRows(); i++) {
            for (int j = 0; j < this.model.getConfig().getNumCols(); j++) {
                Button button = new Button(String.valueOf(model.getConfig().getAt(i, j)));
                button.setFont(Font.font(BUTTON_FONT_SIZE));
                int finalI = i;
                int finalJ = j;
                button.setOnAction((event) -> this.model.select(finalI, finalJ));
                if (model.getConfig().getAt(i, j).equals('.')){
                    button.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 1),
                            CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    if (carMap.containsKey(model.getConfig().getAt(i, j))){
                        button.setBackground(new Background(new BackgroundFill(carMap.get(model.getConfig().getAt(i, j)),
                                CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        button.setBackground(new Background(new BackgroundFill(colorList[carNum],
                                CornerRadii.EMPTY, Insets.EMPTY)));
                        carMap.put(model.getConfig().getAt(i, j), colorList[carNum]);
                        carNum++;
                    }
                }
                button.setMinHeight(ICON_SIZE);
                button.setMinWidth(ICON_SIZE);
                grid.add(button, j, i);
            }
        }
        return grid;
    }

    /**
     * update the GUI
     * @param jamModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(JamModel jamModel, String msg) {
        if (initialized){
            topText.setText(msg);
            this.model = jamModel;
            if (this.model.getConfig().getNumCols() != COLS || this.model.getConfig().getNumRows() != ROWS){
                this.mainGrid = makeMainGrid(model);
                mainbox.setCenter(mainGrid);
                stage.sizeToScene();
            } else {
                ObservableList<Node> list = mainGrid.getChildren();
                for (Node node : list) {
                    Button button = (Button) node;
                    int row = GridPane.getRowIndex(button);
                    int col = GridPane.getColumnIndex(button);
                    button.setText(String.valueOf(model.getConfig().getAt(row, col)));
                    button.setBackground(new Background(new BackgroundFill(carMap.get(model.getConfig().getAt(row, col)),
                            CornerRadii.EMPTY, Insets.EMPTY)));
                }
                if (this.model.getConfig().isSolution()) {
                    topText.setText("Congratulations! You've solved the puzzle!");
                }
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
