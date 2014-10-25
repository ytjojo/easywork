package com.ring.ytjojo.app;

public class Constants {
	// APP_ID �滻Ϊ���Ӧ�ôӹٷ���վ���뵽�ĺϷ�appId
    public static final String WX_APP_ID = "wxa4e5def954d043d3";
    public static final String BDMAP_AK ="DMGUXPY85PYZlz5x72cEoaoI";
//    public static final String APP_NAME ="sunday";//分享到其他平台时显示的软件名称
    //public static final String APP_DOWNLOAD_URL ="http://sunday.so/Share";//分享到其他平台时显示的软件名称
    public static final String APP_DOWNLOAD_URL = "http://www.sunday.so/Share/page/";
   
    public static final String URL_BASE = "http://www.sunday.so/";
    
    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
    public static boolean isDebug = false;
    public static int screenWidth = 0;
    public static int screenHeitht = 0;
	public static boolean isAccept = true;
	public static boolean isVoice = true;
	public static boolean isVibration = true;

	//免打扰时段
	public static boolean mAllDaysOpenBool = false;
	public static boolean mNightOpenBool = false;
	public static boolean mCloseBool = true;
	//设置免打扰时段
	public static AppContext appContext;//437785584
	
	public static String DOLOAD_DB_NAME = "download_db";
	
	
	
    public static final String ACCOUNT_INDICES = "account_indices_key_v2";
    public static final String NOTIFICATION_LAST_ACTIONED_MENTION_ID = "notification_last_actioned_mention_id_v1_";
    public static final String NOTIFICATION_LAST_DISPLAYED_MENTION_ID = "notification_last_displayed_mention_id_v1_";
    public static final String NOTIFICATION_COUNT = "notification_count_";
    public static final String NOTIFICATION_SUMMARY = "notification_summary_";
    public static final String VERSION = "prefs_version";
    public static final String CURRENT_ACCOUNT_ID = "current_account_id_key_v2";
    public static final String TUTORIAL_COMPLETED = "tutorial_completed_v2";
    public static final String NOTIFICATION_LAST_ACTIONED_DIRECT_MESSAGE_ID = "notification_last_actioned_direct_message_id_v1_";
    public static final String NOTIFICATION_LAST_DISPLAYED_DIRECT_MESSAGE_ID = "notification_last_displayed_direct_message_id_v1_";

    public static final String NOTIFICATION_TYPE_MENTION = "_mention";
    public static final String NOTIFICATION_TYPE_DIRECT_MESSAGE = "_directmessage";
    
}
