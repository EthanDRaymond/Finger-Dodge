package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * This is subclass of IconView that displays the information icon.
 * @author Ethan Raymond
 * @see IconView
 */
public class InfoIconView extends IconView {

    public InfoIconView(Context context) {
        super(context);
    }

    public InfoIconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public InfoIconView(Context context, AttributeSet attributeSet, int defStyleInt) {
        super(context, attributeSet, defStyleInt);
    }

    @Override
    protected void onDrawDesign(Canvas canvas) {
        super.onDrawDesign(canvas);
        float dotX, dotY, dotRadius, lineTop, lineBottom, lineLeft, lineRight;
        if (getWidth() < getHeight()){
            dotX = getWidth() / 2.0f;
            dotY = getHeight() / 2.0f - getWidth() / 4.0f;
            dotRadius = getWidth() / 12.0f;
            lineTop = getHeight() / 2.0f;
            lineBottom = lineTop + 3.0f * getWidth() / 8.0f;
            lineLeft = getWidth() / 2.0f - dotRadius;
            lineRight = getWidth() / 2.0f + dotRadius;
        } else {
            dotX = getWidth() / 2.0f;
            dotY = getHeight() / 2.0f - getHeight() / 4.0f;
            dotRadius = getHeight() / 8.0f;
            lineTop = getHeight() / 2.0f;
            lineBottom = lineTop + 3.0f * getHeight() / 8.0f;
            lineLeft = getWidth() / 2.0f - dotRadius;
            lineRight = getWidth() / 2.0f + dotRadius;
        }
        canvas.drawCircle(dotX, dotY, dotRadius, getDesignPaint());
        canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, getDesignPaint());
    }
}
