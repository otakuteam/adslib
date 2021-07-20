package com.otaku.ad.waterfall.inhouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.gms.ads.MobileAds;
import com.otaku.ad.waterfall.AdsPlatform;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.util.AdsLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class InhouseAdsManager extends AdsPlatform {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private PopupAdsListener adPopupListener;
    private RewardAdListener adRewardListener;
    private ArrayList<InhouseAds> banners, popups, rewards;
    public static String CLOSE_POPUP_ACTION = "com.inhouse.close.popup";
    public static String CLOSE_REWARD_ACTION = "com.inhouse.close.reward";
    public static String REWARDED_ACTION = "com.inhouse.rewarded";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CLOSE_POPUP_ACTION.equals(intent.getAction())) {
                if (adPopupListener != null) adPopupListener.OnClose();
            } else if (CLOSE_REWARD_ACTION.equals(intent.getAction())) {
                if (adRewardListener != null) adRewardListener.OnClose();
            } else if (REWARDED_ACTION.equals(intent.getAction())) {
                if (adRewardListener != null) adRewardListener.OnRewarded();
            }
        }
    };

    public InhouseAdsManager(AdModel adModel) {
        this.mAdModel = adModel;
    }

    @Override
    public void init(Context context, boolean testMode) {
        mContext = context;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CLOSE_POPUP_ACTION);
        intentFilter.addAction(CLOSE_REWARD_ACTION);
        intentFilter.addAction(REWARDED_ACTION);
        mContext.registerReceiver(broadcastReceiver, intentFilter);

        MobileAds.initialize(mContext, mAdModel.getAppId());
        banners = parseAds(mAdModel.getBannerId());
        popups = parseAds(mAdModel.getPopupId());
        rewards = parseAds(mAdModel.getRewardId());

        for (InhouseAds ads : banners) {
            File file = new File(context.getFilesDir() + "/inhouse/banners/" + ads.id + ".jpg");
            if (!file.exists()) {
                AdsLog.d(TAG, "downloading_banner...");
                PRDownloader.download(ads.resourceUrl, context.getFilesDir() + "/inhouse/banners", ads.resourceUrl.hashCode() + ".jpg")
                        .build()
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                            }

                            @Override
                            public void onError(Error error) {

                            }
                        });
            }
        }
        for (InhouseAds ads : popups) {
            File file = new File(context.getFilesDir() + "/inhouse/popups/" + ads.id + ".jpg");
            if (!file.exists()) {
                AdsLog.d(TAG, "downloading_popup...");
                PRDownloader.download(ads.resourceUrl, context.getFilesDir() + "/inhouse/popups", ads.resourceUrl.hashCode() + ".jpg")
                        .build()
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                            }

                            @Override
                            public void onError(Error error) {

                            }
                        });
            }
        }
        for (InhouseAds ads : rewards) {
            File file = new File(context.getFilesDir() + "/inhouse/rewards/" + ads.id + ".mp4");
            if (!file.exists()) {
                AdsLog.d(TAG, "downloading_reward...");
                PRDownloader.download(ads.resourceUrl, context.getFilesDir() + "/inhouse/rewards", ads.resourceUrl.hashCode() + ".mp4")
                        .build()
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                            }

                            @Override
                            public void onError(Error error) {

                            }
                        });
            }
        }
    }

    @Override
    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        try {
            if (banner != null) banner.removeAllViews();
            InhouseAds bannerAd = banners.get(new Random().nextInt(banners.size()));
            File file = new File(mContext.getFilesDir(), "/inhouse/banners/" + bannerAd.id + ".jpg");
            if (file.exists()) {
                int height = (int) (50 * ((float) mContext.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                ImageView adView = new ImageView(mContext);
                LinearLayout.LayoutParams vp =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                height);
                adView.setLayoutParams(vp);
                adView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                adView.setImageBitmap(bitmap);
                AdsLog.d(TAG, "show_banner: " + adView.getWidth() + " " + adView.getHeight());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                banner.addView(adView, params);
                AdsLog.d(TAG, "show_banner: " + adView.getWidth() + " " + adView.getHeight());

                AdsLog.d(TAG, "show_banner: " + adView.getWidth() + " " + adView.getHeight());
            } else {
                listener.OnLoadFail();
            }
        } catch (Exception e) {
            listener.OnLoadFail();
        }
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        try {
            adPopupListener = listener;
            AdsLog.d(TAG, "showPopup");
            InhouseAds popupAd = popups.get(new Random().nextInt(popups.size()));
            File file = new File(mContext.getFilesDir(), "/inhouse/popups/" + popupAd.id + ".jpg");
            if (file.exists()) {
                Intent intent = new Intent(mContext, InhouseAdsActivity.class);
                intent.putExtra("type", InhouseAds.POPUP);
                intent.putExtra("resource", file.getAbsolutePath());
                intent.putExtra("action", popupAd.actionUrl);
                mContext.startActivity(intent);
            } else {
                listener.OnShowFail();
            }
        } catch (Exception e) {
            listener.OnShowFail();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        try {
            adRewardListener = listener;
            AdsLog.d(TAG, "showReward");
            InhouseAds rewardAd = rewards.get(new Random().nextInt(rewards.size()));
            File file = new File(mContext.getFilesDir(), "/inhouse/rewards/" + rewardAd.id + ".mp4");
            if (file.exists()) {
                AdsLog.d(TAG, "showReward " + file.getAbsolutePath());
                Intent intent = new Intent(mContext, InhouseAdsActivity.class);
                intent.putExtra("type", InhouseAds.REWARD);
                intent.putExtra("resource", file.getAbsolutePath());
                intent.putExtra("action", rewardAd.actionUrl);
                mContext.startActivity(intent);
            } else {
                listener.OnShowFail();
            }
        } catch (Exception e) {
            listener.OnShowFail();
        }
    }

    private ArrayList<InhouseAds> parseAds(String json) {
        ArrayList<InhouseAds> ads = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String resource = obj.getString("resource");
                String action = obj.getString("action");
                ads.add(new InhouseAds(String.valueOf(resource.hashCode()), resource, action));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ads;
    }
}
