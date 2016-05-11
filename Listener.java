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
import android.os.Build;
import android.os.Environment;
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
    static private boolean listening;

    public Listener(){
        super("Listener");
    }

    protected void onHandleIntent(Intent Intent) {

        listening = true;

        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        double threshold = 8;
        double sensitivity = 20;

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

        Camera cam = Camera.open();
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
        cam.startPreview();

    }

    void stopListening(){
        listening = false;
    }

    static public boolean isListening(){
        if(listening){
            return true;
        }
        return false;
    }


}
