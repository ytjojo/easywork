package com.ring.ytjojo.app;

public interface ICONST {
	public final int SCAN_STARTED = 0;
	public final int SCAN_FINISHED = 1;
	public final int SCAN_DISMISS = 2;
	
	public final String SHARE_PHONE = "phonering_uri";
	public final String SHARE_SMS = "smsring_uri";
	public final String SHARE_SIZE = "size";
	
	public final  int PLAY_MSG = 0;
	public final int PAUSE_MSG = 1;
	public final int STOP_MSG = 2;
	
	public final int LISTVIEW_DELETE = 0;
	public final int LISTVIEW_CHOISE = 1;
	
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	
	public static final int TYPE_PHONE = 0;
	
	public static final int TYPE_SMS = 1;
	
	public static final String[] TABLE_COLUMN = {"_id","name","size","url","displayname","duration","artist","data_added","title"};
	
	public static final int SEND_REQUEST_EXTERNAL = 0;
	
	public static final int SEND_REQUEST_INTENAL = 1;
	
	public static final int RESULT_LIST = 200;
	
	public static final String CONTENT_EX = "content://media/external/audio/media/";
	public static final String  CONTENT_IN= "content://media/internal/audio/media/";
}
