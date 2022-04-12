package puzzles.jam.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.jam.model.JamModel;

public class JamGUI extends Application  implements Observer<JamModel, String>  {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    // for demonstration purposes
    private final static String X_CAR_COLOR = "#DF0101";
    private final static int BUTTON_FONT_SIZE = 20;
    private final static int ICON_SIZE = 75;

    public void init() {
        String filename = getParameters().getRaw().get(0);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Button button1 = new Button();
        button1.setStyle(
                "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                "-fx-background-color: " + X_CAR_COLOR + ";" +
                "-fx-font-weight: bold;");
        button1.setText("X");
        button1.setMinSize(ICON_SIZE, ICON_SIZE);
        button1.setMaxSize(ICON_SIZE, ICON_SIZE);
        Scene scene = new Scene(button1);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void update(JamModel jamModel, String msg) {

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
