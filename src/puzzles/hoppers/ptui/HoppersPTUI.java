package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;

public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;

    @Override
    public void update(HoppersModel model, String msg) {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        }
    }
}
