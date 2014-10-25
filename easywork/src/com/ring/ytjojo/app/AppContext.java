package com.ring.ytjojo.app;

import java.io.File;
import java.util.ArrayList;

import org.androidannotations.annotations.EApplication;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.entity.mime.MinimalField;

import com.ring.ytjojo.UI.MainActivity;
import com.ring.ytjojo.database.DBManager;
import com.ring.ytjojo.model.Song;
import com.ring.ytjojo.utill.AudioUtills;
import com.ring.ytjojo.utill.SharePreferenceUtil;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

@EApplication
public class AppContext extends Application implements Callback {

	private CustomCrashHandler crashHandler;

	private ArrayList<Song> mPhoneRingSongs;
	private ArrayList<Song> mSMSRingSongs;
	private ArrayList<Song> mAudioExSongs;
	private ArrayList<Song> mAudioInsongs;
	private boolean isFirst = true;
	private boolean isSMSRandom = false;
	private boolean isRingRandom = false;
	private boolean isWallRandom = false;
	public static AppContext mAppContext;
	private static  UIHandler mUIHandler = new UIHandler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIALLDATA:
				INITDATA_ISFINISHED = true;
				
				break;

			default:
				break;
			}
			
		};
		
	};
	public static AppContext getAppContext(){
		return mAppContext;
	}
	@Override
	public void onCreate() {
		Constants.appContext = this;
		// TODO Auto-generated method stub
		super.onCreate();
		crashHandler = CustomCrashHandler.getInstance();
		crashHandler.setCustomCrashHanler(getApplicationContext());
		initConfig();
		asyncInitAllData();
		if(mAppContext == null){
			mAppContext = this;
		}
	}
	
	private void initConfig() {
		SharedPreferences sp = SharePreferenceUtil.openSp(this, "config");
		isRingRandom = sp.getBoolean("ringon", true);
		isSMSRandom = sp.getBoolean("smson", true);
		isWallRandom = sp.getBoolean("wallon", true);
		isFirst = sp.getBoolean("first", true);
	}

	public static UIHandler getUiHandler() {
		return mUIHandler;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	private void restart() {
		System.exit(0);
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public void setRandomPhonRing() {
		int size = mPhoneRingSongs.size();

	}

	public void setRandomSMSRing() {
		int size = mPhoneRingSongs.size();
	}

	public ArrayList<Song> getPhoneRingSongs() {
		return mPhoneRingSongs;

	}

	public ArrayList<Song> getAudioEx() {
		return mAudioExSongs;

	}

	public ArrayList<Song> getAudioIn() {
		return mAudioInsongs;

	}

	public ArrayList<Song> getSMSRingSongs() {
		return mSMSRingSongs;

	}

	public void savePhoneRingSongs(ArrayList<Song> from) {
		mPhoneRingSongs = from;
		DBManager db = DBManager.getInstance();
		db.open();
		if (from.size() == 0) {
			db.clearDatabase(0);
		} else {
			db.insertList(mPhoneRingSongs, 0);

		}

		db.close();
	}

	public void saveSMSRingSongs(ArrayList<Song> from) {
		mSMSRingSongs = from;
		DBManager db = DBManager.getInstance();
		db.open();
		if (from.size() == 0) {
			db.clearDatabase(0);
		} else {

			db.insertList(mPhoneRingSongs, 1);
		}

		db.close();
	}

	public void updateExAudio(ArrayList<Song> from) {
		mAudioExSongs = from;

	}

	public void updateInAudio(ArrayList<Song> from) {
		mAudioInsongs = from;
	}

	public void asyncInitAllData() {
		new Thread() {

			@Override
			public void run() {
				DBManager db = DBManager.getInstance();
				db.open();
				mPhoneRingSongs = db.fetchAllData(0);
				mSMSRingSongs = db.fetchAllData(1);

				db.close();
				mAudioExSongs = AudioUtills.readDateExternal(AppContext.this);
				mAudioInsongs = AudioUtills
						.readDataInternal(getApplicationContext());
				mUIHandler.sendEmptyMessage(MSG_INIALLDATA);
//				Logger.i("size ====================== " + mAudioExSongs.size() );
				Log.i("Appcontext", "size ====================== " + mAudioExSongs.size());
			}
		}.start();
	}

	public static final int MSG_INIALLDATA = 10;
	public static boolean INITDATA_ISFINISHED = false;

	@Override
	public boolean handleMessage(Message msg) {
		
		return false;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Constants.appContext = this;
	}
	
}
