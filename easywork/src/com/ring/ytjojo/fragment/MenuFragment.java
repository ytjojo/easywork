package com.ring.ytjojo.fragment;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import com.example.randomringapp.R;
import com.ring.ytjojo.UI.MainActivity;
import com.ring.ytjojo.adapter.MenuAdapter;
import com.ring.ytjojo.model.MenuItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

@EFragment(R.layout.layout_menu)
public class MenuFragment extends Fragment {

	@ViewById(R.id.menu_listview)
	ListView mListView;
	

	
	@AfterViews
	void init(){
		final ArrayList<MenuItem> list = new ArrayList<MenuItem>();
		MenuItem item = null;
		item =  new MenuItem();
		item.imgResource = R.drawable.img_1;
		Fragment f = RingListFragment_.builder().type(0).build();
		item.title = "计时" ;
		list.add(item);
		item =  new MenuItem();
		item.fragment = RingListFragment_.builder().type(1).build();
		item.imgResource = R.drawable.img_2;
		item.title = "图片" ;
		list.add(item);
		item =  new MenuItem();
		item.imgResource = R.drawable.img_3;
		item.fragment = WallpaperFragment_.builder().build();
		item.title = "分享" ;
		list.add(item);
		item =  new MenuItem();
		item.imgResource = R.drawable.img_4;
		item.fragment = WallpaperFragment_.builder().build();
		item.title = "聊天" ;
		list.add(item);
		item =  new MenuItem();
		item.imgResource = R.drawable.img_5;
		item.fragment = WallpaperFragment_.builder().build();
		item.title = "反馈" ;
		list.add(item);
		item =  new MenuItem();
		item.imgResource = R.drawable.img_1;
		item.fragment = WallpaperFragment_.builder().build();
		item.title = "设置" ;
		list.add(item);
		
		mListView.setAdapter(new MenuAdapter(getActivity(), list));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switchFragment(list.get(position).fragment);
			}
		});
	}
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		} 
	}
}
