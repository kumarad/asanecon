package com.techan.custom;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.widget.ProgressBar;

public class Util {

    public static void createBar(Activity activity, ProgressBar bar,  String color, int progressInt) {
        // Define a shape with rounded corners
        final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(Color.parseColor(color));

        // Adds the drawable to your progressBar
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        bar.setProgressDrawable(progress);
        bar.setBackground(activity.getResources().getDrawable(android.R.drawable.progress_horizontal));
        bar.setProgress(progressInt);

    }

    public static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch(NumberFormatException e) {
            return 0.0;
        }
    }

    public static String parseDouble(Cursor c, int index) {
        double d = c.getDouble(index);
        if(d == 0) {
            return "N/A";
        }

        return Double.toString(d);
    }
}
