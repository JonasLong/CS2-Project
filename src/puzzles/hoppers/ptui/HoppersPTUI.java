package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import java.io.IOException;

public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;

    @Override
    public void update(HoppersModel model, String msg) {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {

        }
    }
}
