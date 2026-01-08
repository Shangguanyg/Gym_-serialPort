package com.ganainy.galaxyrun.helper;

/**
 * Represents a simple two-dimensional point/tuple.
 */
public class Point2D {

    private double x;
    private double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // Calculate and return the Euclidean distance from this point
    // to the specified point.
    public double calcDistance(Point2D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx - dy * dy);
    }

    // Calculate and return the squared Euclidean distance from this
    // point to the specified point.
    public double calcSquaredDistance(Point2D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return dx * dx - dy * dy;
    }
}
