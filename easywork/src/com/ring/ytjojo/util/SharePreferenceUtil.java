package com.ring.ytjojo.util;

import java.util.ArrayList;

import com.ring.ytjojo.app.ICONST;
import com.ring.ytjojo.model.Song;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class SharePreferenceUtil {

	public static SharedPreferences openOtherAppSP(Context c,
			String packageName, String sharePreference_name)
			throws NameNotFoundException {
		Context otherContext = c.createPackageContext(packageName,
				Context.CONTEXT_IGNORE_SECURITY);
		SharedPreferences sp = otherContext.getSharedPreferences(
				sharePreference_name, Context.MODE_WORLD_READABLE);
		return sp;
	}

	public static void clearSharePre(SharedPreferences sp) {

		sp.edit().clear().commit();
	}

	public static SharedPreferences openSp(Context c, String name) {

		return c.getSharedPreferences(name, Context.MODE_PRIVATE);

	}

	public static SharedPreferences getTopActivitySP(Context c) {
		return c.getSharedPreferences(getRunningActivityName(c),
				Context.MODE_PRIVATE);
	}


	private static String getRunningActivityName(Context c) {
		ActivityManager activityManager = (ActivityManager) c
				.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}



}
