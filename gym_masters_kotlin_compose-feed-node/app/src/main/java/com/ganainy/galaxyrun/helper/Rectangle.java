package com.ganainy.galaxyrun.helper;

import android.graphics.Rect;

/**
 * Wrapper for a Rect that stores coordinates as doubles
 */

public class Rectangle {

    // Coordinates of rectangle top-left
    double x, y;
    // Dimensions of rectangle
    double width, height;

    /*
    Create Rectangle with top-left at (x, y), and the specified dimensions.
     */
    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Shifts top-left coordinates by `dx` and `dy`
    public void offset(double dx, double dy) {
        x += dx;
        y += dy;
    }

    // Resets fields
    public void reset(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Returns coordinates of Rectangle center
    public Point2D getCenter() {
        return new Point2D(
                x + width / 2.0,
                y + height / 2.0
        );
    }

    // Returns whether this rectangle intersects the specified rectangle.
    public boolean intersects(Rectangle h) {
        return x + width >= h.x &&
                h.x + h.width >= x &&
                y + height >= h.y &&
                h.y + h.height >= y;
    }

    public boolean intersects(double x1, double y1, double w1, double h1) {
        return x + width >= x1 &&
                x1 + w1 >= x &&
                y + height >= y1 &&
                y1 + h1 >= y;
    }

    public boolean isInBounds(double ix, double iy) {
        return ix >= x && ix <= x + width && iy >= y && iy <= y + height;
    }

    // Returns this Rectangle as an `androids.graphics.Rect`
    public Rect toRect() {
        return new Rect(
                (int) x,
                (int) y,
                (int) (x + width),
                (int) (y + height)
        );
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
