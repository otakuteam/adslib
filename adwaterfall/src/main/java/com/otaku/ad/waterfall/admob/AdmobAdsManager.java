package com.otaku.ad.waterfall.admob;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;


public class AdmobAdsManager extends AdsPlatform {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private InterstitialAd popupAd;
    private RewardedAd rewardAd;
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

        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        loadPopupAd();
        loadRewardAd();
    }

    private void loadPopupAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                mContext,
                mAdModel.getPopupId(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        popupAd = interstitialAd;
                        AdsLog.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        popupAd = null;
                                        if (adPopupListener != null)
                                            adPopupListener.OnClose();
                                        loadPopupAd();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        popupAd = null;
                                        AdsLog.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        AdsLog.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        AdsLog.i(TAG, loadAdError.getMessage());
                        popupAd = null;
                        if (isPopupReloaded == false) {
                            isPopupReloaded = true;
                            loadPopupAd();
                        }
                    }
                });
    }

    private void loadRewardAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(mContext, mAdModel.getRewardId(),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        AdsLog.d(TAG, loadAdError.getMessage());
                        rewardAd = null;
                        if (isRewardReloaded == false) {
                            isRewardReloaded = true;
                            loadRewardAd();
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardAd = rewardedAd;
                        AdsLog.d(TAG, "Ad was loaded.");
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                AdsLog.d(TAG, "Ad was shown.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                rewardAd = null;
                                AdsLog.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                AdsLog.d(TAG, "Ad was dismissed.");
                                rewardAd = null;
                                if (adRewardListener != null)
                                    adRewardListener.OnClose();
                                loadRewardAd();
                            }
                        });
                    }
                });
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        AdView adView = new AdView(mContext);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(mAdModel.getBannerId());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                listener.OnLoadFail();
                AdsLog.d(TAG, "onAdFailedToLoad");
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
        AdsLog.i(TAG, "showPopup");
        if (popupAd != null) {
            isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
            popupAd.show((Activity) mContext);
        } else {
            loadPopupAd();
            if (adPopupListener != null)
                adPopupListener.OnShowFail();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        adRewardListener = listener;
        if (rewardAd != null) {
            isRewardReloaded = false;
            rewardAd.show((Activity) mContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    AdsLog.d(TAG, "The user earned the reward.");
                    if (adRewardListener != null)
                        adRewardListener.OnRewarded();
                    loadRewardAd();
                }
            });
        } else {
            loadRewardAd();
            if (adRewardListener != null)
                adRewardListener.OnShowFail();
        }
    }
}
