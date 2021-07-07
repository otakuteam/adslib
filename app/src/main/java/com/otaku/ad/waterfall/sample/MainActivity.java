package com.otaku.ad.waterfall.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ad.waterfall.AdsConstants;
import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.NotSupportPlatformException;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            AdsManager.getInstance().init(this, true,
                    new AdModel(AdsConstants.ADMOB,
                            "ca-app-pub-3940256099942544~3347511713",
                            "ca-app-pub-3940256099942544/",
                            "ca-app-pub-3940256099942544/",
                            "ca-app-pub-3940256099942544/"),
//                    new AdModel(AdsConstants.FACEBOOK,
//                            "ca-app-pub-3940256099942544~3347511713",
//                            "ca-app-pub-3940256099942544/6300978111",
//                            "ca-app-pub-3940256099942544/1033173712",
//                            "ca-app-pub-3940256099942544/5224354917")
                    new AdModel(AdsConstants.UNITY,
                            "3617642",
                            "banner",
                            "video",
                            "rewardedVideo"),
                    new AdModel(AdsConstants.APPODEAL,
                            "006b8405d43949f321184c3e029b7f3337d99fa75381776d",
                            "",
                            "",
                            "")
            );
        } catch (NotSupportPlatformException e) {
            e.printStackTrace();
        }
//        AdsManager.getInstance().setLimitTime(40);
        //ArrayList<String> waterfall = new ArrayList<>();
       // waterfall.add("admob");
//        waterfall.add("unity");
//        AdsManager.getInstance().saveWaterFall(waterfall);
//
//        AdsManager.getInstance().saveAdModel(new AdModel(AdsConstants.UNITY,
//                "3617642",
//                "banner",
//                "video",
//                "rewardedVideo"));

        ((Button) findViewById(R.id.btn_test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
    }
}
