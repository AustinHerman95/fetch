package com.example.[USER].fetch1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Start the screen lock service
        Intent intent = new Intent(this, ScreenLockService.class);
        startService(intent);


        //EXTRA junk, probably will delete this
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

    //Called when a check box on the home screen is clicked or unclicked
    public void onHomeBoxChecked(View view){

        //Get if the box was checked or unchecked
        boolean checked = ((CheckBox) view).isChecked();

        //Get which box was clicked (flash or on/off)
        switch(view.getId()) {
            case R.id.appOnBox:
                if (checked){
                    //Enable services flag
                }
                else{
                    //Disable services flag
                }
                break;
            //I want to send a bundle containing a boolean for if the flash/vibrate/sound etc is enabled/disabled
            //In this case info  is some container and flash is a boolean.
            //Still working on this
            case R.id.flashBox:
                if (checked){
                    //Enable info.flash
                }
                else {
                    //Disable info.flash
                }
                break;
        }
    }

    //Run the info page activity if the info button was pressed
    public void onInfoPageClicked(View view){
        Intent intent = new Intent(this, InfoPage.class);
        startActivity(intent);
        //start infoPage activity
    }
    
    //USELESS junk. Probably will delete this too
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
