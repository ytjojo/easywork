package com.ring.ytjojo.database;
import java.util.ArrayList;
import java.util.List;

import com.ring.ytjojo.app.AppContext;
import com.ring.ytjojo.app.Constants;
import com.ring.ytjojo.app.ICONST;
import com.ring.ytjojo.model.Song;

import android.R.integer;
import android.app.ApplicationErrorReport.CrashInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager {

	private AppContext mContext = null;
	private static DBManager instance;
	private static final String TAG = "DBManager";
	public static final String _ID = "_id";
	public static final String TITLE = "title";
	public static final String ARTIST = "artist";
	public static final String DURATION = "duration";
	public static final String DISPLAYNAME = "displayname";
	public static final String SIZE = "size";
	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String DATA_ADDED = "data_added";

	private static final String DB_NAME = "ringtone.db";
	private static final String DB_TABLE_PHONE = "phonering_tb";
	private static final String DB_TABLE_SMS = "smsring_tb";

	private static final int DB_VERSION = 5;
	private SQLiteDatabase mDb = null;
	private DataBaseHelper mdbHelper = null;
	private static final String CREATE_TABLE_PHONE = "CREATE TABLE "
			+ DB_TABLE_PHONE + " (" + _ID + " INTEGER," + NAME + " TEXT,"
			+ SIZE + " INTEGER," + URL + " TEXT," + DISPLAYNAME + " TEXT,"
			+ DURATION + " INTEGER," + ARTIST + " TEXT," +DATA_ADDED +" TEXT," + TITLE + " TEXT)";
	
	private static final String CREATE_TABLE_SMS = "CREATE TABLE "
			+ DB_TABLE_SMS + " (" + _ID + " INTEGER," + NAME + " TEXT,"
			+ SIZE + " INTEGER," + URL + " TEXT," + DISPLAYNAME + " TEXT,"
			+ DURATION + " INTEGER," + ARTIST + " TEXT," +DATA_ADDED +" TEXT," + TITLE + " TEXT)";

	class DataBaseHelper extends SQLiteOpenHelper {
		public DataBaseHelper(Context context,String name,CursorFactory factory,int version)
		{
			super(context,name,factory,version);
		}
		public DataBaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_SMS);
			db.execSQL(CREATE_TABLE_PHONE);
			System.out.println(CREATE_TABLE_PHONE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PHONE);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SMS);
			onCreate(db);
		}

	}
	public static DBManager getInstance(){
		if(instance == null){
			synchronized (DBManager.class) {
				if(instance == null){
					instance = new DBManager();
				}
			}
			
		}
		return instance;
		
		
	}
	private DBManager() {
		this.mContext = Constants.appContext;
		mdbHelper = new DataBaseHelper(mContext);
	}

	public void open() throws SQLException {
		
		
		mDb = mdbHelper.getWritableDatabase();
//		mdbHelper.onOpen(mDb);
	}
	public SQLiteDatabase getDB(){
		if(mDb == null||!mDb.isOpen()||mDb.isReadOnly()){
			open();
			
		}
		return mDb;
	}
	public void close() {
		if(mDb !=null && mDb.isOpen()){
			
			mDb.close();
		}
	}
	//��ӵ���
	public void clearDatabase(int type){
		try {
			if(type == 0){
				
				mDb.execSQL("delete from " + DB_TABLE_PHONE);
			}else {
				
				mDb.execSQL("delete from " + DB_TABLE_SMS);
			}
			
		} catch (SQLiteException e) {
			// TODO: handle exception
		}
	}
	public long insert(Song song, int type) {
		try {
			Log.i("insert", song.getUrl());
			ContentValues cv = new ContentValues();
			cv.put(DURATION, song.getDuration());
			cv.put(DISPLAYNAME, song.getDisplayName());
			cv.put(NAME, song.getName());
			cv.put(SIZE, song.getSize());
			cv.put(TITLE, song.getTitle());
			cv.put(URL, song.getUrl());
			cv.put(_ID, song.getId());
			cv.put(ARTIST, song.getArtist());
			cv.put(DATA_ADDED, song.getAddedDate());
			switch (type) {
			case ICONST.TYPE_PHONE:
				return mDb.insert(DB_TABLE_PHONE, _ID, cv);
			case ICONST.TYPE_SMS:
				return mDb.insert(DB_TABLE_SMS, _ID, cv);
			}
		} catch (SQLiteException e) {
			// TODO: handle exception
		}
		return -1;
	}
	//�������
	public boolean insertList(List<Song> list,int type) {
		boolean isSucc = false;
		if(list == null || list.size() ==0){
			return false;
		}
		mDb.beginTransaction();
		try {
			
			cleanData(type);
			switch(type)
			{
			case 0:
				for (Song song : list) {

					mDb
					.execSQL(
							"INSERT INTO "
									+ DB_TABLE_PHONE +" (" + _ID + "," + NAME + ","
											+ SIZE + "," + URL + "," + DISPLAYNAME + ","
											+ DURATION + "," + ARTIST + "," +DATA_ADDED +"," + TITLE + ")"
									+ " VALUES( ? , ? , ? , ? , ? , ? , ? , ? ,?)",
							new Object[] { song.getId(),
									song.getName(),
									song.getSize(), song.getUrl(),
									song.getDisplayName(),
									song.getDuration(), song.getArtist(),song.getAddedDate(),
									song.getTitle() });
				}
				break;
			case 1:
				for (Song song : list) {
				mDb
				.execSQL(
						"INSERT INTO "
								+ DB_TABLE_SMS +" (" + _ID + "," + NAME + ","
										+ SIZE + "," + URL + "," + DISPLAYNAME + ","
										+ DURATION + "," + ARTIST + "," +DATA_ADDED +"," + TITLE + ")"
								+ " VALUES( ? , ? , ? , ? , ? , ? , ? , ? ,?)",
						new Object[] { song.getId(),
								song.getName(),
								song.getSize(), song.getUrl(),
								song.getDisplayName(),
								song.getDuration(), song.getArtist(),song.getAddedDate(),
								song.getTitle() });
				
				}
				break;
			}
		
			mDb.setTransactionSuccessful();
			
			isSucc = true;
		} catch (SQLException e) {
			e.printStackTrace();
			isSucc = false;
		} finally {
			mDb.endTransaction();
		}
		return isSucc;
	}
	public boolean insertData(Song song)
	{	
		try{
//			System.out.println("insert into "+ DB_TABLE_PHONE +"(_id,name,size,url,displayname,duration,artist,data_added,title) values("
//		+ song.getId()+ ",'" + song.getName() +"'," + song.getSize() + ", '" + song.getUrl() + "', '" + song.getDisplayName() + "',"
//		+ song.getDuration() + ",'" +song.getArtist() + "','" + song.getData_added()  +" ','" + song.getTitle() +"')" );
			mDb.execSQL("insert into "+ DB_TABLE_PHONE +"(_id,name,size,url,displayname,duration,artist,data_added,title) values("
		+ song.getId()+ ",'" + song.getName() +"'," + song.getSize() + ", '" + song.getUrl() + "', '" + song.getDisplayName() + "',"
		+ song.getDuration() + ",'" +song.getArtist() + "','" + song.getAddedDate()  +" ','" + song.getTitle() +"')" );
			Log.i("insertData", song.getUrl());
//			mDatabase
//				.execSQL(
//						"INSERT INTO "
//								+ DB_TABLE_PHONE
//								+ " VALUES(  ? , ? , ? , ? , ? , ? , ? , ? ,?)",
//						new Object[] { 
//								song.getId(), 
//								song.getName(),
//								song.getSize(),
//								song.getUrl(),
//								song.getDisplayName(),
//								song.getDuration(),
//								song.getArtist(),
//								song.getData_added(),
//								song.getTitle() });
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public ArrayList<Song> cusorToList(Cursor cur)
	{
		ArrayList<Song> list = new ArrayList<Song>();
		if(cur == null||cur.moveToFirst()==false)
		{
			cur.close();
			return list;
		}
		
		Song song = null;
		int id = 0;
		String artist = null;
		int duration = 0; //ʱ��
		String name = null ;
		String displayName = null;
		int size = 0 ;
		String url = null;
		String data_added = null;
		String title = null;
		for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
		{
			id = cur.getInt(cur.getColumnIndex(_ID));
			name = cur.getString(cur.getColumnIndex(NAME));
			size = cur.getInt(cur.getColumnIndex(SIZE));
			artist = cur.getString(cur.getColumnIndex(ARTIST));
			duration = cur.getInt(cur.getColumnIndex(DURATION));
			displayName = cur.getString(cur.getColumnIndex(DISPLAYNAME));
			url = cur.getString(cur.getColumnIndex(URL));
			data_added = cur.getString(cur.getColumnIndex(DATA_ADDED));
			title = cur.getString(cur.getColumnIndex(TITLE));
			song = new Song();
			song.setId(id);
			song.setName(name);
			song.setSize(size);
			song.setArtist(artist);
			song.setDuration(duration);
			song.setDisplayName(displayName);
			song.setUrl(url);
			song.setAddedDate(data_added);
			song.setTitle(title);
			list.add(song);
			
		}
		cur.close();
		return list;
	}
	public ArrayList<Song> getList(Cursor cur)
	{
		ArrayList<Song> list = new ArrayList<Song>();
		if(!cur.moveToFirst())
		{
			return null;
		}
		
		Song song = null;
		int id = 0;
		String artist = null;
		int duration = 0; //ʱ��
		String name = null ;
		String displayName = null;
		int size = 0 ;
		String url = null;
		String data_added = null;
		String title = null;
		do {
			id = cur.getInt(cur.getColumnIndex(_ID));
			name = cur.getString(cur.getColumnIndex(NAME));
			size = cur.getInt(cur.getColumnIndex(SIZE));
			artist = cur.getString(cur.getColumnIndex(ARTIST));
			duration = cur.getInt(cur.getColumnIndex(DURATION));
			displayName = cur.getString(cur.getColumnIndex(DISPLAYNAME));
			url = cur.getString(cur.getColumnIndex(URL));
			data_added = cur.getString(cur.getColumnIndex(DATA_ADDED));
			title = cur.getString(cur.getColumnIndex(TITLE));
			song = new Song();
			song.setId(id);
			song.setName(name);
			song.setSize(size);
			song.setArtist(artist);
			song.setDuration(duration);
			song.setDisplayName(displayName);
			song.setUrl(url);
			song.setAddedDate(data_added);
			song.setTitle(title);
			list.add(song);
		} while (cur.moveToNext());
		
		cur.close();
		return list;
	}
	//ɾ��
	public boolean deleteData(long rowId, int type) {

		Log.i("delete", "id -----" + rowId);
		switch (type) {
		case ICONST.TYPE_PHONE:
			return mDb.delete(DB_TABLE_PHONE, _ID + " = " + rowId, null) > 0;
		case ICONST.TYPE_SMS:
			return mDb.delete(DB_TABLE_SMS, _ID + " = " + rowId, null) > 0;
		}
		return false;

	}
	public void deletePhone(int where)
	{
		mDb.beginTransaction();
		switch(where)
		{
		case 0:
			
			mDb.execSQL("delete from " + DB_TABLE_PHONE + " where url like ?", new Object[]{"%sdcard%"});
			break;
		case 1:
			
			mDb.execSQL("delete from " + DB_TABLE_PHONE + " where url not like ?", new Object[]{"%sdcard%"});
			break;
			
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}
	public void deleteSMS(int where)
	{
		mDb.beginTransaction();
		switch(where)
		{
		case 0:
			
			mDb.execSQL("delete from " + DB_TABLE_SMS + " where url like ?", new Object[]{"%sdcard%"});
			break;
		case 1:
			
			mDb.execSQL("delete from " + DB_TABLE_SMS + " where url not like ?", new Object[]{"%sdcard%"});
			break;
			
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}
	public boolean cleanData(int type)
	{
		switch(type)
		{
		case 0:
			mDb.execSQL("delete from "+DB_TABLE_PHONE);
//			mDatabase.execSQL("select * from sqlite_sequence");
//			mDatabase.execSQL("update sqlite_sequence set seq=0 where name= " + DB_TABLE_PHONE);
			break;
		case 1:
			mDb.execSQL("delete from "+DB_TABLE_SMS);
//			mDatabase.execSQL("select * from sqlite_sequence");
//			mDatabase.execSQL("update sqlite_sequence set seq=0 where name= " + DB_TABLE_SMS);
			break;
		}
	
		return true;
	}
	//��ѯ
	public ArrayList<Song> fetchAllData(int type) {
		Cursor cur = null;
		switch (type) {
		case ICONST.TYPE_PHONE:
			cur = mDb.query(DB_TABLE_PHONE, ICONST.TABLE_COLUMN, null, null,
					null, null, null);
			
		case ICONST.TYPE_SMS:
			cur = mDb.query(DB_TABLE_SMS, ICONST.TABLE_COLUMN, null, null, null,
					null, null);
			
		}
		return cusorToList(cur);
	}
	//��ѯ����
	public void fetchData(long rowId, int type) {

		Cursor cur = null;
		switch (type) {
		case ICONST.TYPE_PHONE:
			cur = mDb.query(DB_TABLE_PHONE, ICONST.TABLE_COLUMN, _ID + "="
					+ rowId, null, null, null, null);
			break;
		case ICONST.TYPE_SMS:
			
			cur = mDb.query(DB_TABLE_SMS, ICONST.TABLE_COLUMN,
					_ID + "=" + rowId, null, null, null, null);
			break;
		}
		if (cur != null) {
			cur.moveToFirst();
		}
		cur.close();
	}
	//����
	public boolean updateData(long rowId, Song song, int type) {

		ContentValues cv = new ContentValues();
		cv.put(DISPLAYNAME, song.getDuration());
		cv.put(DISPLAYNAME, song.getDisplayName());
		cv.put(NAME, song.getName());
		cv.put(SIZE, song.getSize());
		cv.put(TITLE, song.getTitle());
		cv.put(URL, song.getUrl());
		cv.put(_ID, song.getId());
		cv.put(DATA_ADDED, song.getAddedDate());
		switch (type) {
		case ICONST.TYPE_PHONE:
			return mDb
					.update(DB_TABLE_PHONE, cv, _ID + "=" + rowId, null) > 0;
		case ICONST.TYPE_SMS:
			return mDb.update(DB_TABLE_SMS, cv, _ID + "=" + rowId, null) > 0;
		}
		return false;
	}
	
}
