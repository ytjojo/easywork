package com.ring.ytjojo.fragment;

import com.example.randomringapp.R;
import com.ring.ytjojo.util.SharePreferenceUtil;
import com.ring.ytjojo.view.SlipButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AppSettingFragment extends BaseFragment {

	private View mContentView;

	private boolean isSMSRandom = false;
	private boolean isRingRandom = false;
	private boolean isWallRandom = false;
	private SlipButton mSmsSlip = null;
	private SlipButton mRingSlip = null;
	private SlipButton mWallSlip = null;

	private TextView mRingText = null;
	private TextView mSmsText = null;
	private TextView mWallText = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.tab1_activity, null);
		return mContentView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ----------

		// --------
		mSmsSlip = (SlipButton) mContentView.findViewById(R.id.view2);
		mRingSlip = (SlipButton) mContentView.findViewById(R.id.view1);
		mWallSlip = (SlipButton) mContentView.findViewById(R.id.view3);
		
	}
	private void init() {

		SharedPreferences sp = SharePreferenceUtil.openSp(getActivity(),"config");
		isRingRandom = sp.getBoolean("ringon", true);
		isSMSRandom = sp.getBoolean("smson", true);
		isWallRandom = sp.getBoolean("wallon", true);
	}

	public void saveConfig() {
		SharedPreferences sp = SharePreferenceUtil.openSp(getActivity(),"config");
		Editor setting = sp.edit();
		setting.putBoolean("ringon",true);
		setting.putBoolean("smson",true);
		setting.putBoolean("wallon",true);
		setting.commit();
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		saveConfig();
		super.onPause();
	}

	@Override
	public void onResume() {
		init();
		super.onResume();
	}
}
