package com.ring.ytjojo.util;

import java.io.Closeable;

import android.database.Cursor;

public class IOUtils {
	 private IOUtils() {
	    }

	    public static void closeQuietly(Closeable closeable) {
	        if (closeable != null) {
	            try {
	                closeable.close();
	            } catch (Exception e) {
	            }
	        }
	    }

	    public static void closeQuietly(Cursor cursor) {
	        if (cursor != null) {
	            try {
	                cursor.close();
	            } catch (Exception e) {
	            }
	        }
	    }

}
