package com.ring.ytjojo.utill;

import android.util.Log;

public class L {
	
	public static boolean isDebug = false;
	public static String customTagPrefix = "APPLOG";
	private static String generateTag(StackTraceElement stack){
		String tag = "%s.%s(L:%d)";
		String className = stack.getClassName();
		className = className.substring(className.lastIndexOf(".")+1);
		tag = String.format(tag, stack.getClassName(),className,stack.getLineNumber());
		tag = customTagPrefix==null?tag:customTagPrefix+":"+tag;
		return tag;
	}
	
 	public static void i(String msg) {
 		
 		if(isDebug){
 			return;
 		}
 		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
 		String tag = generateTag(caller);
 		Log.i(tag, msg);
 		
 	}

 	public static void d(String msg) {
 		if(isDebug){
 			return;
 		}
 		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
 		String tag = generateTag(caller);
 		Log.i(tag, msg);
 	}

 	public static void e(String msg) {
 		if(isDebug){
 			return;
 		}
 		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
 		String tag = generateTag(caller);
 		Log.i(tag, msg);
 	}

 	public static void v(String msg) {
 		if(isDebug){
 			return;
 		}
 		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
 		String tag = generateTag(caller);
 		Log.i(tag, msg);
 	}
 	public static void w(String msg) {
 		if(isDebug){
 			return;
 		}
 		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
 		String tag = generateTag(caller);
 		Log.i(tag, msg);
 	}

}
