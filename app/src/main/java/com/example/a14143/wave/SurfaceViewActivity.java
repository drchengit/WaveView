package com.example.a14143.wave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * surfaceView 改造后的view ,没有卡顿
 */
public class SurfaceViewActivity extends AppCompatActivity {
MyWaveView waveView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    waveView = findViewById(R.id.waveView);


    }


}
