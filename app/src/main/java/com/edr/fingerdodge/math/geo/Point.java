package com.edr.fingerdodge.math.geo;

/**
 * Created by ethan on 1/24/15.
 */
public class Point {

    public float x, y;

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    public static float getDistance(Point one, Point two){
        return (float) Math.sqrt(Math.pow(two.x - one.x, 2) + Math.pow(two.y - one.y, 2));
    }

}
