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
    private int mSensitivity;
    static private boolean listening;

    public Listener(){
        super("Listener");
    }

    protected void onHandleIntent(Intent intent) {

        listening = true;

        Bundle extra = intent.getExtras();
        flash = extra.getBoolean("FLASH");
        mSensitivity = extra.getInt("SENSITIVITY");

        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        double threshold = 8;
        double sensitivity = (int) mSensitivity;

        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {
                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");
                        clapDetected();
                    }
                }, sensitivity, threshold);
        mDispatcher.addAudioProcessor(mPercussionDetector);

        new Thread(mDispatcher).start();
    }

    void clapDetected(){
        /*if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            CameraManager cam = (CameraManager) getSystemService(this.CAMERA_SERVICE);
            String mCameraID;
            try {
                for (String cameraID : cam.getCameraIdList()) {
                    CameraCharacteristics camChar = cam.getCameraCharacteristics(cameraID);

                    if (camChar.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
                    mCameraID = cameraID;
                    cam.setTorchMode(mCameraID, true);
                    break;
                }
            }
                catch(CameraAccessException e){
                    Log.e("Camera Access Exception", e.getMessage());
                }
        }*/

        if(flash){
            Camera cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
        }
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void stopListening(){
        listening = false;
    }

    static public boolean isListening(){
       return listening;
    }


}
