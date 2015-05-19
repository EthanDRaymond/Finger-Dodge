package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * This view displays a general icon at the bottom left of the gameplay screen.
 * @author Ethan Raymond
 */
public class IconView extends View {

    private Paint bgPaint;
    private Paint designPaint;

    public IconView(Context context){
        super(context);
        init();
    }

    public IconView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }
    public IconView(Context context, AttributeSet attributeSet, int defStyleInt){
        super(context, attributeSet, defStyleInt);
        init();
    }

    protected void init() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setAntiAlias(true);
        bgPaint.setAlpha(18);
        designPaint = new Paint();
        designPaint.setColor(Color.WHITE);
        designPaint.setAntiAlias(true);
        designPaint.setAlpha(80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawBG(canvas);
        onDrawDesign(canvas);
    }

    protected void onDrawBG(Canvas canvas){
        float x, y, radius;
        if (getWidth() < getHeight()){
            x = getWidth() / 2.0f;
            y = getHeight() / 2.0f;
            radius = getWidth() / 2.0f;
        } else {
            x = getWidth() / 2.0f;
            y = getHeight() / 2.0f;
            radius = getHeight() / 2.0f;
        }
        canvas.drawCircle(x, y, radius, bgPaint);
    }

    protected void onDrawDesign(Canvas canvas){

    }

    public void setBgColor(int color){
        bgPaint.setColor(color);
    }

    public void setDesignColor(int color){
        designPaint.setColor(color);
    }

    public Paint getBgPaint() {
        return bgPaint;
    }

    public Paint getDesignPaint() {
        return designPaint;
    }
}