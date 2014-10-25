package com.ring.api;

import java.net.URI;
import java.net.URL;
import java.util.Random;

import com.ring.ytjojo.app.ICONST;
import com.ring.ytjojo.utill.SharePreferenceUtil;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.WebIconDatabase.IconListener;

public class SoundSetting {
	public static final String NOTIFICATION_RINGTONE = "pref_notification_ringtone";
	public static final String PHONE_RINGTONE = "pref_phone_ringtone";

	private SoundSetting() {
	}

	public static void setSMS(Uri uri, Context c) {
		RingtoneManager.setActualDefaultRingtoneUri(c,
				RingtoneManager.TYPE_NOTIFICATION, uri);
	}

	public static void setPhone(Uri uri, Context c) {

		RingtoneManager.setActualDefaultRingtoneUri(c,
				RingtoneManager.TYPE_RINGTONE, uri);
	}
	public static void set(int type, Uri uri,Context c){
		if(type == 0){
			setPhone(uri, c);
			
		}else{
			setSMS(uri, c);
		}
	}

	public static Random mRandom = new Random();

	public static int getRandom(int s, int e) {
		return ((mRandom.nextInt() >>> 1) % (e - s + 1)) + s;
	}

	public static Uri getDefaultUri(int type, Context c) {
		switch (type) {
		case 0:
			return RingtoneManager.getActualDefaultRingtoneUri(c,
					RingtoneManager.TYPE_RINGTONE);
		case 1:
			return RingtoneManager.getActualDefaultRingtoneUri(c,
					RingtoneManager.TYPE_NOTIFICATION);
		}
		return null;
	}

	public static Uri getRingstoneUri(int type, Context c) {
		switch (type) {
		case 0:
			return RingtoneManager.getActualDefaultRingtoneUri(c,
					RingtoneManager.TYPE_RINGTONE);
		case 1:
			return RingtoneManager.getActualDefaultRingtoneUri(c,
					RingtoneManager.TYPE_NOTIFICATION);
		}
		return null;
	}
	public static Uri getUriFromPath(String data,String id){
		return Uri.parse(data + id);
	}


	public void isSDUri(String path) {
		UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI("media", "external/audio/media/#", 1);// #ͨ���ƥ������
		sMatcher.addURI("media", "internal/audio/media/#", 2);
		sMatcher.addURI("settings", "system/*", 3);// notification_sound��*ͨ���ƥ������
		switch (sMatcher.match(Uri.parse(path))) {
		case 1:
			// ���£��ᵼ��д���������װ��SD����ʱ�򣬲��ָܻ�����ǰѡ��SD������
			boolean sdCardExist = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);
			if (!sdCardExist) {
				// Editor editor = prefs.edit();
				// editor.putString(CalendarPreferenceActivity.KEY_ALERTS_RINGTONE,
				// "content://settings/system/notification_sound");
				// editor.commit();
				// notification.sound =
				// Settings.System.DEFAULT_NOTIFICATION_URI;
				// return;
			}
			break;
		default:
			break;
		}
	}
}
