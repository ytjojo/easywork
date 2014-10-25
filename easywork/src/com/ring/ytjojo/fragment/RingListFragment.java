package com.ring.ytjojo.fragment;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.example.randomringapp.R;
import com.ring.api.SoundSetting;
import com.ring.ytjojo.UI.AudioSelectActivity;
import com.ring.ytjojo.adapter.RingAdapter;
import com.ring.ytjojo.app.AppContext;
import com.ring.ytjojo.app.ICONST;
import com.ring.ytjojo.model.Song;
import com.ring.ytjojo.service.PlayService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

@EFragment(R.layout.ringactivity)
public class RingListFragment extends BaseFragment {


	@ViewById(R.id.chosenListView)
	ListView mListview ;

	ArrayList<Song> mSMSRingList ;

	boolean isPlaying = false;
	String[] textItmeStrs ;

	@App
	AppContext app;

	@AfterViews
	public void init() {
		initListView();
	}


	@FragmentArg
	int type;

	@ItemClick
	void chosenListViewItemClicked(Song song) {
		isPlaying = isPlaying == true ? false : true;
		Intent intent = new Intent(getActivity(), PlayService.class);
		intent.putExtra("url", song.getUrl());
		if (isPlaying) {
			intent.putExtra("MSG", ICONST.PLAY_MSG);
		} else {
			intent.putExtra("MSG", ICONST.STOP_MSG);

		}
		getActivity().startService(intent);
	}

	@ItemLongClick
	void chosenListViewItemLongClicked(Song song) {
		String pathString = "";
		Uri songUri = null;
		
		if (song.getUrl().contains("sdcard")) {
			pathString = ICONST.CONTENT_EX + song.getId();
			 Uri.parse(pathString);
			SoundSetting.set(type,songUri,
					getActivity());
			
		} else {
			pathString = ICONST.CONTENT_IN + song.getId();
			songUri = Uri.parse(pathString);
		
			SoundSetting.set(type, songUri, getActivity());
		}

		String settedUri = SoundSetting.getDefaultUri(type, getActivity())
				.toString();
		if (pathString.equals(settedUri)) {
			Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), "设置设置失败", Toast.LENGTH_LONG).show();
		}
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (getActivity().isFinishing()) {
				AppContext.getUiHandler().setTargetHandlar(null);
				return;
			}
			switch (msg.what) {

			case AppContext.MSG_INIALLDATA:
				updateData();
				break;

			}
			
		}

	};

	public static RingListFragment newInstance(int num) {
		RingListFragment f = new RingListFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}

	private void updateData() {
		if(type == 0){
			mSMSRingList = app.getPhoneRingSongs();
		}else{
			mSMSRingList = app.getSMSRingSongs();
			
		}
		if (mSMSRingList == null) {
			mSMSRingList = new ArrayList<Song>();
		}
		if (mRingadapter != null) {
			mRingadapter.setList(mSMSRingList);
			mRingadapter.notifyDataSetChanged();
		}
	}

	private void initListView() {
		
		updateData();
		mRingadapter = new RingAdapter(getActivity(), mSMSRingList, type);
		mListview.setAdapter(mRingadapter);
		mRingadapter.notifyDataSetChanged();

		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(savedInstanceState != null){
			type = savedInstanceState.getInt("type");
		}
	}
	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getActivity().getActionBar().setTitle(type ==0 ? "电话铃声":"短信铃声");
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateData();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		 menu.add("添加")
         .setIcon( R.drawable.ic_menu_add)
         .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 0:
			creatDialog();
			break;
		}
		return true;
	}
	

	private void creatDialog() {
		textItmeStrs = new String[] { "外部存储卡", "手机内存卡" };
		new AlertDialog.Builder(getActivity())
				.setTitle("选择路径")
				.setItems(textItmeStrs, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getActivity(),
								AudioSelectActivity.class);
						switch (which) {
						case 0:
							intent.putExtra("pathtype",
									ICONST.SEND_REQUEST_EXTERNAL);
							break;

						case 1:
							intent.putExtra("pathtype",
									ICONST.SEND_REQUEST_INTENAL);
							break;
						}
						intent.putExtra("fromtype", type);
						startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	RingAdapter mRingadapter = null;

	// ����״̬
	@Override
	public void onSaveInstanceState(Bundle outState) {

		savesharepreference();

		super.onSaveInstanceState(outState);
	}

	private void savesharepreference() {

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppContext.getUiHandler().setTargetHandlar(null);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
	}

}
