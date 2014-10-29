package com.ring.ytjojo.util;

import android.graphics.Color;
import android.text.Html;
import android.widget.TextView;

public class TextViewUtill {
	public static void setTextStarColor(TextView textView){
		if(!textView.isInEditMode()){
			return;
		}
		String text = textView.getText().toString();
        int lastIndex = text.lastIndexOf("*");

        StringBuilder sb = new StringBuilder();
        if (lastIndex >= 0)
        {
            sb.append(text.substring(0, lastIndex));
        } else
        {
            sb.append(text.substring(0));
        }
        sb.append("<font color=\"").append(Color.RED)
        .append("\">*");
      

        textView.setText(Html.fromHtml(sb.toString()));
	}
}	
