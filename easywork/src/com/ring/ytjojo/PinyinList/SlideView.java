package com.ring.ytjojo.PinyinList;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

public class SlideView extends LinearLayout {

	private static final String TAG = "SlideView";

	private Context mContext;
	private LinearLayout mViewContent;
	private Scroller mScroller;
	private OnSlideListener mOnSlideListener;

	private int mHolderWidth = -1;

	private int mLastX = 0;
	private int mLastY = 0;
	private static final int TAN = 1;
	private int mItemWidth = 60;
	private int itemCount;
	private View mSlidHolder;
	private ListView mListView;
	private int mState;
	
	
	private int mTouchSloop;
	public interface OnSlideListener {
		public static final int STATE_IDLE = 0;
		public static final int SLIDE_DOWN = 1;
		public static final int SLIDE_MOVE = 2;
		public static final int SLIDE_SETTING= 3;

		/**
		 * @param view
		 *            current SlideView
		 * @param status
		 *            SLIDE_STATUS_ON or SLIDE_STATUS_OFF
		 */
		public void onSlide(View view, int status);
	}

	public SlideView(Context context) {
		super(context);
		initView();
	}

	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mContext = getContext();
		mScroller = new Scroller(mContext);

		setOrientation(LinearLayout.HORIZONTAL);
		mViewContent = new LinearLayout(mContext);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewContent.setOrientation(LinearLayout.HORIZONTAL);
		addView(mViewContent, params);
		float density = getResources().getDisplayMetrics().density;
		mTouchSloop = ViewConfiguration.getTouchSlop();
		mTouchSloop = (int) (density * mTouchSloop +0.5f);
		
	}



	public void setContentView(View view) {
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewContent.addView(view, params);
		requestLayout();
		invalidate();
	}

	public void setOnSlideListener(OnSlideListener onSlideListener) {
		mOnSlideListener = onSlideListener;
	}

	public void shrink() {
		if (getScrollX() != 0) {
			this.smoothScrollTo(0, 0);
		}
	}

	/**
	 * 直接关闭，无动画
	 */
	public void shrinkImmediately() {
		if (getScrollX() != 0) {
			this.scrollTo(0, 0);
		}
	}

	public boolean isSideslip() {
		if (getScrollX() != 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);

	}
	public boolean isIntercepteOntouch;
	private int mDownX;
	private int mDownY;
	
	public boolean onRequireTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int scrollX = getScrollX();
		Log.d(TAG, "x=" + x + "  y=" + y);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mDownX = (int) event.getX();
			mDownY = (int) event.getY();
			mState = OnSlideListener.SLIDE_DOWN;
			if (mOnSlideListener != null) {
				mOnSlideListener.onSlide(this,
						OnSlideListener.SLIDE_DOWN);
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int deltaX = x - mLastX;
			int deltaY = y - mLastY;
			int distanceX = x - mDownX;
			int distanceY = y - mDownY;
			
			if(mState == OnSlideListener.SLIDE_DOWN &&(distanceX * distanceX + distanceY + distanceY ) >= (mTouchSloop *mTouchSloop) && Math.abs(distanceY)<Math.abs(distanceX)){
			
				isIntercepteOntouch = true;
				mState = OnSlideListener.SLIDE_MOVE;
				
			}
			if (mOnSlideListener != null) {
				mOnSlideListener.onSlide(this,
						mState);
			}
			int newScrollX = scrollX - deltaX;
			final int overScollX = getOverScrollEdge();
			if (deltaX != 0) {
				if (newScrollX < - overScollX) {
					newScrollX = - overScollX;
				} else if (newScrollX > mHolderWidth +overScollX) {
					newScrollX = mHolderWidth + overScollX;
				}
				this.scrollTo(newScrollX, 0);
			}
			
			break;
		}
		case MotionEvent.ACTION_UP: {
			int newScrollX = 0;

			if (scrollX > getHolderWidth() * 0.5) {
				newScrollX = mHolderWidth;
			}
			this.smoothScrollTo(newScrollX, 0);
			if (mOnSlideListener != null) {
				mOnSlideListener.onSlide(this,
						newScrollX == 0 ? OnSlideListener.STATE_IDLE
								: OnSlideListener.SLIDE_SETTING);
			}
			
			break;
		}
		default:
			break;
		}

		mLastX = x;
		mLastY = y;
		return false;
	}

	private void smoothScrollTo(int destX, int destY) {
		// 缓慢滚动到指定位置
		int scrollX = getScrollX();
		int delta = destX - scrollX;
		float radio = Math.abs(delta)/ mHolderWidth;
		final int duration = 300;
		if(delta != 0){
			
			mScroller.startScroll(scrollX, 0, delta, 0, (int)(duration * radio));
			invalidate();
		}
		// mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
	}
	public int getOverScrollEdge(){
		return mHolderWidth/ 6;
	}
	public int getHolderWidth() {
		
		return mHolderWidth;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			return;
		}
		if(mState == OnSlideListener.SLIDE_SETTING ){
			mState = OnSlideListener.STATE_IDLE;
			mOnSlideListener.onSlide(this, mState); 
		}
	}
	

	private ArrayList<View> items = new ArrayList<View>();

	public void setItemCount(int count) {
		this.itemCount = count;
		this.mHolderWidth = itemCount * mItemWidth;
		this.removeAllViews();

		if(mViewContent == null){
			mViewContent = new LinearLayout(mContext);
		}
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewContent.setOrientation(LinearLayout.HORIZONTAL);
		items.clear();
		addView(mViewContent, params);
		for (int i = 0; i < count; i++) {
			FrameLayout f = new FrameLayout(getContext());
			ImageView img = new ImageView(getContext());
			FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fp.gravity = Gravity.CENTER;
			f.addView(img, fp);
			items.add(f);
			params = new LayoutParams(mItemWidth, LayoutParams.MATCH_PARENT);
			this.addView(f, params);
		}
	}

}
