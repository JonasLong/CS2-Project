package puzzles.common.solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Solver {
    /**
     * ints to keep track of configs generated
     */
    private int configsGenerated;
    private int uniqueConfigs;

    /**
     * constructor makes a thing and initializes variables
     */
    public Solver(){
        this.configsGenerated = 1;
        this.uniqueConfigs = 1;
    }

    /**
     * finds the shortest path with BFS
     * @param config the starting configuration
     * @param finalConfig the goal configuration
     * @return the path in the form of a linked list of configs
     */
    public List<Configuration> findPath(Configuration config, Configuration finalConfig){
        List<Configuration> queue = new LinkedList<>();
        queue.add(config);

        Map<Configuration, Configuration> predecessors = new HashMap<>();
        predecessors.put(config, config);

        while (!queue.isEmpty()) {
            Configuration current = queue.remove(0);
            if (current.isSolution()) {
                break;
            }
            for (Configuration nbr : current.getNeighbors()) {
                configsGenerated++;
                if(!predecessors.containsKey(nbr)) {
                    predecessors.put(nbr, current);
                    uniqueConfigs++;
                    queue.add(nbr);
                }
            }
        }

        return constructPath(predecessors, config, finalConfig);
    }

    /**
     * generates the path given a rpedecessor map
     * @param predecessors the mredecessor map
     * @param startNode the start point
     * @param finishNode the end point
     * @return a linked list representing the path
     */
    public List<Configuration> constructPath(Map predecessors, Configuration startNode, Configuration finishNode){
        List<Configuration> path = new LinkedList<>();

        if(predecessors.containsKey(finishNode)) {
            Configuration currNode = finishNode;
            while (currNode != startNode) {
                path.add(0, currNode);
                currNode = (Configuration) predecessors.get(currNode);
            }
            path.add(0, startNode);
        }

        return path;
    }

    /**
     * getter for total configs generated
     * @return configsGenerated
     */
    public int getConfigsGenerated() {
        return configsGenerated;
    }

    /**
     * getter for unique configs
     * @return uniqueConfigs
     */
    public int getUniqueConfigs() {
        return uniqueConfigs;
    }
}
