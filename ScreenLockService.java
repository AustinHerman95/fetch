package com.example.[USER].fetch1;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
 
 //Checks if the screen is locked. This should always be running
public class ScreenLockService extends IntentService {

    public ScreenLockService() {
        super("ScreenLockService");
    }

    public static final String TAG = "ScreenLockService";

    @Override
    protected void onHandleIntent(Intent intent) {

        //Want this thread as light as possible since it will always be running
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //Not set on this loop. Still testing things but this might be uneccesary. 
                        while(!Thread.interrupted()) {
                            //simple way to get if the screen is locked
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
        ).start();
    }

    //Start the listener service if not already running
    protected void startListener(){
        Intent listenerIntent = new Intent(this, Listener.class);
        if(!Listener.isListening()) {
            listenerIntent.setAction("com.example.fetch1.Listener");
            listenerIntent.addCategory(TAG);
            startService(listenerIntent);
        }
    }
    //Stop the listener service if already running. This deals with how we stop listening and might get reworked
    protected void stopListener(){
        Intent listenerIntent = new Intent(this, Listener.class);
        if(Listener.isListening()) {
            listenerIntent.addCategory(TAG);
            stopService(listenerIntent);
        }
    }
}
