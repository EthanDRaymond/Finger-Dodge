package com.edr.fingerdodge.math.geo;

/**
 * Represents a single two dimensional point.
 *
 * @author Ethan Raymond
 */
public class Point {

    public float x, y;

    /**
     * Initializes a point with the given x and y values.
     */
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the distance between the two points.
     *
     * @param one the first point
     * @param two the second point
     * @return the distance between the points
     */
    public static float getDistance(Point one, Point two) {
        return (float) Math.sqrt(Math.pow(two.x - one.x, 2) + Math.pow(two.y - one.y, 2));
    }

}
