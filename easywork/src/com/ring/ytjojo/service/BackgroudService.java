package com.ring.ytjojo.service;


import com.example.randomringapp.R;
import com.ring.ytjojo.UI.AppStart;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BackgroudService extends Service {
	private final static String TAG = BackgroudService.class.getSimpleName();
	public static final String ACTION = "com.ring.ytjojo.startService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setListner();
		
	}
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
//		 Notification notification = new Notification(R.drawable.ic_launcher,  
//				 getString(R.string.app_name), System.currentTimeMillis());  
				  
//				 PendingIntent pendingintent = PendingIntent.getActivity(this, 0,  
//				 new Intent(this, AppStart.class), 0);  
//				 notification.setLatestEventInfo(this, "RING", "正在后台运行",  
//				 pendingintent); 
//				 startForeground(0x111, notification);
				 
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopForeground(true);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(ACTION);
				sendBroadcast(intent);
			}
		}, 2000);
		super.onDestroy();
	}

	public void setListner() {
		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(new OnePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

		ContentObserver co = new SmsReceiver(new Handler(), BackgroudService.this);
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), true, co);
	}

	/**
	 * 电话状态监听.
	 * 
	 * @author stephen
	 * 
	 */
	class OnePhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.i(TAG, "[Listener]电话号码:" + incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i(TAG, "[Listener]等待接电话:" + incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				Log.i(TAG, "[Listener]电话挂断:" + incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.i(TAG, "[Listener]通话中:" + incomingNumber);
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private class SmsReceiver extends ContentObserver { 
		   
	    public SmsReceiver(Handler handler, Context context) { 
	        super(handler); 
	        
	    } 
	 
	    @Override 
	    public void onChange(boolean selfChange) { 
	    	Intent intent = new Intent(BackgroudService.this,RingStoneSetService.class);
			intent.putExtra("MSG", 1);
			startService(intent);
	        super.onChange(selfChange); 
	    } 
	} 
}
