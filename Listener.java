package com.example.overlordsupreme.fetch1;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import java.io.IOException;
import java.security.Policy;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
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
    static private boolean listening;

    public Listener(){
        super("Listener");
    }

    protected void onHandleIntent(Intent intent) {

        int mSensitivity;
        listening = true;

        //get the sensitivity and flash values
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        mSensitivity = extra.getInt("SENSITIVITY");

        //set up new audio factory and percussion values
        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        double threshold = 8;
        double sensitivity = (int) mSensitivity;

        Log.d(TAG, "Sensitivity: " + Double.toString(sensitivity));

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
        audioDispatcher.start();
    }

    void clapDetected(){

        //if the flash value is true
        //these methods/classes are deprecated but will still work
        //we want to build for low API so more users can use the app of course
        if(flash){
            Camera cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
        }
        //set off the vibrator - giggity
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        //try to set off a notification ringtone
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "Destroying Listener");
        listening = false;
    }

    static public boolean isListening(){
       return listening;
    }


}
