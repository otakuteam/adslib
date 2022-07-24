package com.otaku.ad.waterfall;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;

public abstract class AdsPlatform {
    public AdModel mAdModel;
    public abstract void init(Context context, boolean testMode);
    public abstract void showBanner(Activity activity, ViewGroup banner, BannerAdsListener listener) ;
    public abstract void showPopup(Activity activity, PopupAdsListener listener);
    public abstract void forceShowPopup(Activity activity, PopupAdsListener listener);
    public abstract void showReward(Activity activity, RewardAdListener listener);
}
