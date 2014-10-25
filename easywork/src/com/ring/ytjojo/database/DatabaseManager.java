package com.ring.ytjojo.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public interface DatabaseManager {
	SQLiteOpenHelper getSQLiteOpenHelper();
	int getVersion();
	SQLiteDatabase getDB();
	void close();
}
