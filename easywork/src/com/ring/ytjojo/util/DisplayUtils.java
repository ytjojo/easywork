package com.ring.ytjojo.util;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

/**
 * dp、sp 转换为 px 的工具类
 * 
 * @author fxsky 2012.11.12
 *
 */
public class DisplayUtils {
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	

	/**
	 * 将dp/dip值转换为px值，
	 * 
	 * @param spValue
	 * @param dipValue
	 *            
	 * @return px像素值
	 */
	public static int dip2pxByTypeValue(Context context,int dipValue){
		return Math.round(TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,dipValue , context.getResources()
							.getDisplayMetrics()));
	}
	/**
	 * 将sp/dip值转换为px值，
	 * 
	 * @param spValue
	 * @param dipValue
	 *            
	 * @return px像素值
	 */
	public static int sp2pxByTypeValue(Context context,int spValue){
		return Math.round(TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP,spValue , context.getResources()
							.getDisplayMetrics()));
	}
	/**
	 * 
	 * 
	 * 
	 * @param activity
	 * @return 屏幕宽度
	 */
	public static int getScreenWidth(Activity activity){
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		return metrics.widthPixels;
	}
	/**
	 * 
	 * 
	 * 
	 * @param activity
	 * @return 屏幕高度
	 */
	public static int getScreenHeight(Activity activity){
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		return metrics.heightPixels;
	}
	//获取状态栏高度 反射
	public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
	//获得状态栏高度
	public static int getStatusBarHeight(View  decorView){
		Rect rect = new Rect();
		decorView.getWindowVisibleDisplayFrame(rect);
		final int height = rect.top;
		return height;
	}
	
	public static int getTitleHeight(View decorView){
		Rect rect = new Rect();
		decorView.getWindowVisibleDisplayFrame(rect);
		final int height = rect.height() - decorView.getHeight();
		return height;
	}
	
}