package com.ring.ytjojo.receiver;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;


public abstract class SystemSwitchReceiver extends BroadcastReceiver {  
    private IntentFilter mFilter;  
  

    public SystemSwitchReceiver(){  
        mFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);  
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);  
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);  
        mFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);  
        mFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
        mFilter.addAction("android.location.PROVIDERS_CHANGED");  
        mFilter.addAction("com.android.sync.SYNC_CONN_STATUS_CHANGED");  
        mFilter.addAction(Intent.ACTION_UMS_CONNECTED);  
        mFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);  
        mFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);  
        mFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);  
        mFilter.addAction(AudioManager.VIBRATE_SETTING_CHANGED_ACTION);  
        mFilter.addAction(Intent.ACTION_BOOT_COMPLETED);  
    }  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        //To change body of implemented methods use File | Settings | File Templates.  
        OnSysSwitchToggleReceive(context,intent);  
    }  
  
    protected abstract void OnSysSwitchToggleReceive(Context context, Intent intent) ;  
    /** 
     * @return the mFilter 
     */  
    public IntentFilter getFilter() {  
        return mFilter;  
    }  
      
      
      
}  