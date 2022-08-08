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
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.OpenAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;

import org.jetbrains.annotations.NotNull;

import java.util.Date;


public class AdmobAdsManager extends AdsPlatform {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private InterstitialAd popupAd;
    private RewardedAd rewardAd;
    private PopupAdsListener adPopupListener;
    private RewardAdListener adRewardListener;
    private boolean isPopupReloaded = false;
    private boolean isRewardReloaded = false;
    private long mPreviousTime = 0;

    //for open ads
    public boolean isLoadingAd = false;
    public boolean isShowingAd = false;
    private long loadTime = 0;
    private AppOpenAd appOpenAd;
    private OpenAdsListener adOpenListener;

    public AdmobAdsManager(AdModel adModel) {
        this.mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mContext = context;

        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadPopupAd();
        loadRewardAd();
        loadOpenAd();
    }

    private void loadPopupAd() {
        AdsLog.i(TAG, "Admob - loadInterstitialAd()");
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
                                        AdsLog.d(TAG, "The ad was dismissed.");
                                        loadPopupAd();
                                        if (adPopupListener != null) adPopupListener.OnClose();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        popupAd = null;
                                        AdsLog.d(TAG, "The ad failed to show.");
                                        loadPopupAd();
                                        if (adPopupListener != null) adPopupListener.OnShowFail();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        AdsLog.d(TAG, "The ad was shown.");
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
                        if (adPopupListener != null) adPopupListener.OnShowFail();
                    }
                });
    }

    private void loadRewardAd() {
        AdsLog.i(TAG, "Admob - loadReward()");
        if (rewardAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    mContext,
                    mAdModel.getRewardId(),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            AdsLog.d(TAG, loadAdError.getMessage());
                            rewardAd = null;
                            if (isRewardReloaded == false) {
                                isRewardReloaded = true;
                                loadRewardAd();
                            }
                            if (adRewardListener != null)
                                adRewardListener.OnShowFail();

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            rewardAd = rewardedAd;
                        }
                    });
        }
    }

    @Override
    public void showBanner(Activity activity, ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        AdView adView = new AdView(mContext);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(mAdModel.getBannerId());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
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
    public void showPopup(Activity activity, PopupAdsListener listener) {
        adPopupListener = listener;
        AdsLog.i(TAG, "admob-showPopup");
        if (popupAd != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mPreviousTime >= mAdModel.getPopupLimitTime()) {
                isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
                popupAd.show(activity);
            } else {
                if (adPopupListener != null)
                    adPopupListener.OnShowFail();
            }
        } else {
            loadPopupAd();
            if (adPopupListener != null)
                adPopupListener.OnShowFail();
        }
    }

    @Override
    public void forceShowPopup(Activity activity, PopupAdsListener listener) {
        adPopupListener = listener;
        AdsLog.i(TAG, "forceShowPopup");
        if (popupAd != null) {
            isPopupReloaded = false; //Reset the reload-flag everytime showing an ad
            popupAd.show(activity);
        } else {
            loadPopupAd();
            if (adPopupListener != null)
                adPopupListener.OnShowFail();
        }
    }

    @Override
    public void showReward(Activity activity, RewardAdListener listener) {
        adRewardListener = listener;
        //show reward ad
        AdsLog.d(TAG, "check can show reward");
        if (rewardAd != null) {
            AdsLog.d(TAG, "canshow");
            isRewardReloaded = false;
            rewardAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            AdsLog.d(TAG, "onAdShowedFullScreenContent");
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            AdsLog.d(TAG, "onAdFailedToShowFullScreenContent");
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            rewardAd = null;
                            loadRewardAd();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            AdsLog.d(TAG, "onAdDismissedFullScreenContent");
                            rewardAd = null;
                            loadRewardAd();
                        }
                    });
            rewardAd.show(
                    activity,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            AdsLog.d(TAG, "onRewarded");
                            isRewardReloaded = false;
                            loadRewardAd();
                            if (adRewardListener != null)
                                adRewardListener.OnRewarded();
                            AdsLog.d(TAG, "onRewarded: " + rewardItem);
                        }
                    });
        } else {
            if (listener != null)
                listener.OnShowFail();
        }
    }

    //############### Open ads ###################
    @Override
    public boolean isOpenAdsAvailable() {
        if (mAdModel.getOpenId() == null || mAdModel.getOpenId().length() == 0) return false;
        return true;
    }

    private void loadOpenAd() {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isOpenAdsAvailable()) {
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    mContext,
                    mAdModel.getOpenId(),
                    request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        /**
                         * Called when an app open ad has loaded.
                         *
                         * @param ad the loaded app open ad.
                         */
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                            AdsLog.d(TAG, "loadOpenAd onAdLoaded.");
                        }

                        /**
                         * Called when an app open ad has failed to load.
                         *
                         * @param loadAdError the error.
                         */
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                            AdsLog.d(TAG, "loadOpenAd onAdFailedToLoad: " + loadAdError.getMessage());
                        }
                    });
        }
    }

    /**
     * Check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Check if ad exists and can be shown.
     */
    private boolean isAdAvailable() {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    public void showOpenAdIfAvailable(@NonNull final Activity activity) {
        showOpenAdIfAvailable(
                activity,
                new OpenAdsListener() {
                    @Override
                    public void OnShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                });
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showOpenAdIfAvailable(
            @NonNull final Activity activity,
            @NonNull OpenAdsListener onShowAdCompleteListener) {
        adOpenListener = onShowAdCompleteListener;
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            AdsLog.d(TAG, "showAdIfAvailable: The app open ad is already showing.");
            return;
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            AdsLog.d(TAG, "showAdIfAvailable: The app open ad is not ready yet.");
            if (adOpenListener != null) adOpenListener.OnShowAdComplete();
            loadOpenAd();
            return;
        }

        AdsLog.d(TAG, "showAdIfAvailable: Will show ad.");

        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null;
                        isShowingAd = false;
                        AdsLog.d(TAG, "showAdIfAvailable: onAdDismissedFullScreenContent.");
                        if (adOpenListener != null) adOpenListener.OnShowAdComplete();
                        loadOpenAd();
                    }

                    /** Called when fullscreen content failed to show. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        appOpenAd = null;
                        isShowingAd = false;
                        AdsLog.d(TAG, "showAdIfAvailable: onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        if (adOpenListener != null) adOpenListener.OnShowAdComplete();
                        loadOpenAd();
                    }

                    /** Called when fullscreen content is shown. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        AdsLog.d(TAG, "showAdIfAvailable: onAdShowedFullScreenContent.");
                    }
                });

        isShowingAd = true;
        appOpenAd.show(activity);
    }

    public boolean isShowingOpenAd() {
        return isShowingAd;
    }
}
