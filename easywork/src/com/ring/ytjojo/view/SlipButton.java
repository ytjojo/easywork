package com.ring.ytjojo.view;

import com.example.randomringapp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Checkable;
import android.widget.Scroller;

public class SlipButton extends View implements Checkable{
	private final int STATE_IDLE = 0;
	private final int STATE_DRAGING = 1;
	private final int STATE_SETTING = 2;
	private final int MAX_SETTLE_DURATION = 1000;
	private final int MAX_VELOCITY = 300;
	private final int MIN_VELOCITY = 100;

	private Drawable barDrawable;
	private Drawable onBackDrawable;
	private Drawable offBackDrawable;
	private int onColor = -1;
	private int offColor = -1;
	
	
	private boolean mCheckState = false;// 左边为true 右边为false
	private float nowX;// ����ʱ��x,��ǰ��x
	private int scrollX;
	private Scroller mScroller;
	private int mState;

	private int mActivePointerId;
	private boolean isDirty = true;
	
	private OnCheckedChangeListener mListner;
	private Rect barRect,backRect;
	VelocityTracker mVelocityTracker;
	int halfBarWidth;
	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());
		mState = STATE_IDLE;
		mCheckState = true;
		barDrawable = getResources().getDrawable(R.drawable.new_2_but);
		onBackDrawable = getResources().getDrawable(R.drawable.new_2_but_on);
		offBackDrawable = getResources().getDrawable(R.drawable.new_2_but_off);
		halfBarWidth = barDrawable.getIntrinsicWidth()/2;
		backRect = new Rect(0,0,onBackDrawable.getIntrinsicWidth(),onBackDrawable.getIntrinsicHeight());
		barRect = new Rect(0,0,barDrawable.getIntrinsicWidth(),barDrawable.getIntrinsicHeight());
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}
	private void setup(){
		if(!isDirty)return;
	}
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
	}
	public Drawable getBarDrawable() {
		return barDrawable;
	}

	public void setBarDrawable(Drawable barDrawable) {
		this.barDrawable = barDrawable;
	}

	public Drawable getOnBackDrawable() {
		return onBackDrawable;
	}

	public void setOnBackDrawable(Drawable onBackDrawable) {
		this.onBackDrawable = onBackDrawable;
	}

	public Drawable getOffBackDrawable() {
		return offBackDrawable;
	}

	public void setOffBackDrawable(Drawable offBackDrawable) {
		this.offBackDrawable = offBackDrawable;
	}

	public int getOnColor() {
		return onColor;
	}

	public void setOnColor(int onColor) {
		this.onColor = onColor;
	}

	public int getOffColor() {
		return offColor;
	}

	public void setOffColor(int offColor) {
		this.offColor = offColor;
	}


	

	public void setCheckState(boolean on) {
		if (mCheckState != on) {
			scrollForStateChange(on);
		}

		mCheckState = on;
	}

	private void scrollForStateChange(boolean on) {
		int distanceX = 0;
		if (on) {
			distanceX = getWidth() - barDrawable.getIntrinsicWidth();

			smoothScrollTo(distanceX, 0, 200);
		} else {

			distanceX = 0;
			smoothScrollTo(distanceX, 0, 200);
		}
	}

	private void checkTouchEdge() {

	}

	public boolean getCheckState() {
		return mCheckState;
	}

	public void setOnChangedListener(OnCheckedChangeListener l) {
		mListner = l;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		setup();
		if (barDrawable != null) {
			int top = (getHeight() - barDrawable.getIntrinsicHeight()) / 2;
			barDrawable.setBounds(scrollX, top,
					scrollX + barDrawable.getIntrinsicWidth(), top
							+ barDrawable.getIntrinsicHeight());
			;
			barDrawable.draw(canvas);
		}
		if (mCheckState) {
			if (onBackDrawable != null) {
				onBackDrawable.setBounds(0, 0, getWidth(), getHeight());
				onBackDrawable.draw(canvas);
			}
		} else {
			if (onBackDrawable != null) {
				offBackDrawable.setBounds(0, 0, getWidth(), getHeight());
				offBackDrawable.draw(canvas);
			}
		}

	}

	private void moveTo(int x) {
		if (scrollX == x) {
			return;
		}

		scrollX = x - halfBarWidth;
		checkVilideScrollX();
		invalidate();
		checkState();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (barDrawable == null) {
			return false;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		int action = MotionEventCompat.getActionMasked(event);
		switch (action)// ��ݶ�����ִ�д���
		{
		case MotionEvent.ACTION_MOVE:// ����

			mActivePointerId = MotionEventCompat.getActionIndex(event);
			nowX = event.getX();
			moveTo((int) nowX);
			break;
		case MotionEvent.ACTION_DOWN:// ����
			nowX = event.getX();
			onDown((int) nowX);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			checkState();
			mVelocityTracker.computeCurrentVelocity(1000, MAX_VELOCITY);
			int xVelocity = (int) VelocityTrackerCompat.getXVelocity(
					mVelocityTracker, mActivePointerId);
			startScrollAnim(xVelocity);
			recycle();
			break;
		default:
			break;
		}
		invalidate();// �ػ��ؼ�
		return true;
	}

	private void checkVilideScrollX() {
		int right = getWidth() - barDrawable.getIntrinsicWidth() ;
		if (scrollX > right) {
			scrollX = getWidth() - barDrawable.getIntrinsicWidth() ;
		} else if (scrollX < 0) {
			scrollX = 0;
		}
	}

	private int getValideX(int x) {

		int right = getWidth();
		if (x >= right) {
			x = right;
		} else if (x <= 0) {
			x = 0;
		}
		return x;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int pleft = getPaddingLeft();
		int pright = getPaddingRight();
		int ptop = getPaddingTop();
		int pbottom = getPaddingBottom();

		int w = onBackDrawable.getIntrinsicWidth();
		int h = onBackDrawable.getIntrinsicWidth();
		w += pleft + pright;
		h += ptop + pbottom;
		w = Math.max(w, getSuggestedMinimumWidth());
		h = Math.max(h, getSuggestedMinimumHeight());
		int widthSize = this.resolveSizeAndState(w, widthMeasureSpec, 0);
		int heightSize =this.resolveSizeAndState(h, heightMeasureSpec, 0);
		setMeasuredDimension(widthSize, heightSize);
	}
    /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec.  Will take the desired size, unless a different size
     * is imposed by the constraints.  The returned value is a compound integer,
     * with the resolved size in the {@link #MEASURED_SIZE_MASK} bits and
     * optionally the bit {@link #MEASURED_STATE_TOO_SMALL} set if the resulting
     * size is smaller than the size the view wants to be.
     *
     * @param size How big the view wants to be
     * @param measureSpec Constraints imposed by the parent
     * @return Size information bit mask as defined by
     * {@link #MEASURED_SIZE_MASK} and {@link #MEASURED_STATE_TOO_SMALL}.
     */
    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
            if (specSize < size) {
                result = specSize | MEASURED_STATE_TOO_SMALL;
            } else {
                result = size;
            }
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result | (childMeasuredState&MEASURED_STATE_MASK);
    }	
	private void startScrollAnim(int velocity) {

		int toX = 0;
		if (mCheckState) {
			toX = getWidth() - barDrawable.getIntrinsicWidth() - 0;
		} else {
			toX = 0;
		}
		smoothScrollTo(toX, 0, velocity);

	}

	private void recycle() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			final int x = mScroller.getCurrX();
			scrollX = x;
//			scrollTo(x, mScroller.getCurrY());
			invalidate();

		} else {
			obort();
		}
	}

	private void onDown(int x) {
		mState = STATE_DRAGING;
		if (mScroller.computeScrollOffset()) {
			mScroller.abortAnimation();

		}
		scrollX = x - halfBarWidth;//点击点在bar中间
		checkVilideScrollX();
		invalidate();
		checkState();
	}

	/**
	 * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
	 * 
	 * @param x
	 *            the number of pixels to scroll by on the X axis
	 * @param y
	 *            the number of pixels to scroll by on the Y axis
	 * @param velocity
	 *            the velocity associated with a fling, if applicable. (0
	 *            otherwise)
	 */
	void smoothScrollTo(int x, int y, int velocity) {

		int sx = scrollX;
		int dx = x - sx;
		if (dx == 0) {
			obort();
			return;
		}

		mState = STATE_SETTING;

		final int width = getWidth();
		final int barWidth = barDrawable.getIntrinsicWidth();
		final int halfWidth = width / 2;
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		final float distance = halfWidth + halfWidth
				* distanceInfluenceForSnapDuration(distanceRatio);

		int duration = 0;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			final float pageDelta = (float) Math.abs(dx) / width;
			duration = (int) ((pageDelta + 1) * 100);
			duration = MAX_SETTLE_DURATION;
		}
		duration = Math.min(duration, MAX_SETTLE_DURATION);

		mScroller.startScroll(sx, 0, dx, 0, duration);
		invalidate();
	}

	// We want the duration of the page snap animation to be influenced by the
	// distance that
	// the screen has to travel, however, we don't want this duration to be
	// effected in a
	// purely linear fashion. Instead, we use this method to moderate the effect
	// that the distance
	// of travel has on the overall snap duration.
	float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f; // center the values about 0.
		f *= 0.3f * Math.PI / 2.0f;
		return (float) FloatMath.sin(f);
	}

	private void checkState() {
		final boolean lastBool = mCheckState;
		int range = getWidth() / 2 - barDrawable.getIntrinsicWidth() / 2;
		if (scrollX < range) {
			mCheckState = false;
		} else {
			mCheckState = true;
			
		}
		if(lastBool != mCheckState){
			mListner.onChanged(mCheckState, this);
		}
	}



	private void obort() {
		mScroller.abortAnimation();
	}
	public interface OnCheckedChangeListener {
		public void onChanged(boolean on, SlipButton v);
	}
	@Override
	public void setChecked(boolean checked) {
		setCheckState(checked);
		
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return mCheckState;
	}

	@Override
	public void toggle() {
		mCheckState = ! mCheckState;
		invalidate();
		
	}

}
