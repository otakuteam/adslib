package com.otaku.ad.waterfall.sample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.util.AdsLog;

public class TestActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;
        AdsLog.d("TEST__", "onCreate_____________");
        AdsManager.getInstance().showBanner((ViewGroup) findViewById(R.id.ads_banner));
//        AdsManager.getInstance().showPopup(new PopupAdsListener() {
//            @Override
//            public void OnClose() {
//                Log.i("TEST__", "_test_OnClose");
//            }
//
//            @Override
//            public void OnShowFail() {
//                Log.i("TEST__", "_test_OnShowFail");
//            }
//        });

        ((Button) findViewById(R.id.btn_test_reward)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);
                AdsManager.getInstance().showReward(new RewardAdListener() {
                    @Override
                    public void OnClose() {

                    }

                    @Override
                    public void OnShowFail() {

                    }

                    @Override
                    public void OnRewarded() {
                        Log.i("TEST__", "OnRewarded");
                        Toast.makeText(context, "OnRewared", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TEST__", "onDestroy");
    }
}
