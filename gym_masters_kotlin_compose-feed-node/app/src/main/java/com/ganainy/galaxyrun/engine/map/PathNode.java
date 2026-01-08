package com.ganainy.galaxyrun.engine.map;

/*
Used for path finding.
 */
class PathNode implements Comparable<PathNode> {
    public int row, col;
    public int clearance;
    public PathNode prev;
    public PathNode(int row, int col, int clearance, PathNode prev) {
        this.row = row;
        this.col = col;
        this.clearance = clearance;
        this.prev = prev;
    }

    @Override
    public int compareTo(PathNode other) {
        // Longer path (=higher col) wins
        if (col > other.col) {
            return -1;
        } else if (col < other.col) {
            return +1;
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(row) + Integer.hashCode(col);
    }
}
