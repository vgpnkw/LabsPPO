package com.example.labtwoppo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontChangeSize {

    public static void changeFontSizeInViews(View view, Integer value) {
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(value);
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
                if (((ViewGroup) view).getChildAt(i).getId() != R.id.timerTextView) {
                    changeFontSizeInViews(((ViewGroup) view).getChildAt(i), value);
                }
        }
    }

}
