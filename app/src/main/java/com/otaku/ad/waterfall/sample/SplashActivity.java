package com.otaku.ad.waterfall.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import com.otaku.ad.waterfall.AdsConstants;
import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.NotSupportPlatformException;
import com.otaku.ad.waterfall.listener.OpenAdsListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;

public class SplashActivity extends BaseActivity {
    final String TAG = getClass().getSimpleName();
    private long secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new ConfigFetcher(this).execute();

        int waitTime = 3;
        createTimer(waitTime);

    }
    public void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    private void createTimer(long seconds) {
        CountDownTimer countDownTimer =
                new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = ((millisUntilFinished / 1000) + 1);

                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;


                        Application application = getApplication();

                        // If the application is not an instance of MyApplication, log an error message and
                        // start the MainActivity without showing the app open ad.
                        if (!(application instanceof App)) {
                            AdsLog.i(TAG, "Failed to cast application to MyApplication.");
                                startMainActivity();
                            return;
                        }

                        // Show the app open ad.
                        ((App) application)
                                .showAdIfAvailable(
                                        SplashActivity.this,
                                        new OpenAdsListener() {
                                            @Override
                                            public void OnShowAdComplete() {
                                                AdsLog.i(TAG, "OnShowAdComplete.");
                                                    startMainActivity();
                                            }
                                        });
                    }
                };
        countDownTimer.start();
    }
}