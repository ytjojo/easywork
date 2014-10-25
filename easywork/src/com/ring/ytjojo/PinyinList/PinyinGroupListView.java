package com.ring.ytjojo.PinyinList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinyinGroupListView extends ListView {

	private SlideView mFocusedItemView;
	PinyinListWrapper slidBar;
	View mHeaderView;
	boolean mHeaderViewVisible;
	int MAX_ALPHA = 100;
	int mHeaderViewWidth;
	int mHeaderViewHeight;
	PinnedHeaderAdapter mAdapter;

	public PinyinGroupListView(Context context) {
		super(context);
		init();

	}

	public PinyinGroupListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PinyinGroupListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		slidBar = new PinyinListWrapper(this);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
		slidBar.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		// TODO Auto-generated method stub
		return super.onTouchEvent(ev);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:

			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int position = pointToPosition(x, y);

			final int numHeaders = getHeaderViewsCount();
			final int numFooters = getFooterViewsCount();

			if (position != INVALID_POSITION && position >= numHeaders
					&& position < getCount() - numFooters) {
				View view = getChildAt(position - getFirstVisiblePosition());
				if (view instanceof SlideView) {

					// --同时存在可注释掉
					if (mFocusedItemView != view) {
						if (mFocusedItemView != null) {
							if (mFocusedItemView.isSideslip()) {

								mFocusedItemView.shrink();
								// mFocusedItemView =null;
							} else {

								// mFocusedItemView = (SlideView) view;
							}
						}
					}

					mFocusedItemView = (SlideView) view;
				}
			}

			break;
		}

		if (mFocusedItemView != null) {
			mFocusedItemView.onRequireTouchEvent(ev);
			if (mFocusedItemView.isIntercepteOntouch) {
				return true;
			} else {
				return super.onInterceptTouchEvent(ev);
			}
		}
		return super.onInterceptTouchEvent(ev);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		slidBar.dispatchTouchEvent(ev);
		if (slidBar.isPressed()) {
			return true;
		} else {
			return super.dispatchTouchEvent(ev);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if (mHeaderView != null) {
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			configureHeaderView(getFirstVisiblePosition());
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		if (adapter instanceof PinnedHeaderAdapter) {
			mAdapter = (PinnedHeaderAdapter) adapter;
			mHeaderView = mAdapter.getHeadView();
		}
	}

	/**
	 * 创建顶部悬浮框
	 */
	private void createPinnedHeader(int position) {
		// 获取对应位置的悬浮框
		if (mHeaderView == null) {

			mHeaderView = mAdapter.getHeadView();
		}
		if (mHeaderView == null) {
			return;
		}
		// 获取悬浮框的宽高,并根据宽高测量并布局悬浮框view(创建view)
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mHeaderView
				.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}

		int heightMode = MeasureSpec.getMode(layoutParams.height);
		int heightSize = MeasureSpec.getSize(layoutParams.height);

		if (heightMode == MeasureSpec.UNSPECIFIED)
			heightMode = MeasureSpec.EXACTLY;

		int maxHeight = getHeight() - getListPaddingTop()
				- getListPaddingBottom();
		if (heightSize > maxHeight)
			heightSize = maxHeight;

		int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft()
				- getListPaddingRight(), MeasureSpec.EXACTLY);
		int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
		mHeaderView.measure(ws, hs);
		mHeaderViewWidth = mHeaderView.getWidth();
		mHeaderViewHeight = mHeaderView.getHeight();
		mHeaderView.layout(0, 0, mHeaderView.getMeasuredWidth(),
				mHeaderView.getMeasuredHeight());

	}

	public void configureHeaderView(int position) {
		if (mHeaderView == null || null == mAdapter) {
			return;
		}

		int state = mAdapter.getPinnedHeaderState(position);
		switch (state) {
		case PinnedHeaderAdapter.PINNED_HEADER_GONE: {
			mHeaderViewVisible = false;
			break;
		}

		case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE: {
			mAdapter.configurePinnedHeader(mHeaderView, position, MAX_ALPHA);
			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			}
			mHeaderViewVisible = true;
			break;
		}

		case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP: {
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			// int itemHeight = firstView.getHeight();
			int headerHeight = mHeaderView.getHeight();
			int y;
			int alpha;
			if (bottom < headerHeight) {
				y = (bottom - headerHeight);
				alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
			} else {
				y = 0;
				alpha = MAX_ALPHA;
			}
			mAdapter.configurePinnedHeader(mHeaderView, position, alpha);
			if (mHeaderView.getTop() != y) {
				mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight
						+ y);
			}
			mHeaderViewVisible = true;
			break;
		}
		}
	}

}
