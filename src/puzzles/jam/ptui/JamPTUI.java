package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.ptui.ConsoleApplication;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class JamPTUI extends ConsoleApplication implements Observer<JamModel, String> {
    private JamModel model;
    private PrintWriter out;
    private boolean initialized = false;

    @Override
    public void init() throws Exception {
        this.initialized = false;
        this.model = new JamModel();
        this.model.addObserver(this);

        List<String> list = super.getArguments();
        this.load(list.get(0));
    }

    @Override
    public void update(JamModel jamModel, String msg) {
        System.out.println(msg);
        this.model = jamModel;
        System.out.println(printBoard());
        if (!initialized){
            System.out.println(printHelp());
        }
    }

    public String printHelp(){
        return ("""
                h(int)              -- hint next move
                l(oad) filename     -- load new puzzle file
                s(elect) r c        -- select cell at r, c
                q(uit)              -- quit the game
                r(eset)             -- reset the current game""");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        } else {
            ConsoleApplication.launch(JamPTUI.class, args);
        }
    }

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

    public void load(String filename){
        File file = new File(filename);
        if (file != null){
            this.model.load(file);
        }
    }

    public String printBoard(){
        StringBuilder string = new StringBuilder();
        string.append("  ");
        for (int i = 0; i < JamConfig.getNumCols(); i++) {
            string.append(i).append(" ");
        }
        string.append("\n  ");
        for (int i = 0; i < JamConfig.getNumCols(); i++) {
            string.append("__");
        }
        string.append("\n");
        for (int i = 0; i < JamConfig.getNumRows(); i++) {
            string.append(i).append("|");
            for (int j = 0; j < JamConfig.getNumCols(); j++) {
                string.append(model.getConfig().getAt(i, j)).append(" ");
            }
            string.append("\n");
        }
        return String.valueOf(string);
    }
}
