package com.ring.ytjojo.UI;

import com.example.randomringapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.support.v4.widget.ContentLoadingProgressBar;

public class AppStart extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private boolean isPressBack = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_appstart);
		ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);
		progressBar.show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isPressBack)
					return;

				startNewActivity();
			}
		}, SPLASH_DISPLAY_LENGHT);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startNewActivity();
			return true;
		}
		return false;
	}

	private void startNewActivity() {
		Intent mainIntent = new Intent(AppStart.this, MainActivity.class);
		AppStart.this.startActivity(mainIntent);
		AppStart.this.finish();
	}
}
