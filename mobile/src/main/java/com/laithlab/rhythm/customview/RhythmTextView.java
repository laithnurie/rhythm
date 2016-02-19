package com.laithlab.rhythm.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.utils.RalewayFont;


public class RhythmTextView extends TextView {

    public RhythmTextView(Context context) {
        super(context);
    }

    public RhythmTextView(Context context, int defStyle) {
        super(context);
    }

    public RhythmTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        postConstruct(context, attrs);
    }

    public RhythmTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        postConstruct(context, attrs);
    }

    private void postConstruct(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RhythmTextView);
        setTypeface(RalewayFont.getInstance(context.getApplicationContext())
                .getTypeFace(typedArray.getString(R.styleable.RhythmTextView_textType)));
        typedArray.recycle();
    }
}
