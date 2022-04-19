package puzzles.jam.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.JamConfig;

import java.util.LinkedList;

public class Jam {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Jam filename");
        } else {
            Configuration startConfig = new JamConfig(args[0]);
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