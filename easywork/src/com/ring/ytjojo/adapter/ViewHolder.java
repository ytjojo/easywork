package com.ring.ytjojo.adapter;

import android.util.SparseArray;
import android.view.View;

public  class ViewHolder {
		// I added a generic return type to reduce the casting noise in client
		// code
		@SuppressWarnings("unchecked")
		public static <T extends View> T getView(View convertView, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) convertView
					.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				convertView.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = convertView.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}