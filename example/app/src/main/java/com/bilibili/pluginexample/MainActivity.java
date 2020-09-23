package com.bilibili.pluginexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("adsfasfiii","oncreate");
        Log.d("adsfasfddd","oncreate");
        Log.w("adsfasfwww","oncreate");
        Log.e("adsfasfeee","oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OriginMethod.log();
            }
        });

        new OriginMethod().onCreate();
    }




}