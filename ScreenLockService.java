package com.example.overlordsupreme.fetch1;

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
public class ScreenLockService extends IntentService {

    public ScreenLockService() {
        super("ScreenLockService");
    }

    public static final String TAG = "ScreenLockService";

    @Override
    protected void onHandleIntent(Intent intent) {

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
}
