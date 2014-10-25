package com.ring.ytjojo.fragment;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;
import zrc.widget.ZrcListView.OnItemClickListener;
import zrc.widget.ZrcListView.OnStartListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.example.randomringapp.R;
import com.ring.ytjojo.adapter.SelectAdapter;
import com.ring.ytjojo.app.AppContext;
import com.ring.ytjojo.app.ICONST;
import com.ring.ytjojo.model.Song;
import com.ring.ytjojo.service.PlayService;
import com.ring.ytjojo.utill.AudioUtills;
import com.ring.ytjojo.utill.ListUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

@EFragment(R.layout.selectactivity)
public class SelectRingFragment extends BaseFragment {
	public static SelectRingFragment newInstance(int type, int pathType) {
		SelectRingFragment f = new SelectRingFragment();
		mFromType = type;
		mAudioPathType = pathType;
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("type", type);
		args.putInt("path", pathType);
		f.setArguments(args);

		return f;
	}

	@App
	AppContext app;

	@ViewById(R.id.zListView)
	ZrcListView mListView;

	private Bundle mSavedInstanceState;
	private ArrayList<Song> translateDate;
	private ArrayList<Song> mMediaList = null;
	private SelectAdapter mselectAdapter = null;
	private final int MSG_LISTCREATE_OVER = 100;
	private final int MSG_REFRESHFINISHED = 200;
	private final int MSG_LISTADD_OVER = 300;

	@FragmentArg
	static int mFromType = 0;
	@FragmentArg
	static int mAudioPathType = 0;
	private boolean isPlaying = false;
	// -------------------handler

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_LISTCREATE_OVER:
				break;
			case MSG_REFRESHFINISHED:
				mMediaList = translateDate;
				mselectAdapter.setList(mMediaList);
				if (mAudioPathType == 0) {
					app.updateExAudio(translateDate);
				} else {

					app.updateInAudio(translateDate);
				}
				mListView.setRefreshSuccess("加载成功"); // 通知加载成功

				break;
			case AppContext.MSG_INIALLDATA:
				if (mselectAdapter != null) {
					initData();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void initListView() {
		if (mMediaList == null) {
			mMediaList = new ArrayList<Song>();
		}
		SimpleHeader header = new SimpleHeader(getActivity());
		header.setTextColor(0xff0066aa);
		header.setCircleColor(0xff33bbee);
		mListView.setHeadable(header);

		// 下拉刷新事件回调（可选）
		mListView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				onRefreshData();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(ZrcListView parent, View view,
					int position, long id) {
				int index = position - mListView.getHeaderViewsCount();
				Song song = mMediaList.get(index);
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
		});
		mselectAdapter = new SelectAdapter(getActivity(), mMediaList, mFromType);
		mListView.setAdapter(mselectAdapter);

	}

	// ---------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mFromType = savedInstanceState.getInt("type");
			mAudioPathType = savedInstanceState.getInt("path");
		}

	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getActivity().getActionBar().setTitle("选择铃声");
		setHasOptionsMenu(true);
		initListView();
		initDataOnCreate();
	}

	@AfterViews
	void initView() {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	private void initDataOnCreate() {

		if (AppContext.INITDATA_ISFINISHED) {
			initData();

		} else {
			AppContext.getUiHandler().setTargetHandlar(myHandler);
		}
		Log.i("SelectRingFragment", "crate  --------------" + mMediaList);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menu.add("保存").setIcon(R.drawable.ic_compose)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			saveChoise();
			getActivity().finish();
			break;
		}
		return true;
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		saveChoise();
	}
	private void initData() {

		ArrayList<Song> choosenList = new ArrayList<Song>();
		switch (mAudioPathType) {
		case 0:
			mMediaList = app.getAudioEx();
			break;
		case 1:
			mMediaList = app.getAudioIn();

			break;
		}
		if (mFromType == 0) {
			choosenList = app.getPhoneRingSongs();

		} else {
			choosenList = app.getSMSRingSongs();
		}

		ListUtil.markChoosenList(mMediaList, choosenList);
		mselectAdapter.setList(mMediaList);
	}

	public void onRefreshData() {
		new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				if (mAudioPathType == 0) {
					translateDate = AudioUtills.readDateExternal(app);

				} else {
					translateDate = AudioUtills.readDataInternal(app);

				}
				myHandler.sendEmptyMessage(MSG_REFRESHFINISHED);

			}

		}).start();
	}

	@SuppressWarnings("unchecked")
	@Background
	void asynQueryDataBase() {
		if (mAudioPathType == 0) {

			translateDate = AudioUtills.readDateExternal(app);
		} else {

			translateDate = AudioUtills.readDataInternal(app);
		}

	}

	@Override
	public boolean onBackPress() {
		saveChoise();
		return super.onBackPress();
	}
	boolean isSaved = false;
	private void saveChoise() {
		if(isSaved){
			return;
		}
		ArrayList<Song> choosedlist = ListUtil.getChosenList(mMediaList);
		if (mFromType == 0) {
			app.savePhoneRingSongs(choosedlist);
		} else {
			app.saveSMSRingSongs(choosedlist);

		}
		isSaved = true;
		
	}

	private void sort(int type) {

	}

}
