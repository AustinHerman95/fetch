package com.example.overlordsupreme.fetch1;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.os.Process;

import java.util.Iterator;
import java.util.List;

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
    static private boolean ScreenLockRunning;

    public ScreenLockService() {
        super("ScreenLockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ScreenLockRunning = true;
        //get the flash/sensitivity values to be sent to the listener service
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        sensitivity = extra.getInt("SENSITIVITY");
        boolean checkScreenAlive;

        //start a new thread to check if the screen is locked
        Thread checkScreen = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //this ensures the thread will constantly be checking for this... I think... needs testing
                        while(!Thread.interrupted()) {
                            //if the screen is locked start listener else stop listener
                            KeyguardManager myKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                            if (myKeyManager.inKeyguardRestrictedInputMode()) {
                                startListener();
                            }
                            else{
                                stopListener();
                            }
                        }
                    }
                }
        );
        checkScreen.setName("checkScreen");
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
        //if the listener is NOT already running, start the listener

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.Listener".equals(service.service.getClassName())) {
                stopListener();
            }
        }

        Intent listenerIntent = new Intent(this, Listener.class);
        if(!Listener.isListening()) {
            Log.d(TAG, "Starting Listener");
            listenerIntent.setAction("com.example.fetch1.Listener");
            listenerIntent.addCategory(TAG);
            listenerIntent.putExtra("FLASH", flash);
            listenerIntent.putExtra("SENSITIVITY", sensitivity);
            startService(listenerIntent);
        }
    }
    protected void stopListener(){
        //if the listener IS running, stop the listener
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        Iterator<ActivityManager.RunningAppProcessInfo> iterate = runningAppProcesses.iterator();

        while(iterate.hasNext()){
            ActivityManager.RunningAppProcessInfo next = iterate.next();

            String processName = getPackageName() + ":Listener";

            if(next.processName.equals(processName)){
                Process.killProcess(next.pid);
                break;
            }
        }
        /*Intent listenerIntent = new Intent(this, Listener.class);
        if(Listener.isListening()) {
            Log.d(TAG, "Stopping Listener");
            listenerIntent.addCategory(TAG);
            stopService(listenerIntent);
        }*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "Destroying SLS");
        ScreenLockRunning = false;
    }

    static public boolean isRunning(){
        return ScreenLockRunning;
    }
}
