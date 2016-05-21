package com.edr.fingerdodge.math.geo;

/**
 * This represents a circle shape.
 *
 * @author Ethan Raymond
 */
public class Circle {

    private float radius;
    private Point center;

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
        return new Rectangle(
                center.getY() - radius,
                center.getY() + radius,
                center.getX() - radius,
                center.getX() + radius
        );
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCenter(Point center) {
        this.center = center;
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
