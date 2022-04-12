package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.jam.model.JamModel;

public class JamPTUI implements Observer<JamModel, String> {
    private JamModel model;

    @Override
    public void update(JamModel jamModel, String msg) {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        }
    }
}
