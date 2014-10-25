package com.ring.ytjojo.app;

import java.util.LinkedList;

import android.app.Activity;
import android.app.Service;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

public class UIHandler extends Handler {
	private Handler mTargetHandlar;
	private LinkedList<UIHandlerCallback> callbackList = new LinkedList<UIHandler.UIHandlerCallback>();

	public UIHandler() {
		super();
	}

	@Override
	public void dispatchMessage(Message msg) {
		super.dispatchMessage(msg);
		if (mTargetHandlar != null) {
//			Message newMsg = Message.obtain(msg);
			mTargetHandlar.sendMessage(msg);
		}
		if (callbackList.size() == 0) {
			return;
		}
		for (UIHandlerCallback c : callbackList) {
			if(c == null){
				continue;
			}
			if (c instanceof Activity) {
				Activity activity = (Activity) c;
				if (activity.isFinishing()) {
					continue;
				}
			} else if (c instanceof Fragment) {
				Fragment fragment = (Fragment) c;
				if (fragment.isRemoving()) {
					continue;
				}
			} else if (c instanceof Service) {
				Service service = (Service)c;
				if(service.isRestricted()){
					continue;
				}
			}
			c.handleMessage(msg);
		}
	}

	public void setTargetHandlar(Handler handler) {
		mTargetHandlar = handler;
	}

	public interface UIHandlerCallback {
		void handleMessage(Message msg);
	}

	public void setUIHandlerCallback(UIHandlerCallback callback) {
		if (callbackList.contains(callback)) {
			return;
		}
		callbackList.addLast(callback);
	}

	public void removeUIHandlerCallback(UIHandlerCallback callback) {
		callbackList.remove(callback);

	}

	public void clearUIHandlerCallback() {
		callbackList.clear();
	}

}
