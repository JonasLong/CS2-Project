package puzzles.hoppers.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Hoppers {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        } else {
            String fname = args[0];
            try {
                System.out.println("File: " + fname);
                HoppersConfig c = new HoppersConfig(fname);
                Solver s = new Solver();
                List<Configuration> solution = s.findPath(c);
                System.out.println(c);
                System.out.println("Total configs: " + s.getConfigsGenerated());
                System.out.println("Unique configs: " + s.getUniqueConfigs());

                if (solution.isEmpty()) {
                    System.out.println("No solution.");
                } else {
                    for (int i = 0; i < solution.size(); i++) {
                        System.out.println("Step " + i + ":");
                        System.out.println(solution.get(i));
                        if (i < solution.size() - 1) {
                            System.out.println();
                        }
                    }
                }
            } catch (IOException e){
                System.out.println("Failed to load "+fname);
            }
        }
    }
}
