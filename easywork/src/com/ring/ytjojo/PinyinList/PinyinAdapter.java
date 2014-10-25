package com.ring.ytjojo.PinyinList;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomringapp.R;
import com.ring.ytjojo.adapter.TypeBaseAdapteer;

public class PinyinAdapter extends TypeBaseAdapteer<PinyinBean> implements PinnedHeaderAdapter{


	public PinyinAdapter(Context c, List<PinyinBean> list) {
		super(c, list);
		ArrayList<Integer> reList = new ArrayList<Integer>();
		reList.add(R.layout.pinyin_header);
		reList.add(R.layout.item_pinyin);
		setLayoutSources(reList);
	}

	private static final int TYPE_CATEGORY_ITEM = 0;
	private static final int TYPE_ITEM = 1;


	@Override
	public View bindViewData(int type, int position, View convertView,
			ViewGroup parent) {

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (null == mData || position < 0 || position > getCount()) {
			return TYPE_ITEM;
		}

		PinyinBean item = (PinyinBean) mData.get(position);
        if (item.isSection) {  
            return TYPE_CATEGORY_ITEM;  
        }  
          
        return TYPE_ITEM;  
	}

	@Override
	public boolean isEnabled(int position) {
		if (null == mData || position < 0 || position > getCount()) {
			return true;
		}

		PinyinBean item = (PinyinBean) mData.get(position);
		if (item.isSection) {
			return false;
		}

		return true;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	 @Override  
	    public int getPinnedHeaderState(int position) {  
	        if (position < 0) {  
	            return PINNED_HEADER_GONE;  
	        }  
	          
	        PinyinBean item = (PinyinBean) getItem(position);  
	        PinyinBean itemNext = (PinyinBean) getItem(position + 1);  
	        boolean isSection = item.isSection;  
	        boolean isNextSection = (null != itemNext) ? itemNext.isSection : false;  
	        if (!isSection && isNextSection) {  
	            return PINNED_HEADER_PUSHED_UP;  
	        }  
	          
	        return PINNED_HEADER_VISIBLE;  
	    }  
	  
	    @Override  
	    public void configurePinnedHeader(View header, int position, int alpha) {  
	    	
	    }

		@Override
		public View getHeadView() {
			// (mLayoutSources == null||mLayoutSources.size() == 0)? R.layout.pinyin_header: mLayoutSources.get(0);
			return View.inflate(context,mLayoutSources.get(0) ,null );
		}  
	
}
