package com.otaku.ad.waterfall.admob;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;


public class AdmobAdsManager extends AdsPlatform {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private PublisherInterstitialAd popupAd;
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

        popupAd = new PublisherInterstitialAd(mContext);
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
                AdsLog.d(TAG, "onRewarded: " + rewardItem);
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
            popupAd.loadAd(new PublisherAdRequest.Builder().build());
        }
    }

    private void loadRewardAd() {
        if (rewardAd != null && !rewardAd.isLoaded()) {
            rewardAd.loadAd(mAdModel.getRewardId(), new PublisherAdRequest.Builder().build());
        }
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        PublisherAdView adView = new PublisherAdView(mContext);
        adView.setAdSizes(AdSize.SMART_BANNER);
        adView.setAdUnitId(mAdModel.getBannerId());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                listener.OnLoadFail();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                banner.addView(adView, params);

                View view = new View(mContext);
                view.setBackgroundColor(Color.BLACK);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, (int) (4* ((float) mContext.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
                params2.addRule(RelativeLayout.ABOVE, adView.getId());
                banner.addView(view, params2);
            }
        });

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        adPopupListener = listener;
        AdsLog.i(TAG, "showPopup");
        if (canShowPopupAd(popupAd)) {
            isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
            popupAd.show();
        } else {
            loadPopupAd();
            if (adPopupListener != null)
                adPopupListener.OnShowFail();
        }
    }

    private boolean canShowPopupAd(PublisherInterstitialAd ad) {
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