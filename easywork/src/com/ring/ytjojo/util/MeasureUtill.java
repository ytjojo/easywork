package com.ring.ytjojo.util;
import android.view.View;
import android.view.View.MeasureSpec;


public class MeasureUtill {
	
	
	public static void measure(View v,int widthMeasureSpec, int heightMeasureSpec ){
		
		
		
		
	}
	
	private static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
	    int result = size;
	    int specMode = MeasureSpec.getMode(measureSpec);
	    int specSize =  MeasureSpec.getSize(measureSpec);
	    switch (specMode) {
	    case MeasureSpec.UNSPECIFIED:
	        result = size;
	        break;
	    case MeasureSpec.AT_MOST:
	        if (specSize < size) {
	            result = specSize |View. MEASURED_STATE_TOO_SMALL;
	        } else {
	            result = size;
	        }
	        break;
	    case MeasureSpec.EXACTLY:
	        result = specSize;
	        break;
	    }
	    return result | (childMeasuredState&View.MEASURED_STATE_MASK);
	}
}
