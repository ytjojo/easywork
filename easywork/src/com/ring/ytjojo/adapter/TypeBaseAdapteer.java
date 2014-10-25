package com.ring.ytjojo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class TypeBaseAdapteer<T> extends AppBaseAdapter {
	protected List<Integer> mLayoutSources;
	public TypeBaseAdapteer(Context c) {
		super(c);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public TypeBaseAdapteer(Context c,List<T> list){
		super(c, list);
	}
	@SuppressWarnings("unchecked")
	public TypeBaseAdapteer(Context c,List<T> list,List<Integer> resList){
		super(c, resList);
		this.mLayoutSources = resList;
	}
	public void setLayoutSources(List<Integer> list){
		this.mLayoutSources = list;
	}
	public void setmLayoutSources(List<Integer> mLayoutSources) {
		this.mLayoutSources = mLayoutSources;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int typeCount = getViewTypeCount();
		int type = getItemViewType(position);
		
		if(convertView == null){
			if(mLayoutSources != null && mLayoutSources.size()> 0){
				convertView = LayoutInflater.from(context).inflate(mLayoutSources.get(type),null);
			}else{
				convertView = LayoutInflater.from(context).inflate(getItemLayoutSourceByType(type),
						null);
				
			}
		}
		return bindViewData(type,position, convertView, parent);
	}
	
	public int getItemLayoutSourceByType(int type){
		return -1;
	}
	public abstract View bindViewData(int type,int position, View convertView, ViewGroup parent);
	@Override
	public View bindViewData(int position, View convertView, ViewGroup parent) {
		
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return super.getItemViewType(position);
	}
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return super.getViewTypeCount();
	}
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return super.isEnabled(position);
	}

}
