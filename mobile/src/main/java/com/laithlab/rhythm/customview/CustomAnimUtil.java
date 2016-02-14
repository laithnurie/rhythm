package com.laithlab.rhythm.customview;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;

public class CustomAnimUtil {

    public static void overShootAnimation(View view) {
        RotateAnimation rotate = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setInterpolator(new OvershootInterpolator());
        view.startAnimation(rotate);
    }
}
