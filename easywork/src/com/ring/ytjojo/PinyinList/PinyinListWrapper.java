package com.ring.ytjojo.PinyinList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.ring.ytjojo.util.StringUtils;

// Downloads By http://www.veryhuo.com
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RemoteViews.ActionException;

public class PinyinListWrapper {

	int onTouchBackColor = Color.parseColor("#40000000");
	int textColor = Color.parseColor("#3399ff");
	int centerPaintColor = Color.parseColor("#d0606060");
	int textSize = 12;
	int paddingLeftAndRight = 8;
	int paddingTopAndBottom = 5;
	int roundPixs = 3;
	private boolean isShowCenter;
	boolean isSlidFill;
	Rect touchRect = new Rect();
	RectF touchRectF = new RectF();
	RectF centerRect = new RectF();
	int centerPaintTextSize = 30;
	int centerSize = 68;
	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// ↑☆
	public static String[] b = { "#", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };
	int choosedPostion = -1;
	Paint backPaint = new Paint();
	TextPaint centerTextPaint = new TextPaint();
	TextPaint textPaint = new TextPaint();
	int centerTextHeight;
	int textWidth;
	int textHeight;

	public PinyinListWrapper(AdapterView listView) {
		mListView = listView;
		init();
	}

	private void init() {
		isSlidFill = true;

		backPaint.setColor(onTouchBackColor);
		backPaint.setStyle(Paint.Style.FILL);
		backPaint.setAntiAlias(true);

		textPaint.setAntiAlias(true);
		DisplayMetrics dm = mListView.getResources().getDisplayMetrics();
		paddingLeftAndRight = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, paddingLeftAndRight, dm));
		paddingTopAndBottom = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, paddingTopAndBottom, dm));
		roundPixs = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, roundPixs, dm));

		textSize = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, textSize, dm));
		textPaint.setTextSize(textSize);
		textWidth = (int) textPaint.measureText("R");

		centerTextPaint.setAntiAlias(true);
		centerSize = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, centerSize, dm));
		centerPaintTextSize = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, centerPaintTextSize, dm));
		centerTextPaint.setTextSize(centerPaintTextSize);
		centerTextPaint.setColor(Color.WHITE);
		FontMetrics fm = centerTextPaint.getFontMetrics();
		centerTextHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
	}

	public void getVilidRect() {
		if (isSlidFill) {

		}
	}

	@SuppressWarnings("rawtypes")
	AdapterView mListView;

	/**
	 * ��д�������
	 */
	protected void draw(Canvas canvas) {
		canvas.save();
		int parentWidth = mListView.getWidth();
		int parentHeight = mListView.getHeight();
		int dx = (parentWidth - centerSize) / 2;
		int dy = (parentHeight - centerSize) / 2;
		centerRect.set(dx, dy, dx + centerSize, dy + centerSize);
		int left = parentWidth - 2 * paddingLeftAndRight - textWidth;
		int top = mListView.getPaddingTop() + paddingTopAndBottom;
		int right = parentWidth;
		int bottom = parentHeight - mListView.getPaddingBottom();
		touchRect.set(left, top, right, bottom);
		touchRectF.set(touchRect);
		if (choosedPostion >= 0) {
			backPaint.setColor(onTouchBackColor);
			canvas.drawRoundRect(touchRectF, roundPixs, roundPixs, backPaint);

		}
		if (isShowCenter) {
			backPaint.setColor(centerPaintColor);
			canvas.drawRoundRect(centerRect, roundPixs * 3, roundPixs * 3,
					backPaint);
			int centerTextX = (int) (parentWidth - centerTextPaint
					.measureText(b[choosedPostion])) / 2;
			int centerTextY = (parentHeight - centerTextHeight) / 2;
			canvas.drawText(b[choosedPostion], centerTextX, centerTextY,
					centerTextPaint);
		}
		int height = bottom - top - paddingTopAndBottom * 2;

		int singleHeight = height / b.length;
		int offsetY = 3;
		for (int i = 0; i < b.length; i++) {

			if (i == choosedPostion) {

			}

			float xPos = paddingLeftAndRight;

			float yPos = top + singleHeight * i + singleHeight + offsetY;
			canvas.drawText(b[i], xPos, yPos, textPaint);
		}
		canvas.restore();
	}

	int mActivePointerId;

	public boolean dispatchTouchEvent(MotionEvent event) {

		
		MotionEventCompat.getActionMasked(event);
		final int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		if(mActivePointerId >=0){
			final int index = MotionEventCompat.findPointerIndex(event,
					mActivePointerId);
			x = MotionEventCompat.getX(event, index);
			y = MotionEventCompat.getY(event, index);
		}

		final int oldChoose = choosedPostion;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int position = (int) (y / touchRect.height() * b.length);
		switch (action) {
		case MotionEvent.ACTION_DOWN:

			if (touchRectF.contains(x, y)) {
				mActivePointerId = MotionEventCompat.getPointerId(event, 0);

				if (oldChoose != position && listener != null) {
					if (position >= 0 && position < b.length) {

						listener.onTouchingLetterChanged(b[position]);
						choosedPostion = position;

						scrollListView(choosedPostion);
					}
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (mActivePointerId < 0) {
				return true;
			}
			
//			if (!touchRectF.contains(x, y)) {
//				choosedPostion = -1;
//				mActivePointerId = -1;
//				mListView.invalidate();
//				return true;
//
//			}
			if (oldChoose != position && listener != null) {
				if (position >= 0 && position < b.length) {
					listener.onTouchingLetterChanged(b[position]);
					choosedPostion = position;
					scrollListView(choosedPostion);
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			isShowCenter = false;
			choosedPostion = -1;
			mActivePointerId = -1;
			mListView.invalidate();
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			final int eventIndex = MotionEventCompat.getActionIndex(event);
			final int curId = MotionEventCompat.getPointerId(event, 0);
			if (curId == mActivePointerId) {
				isShowCenter = false;
				choosedPostion = -1;
				mActivePointerId = -1;
				mListView.invalidate();

			}
			break;
		}
		return true;
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

	private HashMap<String, Integer> sectionMap = new HashMap<String, Integer>();

	/**
	 * 获取排序后的新数据
	 * 
	 * @param persons
	 * @return
	 */

	public List<PinyinBean> sort(List<PinyinBean> srcList) {
		if (srcList == null || srcList.size() == 0) {
			return srcList;
		}

		String compareField = "";
		String headChar;
		for (PinyinBean bean : srcList) {
			compareField = bean.getCompareableFeild();
			if (!TextUtils.isEmpty(compareField)) {
				headChar = StringUtils.getPinYinHeadChar(compareField)
						.substring(0, 1);
				char ch = headChar.charAt(0);
				headChar = String.valueOf(ch);
				if (Character.isLetter(ch)) {
					bean.setPinyin(StringUtils.getPingYin(compareField));
				} else {
					bean.setPinyin("#" + compareField);
				}
			} else {
				bean.setPinyin("#");
			}

		}
		Collections.sort(srcList);

		ArrayList<PinyinBean> newList = new ArrayList<PinyinBean>();
		PinyinBean bean = new PinyinBean();

		String firstLetter = "";
		for (int i = 0; i < srcList.size(); i++) {

			String pinyin = srcList.get(i).getPinyin();
			String lastChar = "";
			String curChar = "";
			curChar = srcList.get(i).getCompareableFeild().substring(0, 1);
			if (i == 0) {
				bean = new PinyinBean();
				bean.setSection(true);
				bean.setPinyin(curChar);
				sectionMap.put(curChar, i);
				newList.add(bean);
				newList.add(srcList.get(i));

			} else {
				lastChar = srcList.get(i - 1).getCompareableFeild()
						.substring(0, 1);

				if (lastChar.equals(curChar)) {
					newList.add(srcList.get(i));
				} else {
					sectionMap.put(curChar, i);
					bean = new PinyinBean();
					bean.setSection(true);
					bean.setPinyin(curChar);
					newList.add(bean);
					newList.add(srcList.get(i));
				}
			}
		}
		return newList;
	}

	public void scrollListView(int index) {
		final String letter = b[index];
		if (sectionMap.containsKey(letter)) {
			isShowCenter = true;
			int pos = sectionMap.get(letter);
			if (((ListView) mListView).getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。

				((ListView) mListView).setSelectionFromTop(pos
						+ ((ListView) mListView).getHeaderViewsCount(), 0);
			} else {
				((ListView) mListView).setSelectionFromTop(pos, 0);// 滑动到第一项
			}
		}
	}
	public void deleteItem(int postion,List<PinyinBean> list){
		if(postion ==1){
			list.remove(0);
			list.remove(0);
			return;
		}
		String s1 =  list.get(postion - 2).getPinyin().substring(0, 1);
		String s2 =  list.get(postion - 1).getPinyin().substring(0,1);
		if(s1.equals(s2)){
			list.remove(postion);
		}else{
			list.remove(postion -1);
			list.remove(postion -1);
		}
			
		
	}
	public boolean isPressed(){
		return choosedPostion >= 0;
	}
}
