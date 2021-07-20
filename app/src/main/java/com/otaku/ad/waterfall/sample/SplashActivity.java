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
        try {
            AdsManager.getInstance().init(this, true,
                    new AdModel(AdsConstants.ADMOB,
                            "ca-app-pub-3940256099942544~3347511713",
                            "ca-app-pub-3940256099942544/6300978111",
                            "ca-app-pub-3940256099942544/1033173712",
                            "ca-app-pub-3940256099942544/5224354917")
//                    new AdModel(AdsConstants.FACEBOOK,
//                            "ca-app-pub-3940256099942544~3347511713",
//                            "ca-app-pub-3940256099942544/6300978111",
//                            "ca-app-pub-3940256099942544/1033173712",
//                            "ca-app-pub-3940256099942544/5224354917")
//                    new AdModel(AdsConstants.UNITY,
//                            "3617642",
//                            "banner",
//                            "video",
//
//                    new AdModel(AdsConstants.INHOUSE,
//                            "",
//                            "[\n" +
//                                    "  {\n" +
//                                    "    \"resource\": \"https://i.pinimg.com/564x/b2/24/77/b2247729bdc95247ac3724474b7a03f4.jpg\",\n" +
//                                    "    \"action\": \"https://play.google.com/store/apps/details?id=com.roblox.client\"\n" +
//                                    "  },\n" +
//                                    "  {\n" +
//                                    "    \"resource\": \"https://i.pinimg.com/564x/78/a5/ed/78a5eddb161d03ed314967bb651265eb.jpg\",\n" +
//                                    "    \"action\": \"https://play.google.com/store/apps/details?id=com.innersloth.spacemafia\"\n" +
//                                    "  }\n" +
//                                    "]",
//                            "[\n" +
//                                    "  {\n" +
//                                    "    \"resource\": \"https://i.pinimg.com/564x/26/a7/6d/26a76dbc138c4f1f79e2b8c7924462aa.jpg\",\n" +
//                                    "    \"action\": \"https://play.google.com/store/apps/details?id=com.roblox.client\"\n" +
//                                    "  },\n" +
//                                    "  {\n" +
//                                    "    \"resource\": \"https://i.pinimg.com/564x/2f/e2/53/2fe25352bdcb4d296124f68dba652fa8.jpg\",\n" +
//                                    "    \"action\": \"https://play.google.com/store/apps/details?id=com.innersloth.spacemafia\"\n" +
//                                    "  }\n" +
//                                    "]",
//                            "[\n" +
//                                    "  {\n" +
//                                    "    \"resource\": \"https://v.pinimg.com/videos/mc/720p/83/05/b5/8305b5bc6da97a7ceef4da419813e59d.mp4\",\n" +
//                                    "    \"action\": \"https://play.google.com/store/apps/details?id=com.roblox.client\"\n" +
//                                    "  },\n" +
//                                    "  {\n" +
//                                    "    \"resource\": \"https://v.pinimg.com/videos/mc/720p/c8/1e/9d/c81e9d0bffd56719fb9de78a3d556760.mp4\",\n" +
//                                    "    \"action\": \"https://play.google.com/store/apps/details?id=com.innersloth.spacemafia\"\n" +
//                                    "  }\n" +
//                                    "]")
            );
        } catch (NotSupportPlatformException e) {
            e.printStackTrace();
        }
        AdsManager.getInstance().setLimitTime(0);
//        ArrayList<String> waterfall = new ArrayList<>();
//        waterfall.add("admob");
//        waterfall.add("unity");
//        AdsManager.getInstance().saveWaterFall(waterfall);
//        AdsManager.getInstance().saveAdModel( new AdModel(AdsConstants.ADMOB,
//                "ca-app-pub",
//                "ca-app-pub",
//                "ca-app-pub-3940256099942544/",
//                "ca-app-pub-3940256099942544/"));
//        AdsManager.getInstance().saveAdModel(new AdModel(AdsConstants.UNITY,
//                "3617642",
//                "banner",
//                "video",
//                "rewardedVideo"));

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