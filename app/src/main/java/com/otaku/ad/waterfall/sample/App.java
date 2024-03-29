package com.otaku.ad.waterfall.sample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.otaku.ad.waterfall.AdsConstants;
import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.NotSupportPlatformException;
import com.otaku.ad.waterfall.listener.OpenAdsListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;

public class App extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private Activity currentActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            AdsManager.getInstance().init(this, false,
//                    new AdModel(AdsConstants.UNITY,
//                            "3617642",
//                            "banner",
//                            "video",
//                            "video",
//                            "")
                    new AdModel(AdsConstants.FACEBOOK,
                            "",
                            "486294252545927_503710067471012",
                            "486294252545927_503711464137539",
                            "",
                            "")
            );
        } catch (NotSupportPlatformException e) {
            e.printStackTrace();
        }
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * LifecycleObserver method that shows the app open ad when the app moves to foreground.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        AdsLog.i("SplashActivity_openads", "onMoveToForeground");
        AdsManager.getInstance().showOpenAdIfAvailable(currentActivity);
    }

    /**
     * ActivityLifecycleCallback methods.
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!AdsManager.getInstance().isShowingOpenAd()) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    /**
     * Shows an app open ad.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OpenAdsListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        AdsLog.i("SplashActivity_openads", "showAdIfAvailable");
        AdsManager.getInstance().showOpenAdIfAvailable(activity, onShowAdCompleteListener);
    }
}
