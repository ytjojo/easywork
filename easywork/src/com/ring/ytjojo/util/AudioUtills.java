package com.ring.ytjojo.util;

import java.util.ArrayList;
import java.util.List;

import com.ring.ytjojo.model.Song;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.sax.StartElementListener;
import android.util.Log;

public class AudioUtills {

	private AudioUtills() {
	}

	public static Cursor getMediaCursor(Uri url, Context context) {
		Cursor cursor = context.getContentResolver().query(
				url,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.DATE_ADDED }, "_size>?",
				new String[] { 1024 + "" }, null);
		return cursor;
	}

	public static ArrayList getSongList(Cursor cursor) {
		ArrayList<Song> songs = new ArrayList();
		if (!cursor.moveToFirst()) {
			cursor.close();
			return songs;
		}

		int id = 0;
		String title = null;
		String artist = null;
		String album = null;
		String year = null;
		int duration = 0; // ʱ��
		String name = null;
		String end = null;
		String displayName = null;
		int size = 0;
		String mime_type = null;
		String url = null;
		String[] strArr = null;
		String temp = null;
		String addDate = null;
		String data;

		do {
			id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			title = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			album = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			artist = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			year = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
			duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			displayName = cursor
					.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
			size = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			mime_type = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
			addDate = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
			data = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			// ����ļ���
			url = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			strArr = url.split("/");
			temp = strArr[strArr.length - 1];

			strArr = temp.split("\\.");
			end = strArr[strArr.length - 1];
			name = temp.replace(end, "");
			if (artist.equals("<unknown>")) {
				if (displayName.contains("-"))
					artist = displayName.split("-")[0];
			}
			Song song = new Song();
			song.setAlbum(album);
			song.setArtist(artist);
			song.setDuration(duration);
			song.setId(id);
			song.setName(name);
			song.setTitle(title);
			song.setYear(year);
			song.setMime_type(mime_type);
			song.setSize(size);
			song.setDisplayName(displayName);
			song.setUrl(url);
			song.setAddedDate(addDate);
			songs.add(song);
		} while (cursor.moveToNext());

		cursor.close();
		return songs;
	}

	public static ArrayList<Song> readDateExternal(Context context) {
		return getSongList(getMediaCursor(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, context));
	}

	public static ArrayList<Song> readDataInternal(Context context) {
		return getSongList(getMediaCursor(
				MediaStore.Audio.Media.INTERNAL_CONTENT_URI, context));
	}

	public static ArrayList<Song> getAllAudio(Context context) {

		ArrayList<Song> temp = readDataInternal(context);
		temp.addAll(readDateExternal(context));
		return temp;
	}

	
}
