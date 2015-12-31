package com.edr.fingerdodge.math.geo;

/**
 * This represents a circle shape.
 *
 * @author Ethan Raymond
 */
public class Circle {

    private Point center;
    public float radius;

    /**
     * Creates a new circle.
     *
     * @param center the center point of the circle
     * @param radius the radius of the circle
     */
    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * Returns a Rectangle as an AABB of the circle.
     *
     * @return a rectangle AABB
     */
    public Rectangle getAABB() {
        return new Rectangle(center.y - radius, center.y + radius,
                center.x - radius, center.x + radius);
    }

    /**
     * Returns the center point of the circle.
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Returns the radius of the circle.
     */
    public float getRadius() {
        return radius;
    }
}
