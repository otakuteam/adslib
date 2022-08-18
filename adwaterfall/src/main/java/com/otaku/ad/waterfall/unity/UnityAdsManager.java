package com.otaku.ad.waterfall.unity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;
import com.unity3d.mediation.IInitializationListener;
import com.unity3d.mediation.IInterstitialAdLoadListener;
import com.unity3d.mediation.IInterstitialAdShowListener;
import com.unity3d.mediation.IReward;
import com.unity3d.mediation.IRewardedAdLoadListener;
import com.unity3d.mediation.IRewardedAdShowListener;
import com.unity3d.mediation.InitializationConfiguration;
import com.unity3d.mediation.InterstitialAd;
import com.unity3d.mediation.RewardedAd;
import com.unity3d.mediation.UnityMediation;
import com.unity3d.mediation.errors.LoadError;
import com.unity3d.mediation.errors.SdkInitializationError;
import com.unity3d.mediation.errors.ShowError;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityAdsManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;

    public UnityAdsManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        InitializationConfiguration configuration = InitializationConfiguration.builder()
                .setGameId(mAdModel.getAppId())
                .setInitializationListener(new IInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        // Unity Mediation is initialized. Try loading an ad.
                        AdsLog.d(TAG, "Unity Mediation is successfully initialized.");
                        //loadInterstitialAd();
                    }

                    @Override
                    public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
                        // Unity Mediation failed to initialize. Printing failure reason...
                        AdsLog.d(TAG, "Unity Mediation Failed to Initialize : " + msg);
                    }
                }).build();

        UnityMediation.initialize(configuration);
    }

    @Override
    public void showBanner(Activity activity, ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        BannerView adView = new BannerView(activity, mAdModel.getBannerId(),
                UnityBannerSize.getDynamicSize(activity));
        adView.setListener(new BannerView.Listener() {
            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                super.onBannerFailedToLoad(bannerAdView, errorInfo);
                if (listener != null) listener.OnLoadFail();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(adView, params);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adView.load();
            }
        }, 500);
    }

    @Override
    public void showPopup(Activity activity, PopupAdsListener listener) {
        AdsLog.d(TAG, "showUnityPopup_id: " + mAdModel.getPopupId());
        interstitialAd = new InterstitialAd(activity, mAdModel.getPopupId());
        AdsLog.d(TAG, "unity-showPopup: " + interstitialAd.getAdState());
        interstitialAd.load(new IInterstitialAdLoadListener() {
            @Override
            public void onInterstitialLoaded(InterstitialAd interstitialAd) {
                AdsLog.d(TAG, "onInterstitialLoaded");
                // interstitial ad is ready to show
                // you can also check the ad state prior to showing by using interstitialAd.getState()
                interstitialAd.show(new IInterstitialAdShowListener() {
                    @Override
                    public void onInterstitialShowed(InterstitialAd interstitialAd) {
                        // The ad has started to show.
                        AdsLog.d(TAG, "onInterstitialShowed");
                    }

                    @Override
                    public void onInterstitialClicked(InterstitialAd interstitialAd) {
                        // The user has selected the ad.
                        AdsLog.d(TAG, "onInterstitialClicked");
                    }

                    @Override
                    public void onInterstitialClosed(InterstitialAd interstitialAd) {
                        // The ad has finished showing.
                        AdsLog.d(TAG, "onInterstitialClosed");
                        if (listener != null) listener.OnClose();
                    }

                    @Override
                    public void onInterstitialFailedShow(InterstitialAd interstitialAd, ShowError error, String msg) {
                        // An error occurred during the ad playback.
                        AdsLog.d(TAG, "onInterstitialFailedShow");
                        if (listener != null) listener.OnShowFail();
                    }
                });
            }

            @Override
            public void onInterstitialFailedLoad(InterstitialAd interstitialAd, LoadError error, String msg) {
                // interstitial ad has failed to load
                AdsLog.d(TAG, "onInterstitialFailedLoad");
                if (listener != null) listener.OnShowFail();
            }
        });
    }

    @Override
    public void forceShowPopup(Activity activity, PopupAdsListener listener) {
        showPopup(activity, listener);
    }

    @Override
    public void showReward(Activity activity, RewardAdListener listener) {
        rewardedAd = new RewardedAd(activity, mAdModel.getRewardId());
        rewardedAd.load(new IRewardedAdLoadListener() {
            @Override
            public void onRewardedLoaded(RewardedAd rewardedAd) {
                // rewarded ad is ready to show
                // you can also check the ad state prior to showing using interstitialAd.getState()
                AdsLog.d(TAG, "onRewardedLoaded");
                rewardedAd.show(new IRewardedAdShowListener() {
                    @Override
                    public void onRewardedShowed(RewardedAd rewardedAd) {
                        // Ad has played
                        AdsLog.d(TAG, "onRewardedShowed");
                    }

                    @Override
                    public void onRewardedClicked(RewardedAd rewardedAd) {
                        // Ad has been selected
                        AdsLog.d(TAG, "onRewardedClicked");
                    }

                    @Override
                    public void onRewardedClosed(RewardedAd rewardedAd) {
                        // Ad has been closed
                        try {
                            AdsLog.d(TAG, "onRewardedClosed");
                            if (listener != null) listener.OnClose();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onRewardedFailedShow(RewardedAd rewardedAd, ShowError error, String msg) {
                        // Ad has failed to play
                        // Use the message and ShowError enum to determine the ad network and cause
                        try {
                            AdsLog.d(TAG, "onRewardedFailedShow");
                            if (listener != null) listener.OnShowFail();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onUserRewarded(RewardedAd rewardedAd, IReward reward) {
                        // A reward can be issued based on the reward callback.
                        // Timing of this event can vary depending on the ad network to serve the impression.
                        try {
                            AdsLog.d(TAG, "onUserRewarded");
                            if (listener != null) listener.OnRewarded();
                        } catch (Exception e) {

                        }
                    }
                });
            }


            @Override
            public void onRewardedFailedLoad(RewardedAd rewardedAd, LoadError error, String msg) {
                // ad has failed to show
                // use the message and ShowError enum to determine ad network and cause
                try {
                    AdsLog.d(TAG, "onRewardedFailedLoad");
                    if (listener != null) listener.OnShowFail();
                } catch (Exception e) {

                }
            }
        });
    }
}
