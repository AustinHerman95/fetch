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

import java.util.Timer;
import java.util.TimerTask;

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
    private int threshold;
    private static boolean threadStopped;
    private static boolean checkScreenAlive;

    public ScreenLockService() {
        super("ScreenLockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //get the flash/sensitivity values to be sent to the listener service
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        sensitivity = extra.getInt("SENSITIVITY");
        threshold = extra.getInt("THRESHOLD");
        checkScreenAlive = true;
        threadStopped = false;

        //start a new thread to check if the screen is locked
        Thread checkScreen = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        Boolean lockInput = false;

                        while(!Thread.interrupted()) {

                            if(threadStopped){
                                Log.d(TAG, "Stopping SLS thread");
                                checkScreenAlive = false;
                                return;
                            }

                            KeyguardManager firstKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                            if (firstKeyManager.inKeyguardRestrictedInputMode()) {
                                lockInput = true;
                            }

                            if(lockInput) {
                                lockInput = false; //Only want this called once per check

                                //Put a timer here do slow the app down and figure out what's going wrong
                                new Timer().schedule(
                                        new TimerTask() {

                                            @Override
                                            public void run() {

                                                //if the screen is locked start listener service else stop listener service (METHODS WITHIN THIS SERVICE HANDLE THIS)
                                                KeyguardManager secondKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

                                                if (secondKeyManager.inKeyguardRestrictedInputMode()) {
                                                    startListener();
                                                }
                                                else{
                                                    stopListener();
                                                }
                                            }
                                        }, 100 //Fire of the check almost instantly
                                );
                            }
                            else{
                                stopListener();
                            }

                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        checkScreen.setName("checkScreen");
        Log.d(TAG, "Starting SLS thread");
        checkScreen.start();

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
            listenerIntent.putExtra("THRESHOLD", threshold);
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
        threadStopped = true;
        super.onDestroy();
    }

}
