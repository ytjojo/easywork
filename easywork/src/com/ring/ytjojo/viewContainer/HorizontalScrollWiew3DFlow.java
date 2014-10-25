package com.ring.ytjojo.viewContainer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class HorizontalScrollWiew3DFlow extends HorizontalScrollView{

	private Camera mCamera = new Camera();
	private int mMaxRotationAngle = 60;
	private int mMaxZoom = -300;
	private int mCoverflowCenter = 0;
	public HorizontalScrollWiew3DFlow(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);
	}
	public HorizontalScrollWiew3DFlow(Context context,AttributeSet att)
	{
		super(context, att);
		this.setStaticTransformationsEnabled(true);
	}
	
	public int getMaxRotationAngle()
	{
		return mMaxRotationAngle;
	}
	public void setMaxRotationAngle(int maxRotationAngle)
	{
		this.mMaxRotationAngle = maxRotationAngle;
	}
	public void setMaxZoon(int maxZoom)
	{
		this.mMaxZoom = mMaxZoom;
	}
	public int getMaxZoom()
	{
		return mMaxZoom;
	}
	private int getCenterofCoverflow()
	{
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}
	private int getCenterofView(View view)
	{
		return view.getLeft() + getWidth() / 2;
				
	}
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		final int childCenter = getCenterofView(child);
		final int childWidht = child.getWidth();
		int rotationAngle = 0;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		if(childCenter == mCoverflowCenter)
		{
			transformImageBitmap((ImageView)child, t, rotationAngle);
		
		}
		else
		{
			rotationAngle = (int)(((float)(mCoverflowCenter - childCenter)/ childWidht)* mMaxRotationAngle);
			if(Math.abs(rotationAngle) > mMaxRotationAngle)
			{
				rotationAngle = (rotationAngle < 0) ? - mMaxRotationAngle : mMaxRotationAngle;
				
			}
			transformImageBitmap((ImageView)child, t, rotationAngle);
		}
		return true;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoverflowCenter = getCenterofCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle)
	{
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);
		mCamera.translate(0.0f, 0.0f, 100.0f);
		if(rotation < mMaxRotationAngle)
		{
			float zoomAmount = (float)(mMaxZoom + (rotation * 1.5));
			mCamera.rotate(0.0f, 0.0f, zoomAmount);
		}
		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth/2), -(imageWidth / 2));
		imageMatrix.postTranslate(imageWidth / 2, imageHeight / 2);
		mCamera.restore();
	}
}
