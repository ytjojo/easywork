package com.ring.ytjojo.fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class BaseFragment extends SherlockFragment implements OnBackPressListner{
	public static String TAG;
	protected com.actionbarsherlock.app.ActionBar actionBar;
	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static BaseFragment newInstance(int num) {
		BaseFragment f = new BaseFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	@Override
	public boolean onBackPress() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		actionBar =( (SherlockFragmentActivity)getActivity()).getSupportActionBar();
	setHasOptionsMenu(true);
	}



}
