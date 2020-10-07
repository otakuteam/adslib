package com.otaku.ad.waterfall.ironsource;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.LogUtil;

public class IronSourceManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private PopupAdsListener mPopupListener;
    private RewardAdListener mRewardAdListener;
    private boolean isPopupReloaded = false;
    private IronSourceBannerLayout ironBanner;

    public IronSourceManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mActivity = (Activity) context;
        IronSource.init(mActivity, mAdModel.getAppId());
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
            }
            /**
             invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
                LogUtil.i(TAG, "popup_onInterstitialFailed " + error);
                if (isPopupReloaded == false) {
                    isPopupReloaded = true;
                    IronSource.loadInterstitial();
                }
            }
            /**
             Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
            }
            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
                LogUtil.i(TAG, "popup_onInterstitialDismissed");
                if (mPopupListener != null)
                    mPopupListener.OnClose();
                IronSource.loadInterstitial();
            }
            /*
             * Invoked when the ad was opened and shown successfully.
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
            }
            /**
             * Invoked when Interstitial ad failed to show.
             // @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
                if(mPopupListener!=null)
                    mPopupListener.OnShowFail();
            }
            /*
             * Invoked when the end user clicked on the interstitial ad.
             */
            @Override
            public void onInterstitialAdClicked() {
            }
        });

        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
            /**
             * Invoked when the RewardedVideo ad view has opened.
             * Your Activity will lose focus. Please avoid performing heavy
             * tasks till the video ad will be closed.
             */
            @Override
            public void onRewardedVideoAdOpened() {
            }
            /*Invoked when the RewardedVideo ad view is about to be closed.
            Your activity will now regain its focus.*/
            @Override
            public void onRewardedVideoAdClosed() {
                if (mRewardAdListener != null)
                    mRewardAdListener.OnClose();
            }
            /**
             * Invoked when there is a change in the ad availability status.
             *
             * @param - available - value will change to true when rewarded videos are *available.
             *          You can then show the video by calling showRewardedVideo().
             *          Value will change to false when no videos are available.
             */
            @Override
            public void onRewardedVideoAvailabilityChanged(boolean available) {
                //Change the in-app 'Traffic Driver' state according to availability.
            }
            /**
             /**
             * Invoked when the user completed the video and should be rewarded.
             * If using server-to-server callbacks you may ignore this events and wait *for the callback from the ironSource server.
             *
             * @param - placement - the Placement the user completed a video from.
             */
            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {
                /** here you can reward the user according to the given amount.
                 String rewardName = placement.getRewardName();
                 int rewardAmount = placement.getRewardAmount();
                 */
                if (mRewardAdListener != null)
                    mRewardAdListener.OnRewarded();
            }
            /* Invoked when RewardedVideo call to show a rewarded video has failed
             * IronSourceError contains the reason for the failure.
             */
            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError error) {
                if (mRewardAdListener != null)
                    mRewardAdListener.OnShowFail();
            }
            /*Invoked when the end user clicked on the RewardedVideo ad
             */
            @Override
            public void onRewardedVideoAdClicked(Placement placement){
            }

            @Override
            public void onRewardedVideoAdStarted(){
            }
            /* Invoked when the video ad finishes plating. */
            @Override
            public void onRewardedVideoAdEnded(){
            }
        });

        ironBanner = IronSource.createBanner(mActivity, ISBannerSize.BANNER);
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        if(ironBanner!=null) {
            IronSource.destroyBanner(ironBanner);
            ironBanner = IronSource.createBanner(mActivity, ISBannerSize.BANNER);
            ironBanner.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    // Called after a banner ad has been successfully loaded
                }

                @Override
                public void onBannerAdLoadFailed(IronSourceError error) {
                    LogUtil.i(TAG, "onBannerAdLoadFailed");
                    listener.OnLoadFail();
                }

                @Override
                public void onBannerAdClicked() {
                    // Called after a banner has been clicked.
                }

                @Override
                public void onBannerAdScreenPresented() {
                    // Called when a banner is about to present a full screen content.
                }

                @Override
                public void onBannerAdScreenDismissed() {
                    // Called after a full screen content has been dismissed
                }

                @Override
                public void onBannerAdLeftApplication() {
                    // Called when a user would be taken out of the application context.
                }
            });
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            banner.addView(ironBanner, params);
            IronSource.loadBanner(ironBanner, mAdModel.getBannerId());
        }else{
            listener.OnLoadFail();
        }
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        mPopupListener = listener;
        LogUtil.i(TAG, "showPopup");
        if (IronSource.isInterstitialReady()) {
            LogUtil.i(TAG, "showPopup ready");
            isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
            IronSource.showInterstitial(mAdModel.getPopupId());
        } else {
            LogUtil.i(TAG, "showPopup fail");
            if (mPopupListener != null) {
                mPopupListener.OnShowFail();
            }
            IronSource.loadInterstitial();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        mRewardAdListener = listener;
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo(mAdModel.getRewardId());
        } else {
            if (mRewardAdListener != null)
                mRewardAdListener.OnShowFail();
        }
    }
}
