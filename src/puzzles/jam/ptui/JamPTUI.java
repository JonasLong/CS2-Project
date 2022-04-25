package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class JamPTUI extends ConsoleApplication implements Observer<JamModel, String> {
    private JamModel model;
    private PrintWriter out;
    private boolean initialized = false;

    /**
     * initialize the beginning of the application, creating model and adding the ui to the observer list.
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        this.initialized = false;
        this.model = new JamModel();
        this.model.addObserver(this);

        List<String> list = super.getArguments();
        this.load(list.get(0));
    }

    /**
     * update the current display of teh model
     * @param jamModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(JamModel jamModel, String msg) {
        System.out.println(msg);
        this.model = jamModel;
        System.out.println(printBoard());
        if (!initialized){
            System.out.println(printHelp());
        }
    }

    /**
     * method to return the help string for use later
     * @return the help string
     */
    public String printHelp(){
        return ("""
                h(int)              -- hint next move
                l(oad) filename     -- load new puzzle file
                s(elect) r c        -- select cell at r, c
                q(uit)              -- quit the game
                r(eset)             -- reset the current game""");
    }

    /**
     * main, used to launch the application.
     * @param args String array containing the file name to open.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        } else {
            ConsoleApplication.launch(JamPTUI.class, args);
        }
    }

    /**
     * Initialize the game, and set up commands
     * @param console Where the UI should print output. It is recommended to save
     *                this object in a field in the subclass.
     * @throws Exception
     */
    @Override
    public void start(PrintWriter console) throws Exception {
        this.out = console;
        this.initialized = true;
        super.setOnCommand("s", 2, "(elect) <r c>: select a tile",
                args -> this.model.select(Integer.parseInt(args[0]), Integer.parseInt(args[1]))
        );
        super.setOnCommand("r", 0, "(eset): Reset the current file",
                args -> this.model.reset()
        );
        super.setOnCommand("l", 1, "(oad): load a file at this path",
                args -> this.load(args[0])
        );
        super.setOnCommand("h", 0, "(int): Get a hint to solve the puzzle",
                args -> this.model.getHint()
        );
    }

    /**
     * load a new file
     * @param filename
     */
    public void load(String filename){
        File file = new File(filename);
        if (file != null){
            this.model.load(file);
        }
    }

    /**
     * return the String representation of the current board
     * @return the string
     */
    public String printBoard(){
        StringBuilder string = new StringBuilder();
        string.append("  ");
        for (int i = 0; i < this.model.getConfig().getNumCols(); i++) {
            string.append(i).append(" ");
        }
        string.append("\n  ");
        for (int i = 0; i < this.model.getConfig().getNumCols(); i++) {
            string.append("__");
        }
        string.append("\n");
        for (int i = 0; i < this.model.getConfig().getNumRows(); i++) {
            string.append(i).append("|");
            for (int j = 0; j < this.model.getConfig().getNumCols(); j++) {
                string.append(model.getConfig().getAt(i, j)).append(" ");
            }
            string.append("\n");
        }
        return String.valueOf(string);
    }
}
