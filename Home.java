package com.example.overlordsupreme.fetch1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = new Intent(this, ScreenLockService.class);
        intent.putExtra("FLASH", true);
        intent.putExtra("SENSITIVITY", 0);
        startService(intent);

        SeekBar seekbar;
        seekbar = (SeekBar) findViewById(R.id.Sensitivity);
        TextView sensitivityText;
        sensitivityText = (TextView) findViewById(R.id.SensitivityText);

        sensitivityText.setText("Sensitivity:" + seekbar.getProgress());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int finalProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                finalProgress = progress;
                //Start screenLockService with new sensitivity setting
                Intent SLS = new Intent(ScreenLockService.class.getName());
                SLS.putExtra("SENSITIVITY", finalProgress);
                Home.this.startService(SLS);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void onHomeBoxChecked(View view){

        //Get if the box was checked or unchecked
        boolean checked = ((CheckBox) view).isChecked();

        //Get which box was clicked (flash or on/off)
        switch(view.getId()) {
            case R.id.appOnBox:
                if (checked){
                    //Start the Screen Lock Service
                    Intent lockService = new Intent(this, ScreenLockService.class);
                    if(!ScreenLockService.isRunning()){
                        lockService.setAction("com.example.fetch1.ScreenLockService");
                        startService(lockService);
                    }
                }
                else{
                    //Stop the Screen Lock Service
                    Intent lockService = new Intent(this, ScreenLockService.class);
                    if(ScreenLockService.isRunning()){
                        stopService(lockService);
                    }
                }
                break;
            case R.id.flashBox:
                if (checked){
                    //Enable info.flash
                    Intent SLS = new Intent(ScreenLockService.class.getName());
                    SLS.putExtra("FLASH", true);
                    Home.this.startService(SLS);
                }
                else {
                    //Disable the flash
                    Intent SLS = new Intent(ScreenLockService.class.getName());
                    SLS.putExtra("FLASH", false);
                    Home.this.startService(SLS);
                }
                break;
        }
    }


    public void onInfoPageClicked(View view){
        Intent intent = new Intent(this, InfoPage.class);
        startActivity(intent);
        //start infoPage activity
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
