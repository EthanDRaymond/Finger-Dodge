package com.edr.fingerdodge.math.geo;

/**
 * This represents one geometric rectangle.
 *
 * @author Ethan Raymond
 */
public class Rectangle {

    public float top, bottom, left, right;

    /**
     * Initializes a new rectangle with the locations of the four edges.
     */
    public Rectangle(float top, float bottom, float left, float right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    /**
     * This moves the rectangle in the by the given x and y amounts.
     *
     * @param dx the amount to move the rectangle in the x direction
     * @param dy the amount to move the rectangle in the y direction
     */
    public void shiftRectangle(float dx, float dy) {
        top += dy;
        bottom += dy;
        left += dx;
        right += dx;
    }

    /**
     * Gets a point at the top left corner of the rectangle.
     */
    public Point getTopLeftCorner() {
        return new Point(left, top);
    }

    /**
     * Gets a point at the top right corner of the rectangle.
     */
    public Point getTopRightCorner() {
        return new Point(right, top);
    }

    /**
     * Gets a point at the bottom left corner of the rectangle.
     */
    public Point getBottomLeftCorner() {
        return new Point(left, bottom);
    }

    /**
     * Gets a point at the bottom right corner of the rectangle.
     */
    public Point getBottomRightCorner() {
        return new Point(right, bottom);
    }

    /**
     * Gets the radius of the smallest circle that would completely enclose the rectangle.
     */
    public float getRadius() {
        return (float) Math.sqrt(Math.pow(bottom - top, 2) + Math.pow(right - left, 2));
    }

    /**
     * Returns the center point of the rectangle.
     *
     * @return  the center point of this rectangle.
     */
    public Point getCenterPoint() {
        return new Point((right + left) / 2.0f, (top + bottom) / 2.0f);
    }

    /**
     * Checks to see if the two given rectangles are overlapping.
     *
     * @param rect1 the first rectangle
     * @param rect2 the second rectangle
     * @return true if the rectangles are overlapping, false if they are not overlapping
     */
    public static boolean isColliding(Rectangle rect1, Rectangle rect2) {
        return ((rect1.left > rect2.left && rect1.left < rect2.right)
                || (rect1.right > rect2.left && rect1.right < rect2.right))
                && ((rect1.top > rect2.top && rect1.top < rect2.bottom)
                || (rect1.bottom > rect2.top && rect1.bottom < rect2.right));
    }

}
