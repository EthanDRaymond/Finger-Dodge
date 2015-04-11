package com.edr.fingerdodge.math.geo;

public class Rectangle {

    public float top, bottom, left, right;

    public Rectangle(float top, float bottom, float left, float right){
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public void shiftRectangle(float dx, float dy){
        top += dy;
        bottom += dy;
        left += dx;
        right += dx;
    }

    public Point getTopLeftCorner(){
        return new Point(left, top);
    }

    public Point getTopRightCorner(){
        return new Point(right, top);
    }

    public Point getBottomLeftCorner(){
        return new Point(left, bottom);
    }

    public Point getBottomRightCorner(){
        return new Point(right, bottom);
    }

    public float getRadius(){
        return (float) Math.sqrt(Math.pow(bottom - top, 2) + Math.pow(right - left, 2));
    }

    public Point getCenterPoint(){
        return new Point((right + left) / 2.0f, (top + bottom) / 2.0f);
    }

    public static boolean isColliding(Rectangle rect1, Rectangle rect2){
        return ((rect1.left > rect2.left && rect1.left < rect2.right)
                || (rect1.right > rect2.left && rect1.right < rect2.right))
                && ((rect1.top > rect2.top && rect1.top < rect2.bottom)
                || (rect1.bottom > rect2.top && rect1.bottom < rect2.right));
    }

}
