package puzzles.common.solver;

import java.util.Collection;


/**
 * Represents some puzzle with a graph-like structure that can be solved using breadth-first-search
 */
public interface Configuration {
    /**
     * Whether the current configuration represents the goal node of this graph problem
     * @return whether this node is the solution
     */
    boolean isSolution();

    /**
     * Get a list of Configurations that neighbor this configuration
     * @return list of neighbors
     */
    Collection<Configuration> getNeighbors();

    /**
     * Whether the provided object is equal to this Configuration object
     * @param other other object
     * @return whether the Configurations match
     */
    boolean equals(Object other);

    /**
     * Generates a hashcode for this Configuration
     * Should be designed to return the same value for every instance of Configuration that has an equivalent position
     * in the graph, ie if all the properties of this Configuration are the same as another instance of
     * Configuration, they should have the same hash code.
     * @return hash code
     */
    int hashCode();

    /**
     * Return a representation of this Configuration object as a String
     * @return string representation
     */
    String toString();
}