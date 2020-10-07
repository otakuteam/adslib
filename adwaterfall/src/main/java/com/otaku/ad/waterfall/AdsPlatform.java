package com.otaku.ad.waterfall;

import android.content.Context;
import android.view.ViewGroup;

import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;

public abstract class AdsPlatform {
    public AdModel mAdModel;
    public abstract void init(Context context, boolean testMode);
    public abstract void showBanner(ViewGroup banner, BannerAdsListener listener) ;
    public abstract void showPopup(PopupAdsListener listener);
    public abstract void showReward(RewardAdListener listener);
}
