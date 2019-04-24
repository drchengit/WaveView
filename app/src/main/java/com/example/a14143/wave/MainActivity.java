package com.example.a14143.wave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
WaveView waveView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    waveView = findViewById(R.id.waveView);


    }

    @Override
    protected void onResume() {
        super.onResume();
    waveView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    waveView.onPause();
    }
}
