package puzzles.hoppers.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

public class Hoppers {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        } else {
            String fname=args[0];
            System.out.println("File: "+fname);
            Configuration c = new HoppersConfig(fname);
            Solver s = new Solver();
            s.findPath(c);
        }
    }
}
