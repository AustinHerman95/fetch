package com.example.overlordsupreme.fetch1;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Listener extends IntentService {

    public static int SAMPLE_RATE = 1024;
    public static int BUFFER_OVERLAP = 512;
    public static String TAG = "Listener";
    private boolean flash;

    public Listener(){
        super("Listener");
    }

    protected void onHandleIntent(Intent intent) {
        boolean run = true;
        int mSensitivity;

        //get the sensitivity and flash values
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        mSensitivity = extra.getInt("SENSITIVITY");

        //set up new audio factory and percussion values
        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        double threshold = 8;
        double sensitivity = (int) mSensitivity;

        //create percussion detector object with instructions for when a clap is heard
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                //if a clap was heard do this:
                new OnsetHandler() {
                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");
                        clapDetected();
                    }
                }, sensitivity, threshold);

        //add object to audio processor
        mDispatcher.addAudioProcessor(mPercussionDetector);
        //start a thread to begin listening
        Thread audioDispatcher = new Thread(mDispatcher);
        audioDispatcher.setName("Audio Dispatcher Thread");
        Log.d(TAG, "Starting dispatcher thread");
        audioDispatcher.start();

        while(run){
            if(!audioDispatcher.isAlive()){
                Log.d(TAG, "Dispatcher thread was stopped, restarting!");
                audioDispatcher.start();
            }
        }
    }

    void clapDetected(){
        //if the flash value is true
        //these methods/classes are deprecated but will still work
        //we want to build for low API so more users can use the app of course
        if(flash){
            Log.d(TAG, "Triggering flash");
            Camera cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
        }

        Log.d(TAG, "Triggering vibrator - giggity");
        //set off the vibrator - giggity
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        //try to set off a notification ringtone
        try {
            Log.d(TAG, "Attempting ringtone alarm");
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy(){
        Log.i(TAG, "Destroying Listener");
        super.onDestroy();
    }
}
