package puzzles.crossing;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class CrossingConfig implements Configuration {

    /**
     * variables to keep track of numbers of wolves and pups on each side, as well as boat location
     */
    private int leftPups;
    private int leftWolves;
    private int rightPups;
    private int rightWolves;
    private boolean boatLeft;

    /**
     * constructor makes a new config
     * @param pups left pups
     * @param wolves left wolves
     * @param rPups right pups
     * @param rWolves right wolves
     * @param boat boatLeft boolean
     */
    public CrossingConfig(int pups, int wolves, int rPups, int rWolves, boolean boat){
        this.leftPups = pups;
        this.leftWolves = wolves;
        this.rightPups = rPups;
        this.rightWolves = rWolves;
        this.boatLeft = boat;
    }

    /**
     * Copy Constructor copies another config, then changes counts based on boat position
     * @param other other config to copy
     * @param newPups number to change pups number by
     * @param newWolves number to change wolf number by
     */
    public CrossingConfig(CrossingConfig other, int newPups, int newWolves){
        if (other.boatLeft){
            this.leftPups = other.leftPups - newPups;
            this.rightPups = other.rightPups + newPups;
            this.leftWolves = other.leftWolves - newWolves;
            this.rightWolves = other.rightWolves + newWolves;
            this.boatLeft = false;
        }else{
            this.leftPups = other.leftPups + newPups;
            this.rightPups = other.rightPups - newPups;
            this.leftWolves = other.leftWolves + newWolves;
            this.rightWolves = other.rightWolves - newWolves;
            this.boatLeft = true;
        }
    }

    /**
     * tests if it is a solution
     * @return boolean for if it is the solution
     */
    @Override
    public boolean isSolution() {
        return this.leftPups == 0 && this.leftWolves == 0;
    }

    /**
     * gets the neighbors
     * @return Collection of the neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        if(this.boatLeft) {
            if (leftPups >= 1){neighbors.add(new CrossingConfig(this, 1, 0));}
            if (leftPups >= 2){neighbors.add(new CrossingConfig(this, 2, 0));}
            if (leftWolves >= 1){neighbors.add(new CrossingConfig(this, 0, 1));}
        } else {
            if (rightPups >= 1){neighbors.add(new CrossingConfig(this, 1, 0));}
            if (rightPups >= 2){neighbors.add(new CrossingConfig(this, 2, 0));}
            if (rightWolves >= 1){neighbors.add(new CrossingConfig(this, 0, 1));}
        }
        return neighbors;
    }

    /**
     * compares if things are equals
     * @param o object to compare the config to
     * @return boolean for if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrossingConfig that = (CrossingConfig) o;
        return leftPups == that.leftPups && leftWolves == that.leftWolves && rightPups == that.rightPups && rightWolves == that.rightWolves && boatLeft == that.boatLeft;
    }

    /**
     * gets the hash code
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(leftPups, leftWolves, rightPups, rightWolves, boatLeft);
    }

    @Override
    public String toString() {
        if (this.boatLeft){
            return "(BOAT) left=[" + leftPups + ", " + leftWolves + "], right=[" + rightPups + ", " + rightWolves + "]";
        }else{
            return "       left=[" + leftPups + ", " + leftWolves + "], right=[" + rightPups + ", " + rightWolves + "]  (BOAT)";
        }
    }
}
