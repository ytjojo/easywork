package com.ring.ytjojo.fragment;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

public class CircleScrollViewPagerFragment extends BaseFragment implements
		OnPageChangeListener {

	ArrayList<View> views = new ArrayList<View>();
	ViewPager viewPager;

	void initViewPager() {

	}

	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if(views.size() ==2 || views.size() ==3){
				return;
			}
			((ViewPager) container).removeView(views.get(position
					% views.size()));

		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			boolean isSucss = false;
			View view = views.get(position % views.size());
			try {
				 container.addView(view,0);
				isSucss = true;
				
			} catch (Exception e) {
				isSucss = false;
			
				
			}
			if(!isSucss){
				ViewGroup group = (ViewGroup) view.getParent();
				group.removeView(view);
				container.addView(view,0);
			}
			return views.get(position % views.size());
		}

	}
	private int lastState;
	@Override
	public void onPageScrollStateChanged(int arg0) {
		switch(arg0){  
        case ViewPager.SCROLL_STATE_DRAGGING:  
        case ViewPager.SCROLL_STATE_SETTLING:  
            lastState = arg0;  
            break;  
        case ViewPager.SCROLL_STATE_IDLE:  
            if(lastState == ViewPager.SCROLL_STATE_DRAGGING){  
                if(viewPager.getCurrentItem()==views.size()-1){  
                    viewPager.setCurrentItem(0);  
                }else if(viewPager.getCurrentItem()==0){  
                	viewPager.setCurrentItem(views.size());  
                }  
            }  
            break;  
    }  
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		
		
	}

}
