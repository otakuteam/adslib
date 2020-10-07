package com.otaku.ad.waterfall.unity;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.LogUtil;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityAdsManager extends AdsPlatform {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private PopupAdsListener mPopupListener;
    private RewardAdListener mRewardAdListener;

    public UnityAdsManager(AdModel adModel) {
        mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mActivity = (Activity) context;
        UnityAds.initialize(mActivity, mAdModel.getAppId(), testMode);

        UnityAds.addListener(new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {
                LogUtil.i(TAG, "onUnityAdsReady " + s);

            }

            @Override
            public void onUnityAdsStart(String s) {
                LogUtil.i(TAG, "onUnityAdsStart " + s);

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                LogUtil.i(TAG, "onUnityAdsFinish " + s);
                if (mAdModel.getPopupId().equals(s)) {
                    if (mPopupListener != null) {
                        mPopupListener.OnClose();
                    }
                } else {
                    if (finishState == UnityAds.FinishState.COMPLETED) {
                        LogUtil.i(TAG, "finishState completed");
                        if (mRewardAdListener != null) {
                            mRewardAdListener.OnRewarded();
                        }
                        // Reward the user for watching the ad to completion.
                    } else if (finishState == UnityAds.FinishState.SKIPPED) {
                        // Do not reward the user for skipping the ad.
                        LogUtil.i(TAG, "finishState skipped");
                        if (mRewardAdListener != null) {
                            mRewardAdListener.OnClose();
                        }
                    } else if (finishState == UnityAds.FinishState.ERROR) {
                        // Log an error.
                        LogUtil.i(TAG, "finishState error");
                    }
                }
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
                LogUtil.i(TAG, "onUnityAdsError " + s);
                if (mPopupListener != null) {
                    mPopupListener.OnClose();
                }
            }
        });
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        BannerView adView = new BannerView(mActivity, mAdModel.getBannerId(),
                UnityBannerSize.getDynamicSize(mActivity));
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
        adView.load();
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        mPopupListener = listener;
        LogUtil.i(TAG, "showPopup");
        if (UnityAds.isReady(mAdModel.getPopupId())) {
            LogUtil.i(TAG, "showPopup ready " + mAdModel.getPopupId());
            UnityAds.show(mActivity, mAdModel.getPopupId());
        } else {
            LogUtil.i(TAG, "showPopup fail");
            if (mPopupListener != null) {
                mPopupListener.OnShowFail();
            }
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        mRewardAdListener = listener;
        if (UnityAds.isReady(mAdModel.getRewardId())) {
            UnityAds.show(mActivity, mAdModel.getRewardId());
        } else {
            if (mRewardAdListener != null) {
                mRewardAdListener.OnShowFail();
            }
        }
    }
}
