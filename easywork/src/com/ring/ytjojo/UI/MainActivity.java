package com.ring.ytjojo.UI;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.example.randomringapp.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnScrollListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nineoldandroids.view.ViewHelper;
import com.ring.ytjojo.fragment.ContentFragment;
import com.ring.ytjojo.fragment.ContentFragment_;
import com.ring.ytjojo.fragment.MenuFragment;
import com.ring.ytjojo.fragment.MenuFragment_;
import com.ring.ytjojo.model.MenuItem;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class MainActivity extends SlidingFragmentActivity {

	private Fragment mContent;
	private int lastModel;
	private ActionBar actionBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		 actionBar = getSupportActionBar();
		 actionBar.setDisplayHomeAsUpEnabled(true);
		 actionBar.setDisplayShowHomeEnabled(false);
		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}

		if (mContent == null) {
			mContent = ContentFragment_.builder().arg("num", 0).build();
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		
		Fragment menuFragment = MenuFragment_.builder().arg("num", 0).build();
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame,menuFragment ).commit();

		// customize the SlidingMenu
		final SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeEnabled(true);
		sm.setFadeDegree(0.9f);
		sm.setBehindScrollScale(0.0f);
		sm.setFadeDrawInSlidingMenu(true);
		
		sm.setBackgroundResource(R.drawable.img_frame_background);
		sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float rateScale = 0.35f;
				float scale = (float) (percentOpen * 0.35f + 0.65f);
				float dim = (float)((1 -percentOpen ) * 0.75f );
				float alpha = (float)(percentOpen);//alpha鍊艰秺浣庨�鏄庡害瓒婇珮
				
				
				canvas.scale(scale, scale,-canvas.getWidth() /16,
						canvas.getHeight() / 2);
//				sm.getMenu().setAlpha(alpha);
				ViewHelper.setAlpha(sm.getMenu(), alpha);
//				DrawableUtlil.setDimAmount(sm.getBackground(), dim);
//				final View contentView =  sm.getContent();
//				float contentScale = (float) (1 - percentOpen * 0.25);
//
//				contentView.setPivotX(0);
//				contentView.setPivotY(contentView.getHeight()/2);
//				contentView.setScaleX(contentScale);
//				contentView.setScaleY(contentScale);


			}
		});
		
		
		sm.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(int offsetPix, int xpos, float offset) {
				final View contentView =  sm.getContent();
				float contentScale = (float) (1  + offset * 0.25f  );

//				contentView.setPivotX(0);
//				contentView.setPivotY(contentView.getHeight()/2);
//				contentView.setScaleX(contentScale);
//				contentView.setScaleY(contentScale);
				ViewHelper.setPivotX(contentView, 0);
				ViewHelper.setPivotY(contentView, contentView.getHeight()/2);
				ViewHelper.setScaleX(contentView, contentScale);
				ViewHelper.setScaleY(contentView, contentScale);
				
				
			}

			
		});

//		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//			@Override
//			public void transformCanvas(Canvas canvas, float percentOpen) {
//				float scale = (float) (1 - percentOpen * 0.25);
//				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
//			}
//		});
		

	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	interface OnContenChangedListner{
		void onchanged(int position,MenuItem item);
	}
	
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
}
