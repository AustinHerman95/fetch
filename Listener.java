package com.example.overlordsupreme.fetch1;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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

    public static String TAG = "Listener";
    private boolean flash;

    public Listener(){
        super("Listener");
    }

    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Starting Listener");

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String framesPerBuffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d(TAG, "FPB: " + sampleRate + " Sample rate: " + framesPerBuffer);

        int SAMPLE_RATE = Integer.parseInt(sampleRate);
        int BUFFER_SIZE = Integer.parseInt(framesPerBuffer);
        int BUFFER_OVERLAP = 0;

        //get the sensitivity and flash values
        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        double sensitivity = extra.getInt("SENSITIVITY");
        double threshold = extra.getInt("THRESHOLD");

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, BUFFER_OVERLAP);

        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(SAMPLE_RATE, BUFFER_SIZE,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");
                        clapDetected();
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        Thread audioDispatcher = new Thread(dispatcher);
        audioDispatcher.setName("Audio Dispatcher Thread");
        Log.d(TAG, "Starting dispatcher thread");
        audioDispatcher.start();

    }

    void clapDetected(){

        if(flash){
            Log.d(TAG, "Triggering flash");
            Camera cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
            cam.stopPreview();
            cam.release();
        }

        Log.d(TAG, "Triggering vibrator");
        //set off the vibrator - giggity
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate pattern
        long pattern[] = {0, 1000, 500, 1000, 500, 1000};
        v.vibrate(pattern, -1);

        //try to set off a notification ringtone
        try {
            Log.d(TAG, "Ringtone triggered");
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startNextActivity(){
        Log.d(TAG, "Starting phone found activity");
        Intent nextIntent = new Intent(this, PhoneFound.class);
        startActivity(nextIntent);
    }
    @Override
    public void onDestroy(){
        Log.d(TAG, "Destroying Listener");
        super.onDestroy();
    }
}
