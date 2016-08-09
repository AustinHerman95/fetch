package com.example.overlordsupreme.fetch1;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PhoneFound extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_found);
    }

    public void onYes(View view){
        //keep settings
        finish();
    }

    public void onNo(View view){
        //Save the failure somehow
        finish();
    }
}
