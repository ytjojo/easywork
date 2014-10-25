package com.ring.ytjojo.model;

import javax.xml.transform.Source;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

public class Song implements Parcelable ,Comparable,Cloneable{
	int isChosen = -1;
	int id = 0;
	String title = null;
	String artist = null;
	String album = null;
	String year = null;
	int duration = 0; //ʱ��
	String fileName = null ;
	String displayName = null;
	int size = 0 ;
	String mime_type = null;
	String url = null;
	String addedDate;
	public String getAddedDate() {
		return addedDate;
	}
	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getName() {
		return fileName;
	}
	public void setName(String name) {
		this.fileName = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getMime_type() {
		return mime_type;
	}
	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	

	public int isChosen() {
		return isChosen;
	}
	public void setChosen(int isChosen) {
		this.isChosen = isChosen;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public int compareTo(Object o) {
		Song tmp=(Song) o;
		 int result =  tmp.fileName.compareTo(fileName);
	        if (result==0) {
	        	 result=tmp.duration>duration?1:(tmp.duration==duration?1:-1);
	        }
	        return result;
	}
	public static final Parcelable.Creator<Song> CREATOR = new Creator<Song>() {

		@Override
		public Song createFromParcel(Parcel source) {
			Log.e(TAG, "createparcel");
			Song song = new Song();
			song.album = source.readString();
			song.artist = source.readString();
			song.displayName = source.readString();
			song.fileName = source.readString();
			song.mime_type = source.readString();
			song.title = source.readString();
			song.url = source.readString();
			song.year = source.readString();
			song.id = source.readInt();
			song.duration = source.readInt();
			song.size = source.readInt();
			song.isChosen = source.readInt();
			song.addedDate = source.readString();
			return song;
		}

		@Override
		public Song[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Song[size];
		}
		
		
		
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public static final String TAG = "Parcel";
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.e(TAG, "writeparcel");
		dest.writeString(album);
		dest.writeString(artist);
		dest.writeString(displayName);
		dest.writeString(fileName);
		dest.writeString(mime_type);
		dest.writeString(title);
		dest.writeString(url);
		dest.writeString(year);
		dest.writeInt(id);
		dest.writeInt(duration);
		dest.writeInt(size);
		dest.writeInt(isChosen);
		dest.writeString(addedDate);
		
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		Song song = new Song();
		song.album = this.album;
		song.artist = this.artist;
		song.displayName = this.displayName;
		song.fileName = this.fileName;
		song.mime_type = this.mime_type;
		song.title = this.title;
		song.url = this.url;
		song.year = this.year;
		song.id = this.id;
		song.duration = this.duration;
		song.size = this.size;
		song.isChosen = this.isChosen;
		return song;
	}
	
	
}
