package com.ring.ytjojo.app;

import java.io.File;  
import java.io.FileOutputStream;  
import java.io.PrintWriter;  
import java.io.StringWriter;  
import java.lang.Thread.UncaughtExceptionHandler;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.TimeZone;  

import com.example.randomringapp.R;
  
import android.content.Context;  
import android.content.pm.PackageInfo;  
import android.content.pm.PackageManager;  
import android.content.pm.PackageManager.NameNotFoundException;  
import android.content.res.Resources.Theme;
import android.os.Build;  
import android.os.Environment;  
import android.os.Looper;  
import android.util.Log;  
import android.widget.Toast;  
public class CustomCrashHandler implements UncaughtExceptionHandler {  
    private static final String TAG =  CustomCrashHandler.class.getSimpleName();
    private Context mContext;  
    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();  
    private static CustomCrashHandler mInstance = new CustomCrashHandler();  
      
      
    private CustomCrashHandler(){}  
 
    public static CustomCrashHandler getInstance(){  
        return mInstance;  
    }  
  
   
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        //��һЩ��Ϣ���浽SDcard��  
        savaInfoToSD(mContext, ex);  
          
        showToast(mContext,"啊哦，软件出现问题了，请稍候重启"+ ex.toString() + "thread" +thread.getName());  
        try {  
            thread.sleep(2000);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
//      android.os.Process.killProcess(android.os.Process.myPid());    
//        System.exit(1);  
          
        //�����˳����򷽷�  
        ExitAppUtils.getInstance().exit();  
          
    }  
  
    
    /** 
     * Ϊ���ǵ�Ӧ�ó��������Զ���Crash���� 
     */  
    public void setCustomCrashHanler(Context context){  
        mContext = context;  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
      
    /** 
     * ��ʾ��ʾ��Ϣ����Ҫ���߳�����ʾToast 
     * @param context 
     * @param msg 
     */  
    private void showToast(final Context context, final String msg){  
        new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();  
                Looper.loop();  
            }  
        }).start();  
    }  
      
      
    /** 
     * ��ȡһЩ�򵥵���Ϣ,����汾���ֻ�汾���ͺŵ���Ϣ�����HashMap�� 
     * @param context 
     * @return 
     */  
    private HashMap<String, String> obtainSimpleInfo(Context context){  
        HashMap<String, String> map = new HashMap<String, String>();  
        PackageManager mPackageManager = context.getPackageManager();  
        PackageInfo mPackageInfo = null;  
        try {  
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
          
        map.put("versionName", mPackageInfo.versionName);  
        map.put("versionCode", "" + mPackageInfo.versionCode);  
          
        map.put("MODEL", "" + Build.MODEL);  
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);  
        map.put("PRODUCT", "" +  Build.PRODUCT);  
          
        return map;  
    }  
      
      
    /** 
     * ��ȡϵͳδ��׽�Ĵ�����Ϣ 
     * @param throwable 
     * @return 
     */  
    private String obtainExceptionInfo(Throwable throwable) {  
        StringWriter mStringWriter = new StringWriter();  
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);  
        throwable.printStackTrace(mPrintWriter);  
        mPrintWriter.close();  
          
        Log.e(TAG, mStringWriter.toString());  
        return mStringWriter.toString();  
    }  
      
    /** 
     * �����ȡ�� �����Ϣ���豸��Ϣ�ͳ�����Ϣ������SDcard�� 
     * @param context 
     * @param ex 
     * @return 
     */  
    private String savaInfoToSD(Context context, Throwable ex){  
        String fileName = null;  
        StringBuffer sb = new StringBuffer();  
          
        for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key).append(" = ").append(value).append("\n");  
        }    
          
        sb.append(obtainExceptionInfo(ex));  
          
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
            File dir = new File(SDCARD_ROOT + File.separator + "crash" + File.separator);  
            if(! dir.exists()){  
                dir.mkdir();  
            }  
              
            try{  
                fileName = dir.toString() + File.separator + paserTime(System.currentTimeMillis()) + ".log";  
                FileOutputStream fos = new FileOutputStream(fileName);  
                fos.write(sb.toString().getBytes());  
                fos.flush();  
                fos.close();  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
              
        }  
          
        return fileName;  
          
    } 
    /** 
     * ��������ת����yyyy-MM-dd-HH-mm-ss�ĸ�ʽ 
     * @param milliseconds 
     * @return 
     */  
    private String paserTime(long milliseconds) {  
        System.setProperty("user.timezone", "Asia/Shanghai");  
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");  
        TimeZone.setDefault(tz);  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
        String times = format.format(new Date(milliseconds));  
          
        return times;  
    }  
    
}   