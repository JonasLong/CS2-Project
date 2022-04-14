package puzzles.crossing;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.strings.StringsConfig;

import java.util.LinkedList;

public class Crossing {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Crossing pups wolves"));
        } else {
            System.out.println("Pups: " + args[0] + ", Wolves: " + args[1]);
            Configuration startConfig = new CrossingConfig(Integer.parseInt(args[0]), Integer.parseInt(args[1]), 0, 0, true);
            Solver solv = new Solver();
            LinkedList<Configuration> path = (LinkedList<Configuration>) solv.findPath(startConfig);
            System.out.println("Total configs: " + solv.getConfigsGenerated());
            System.out.println("Unique configs: " + solv.getUniqueConfigs());
            if (path.size() == 0){
                System.out.println("No solution.");
            }
            for (int i = 0; i < path.size(); ++i){
                System.out.println("Step " + i + ": " + path.get(i));
            }
        }
    }
}
