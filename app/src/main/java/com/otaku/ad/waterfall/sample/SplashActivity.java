package com.otaku.ad.waterfall.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.otaku.ad.waterfall.AdsConstants;
import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.NotSupportPlatformException;
import com.otaku.ad.waterfall.model.AdModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new ConfigFetcher(this).execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);
    }
}