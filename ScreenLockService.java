package com.example.overlordsupreme.fetch1;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ScreenLockService extends IntentService {

    public static final String TAG = "ScreenLockService";
    private boolean flash;
    private int sensitivity;
    static boolean stopFlag;

    public ScreenLockService() {
        super("ScreenLockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        stopFlag = false;

        //get the flash/sensitivity values to be sent to the listener service
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        sensitivity = extra.getInt("SENSITIVITY");
        boolean checkScreenAlive;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                Boolean screenState = bundle.getBoolean("ScreenState");
                if(screenState){
                    startListener();
                    stopFlag = true;
                }
                else if(!screenState){
                    stopListener();
                }
            }
        };

        //start a new thread to check if the screen is locked
        Thread checkScreen = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //this ensures the thread will constantly be checking for this... I think... needs testing
                        while(!Thread.interrupted()) {
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();

                            //if the screen is locked start listener else stop listener
                            KeyguardManager myKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                            if (myKeyManager.inKeyguardRestrictedInputMode()) {
                                //startListener();
                                //stopFlag = true;
                                bundle.putBoolean("ScreenState", true);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                            else if(!myKeyManager.inKeyguardRestrictedInputMode() && stopFlag){
                                //stopListener();
                                bundle.putBoolean("ScreenState", false);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
        );
        checkScreen.setName("checkScreen");
        Log.d(TAG, "Starting SLS thread");
        checkScreen.start();
        checkScreenAlive = true;

        while(checkScreenAlive){
            if(!checkScreen.isAlive()){
                Log.d(TAG, "Restarting check screen!");
                checkScreen.start();
            }
        }
    }

    protected void startListener(){
        if(!isMyServiceRunning(Listener.class)) {
            Log.d(TAG, "Starting Listener");
            Intent listenerIntent = new Intent(this, Listener.class);
            listenerIntent.putExtra("FLASH", flash);
            listenerIntent.putExtra("SENSITIVITY", sensitivity);
            startService(listenerIntent);
        }
    }
    protected void stopListener(){
        if(isMyServiceRunning(Listener.class)) {
            Log.d(TAG, "Stopping Listener");
            Intent listenerIntent = new Intent(this, Listener.class);
            stopService(listenerIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "Listener service already running");
                return true;
            }
        }
        Log.d(TAG, "Listener service not running");
        return false;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "Destroying SLS");
        super.onDestroy();
    }

}
