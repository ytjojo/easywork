package com.ring.ytjojo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ring.ytjojo.model.Song;

public class ListUtil {

	// ��ϵͳ�и����ǳ�ѡ�е�

	public static List<Song> deepCopy(List<Song> from, List<Song> to) {
		if (to == null) {
			to = new ArrayList<Song>();
		}
		if (from == null || from.size() == 0) {
			return to;
		}
		for(Song song: to){
			try {
				from.add((Song) song.clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return to;

	}



	public static void markChoosenList(List<Song> target ,List<Song> source){
		if(source == null || source.size() == 0){
			return ;
		}
		Map<String, String> map = new HashMap<String, String>();
		for (Song song :source) {
			map.put(song.getUrl(), song.getUrl());
			
		}
		for (Song song: target) {
			if(map.containsKey(song.getUrl())){
				song.setChosen(0);
			}
		}
	}

	// ��ϵͳ��ѡ�еĸ��������
	public static ArrayList getChosenList(List<Song> mediaList) {
		ArrayList<Song> temp = new ArrayList<Song>();
		for (Song song : mediaList) {
			if (song.isChosen() == 0) {
				temp.add(song);
			}
		}
		return temp;
	}

	public void arrayCopy(Object[] src, int srcPos, Object[] dst, int dstPos,
			int length) {
		System.arraycopy(src, srcPos, dst, dstPos, length);
	}

	public void listCopy(Object[] src, int srcPos, Object[] dst, int dstPos,
			int length) {
		System.arraycopy(src, srcPos, dst, dstPos, length);
	}
}
