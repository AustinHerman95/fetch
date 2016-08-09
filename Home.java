package com.example.overlordsupreme.fetch1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class Home extends Activity {

    public static final String TAG = "HomeActivity";
    public static boolean flash;
    public static int sensitivity;
    public static int threshold;
    public static boolean appOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appOn = true;
        flash = true;
        sensitivity = 0;
        threshold = 0;

        //set up a new intent with default flash/sensitivity settings
        Intent intent = new Intent(this, ScreenLockService.class);
        intent.putExtra("FLASH", flash);
        intent.putExtra("SENSITIVITY", sensitivity);
        startService(intent);

        //set up the seekbar and the progress text
        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.Sensitivity);
        SeekBar thresholdBar = (SeekBar) findViewById(R.id.Threshold);
        TextView sensitivityText = (TextView) findViewById(R.id.SensitivityText);
        TextView thresholdText = (TextView) findViewById(R.id.ThresholdText);

        sensitivityText.setText(getString(R.string.sensitivity, sensitivityBar.getProgress(), sensitivityBar.getMax()));
        thresholdText.setText(getString(R.string.threshold, thresholdBar.getProgress(), thresholdBar.getMax()));

        //write what happens when the user changes the seek bar
        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //when the user releases the slider
            @Override
            public void onProgressChanged(SeekBar sensitivityBar, int progress, boolean fromUser) {
                sensitivity = progress;

                TextView sensitivityText;
                sensitivityText = (TextView) findViewById(R.id.SensitivityText);
                sensitivityText.setText(getString(R.string.sensitivity, progress, sensitivityBar.getMax()));

                //Start screenLockService with new sensitivity setting
                if(appOn) {
                    Context context = getApplicationContext();
                    Intent SLS = new Intent(context, ScreenLockService.class);
                    SLS.putExtra("SENSITIVITY", sensitivity);
                    context.startService(SLS);
                }
            }
            //the next two should probably have some implementation, still figuring out what to put there
            //when the user has control of the slider
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //when the user stops controlling the slider
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        thresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //when the user releases the slider
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold = progress;

                TextView thresholdText;
                thresholdText = (TextView) findViewById(R.id.ThresholdText);
                thresholdText.setText(getString(R.string.threshold, progress, seekBar.getMax()));

                //Start screenLockService with new sensitivity setting
                if(appOn) {
                    Context context = getApplicationContext();
                    Intent SLS = new Intent(context, ScreenLockService.class);
                    SLS.putExtra("THRESHOLD", threshold);
                    context.startService(SLS);
                }
            }
            //the next two should probably have some implementation, still figuring out what to put there
            //when the user has control of the slider
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //when the user stops controlling the slider
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void onHomeBoxChecked(View view){
        //Get if the box was checked or unchecked
        boolean checked = ((CheckBox) view).isChecked();

        //Get which box was clicked (flash or on/off)
        switch(view.getId()) {
            //the ON/OFF check box - works I think, doesn't throw any exceptions
            case R.id.appOnBox:
                if (checked){
                    appOn = true;
                    //Start the Screen Lock Service
                    Intent SLS = new Intent(this, ScreenLockService.class);
                    SLS.setAction("com.example.fetch1.ScreenLockService");
                    SLS.putExtra("FLASH", flash);
                    SLS.putExtra("SENSITIVITY", sensitivity);
                    SLS.putExtra("THRESHOLD", threshold);
                    startService(SLS);
                    Log.d(TAG, "Started SLS");
                }
                else{
                    appOn = false;
                    //Stop the Screen Lock Service
                    Intent intent = new Intent(getApplicationContext(), ScreenLockService.class);
                    intent.addCategory(TAG);
                    stopService(intent);
                    Log.d(TAG, "Stopped SLS");
                }
                break;
            //the flash light check box *does NOT work - check issues on github
            case R.id.flashBox:
                if (checked){
                    if(appOn) {
                        //set up new intent with flash
                        flash = true;
                        Context context = getApplicationContext();
                        Intent SLS = new Intent(context, ScreenLockService.class);
                        SLS.putExtra("FLASH", flash);
                        SLS.putExtra("SENSITIVITY", sensitivity);
                        SLS.putExtra("THRESHOLD", threshold);
                        context.startService(SLS);
                        Log.d(TAG, "Flash is on");
                    }
                }
                else {
                    if(appOn) {
                        //set up new intent with no flash
                        flash = false;
                        Context context = getApplicationContext();
                        Intent SLS = new Intent(context, ScreenLockService.class);
                        SLS.putExtra("FLASH", flash);
                        SLS.putExtra("SENSITIVITY", sensitivity);
                        SLS.putExtra("THRESHOLD", threshold);
                        context.startService(SLS);
                        Log.d(TAG, "Flash is off");
                    }
                }
                break;
        }
    }

    public void onInfoPageClicked(View view){
        Intent intent = new Intent(this, InfoPage.class);
        startActivity(intent);
    }
}
