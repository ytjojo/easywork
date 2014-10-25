package com.ring.ytjojo.fragment;

import org.androidannotations.annotations.EFragment;

import com.example.randomringapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;

@EFragment(R.layout.layout_wallpaper)
public class WallpaperFragment extends BaseFragment {
	
	public static WallpaperFragment newInstance(int num) {
		WallpaperFragment f = new WallpaperFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}
	
}
