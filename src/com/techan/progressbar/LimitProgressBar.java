package com.techan.progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.techan.R;
import com.techan.custom.Util;

public class LimitProgressBar extends SaundProgressBar {
    private Drawable firstLimitDrawable;
    private Drawable secondLimitDrawable;

    public LimitProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        firstLimitDrawable = getResources().getDrawable(R.drawable.progress_indicator_b2);
        Rect bounds = new Rect(0, 0, firstLimitDrawable.getIntrinsicWidth() + 5, firstLimitDrawable.getIntrinsicHeight());
        firstLimitDrawable.setBounds(bounds);

        secondLimitDrawable = getResources().getDrawable(R.drawable.progress_indicator_b2);
        bounds = new Rect(0, 0, secondLimitDrawable.getIntrinsicWidth() + 5, secondLimitDrawable.getIntrinsicHeight());
        secondLimitDrawable.setBounds(bounds);


        m_indicator = getResources().getDrawable(R.drawable.progress_indicator_b2);
        bounds = new Rect(0, 0, m_indicator.getIntrinsicWidth() + 5, m_indicator.getIntrinsicHeight());
        m_indicator.setBounds(bounds);
        setProgressIndicator(m_indicator);
    }


    String firstLimitStr;
    double firstLimitProgress;

    String secondLimitStr;
    double secondLimitProgress;

    public synchronized void setValue(double val, double firstLimit, String firstLimitStr, double secondLimit, String secondLimitStr) {
        this.firstLimitStr = firstLimitStr;
        this.secondLimitStr = secondLimitStr;

        double min = Util.findMin(val, firstLimit, secondLimit);
        double max = Util.findMax(val, firstLimit, secondLimit);

        if(val == min) {
            setProgressDrawable(getResources().getDrawable(R.drawable.red_progressbar));
        } else {
            setProgressDrawable(getResources().getDrawable(R.drawable.green_progressbar));
        }


        int range =  (int)(max - min);
        int interval = range/10;
        if(interval == 0) {
            interval = range;
        }

        int start = (int)min - interval;
        int end = (int)max + interval;


        int rangeProgress = end - start;

        firstLimitProgress = (((firstLimit - start)/rangeProgress) * 100);
        secondLimitProgress = (((secondLimit - start)/rangeProgress) * 100);

        int valProgress = (int)(((val - start)/rangeProgress) * 100);
        setValue(Double.toString(val), valProgress);
    }


    @Override
    protected synchronized void handleOtherIndicators(Canvas canvas, int dx) {
        int progress = getProgress();
        double increment = (double)dx/(double)progress;

        if(firstLimitDrawable !=null) {
            canvas.save();
            double firstInc = increment * firstLimitProgress;
            // translate the canvas to the position where we should draw the indicator
            canvas.translate((int)firstInc, 0);
            firstLimitDrawable.draw(canvas);

            canvas.drawText(firstLimitStr, getIndicatorWidth()/2, getIndicatorHeight()/2 + 1, m_textPaint);

            // restore canvas to original
            canvas.restore();
        }

        if(secondLimitDrawable !=null) {
            canvas.save();
            double secondInc = increment * secondLimitProgress;
            // translate the canvas to the position where we should draw the indicator
            canvas.translate((int)secondInc, 0);
            secondLimitDrawable.draw(canvas);

            canvas.drawText(secondLimitStr, getIndicatorWidth()/2, getIndicatorHeight()/2 + 1, m_textPaint);

            // restore canvas to original
            canvas.restore();
        }
    }
}
