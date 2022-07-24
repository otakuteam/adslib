package com.otaku.ad.waterfall.sample;

import android.app.Application;

import com.otaku.ad.waterfall.AdsConstants;
import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.NotSupportPlatformException;
import com.otaku.ad.waterfall.model.AdModel;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            AdsManager.getInstance().init(this, false,
                    new AdModel(AdsConstants.UNITY,
                            "3617642",
                            "banner",
                            "video",
                            "video"));
        } catch (NotSupportPlatformException e) {
            e.printStackTrace();
        }

    }
}
