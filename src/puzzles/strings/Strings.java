package puzzles.strings;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.LinkedList;

public class Strings {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            System.out.println("Start: " + args[0] + ", End: " + args[1]);
            Configuration startConfig = new StringsConfig(args[1], args[0]);
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
