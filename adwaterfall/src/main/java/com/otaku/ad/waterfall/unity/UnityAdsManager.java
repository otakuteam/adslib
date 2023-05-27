package com.otaku.ad.waterfall.unity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.OpenAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityAdsManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private PopupAdsListener mPopupListener;
    private RewardAdListener mRewardAdListener;

    public UnityAdsManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        UnityAds.initialize(context, mAdModel.getAppId(), testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                AdsLog.d(TAG, "onInitializationComplete");
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                AdsLog.d(TAG, "onInitializationFailed");
            }
        });
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
                listener.OnLoadFail();
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
        }, 300);
    }

    @Override
    public void showPopup(Activity activity, PopupAdsListener listener) {
        mPopupListener = listener;
        AdsLog.i(TAG, "showPopup");
        UnityAds.load(mAdModel.getPopupId(), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                AdsLog.d(TAG, "onUnityAdsAdLoaded");
                UnityAds.show(activity, mAdModel.getPopupId(), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        AdsLog.d(TAG, "onUnityAdsShowFailure");
                        if (mPopupListener != null) {
                            mPopupListener.OnShowFail();
                        }
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                        AdsLog.d(TAG, "onUnityAdsShowStart");
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {
                        AdsLog.d(TAG, "onUnityAdsShowClick");
                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        AdsLog.d(TAG, "onUnityAdsShowComplete");
                        if (mPopupListener != null) {
                            mPopupListener.OnClose();
                        }
                    }
                });
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                AdsLog.d(TAG, "onUnityAdsFailedToLoad");
                if (mPopupListener != null) {
                    mPopupListener.OnShowFail();
                }
            }
        });
    }

    @Override
    public void forceShowPopup(Activity activity, PopupAdsListener listener) {
        showPopup(activity, listener);
    }

    @Override
    public void showReward(Activity activity, RewardAdListener listener) {
        AdsLog.i(TAG, "showReward");
        mRewardAdListener = listener;
        UnityAds.load(mAdModel.getRewardId(), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                AdsLog.d(TAG, "onUnityAdsAdLoaded");
                UnityAds.show(activity, mAdModel.getPopupId(), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        AdsLog.d(TAG, "onUnityAdsShowFailure");
                        if (mRewardAdListener != null) {
                            mRewardAdListener.OnShowFail();
                        }
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                        AdsLog.d(TAG, "onUnityAdsShowStart");
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {
                        AdsLog.d(TAG, "onUnityAdsShowClick");
                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        AdsLog.d(TAG, "onUnityAdsShowComplete");
                        if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                            // Reward the user for watching the ad to completion
                            AdsLog.d(TAG, "rewarded");
                            if (mRewardAdListener != null) {
                                mRewardAdListener.OnRewarded();
                            }
                        } else {
                            // Do not reward the user for skipping the ad
                            AdsLog.d(TAG, "skipped");
                            if (mRewardAdListener != null) {
                                mRewardAdListener.OnClose();
                            }
                        }

                    }
                });
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                AdsLog.d(TAG, "onUnityAdsFailedToLoad");
                if (mRewardAdListener != null) {
                    mRewardAdListener.OnShowFail();
                }
            }
        });
    }

    @Override
    public boolean isOpenAdsAvailable() {
        return false;
    }

    @Override
    public boolean isShowingOpenAd() {
        return false;
    }

    @Override
    public void showOpenAdIfAvailable(Activity activity) {

    }

    @Override
    public void showOpenAdIfAvailable(Activity activity, OpenAdsListener onShowAdCompleteListener) {

    }
}