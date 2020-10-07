package com.otaku.ad.waterfall.admob;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.LogUtil;


public class AdmobAdsManager extends AdsPlatform {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private InterstitialAd popupAd;
    private RewardedVideoAd rewardAd;
    private PopupAdsListener adPopupListener;
    private RewardAdListener adRewardListener;
    private boolean isPopupReloaded = false;
    private boolean isRewardReloaded = false;

    public AdmobAdsManager(AdModel adModel) {
        this.mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mContext = context;

        MobileAds.initialize(mContext, mAdModel.getAppId());

        popupAd = new InterstitialAd(mContext);
        popupAd.setAdUnitId(mAdModel.getPopupId());
        popupAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (adPopupListener != null)
                    adPopupListener.OnClose();
                loadPopupAd();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (isPopupReloaded == false) {
                    isPopupReloaded = true;
                    loadPopupAd();
                }
            }
        });
        loadPopupAd();

        rewardAd = MobileAds.getRewardedVideoAdInstance(mContext);
        rewardAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                if (adRewardListener != null)
                    adRewardListener.OnClose();
                loadRewardAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                if (adRewardListener != null)
                    adRewardListener.OnRewarded();
                LogUtil.d(TAG, "onRewarded: " + rewardItem);
                loadRewardAd();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                if (isRewardReloaded == false) {
                    isRewardReloaded = true;
                    loadRewardAd();
                }
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        loadRewardAd();
    }

    private void loadPopupAd() {
        if (popupAd != null && !popupAd.isLoading() && !popupAd.isLoaded()) {
            popupAd.loadAd(new AdRequest.Builder().build());
        }
    }

    private void loadRewardAd() {
        if (rewardAd != null && !rewardAd.isLoaded()) {
            rewardAd.loadAd(mAdModel.getRewardId(), new AdRequest.Builder().build());
        }
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        AdView adView = new AdView(mContext);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(mAdModel.getBannerId());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                listener.OnLoadFail();
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(adView, params);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        adPopupListener = listener;
        LogUtil.i(TAG, "showPopup");
        if (canShowPopupAd(popupAd)) {
            isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
            popupAd.show();
        } else {
            loadPopupAd();
            if (adPopupListener != null)
                adPopupListener.OnShowFail();
        }
    }

    private boolean canShowPopupAd(InterstitialAd ad) {
        return (ad != null && ad.isLoaded());
    }

    @Override
    public void showReward(RewardAdListener listener) {
        adRewardListener = listener;
        if (canShowReward(rewardAd)) {
            isRewardReloaded = false;
            rewardAd.show();
        } else {
            loadRewardAd();
            if (adRewardListener != null)
                adRewardListener.OnShowFail();
        }

    }

    private boolean canShowReward(RewardedVideoAd rewardAd) {
        return (rewardAd != null && rewardAd.isLoaded());
    }
}
