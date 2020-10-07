package com.otaku.ad.waterfall.mopub;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.LogUtil;

import java.util.Set;

import static com.mopub.common.logging.MoPubLog.LogLevel.DEBUG;

public class MopubManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private PopupAdsListener mPopupListener;
    private RewardAdListener mRewardAdListener;
    private MoPubInterstitial mInterstitial;
    private boolean isPopupReloaded = false;
    private boolean isRewardReloaded = false;

    public MopubManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mActivity = (Activity) context;
        final SdkConfiguration.Builder configBuilder = new SdkConfiguration.Builder(mAdModel.getBannerId());
        if(testMode){
            configBuilder.withLogLevel(DEBUG);
        }
        MoPub.initializeSdk(mActivity, configBuilder.build(), new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                LogUtil.i(TAG, "onInitializationFinished");
                mInterstitial = new MoPubInterstitial(mActivity, mAdModel.getPopupId());
                mInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                    @Override
                    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                        LogUtil.i(TAG, "popup_onInterstitialLoaded");
                    }

                    @Override
                    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                        LogUtil.i(TAG, "popup_onInterstitialFailed " + errorCode);
                        if (isPopupReloaded == false) {
                            isPopupReloaded = true;
                            mInterstitial.load();
                        }
                    }

                    @Override
                    public void onInterstitialShown(MoPubInterstitial interstitial) {
                        LogUtil.i(TAG, "popup_onInterstitialShown");
                    }

                    @Override
                    public void onInterstitialClicked(MoPubInterstitial interstitial) {
                        LogUtil.i(TAG, "popup_onInterstitialClicked");
                    }

                    @Override
                    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                        LogUtil.i(TAG, "popup_onInterstitialDismissed");
                        if (mPopupListener != null)
                            mPopupListener.OnClose();
                        mInterstitial.load();
                    }
                });
                mInterstitial.load();

                MoPubRewardedVideos.setRewardedVideoListener(new MoPubRewardedVideoListener() {
                    @Override
                    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                        LogUtil.i(TAG, "reward_onRewardedVideoLoadSuccess");
                    }

                    @Override
                    public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                        LogUtil.i(TAG, "reward_onRewardedVideoLoadFailure");
                        if (isRewardReloaded == false) {
                            isRewardReloaded = true;
                            MoPubRewardedVideos.loadRewardedVideo(mAdModel.getRewardId());
                        }
                    }

                    @Override
                    public void onRewardedVideoStarted(@NonNull String adUnitId) {
                        LogUtil.i(TAG, "reward_onRewardedVideoStarted");
                    }

                    @Override
                    public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                        LogUtil.i(TAG, "reward_onRewardedVideoPlaybackError");
                    }

                    @Override
                    public void onRewardedVideoClicked(@NonNull String adUnitId) {
                        LogUtil.i(TAG, "reward_onRewardedVideoClicked");
                    }

                    @Override
                    public void onRewardedVideoClosed(@NonNull String adUnitId) {
                        LogUtil.i(TAG, "reward_onRewardedVideoClosed");
                        if (mRewardAdListener != null)
                            mRewardAdListener.OnClose();
                        MoPubRewardedVideos.loadRewardedVideo(mAdModel.getRewardId());
                    }

                    @Override
                    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                        LogUtil.i(TAG, "reward_onRewardedVideoCompleted");
                        if (mRewardAdListener != null)
                            mRewardAdListener.OnRewarded();
                        MoPubRewardedVideos.loadRewardedVideo(mAdModel.getRewardId());
                    }
                });
                MoPubRewardedVideos.loadRewardedVideo(mAdModel.getRewardId());
            }
        });
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        MoPubView moPubView = new MoPubView(mActivity);
        moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(@NonNull MoPubView banner) {
                LogUtil.i(TAG, "onBannerLoaded");
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                LogUtil.i(TAG, "onBannerFailed " + errorCode);
                listener.OnLoadFail();
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                LogUtil.i(TAG, "onBannerClicked");
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
                LogUtil.i(TAG, "onBannerExpanded");
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                LogUtil.i(TAG, "onBannerCollapsed");
            }
        });
        moPubView.setAdUnitId(mAdModel.getBannerId());
        moPubView.setAdSize(MoPubView.MoPubAdSize.MATCH_VIEW); // Call this if you want to set an ad size programmatically
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(moPubView, params);
        moPubView.loadAd();
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        mPopupListener = listener;
        LogUtil.i(TAG, "showPopup");
        if (mInterstitial.isReady()) {
            LogUtil.i(TAG, "showPopup ready");
            isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
            mInterstitial.show();
        } else {
            LogUtil.i(TAG, "showPopup fail");
            if (mPopupListener != null) {
                mPopupListener.OnShowFail();
            }
            mInterstitial.load();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        mRewardAdListener = listener;
        if (MoPubRewardedVideos.hasRewardedVideo(mAdModel.getRewardId())) {
            isRewardReloaded = false;
            MoPubRewardedVideos.showRewardedVideo(mAdModel.getRewardId());
        } else {
            if (mRewardAdListener != null)
                mRewardAdListener.OnShowFail();
            MoPubRewardedVideos.loadRewardedVideo(mAdModel.getRewardId());
        }
    }
}
