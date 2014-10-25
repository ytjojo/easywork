package com.ring.ytjojo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.randomringapp.R;
import com.ring.api.SoundSetting;
import com.ring.ytjojo.app.ICONST;
import com.ring.ytjojo.database.DBManager;
import com.ring.ytjojo.model.Song;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RingAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<Song> mDataList = null;
	private ViewHolder viewHolder = null;
	private int checkIndex = -1;
	private Song song = null;
	private int mListType = 0;
	private Uri tempuri = null;
	private View settedView = null;

	public RingAdapter(Context con, ArrayList list, int type) {
		this.context = con;
		this.mDataList = list;
		this.mListType = type;
		init();
	}

	DBManager dbManager = null;

	public void init() {
		dbManager = DBManager.getInstance();
		getDefaultUri();
	}

	private void dbdeleteSong(Song song) {
		dbManager.open();
		dbManager.deleteData(song.getId(), mListType);
		dbManager.close();
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
					R.layout.item_list_delete, null);

			viewHolder.artist = (TextView) convertView
					.findViewById(R.id.tv_author);
			viewHolder.duration = (TextView) convertView
					.findViewById(R.id.tv_duration);
			viewHolder.filename = (TextView) convertView
					.findViewById(R.id.tv_filename);
			viewHolder.imgbtnDel = (ImageView) convertView
					.findViewById(R.id.btn_del);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.imgbtnDel.setOnClickListener(new View.OnClickListener() {
			final int curPostion =position ;
			@Override
			public void onClick(View v) {
				song = (Song) mDataList.get(position);
				buildDailog(curPostion);
			}

		});
		song = (Song) mDataList.get(position);
		viewHolder.artist.setText(song.getArtist());
		viewHolder.duration.setText(toTime(song.getDuration()));
		viewHolder.filename.setText(song.getTitle());

		if (song.getArtist().equals("<unknown>")) {
			viewHolder.artist.setText("未知歌手");
		} else {
			viewHolder.artist.setText(song.getArtist());
		}

		if (song.getUrl().contains("sdcard")) {
			if ((ICONST.CONTENT_EX + song.getId()).equals(uriStr)) {

				convertView.setBackgroundColor(Color.parseColor("#ffc5F0"));
			} else {
				convertView.setBackgroundColor(Color.alpha(0));
			}
		} else {
			if ((ICONST.CONTENT_IN + song.getId()).equals(uriStr)) {

				convertView.setBackgroundColor(Color.parseColor("#ffc5F0"));
			} else {
				convertView.setBackgroundColor(Color.alpha(0));
			}
		}
		settedView = convertView;
		convertView.setTag(viewHolder);
		return convertView;

	}



	public ArrayList<Song> getList() {
		return mDataList;
	}

	private void buildDailog(final int position) {
		new AlertDialog.Builder(context).setTitle("�Ƿ�ɾ��  :")
				.setMessage("�Ƿ�ɾ�� " + song.getFileName())
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						song.setChosen(-1);
						dbdeleteSong(song);
						mDataList.remove(position);
						notifyDataSetChanged();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

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
		public ImageView imgbtnDel;
	}

	String uriStr = "";

	public String getDefaultUri() {
		uriStr = SoundSetting.getRingstoneUri(mListType, context).toString();
		return uriStr;
	}

	public void setDefaultUri(String uri) {
		this.uriStr = uri;
	}

	public void setList(ArrayList<Song> mDataList) {
		this.mDataList = mDataList;
		notifyDataSetChanged();

	}
}
