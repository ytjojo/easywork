package com.ring.widget;

import com.example.randomringapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.Checkable;
import android.widget.ImageView;

public class SlipButton extends View implements OnTouchListener, Checkable {
	private boolean ischecked = false;//

	private boolean OnSlip = false;//

	private float DownX, eventX;//

	private Rect onRect, offRect;//

	private OnChangedListener listener;

	private Bitmap mBgOn, mBgOff, mSlipButton;

	Matrix matrix = new Matrix();
	Paint paint = new Paint();

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mBgOn = BitmapFactory.decodeResource(getResources(),
				R.drawable.new_2_but_on);
		mBgOff = BitmapFactory.decodeResource(getResources(),
				R.drawable.new_2_but_off);
		mSlipButton = BitmapFactory.decodeResource(getResources(),
				R.drawable.new_2_but);
		onRect = new Rect(0, 0, mSlipButton.getWidth(), mSlipButton.getHeight());
		offRect = new Rect(mBgOff.getWidth() - mSlipButton.getWidth(), 0,
				mBgOff.getWidth(), mSlipButton.getHeight());
		setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (ischecked) {
			canvas.drawBitmap(mBgOff, matrix, paint);
		} else {
			canvas.drawBitmap(mBgOn, matrix, paint);
		}
		float mLeft;
		if (eventX >= mBgOn.getWidth()) {
			mLeft = mBgOn.getWidth() - mSlipButton.getWidth();
		} else if (eventX < 0) {
			mLeft = 0;
		} else {
			mLeft = eventX - mSlipButton.getWidth() / 2;
		}
		if (mLeft < 0)
			mLeft = 0;
		else if (mLeft > mBgOn.getWidth() - mSlipButton.getWidth())
			mLeft = mBgOn.getWidth() - mSlipButton.getWidth();
		canvas.drawBitmap(mSlipButton, mLeft,
				(mBgOn.getHeight() - mSlipButton.getHeight()) / 2, paint);
	}

	public boolean onTouch(View v, MotionEvent event) {
		boolean LastChoose = ischecked;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:// ����
			eventX = event.getX();
			if (eventX < (mBgOn.getWidth() / 2)) {
				ischecked = false;
			} else {
				ischecked = true;

			}
			// if (listener != null && (LastChoose != ischecked)) {
			// listener.OnChanged(ischecked);
			// }
			break;

		case MotionEvent.ACTION_DOWN:// ����

			if (event.getX() > mBgOn.getWidth()
					|| event.getY() > mBgOn.getHeight()) {
				return false;
			}
			OnSlip = true;
			DownX = event.getX();
			eventX = DownX;
			break;

		case MotionEvent.ACTION_CANCEL: //

			OnSlip = false;
			boolean choose = ischecked;
			if (eventX >= (mBgOn.getWidth() / 2)) {
				eventX = mBgOn.getWidth() - mSlipButton.getWidth() / 2;
				ischecked = true;
			} else {
				eventX = eventX - mSlipButton.getWidth() / 2;
				ischecked = false;
			}
			if (listener != null && (choose != ischecked)) {
				listener.OnChanged(ischecked);
			}
			break;
		case MotionEvent.ACTION_UP://

			OnSlip = false;

			if (event.getX() >= (mBgOn.getWidth() / 2)) {
				eventX = mBgOn.getWidth() - mSlipButton.getWidth() / 2;
				ischecked = true;
			} else {
				eventX = eventX - mSlipButton.getWidth() / 2;
				ischecked = false;
			}

			if (listener != null && (LastChoose != ischecked)) {
				listener.OnChanged(ischecked);
			}
			break;
		default:
		}
		invalidate();
		return true;
	}

	public void SetOnChangedListener(OnChangedListener l) {// ���ü�����,��״̬�޸ĵ�ʱ��
		ischecked = true;
		listener = l;
		invalidate();
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return ischecked;
	}

	@Override
	public void setChecked(boolean arg0) {
		ischecked = arg0;
		invalidate();
	}

	@Override
	public void toggle() {
		ischecked = !ischecked;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int pleft = getPaddingLeft();
		int pright = getPaddingRight();
		int ptop = getPaddingTop();
		int pbottom = getPaddingBottom();

		int w = mBgOn.getWidth();
		int h = mBgOn.getHeight();
		w += pleft + pright;
		h += ptop + pbottom;
		w = Math.max(w, getSuggestedMinimumWidth());
		h = Math.max(h, getSuggestedMinimumHeight());
		int widthSize = this.resolveSizeAndState(w, widthMeasureSpec, 0);
		int heightSize = this.resolveSizeAndState(h, heightMeasureSpec, 0);
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
    
    public static void get(){
    	
    }
}
