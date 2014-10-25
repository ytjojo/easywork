package com.ring.ytjojo.service;


import com.ring.api.SoundSetting;
import com.ring.ytjojo.app.AppContext;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.SoundEffectConstants;

public class RingStoneSetService extends Service{
	private static final String TAG ="RingStoneSetService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
	
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		
		super.onStart(intent, startId);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		int type = intent.getIntExtra("MSG", 0);
		final AppContext app = (AppContext) getApplicationContext();
		if(type == 0){
			app.setRandomPhonRing();
		}else {
			app.setRandomSMSRing();
		}
		Log.i(TAG, "receive---------------------");
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
