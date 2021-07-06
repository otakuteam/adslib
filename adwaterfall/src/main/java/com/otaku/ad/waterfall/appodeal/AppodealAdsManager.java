package com.otaku.ad.waterfall.appodeal;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.utils.Log;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;

public class AppodealAdsManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private BannerAdsListener mBannerListener;
    private PopupAdsListener mPopupListener;
    private RewardAdListener mRewardAdListener;

    public AppodealAdsManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mActivity = (Activity) context;
        Appodeal.disableLocationPermissionCheck(); //To disable toast message "ACCESS_COARSE_LOCATION permission is missing"
        Appodeal.initialize(mActivity, mAdModel.getAppId(), Appodeal.BANNER | Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO, false);
        //Appodeal.setTesting(testMode);
        Appodeal.setLogLevel(Log.LogLevel.verbose);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {

            }

            @Override
            public void onBannerFailedToLoad() {
                if(mBannerListener!=null)
                mBannerListener.OnLoadFail();
            }

            @Override
            public void onBannerShown() {

            }

            @Override
            public void onBannerShowFailed() {

            }

            @Override
            public void onBannerClicked() {

            }

            @Override
            public void onBannerExpired() {

            }
        });

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                // Called when interstitial is loaded
            }

            @Override
            public void onInterstitialFailedToLoad() {
                // Called when interstitial failed to load
                if(mPopupListener!=null)
                mPopupListener.OnShowFail();
            }

            @Override
            public void onInterstitialShown() {
                // Called when interstitial is shown
            }

            @Override
            public void onInterstitialShowFailed() {
                // Called when interstitial show failed
                if(mPopupListener!=null)
                mPopupListener.OnShowFail();
            }

            @Override
            public void onInterstitialClicked() {
                // Called when interstitial is clicked
            }

            @Override
            public void onInterstitialClosed() {
                // Called when interstitial is closed
                if(mPopupListener!=null)
                mPopupListener.OnClose();
            }

            @Override
            public void onInterstitialExpired() {
                // Called when interstitial is expired
            }
        });

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {
                // Called when rewarded video is loaded
            }

            @Override
            public void onRewardedVideoFailedToLoad() {
                // Called when rewarded video failed to load
                if(mRewardAdListener!=null)
                mRewardAdListener.OnShowFail();
            }

            @Override
            public void onRewardedVideoShown() {
                // Called when rewarded video is shown
            }

            @Override
            public void onRewardedVideoShowFailed() {
                // Called when rewarded video show failed
                if(mRewardAdListener!=null)
                mRewardAdListener.OnShowFail();
            }

            @Override
            public void onRewardedVideoClicked() {
                // Called when rewarded video is clicked
            }

            @Override
            public void onRewardedVideoFinished(double amount, String name) {
                // Called when rewarded video is viewed until the end
                if(mRewardAdListener!=null)
                mRewardAdListener.OnRewarded();
            }

            @Override
            public void onRewardedVideoClosed(boolean finished) {
                // Called when rewarded video is closed
                if(mRewardAdListener!=null)
                mRewardAdListener.OnClose();
            }

            @Override
            public void onRewardedVideoExpired() {
                // Called when rewarded video is expired
            }
        });
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        mBannerListener = listener;
        if (banner != null) banner.removeAllViews();
        BannerView adView = Appodeal.getBannerView(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(adView, params);
        Appodeal.show(mActivity, Appodeal.BANNER_VIEW);
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        mPopupListener = listener;
        AdsLog.i(TAG, "showPopup");
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            AdsLog.i(TAG, "showPopup ready " + mAdModel.getPopupId());
            Appodeal.show(mActivity, Appodeal.INTERSTITIAL);
        } else {
            AdsLog.i(TAG, "showPopup fail");
            if (mPopupListener != null) {
                mPopupListener.OnShowFail();
            }
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        AdsLog.i(TAG, "showReward");
        mRewardAdListener = listener;
        if (Appodeal.isInitialized(Appodeal.REWARDED_VIDEO)) {
            Appodeal.show(mActivity, Appodeal.REWARDED_VIDEO);
        } else {
            if (mRewardAdListener != null) {
                mRewardAdListener.OnShowFail();
            }
        }
    }
}
