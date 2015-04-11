package com.edr.fingerdodge.math;

/**
 * This represents a range of numbers.
 * @author Ethan Raymond
 * @version 1.0
 */
public class Range {

    public float start, end;

    public Range(float start, float end){
        this.start = start;
        this.end = end;
    }

    public boolean isInRangeInclusively(float value){
        return value >= start && value <= end;
    }

    public boolean isInRangeExclusively(float value){
        return value > start && value < end;
    }

}
