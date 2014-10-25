package com.ring.ytjojo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.randomringapp.R;
import com.ring.ytjojo.model.MenuItem;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
@SuppressWarnings("rawtypes")
public class MenuAdapter extends AppBaseAdapter{
	@SuppressWarnings("unchecked")
	public MenuAdapter(Activity context,List<MenuItem> list){
		super(context, list);
	}

	@Override
	public int getItemLayoutRes() {
		// TODO Auto-generated method stub
		return R.layout.item_menu;
	}

	@Override
	public View bindViewData(int position, View convertView, ViewGroup parent) {
		ImageView icon = ViewHolder.getView(convertView, R.id.icon);
		TextView titleTv = ViewHolder.getView(convertView, R.id.title);
		MenuItem item = (MenuItem) mData.get(position);
		icon.setImageResource(item.imgResource);
		titleTv.setText(item.title);
		return convertView;
	}



}
