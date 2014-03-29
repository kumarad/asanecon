package com.techan.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class ReverseProgressBar extends ProgressBar {
    public ReverseProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ReverseProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ReverseProgressBar(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        // Rotate around the center of the progress bar. By default rotates about origin.
        float py = this.getHeight()/2.0f;
        float px = this.getWidth()/2.0f;
        canvas.rotate(180,px,py);

        // Draw the progress bar.
        super.onDraw(canvas);

        // Restore the canvas
        canvas.restore();
    }
}
