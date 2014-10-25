package com.ring.ytjojo.service;

import java.io.IOException;

import com.ring.ytjojo.app.ICONST;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class PlayService extends Service {

	// ���contexΪthis
	Context context;
	Cursor cursor = null;
	private MediaPlayer mediaPlayer = new MediaPlayer(); // ý�岥��������
	private String path; // �����ļ�·��
	private boolean isPause;
//	private String beforUrl = "";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("service", "start");
		if (mediaPlayer.isPlaying()) {
			stop();
		}
		path = intent.getStringExtra("url");
		
		int msg = intent.getIntExtra("MSG", 0);
		
		if (msg ==ICONST.PLAY_MSG) {
			play(0);
		} else if (msg ==ICONST. PAUSE_MSG) {
			pause();
		} else if (msg ==ICONST. STOP_MSG) {
			stop();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	/*
	 * 
	 * ��������
	 * 
	 * @param position
	 */
	private void play(int position) {
//		try {
//			mediaPlayer.reset();// �Ѹ������ָ�����ʼ״̬
//			mediaPlayer.setDataSource(path);
//			mediaPlayer.prepare(); // ���л���
//			mediaPlayer.setOnPreparedListener(new PreparedListener(position));// ע��һ��������
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		Uri myUri = Uri.parse(path);
		//mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(getApplicationContext(), myUri);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("service", "play");
		mediaPlayer.start();

	}

	/**
	 * ��ͣ����
	 */
	private void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}

	/**
	 * ֹͣ����
	 */
	private void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare(); // �ڵ���stop�������Ҫ�ٴ�ͨ��start���в���,��Ҫ֮ǰ����prepare����
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}

	/**
	 * 
	 * ʵ��һ��OnPrepareLister�ӿ�,������׼���õ�ʱ��ʼ����
	 * 
	 */
	private final class PreparedListener implements OnPreparedListener {
		private int positon;

		public PreparedListener(int positon) {
			this.positon = positon;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start(); // ��ʼ����
			if (positon > 0) { // ������ֲ��Ǵ�ͷ����
				mediaPlayer.seekTo(positon);
			}
		}
	}

}
