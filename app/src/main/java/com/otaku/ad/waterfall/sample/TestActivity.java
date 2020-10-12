package com.otaku.ad.waterfall.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.listener.PopupAdsListener;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        AdsManager.getInstance().showBanner((ViewGroup) findViewById(R.id.ads_banner));
        AdsManager.getInstance().showPopup(new PopupAdsListener() {
            @Override
            public void OnClose() {

            }

            @Override
            public void OnShowFail() {
                Log.i("TEST__", "_test_OnShowFail");
            }
        });
    }
}
