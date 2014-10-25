package com.ring.ytjojo.fragment;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import com.example.randomringapp.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ring.widget.PagerSlidingTabStrip;
import com.ring.ytjojo.UI.MainActivity;
import com.ring.ytjojo.model.MenuItem;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.renderscript.Type;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

@EFragment(R.layout.layout_content)
public class ContentFragment extends BaseFragment {
	@ViewById(R.id.content_viewpager)
	ViewPager vp;

	@ViewById(R.id.slidingTabStrip_2)
	PagerSlidingTabStrip pagerSlidingTabStrip;

	@AfterViews
	void init() {
		
		
		vp.setAdapter(new EasyPagerAdapter(getFragmentManager()));
		vp.setCurrentItem(0);
		pagerSlidingTabStrip.setDisableTensileSlidingBlock(false);
		 pagerSlidingTabStrip.setViewPager(vp);
		 pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageSelected(int position) {
					if (getActivity() instanceof MainActivity) {
						MainActivity main = (MainActivity) getActivity();
						switch (position) {
						case 0:
							main.getSlidingMenu().setTouchModeAbove(
									SlidingMenu.TOUCHMODE_FULLSCREEN);
							break;
						default:
							main.getSlidingMenu().setTouchModeAbove(
									SlidingMenu.TOUCHMODE_MARGIN);
							break;
						}
					}

				}

			});
		if (getActivity() instanceof MainActivity) {
			MainActivity main = (MainActivity) getActivity();
			main.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_FULLSCREEN);
		}

	}
	
	public static ContentFragment newInstance(int num) {
		ContentFragment f = new ContentFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	public class EasyPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mFragments;

		public EasyPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<Fragment>();
			Fragment f= null;
			f = RingListFragment_.builder().type(0).build();
			
			mFragments.add(f);
			f = RingListFragment_.builder().type(1).build();
			mFragments.add(f);

			f = WallpaperFragment_.builder().build();
			mFragments.add(f);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

	}

}
