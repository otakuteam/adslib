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
                adPopupListener.OnClose();
            } else if (CLOSE_REWARD_ACTION.equals(intent.getAction())) {
                adRewardListener.OnClose();
            } else if (REWARDED_ACTION.equals(intent.getAction())) {
                adRewardListener.OnRewarded();
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
            File file = new File(context.getFilesDir() + "/inhouse/banners/" + ads.id + ".png");
            if (!file.exists()) {
                PRDownloader.download(ads.resourceUrl, context.getFilesDir() + "/inhouse/banners", ads.id + ".png")
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
            File file = new File(context.getFilesDir() + "/inhouse/banners/" + ads.id + ".png");
            if (!file.exists()) {
                PRDownloader.download(ads.resourceUrl, context.getFilesDir() + "/inhouse/popups", ads.id + ".png")
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
                PRDownloader.download(ads.resourceUrl, context.getFilesDir() + "/inhouse/rewards", ads.id + ".mp4")
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
        if (banner != null) banner.removeAllViews();
        File[] files = new File(mContext.getFilesDir(), "/inhouse/banners").listFiles();
        if (files.length == 0) {
            listener.OnLoadFail();
        } else {
            Random random = new Random();
            File file = files[random.nextInt(files.length)];
            if ("png".equals(getFileExtension(file))) {
                int height = (int) (50 * ((float) mContext.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                ImageView adView = new ImageView(mContext);
                LinearLayout.LayoutParams vp =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                height);
                adView.setLayoutParams(vp);
                //adView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                adView.setImageBitmap(bitmap);
                AdsLog.d(TAG, "show_banner: " + adView.getWidth() + " " + adView.getHeight());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                banner.addView(adView, params);
                AdsLog.d(TAG, "show_banner: " + adView.getWidth() + " " + adView.getHeight());

                AdsLog.d(TAG, "show_banner: " + adView.getWidth() + " " + adView.getHeight());
            } else {
                listener.OnLoadFail();
            }
        }
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        adPopupListener = listener;
        AdsLog.d(TAG, "showPopup");
        File[] files = new File(mContext.getFilesDir(), "/inhouse/popups").listFiles();
        if (files.length == 0) {
            adPopupListener.OnShowFail();
        } else {
            Random random = new Random();
            int id = random.nextInt(files.length);
            File file = files[id];
            AdsLog.d(TAG, "showPopup " + getFileExtension(file));
            if ("png".equals(getFileExtension(file))) {
                AdsLog.d(TAG, "file_name: " + getFileNameWithouExtension(file));
                InhouseAds ads = findAdsById(popups, getFileNameWithouExtension(file));
                if (ads == null) {
                    AdsLog.d(TAG, "ads_null");
                    adPopupListener.OnShowFail();
                } else {
                    AdsLog.d(TAG, "showPopup " + file.getAbsolutePath());
                    Intent intent = new Intent(mContext, InhouseAdsActivity.class);
                    intent.putExtra("type", InhouseAds.POPUP);
                    intent.putExtra("resource", file.getAbsolutePath());
                    intent.putExtra("action", ads.actionUrl);
                    mContext.startActivity(intent);
                }
            } else {
                listener.OnShowFail();
            }
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        adRewardListener = listener;
        AdsLog.d(TAG, "showReward");
        File[] files = new File(mContext.getFilesDir(), "/inhouse/rewards").listFiles();
        if (files.length == 0) {
            adRewardListener.OnShowFail();
        } else {
            Random random = new Random();
            int id = random.nextInt(files.length);
            File file = files[id];
            AdsLog.d(TAG, "showPopup " + getFileExtension(file));
            if ("mp4".equals(getFileExtension(file))) {
                AdsLog.d(TAG, "file_name: " + getFileNameWithouExtension(file));
                InhouseAds ads = findAdsById(rewards, getFileNameWithouExtension(file));
                if (ads == null) {
                    AdsLog.d(TAG, "ads_null");
                    adRewardListener.OnShowFail();
                } else {
                    AdsLog.d(TAG, "showPopup " + file.getAbsolutePath());
                    Intent intent = new Intent(mContext, InhouseAdsActivity.class);
                    intent.putExtra("type", InhouseAds.REWARD);
                    intent.putExtra("resource", file.getAbsolutePath());
                    intent.putExtra("action", ads.actionUrl);
                    mContext.startActivity(intent);
                }
            } else {
                listener.OnShowFail();
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private String getFileNameWithouExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(0, lastIndexOf);
    }

    private ArrayList<InhouseAds> parseAds(String json) {
        ArrayList<InhouseAds> ads = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                String resource = obj.getString("resource");
                String action = obj.getString("action");
                ads.add(new InhouseAds(id, resource, action));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ads;
    }

    private InhouseAds findAdsById(ArrayList<InhouseAds> ads, String id) {
        for (int i = 0; i < ads.size(); i++) {
            if (id.equals(ads.get(i).id)) return ads.get(i);
        }
        return null;
    }
}
