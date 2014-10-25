package com.ring.ytjojo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import com.example.randomringapp.R;
import com.ring.ytjojo.database.DBManager;
import com.ring.ytjojo.model.Song;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SelectAdapter extends BaseAdapter {
	private Context context;

	private ArrayList<Song> mDataList = null;
	private ViewHolder viewHolder = null;
	private int checkIndex = -1;
	private Song song = null;
	private int mListType = 0;
	private DBManager mDb = null;

	public SelectAdapter(Context con, ArrayList list, int type) {
		this.context = con;
		this.mDataList = list;
		this.mListType = type;
		init();
	}

	public void setList(ArrayList<Song> list) {
		this.mDataList = list;
		notifyDataSetChanged();
	}

	public void init() {
		mDb = DBManager.getInstance();

	}

	@Override
	public int getCount() {

		return this.mDataList.size();
	}

	@Override
	public Object getItem(int position) {

		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_add, null);
			viewHolder = new ViewHolder();
			viewHolder.artist = (TextView) convertView
					.findViewById(R.id.tv_author);
			viewHolder.duration = (TextView) convertView
					.findViewById(R.id.tv_duration);
			viewHolder.filename = (TextView) convertView
					.findViewById(R.id.tv_filename);
			viewHolder.checkImg = (ImageView) convertView
					.findViewById(R.id.img_choise);

			Log.i("checkbox", position + "create");

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// -=----

		song = (Song) mDataList.get(position);

		if (song.isChosen() == 0) {
			viewHolder.checkImg
					.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
		} else {
			viewHolder.checkImg
					.setImageResource(R.drawable.btn_check_off_disabled_focused_holo_dark);

		}
		viewHolder.checkImg.setOnClickListener(new OnClickListener() {
			final int curPositon = position;

			public void onClick(View v) {
				// check_state.get(position) == true? false:true;
				Song song = mDataList.get(curPositon);
				boolean isChoose = song.isChosen() == 0 ? true : false;

				switchCheckState((ImageView) v, isChoose, song);
				notifyDataSetChanged();
			}
		});
		Log.i("checkbox", position + "reUsed");
		viewHolder.duration.setText(toTime(song.getDuration()));
		viewHolder.filename.setText(song.getTitle());
		if (TextUtils.isEmpty(song.getArtist())||song.getArtist().equals("<unknown>")) {
			viewHolder.artist.setText("未知歌手");
		} else {
			viewHolder.artist.setText(song.getArtist());

		}
		return convertView;

	}

	public static void switchCheckState(ImageView img, boolean isChoosed,
			Song song) {
		if (isChoosed) {
			img.setImageResource(R.drawable.btn_check_off_disabled_focused_holo_dark);
			song.setChosen(-1);
		} else {
			img.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
			song.setChosen(0);

		}
	}

	public ArrayList<Song> getList() {
		return mDataList;
	}

	/* ʱ���ʽת�� */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	/* �ַ�ü� */
	public static String bSubstring(String s, int length) throws Exception {

		byte[] bytes = s.getBytes("Unicode");
		int n = 0; // ��ʾ��ǰ���ֽ���
		int i = 2; // Ҫ��ȡ���ֽ���ӵ�3���ֽڿ�ʼ
		for (; i < bytes.length && n < length; i++) {
			// ����λ�ã���3��5��7�ȣ�ΪUCS2�����������ֽڵĵڶ����ֽ�
			if (i % 2 == 1) {
				n++; // ��UCS2�ڶ����ֽ�ʱn��1
			} else {
				// ��UCS2����ĵ�һ���ֽڲ�����0ʱ����UCS2�ַ�Ϊ���֣�һ�������������ֽ�
				if (bytes[i] != 0) {
					n++;
				}
			}
		}
		// ���iΪ����ʱ�������ż��
		if (i % 2 == 1)

		{
			// ��UCS2�ַ��Ǻ���ʱ��ȥ�������һ��ĺ���
			if (bytes[i - 1] != 0)
				i = i - 1;
			// ��UCS2�ַ�����ĸ�����֣��������ַ�
			else
				i = i + 1;
		}

		return new String(bytes, 0, i, "Unicode");
	}

	public final class ViewHolder {
		// public ImageView img;
		public TextView filename;
		public TextView duration;
		public TextView artist;
		public ImageView checkImg;
	}

	public void insert(Song song) {
		mDb.open();
		mDb.insert(song, mListType);
		mDb.close();
	}

	public void delete(int id) {
		mDb.open();
		mDb.deleteData(id, mListType);
		mDb.close();
	}
}
