package com.example.overlordsupreme.fetch1;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

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
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        sensitivity = extra.getInt("SENSITIVITY");

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        while(!Thread.interrupted()) {
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

    protected void startListener(){
        Intent listenerIntent = new Intent(this, Listener.class);
        if(!Listener.isListening()) {
            listenerIntent.setAction("com.example.fetch1.Listener");
            listenerIntent.addCategory(TAG);
            listenerIntent.putExtra("FLASH", flash);
            listenerIntent.putExtra("SENSITIVITY", sensitivity);
            startService(listenerIntent);
        }
    }
    protected void stopListener(){
        Intent listenerIntent = new Intent(this, Listener.class);
        if(Listener.isListening()) {
            listenerIntent.addCategory(TAG);
            stopService(listenerIntent);
        }
    }

    static public boolean isRunning(){
        return ScreenLockRunning;
    }
}
