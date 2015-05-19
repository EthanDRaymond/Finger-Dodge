package com.edr.fingerdodge.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * @author Ethan Raymond
 */
public class SettingsIconView extends IconView {

    public SettingsIconView(Context context) {
        super(context);
        init();
    }

    public SettingsIconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SettingsIconView(Context context, AttributeSet attributeSet, int defStyleInt) {
        super(context, attributeSet, defStyleInt);
        init();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void onDrawDesign(Canvas canvas) {
        super.onDrawDesign(canvas);
        //onDrawSpokes(canvas);
        onDrawRing(canvas);
    }

    private void onDrawSpokes(Canvas canvas) {
        for (double theta = 0.0; theta < Math.PI; theta += Math.PI / 4.0f) {
            float top = getHeight() / 6.0f;
            float bottom = getHeight() - (getHeight() / 6.0f);
            float left = getWidth() / 3.0f;
            float right = getWidth() - (getWidth() / 3.0f);
            canvas.rotate((float) theta, getWidth() / 2.0f, getHeight() / 2.0f);
            canvas.drawRect(left, top, right, bottom, getDesignPaint());
            canvas.restore();
        }
        canvas.rotate((float) -Math.PI, getWidth() / 2.0f, getHeight() / 2.0f);
    }

    private void onDrawRing(Canvas canvas) {
        float radius = 2 * getWidth() / 3;
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, radius, getDesignPaint());
    }

}
