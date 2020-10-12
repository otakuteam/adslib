package com.otaku.ad.waterfall.facebook;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;

public class FanManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private PopupAdsListener mPopupListener;
    private RewardAdListener mRewardAdListener;
    private InterstitialAd popupAd;
    private InterstitialAdListener interstitialAdListener;
    private RewardedVideoAd rewardedVideoAd;
    private RewardedVideoAdListener rewardedVideoAdListener;

    public FanManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mContext = context;
        if (testMode) {
            AdSettings.turnOnSDKDebugger(context);
        }

        AudienceNetworkAds.buildInitSettings(context)
                .withInitListener(new AudienceNetworkAds.InitListener() {
                    @Override
                    public void onInitialized(AudienceNetworkAds.InitResult initResult) {

                    }
                })
                .initialize();
        popupAd = new InterstitialAd(mContext, mAdModel.getPopupId());
        interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                AdsLog.i(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                AdsLog.i(TAG, "Interstitial ad dismissed.");
                if (mPopupListener != null)
                    mPopupListener.OnClose();
                loadPopup();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                AdsLog.i(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                if (mPopupListener != null)
                    mPopupListener.OnShowFail();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                AdsLog.i(TAG, "Interstitial ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                AdsLog.i(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                AdsLog.i(TAG, "Interstitial ad impression logged!");
            }
        };
        loadPopup();

        rewardedVideoAd = new RewardedVideoAd(mContext, mAdModel.getRewardId());
        rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                AdsLog.i(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
                loadReward();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                AdsLog.i(TAG, "Rewarded video ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                AdsLog.i(TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                AdsLog.i(TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                AdsLog.i(TAG, "Rewarded video completed!");
                if (mRewardAdListener != null)
                    mRewardAdListener.OnRewarded();
                loadReward();
                // Call method to give reward
                // giveReward();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                AdsLog.i(TAG, "Rewarded video ad closed!");
                if (mRewardAdListener != null)
                    mRewardAdListener.OnClose();
                loadReward();
            }
        };
        loadReward();

    }

    private void loadPopup() {
        popupAd.loadAd(popupAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build());
    }

    private void loadReward() {
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        AdView adView = new AdView(mContext, mAdModel.getBannerId(), AdSize.BANNER_HEIGHT_50);
        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                listener.OnLoadFail();
                AdsLog.i(TAG, "onError");
            }

            @Override
            public void onAdLoaded(Ad ad) {
                AdsLog.i(TAG, "onAdLoaded");

            }

            @Override
            public void onAdClicked(Ad ad) {
                AdsLog.i(TAG, "onAdClicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                AdsLog.i(TAG, "onLoggingImpression");
            }
        }).build());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(adView, params);
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        mPopupListener = listener;
        AdsLog.i(TAG, "showPopup");
        if (popupAd != null && popupAd.isAdLoaded() && !popupAd.isAdInvalidated()) {
            AdsLog.i(TAG, "showPopup ready");
            popupAd.show();
        } else {
            AdsLog.i(TAG, "showPopup fail");
            if (mPopupListener != null) {
                mPopupListener.OnShowFail();
            }
            loadPopup();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        mRewardAdListener = listener;
        if (rewardedVideoAd != null && !rewardedVideoAd.isAdLoaded() && !rewardedVideoAd.isAdInvalidated()) {
            rewardedVideoAd.show();
        } else {
            AdsLog.i(TAG, "showReward fail");
            if (mRewardAdListener != null) {
                mRewardAdListener.OnShowFail();
            }
            loadReward();
        }
    }
}
