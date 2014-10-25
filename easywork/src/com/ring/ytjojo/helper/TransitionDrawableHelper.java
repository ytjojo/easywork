package com.ring.ytjojo.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

public class TransitionDrawableHelper {
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	float ratio;
	int duration;
	int size;
	TransitionDrawable transitionDrawable;
	// boolean isReverse;
	Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			int index = scheduleIndex;
			int nextIndex = index + 1;
			if(transitionDrawable == null){
				transitionDrawable = new TransitionDrawable(new Drawable[] {
						drawables.get(0), drawables.get( 1) });
				transitionDrawable.setId(0,0);
				transitionDrawable.setId(1,1);
				
				transitionDrawable.setCrossFadeEnabled(true);
			}else{
//				transitionDrawable.findDrawableByLayerId(transitionDrawable.getId(1));
				transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(0), drawables.get(index));
				transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(1), drawables.get(nextIndex));
				
			}
			
			target.setImageDrawable(transitionDrawable);
			transitionDrawable.startTransition(duration / (size - 1));
			if (nextIndex == size - 1) {
				if (task != null) {
					task.cancel(true);
					task = null;
					transitionDrawable = null;
				}
			}

		}
	};

	ImageView target;
	ScheduledExecutorService scheduledExecutorServic;

	public TransitionDrawableHelper(ImageView view, List<Drawable> list) {
		this.drawables = new ArrayList<Drawable>(list);
		this.target = view;
		this.size = drawables.size();
		this.scheduledExecutorServic = Executors.newScheduledThreadPool(1);
	}

	float lastOffse = -2f;

	public void doTransition(float offset) {// 0-1从第一个开始，1 - 0,从最后一个开始
		offset = Math.abs(offset);
		int curCount = (int) ((offset * (size - 1)) * 10000);
		if (lastOffse < -1f) {
			if (curCount == 0) {
				transitionDrawable = new TransitionDrawable(new Drawable[] {
						drawables.get(0), drawables.get(1) });
			} else if (curCount == (size - 1) * 10000) {
				transitionDrawable = new TransitionDrawable(new Drawable[] {
						drawables.get(size - 1), drawables.get(size - 2) });
				transitionDrawable.setCrossFadeEnabled(true);
			}
			transitionDrawable.setId(0,0);
			transitionDrawable.setId(1,1);
			target.setImageDrawable(transitionDrawable);
			lastOffse = offset;
		}

		int level = curCount % 10000;
		checkChange(offset, curCount);

		transitionDrawable.setLevel(level);
		checkFinish(curCount);
	}

	private void checkFinish(int curCount) {
		if (curCount == 0 || curCount == (size - 1) * 10000) {
			lastOffse = -2f;
			transitionDrawable = null;
		}

	}

	private void checkChange(float offset, int curCount) {
		int lastcount = (int) ((lastOffse * (size - 1)) * 10000);
		int lastIndex = lastcount / 10000;
		int curIndex = curCount / 10000;
		if (lastIndex != curIndex) {
			transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(0), drawables.get(lastIndex));
			transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(1), drawables.get(curIndex));
			target.setImageDrawable(transitionDrawable);
		}
	}

	ScheduledFuture<?> task;

	public void apllyTransition(int duration) {
		this.duration = duration;
		scheduleIndex = 0;
		task = scheduledExecutorServic.scheduleAtFixedRate(runnable, 0,
				duration / (size - 1), TimeUnit.MILLISECONDS);
	}

	int scheduleIndex;
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			handler.sendEmptyMessage(0);

		}
	};
}
