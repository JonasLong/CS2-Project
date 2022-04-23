package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HoppersPTUI implements Observer<HoppersModel, String> {
    /**
     * Instance of HoppersModel to interact with
     */
    private HoppersModel model;

    /**
     * Constructor for HoppersPTUI
     *
     * @param fname filename
     */
    public HoppersPTUI(String fname) {
            model = new HoppersModel();
            model.addObserver(this);
            load(fname);
            printHelp();
    }

    /**
     * Main function of HoppersPTUI
     * Calls getInput()
     *
     * @param args filename
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            String fname = args[0];
            HoppersPTUI ptui = new HoppersPTUI(fname);
            ptui.getInput();
        }
    }

    @Override
    public void update(HoppersModel model, String msg) {
        System.out.println(msg);
        prettyPrint(model.getConfig());
    }

    /**
     * Gets and processes commands input from System.in until quit command is entered
     */
    public void getInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean loop = true;
        try {
            while (loop) {
                System.out.print("> ");
                String line = reader.readLine();
                if (line.length() != 0) {
                    String[] inp = line.split(" ");
                    String[] params = new String[inp.length - 1];
                    System.arraycopy(inp, 1, params, 0, inp.length - 1);
                    char startChar = line.charAt(0);
                    switch (startChar) {
                        case 's':
                            if (requArgs(params, 2)) {
                                select(params[0], params[1]);
                            }
                            break;
                        case 'l':
                            if (requArgs(params, 1)) {
                                load(params[0]);
                            }
                            break;
                        case 'q':
                            if (requArgs(params, 0)) {
                                loop = false;
                            }
                            continue;
                        case 'h':
                            if (requArgs(params, 0)) {
                                hint();
                            }
                            break;
                        case 'r':
                            if (requArgs(params, 0)) {
                                reset();
                            }
                            break;
                    }
                } else {
                    System.out.println("Empty input not allowed");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks that the number of arguments entered is correct
     *
     * @param args array of arguments
     * @param argNum number of required arguments
     * @return whether there are enough args given
     */
    private boolean requArgs(String[] args, int argNum) {
        if (args.length < argNum) {
            System.out.println("Wrong number of args, requires " + argNum + " args, given " + args.length);
            return false;
        }
        return true;
    }

    /**
     * Gets a hint from the model
     */
    private void hint() {
        model.getHint();
    }

    /**
     * Resets the model to its starting configuration
     */
    private void reset() {
        model.reset();
    }

    /**
     * Loads a new configuration from a given file
     *
     * @param fname filename
     */
    private void load(String fname) {
        model.load(fname);
    }

    private void select(String rowStr, String colStr) {
        int row = Integer.parseInt(rowStr);
        int col = Integer.parseInt(colStr);
        model.select(row, col);
    }

    /**
     * Outputs a help message
     */
    private void printHelp() {
        System.out.println("""
                h(int)              -- hint next move
                l(oad) filename     -- load new puzzle file
                s(elect) r c        -- select cell at r, c
                q(uit)              -- quit the game
                r(eset)             -- reset the current game""");
    }

    /**
     * Prints out the given configuration with row and column numbers for the PTUI
     *
     * @param config configuration
     */
    private void prettyPrint(HoppersConfig config) {
        String[] lines = config.toString().split("\n");
        //Print the column numbers at the top
        System.out.print("  ");
        for (int col = 0; col < HoppersConfig.COLS; col++) {
            System.out.print(" " + col);
        }
        System.out.println();
        //Print the dashes below the column numbers
        System.out.print("  ");
        for (int col = 0; col < HoppersConfig.COLS * 2; col++) {
            System.out.print("-");
        }
        System.out.println();
        //Print each row with numbering
        for (int row = 0; row < HoppersConfig.ROWS; row++) {
            System.out.println(row + "| " + lines[row]);
        }
        System.out.println();
    }
}
