package com.ring.ytjojo.UI;

import com.actionbarsherlock.app.ActionBar;
import com.example.randomringapp.R;
import com.ring.ytjojo.app.AppContext;
import com.ring.ytjojo.fragment.SelectRingFragment_;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class AudioSelectActivity extends BaseActivity{
	private int mFromType = 0;
	private int mAudioPathType = 0;
	Fragment mContentFragment;
	ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_audioselect);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
		initExtras();
		if(savedInstanceState != null){
			mContentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mContentFragment");
			
		}
		
		if(mContentFragment == null){
			mContentFragment = SelectRingFragment_.builder().mFromType(mFromType).mAudioPathType(mAudioPathType).build();
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	}

	private void initExtras() {
		Intent intent = getIntent();
		mFromType = intent.getIntExtra("fromtype", 0);
		mAudioPathType = intent.getIntExtra("pathtype", 0);
		
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState,"mContentFragment", mContentFragment);
	}
}
