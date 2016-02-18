package com.laithlab.rhythm.utils;

import android.content.Context;
import android.graphics.Typeface;


public class RalewayFont {

    private static RalewayFont instance;
    private static Typeface lightTypeface;
    private static Typeface regularTypeface;

    public static RalewayFont getInstance(Context context) {
        synchronized (RalewayFont.class) {
            if (instance == null) {
                instance = new RalewayFont();
                lightTypeface = Typeface.createFromAsset(context.getApplicationContext().getResources().getAssets(), "raleway-light.ttf");
                regularTypeface = Typeface.createFromAsset(context.getApplicationContext().getResources().getAssets(), "raleway-regular.ttf");
            }
            return instance;
        }
    }


    public Typeface getTypeFace(String fontType) {
        if(fontType != null && fontType.equals("light")){
            return lightTypeface;
        }
        return regularTypeface;
    }

}
