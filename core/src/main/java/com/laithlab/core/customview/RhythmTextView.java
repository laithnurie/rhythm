package com.laithlab.core.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.laithlab.core.R;


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
		String fontType = typedArray.getString(R.styleable.RhythmTextView_textType);
		fontType = fontType != null ? fontType : "regular";
		setTypeface(Typeface.createFromAsset(getResources().getAssets(), "raleway-" + fontType + ".ttf"));
		typedArray.recycle();
	}
}
