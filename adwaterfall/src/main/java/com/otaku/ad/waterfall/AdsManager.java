package com.otaku.ad.waterfall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otaku.ad.waterfall.admob.AdmobAdsManager;
import com.otaku.ad.waterfall.facebook.FanManager;
import com.otaku.ad.waterfall.ironsource.IronSourceManager;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.mopub.MopubManager;
import com.otaku.ad.waterfall.unity.UnityAdsManager;
import com.otaku.ad.waterfall.util.LogUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdsManager implements IAdManager {
    private final String TAG = "AD_DEBUG_" + getClass().getSimpleName();
    private boolean mEnableAd = true;
    private long mPreviousTime = 0;
    private boolean mShow = false;
    private SharedPreferences mPref;
    private ArrayList<AdsPlatform> mAdsPlatform = new ArrayList<>();

    private AdsManager() {
    }

    private static AdsManager instance;

    public static AdsManager getInstance() {
        if (instance == null) {
            instance = new AdsManager();
        }
        return instance;
    }


    @Override
    public void init(Context context, boolean testMode, AdModel... models) throws NotSupportPlatformException {
        LogUtil.isDebug = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        mPref = context.getSharedPreferences("ads_config", Context.MODE_PRIVATE);
        mEnableAd = mPref.getBoolean(AdsConstants.PREF_ENABLE_AD, true);
        List<String> supportPlatforms = Arrays.asList(new String[]{AdsConstants.ADMOB, AdsConstants.UNITY,
                AdsConstants.FACEBOOK, AdsConstants.MOPUB, AdsConstants.IRONSOURCE});
        ArrayList<String> waterFall = getWaterfall();
        if (waterFall == null || waterFall.isEmpty()) {
            waterFall= new ArrayList<>();
            for (AdModel adModel : models) {
                if (!supportPlatforms.contains(adModel.getName()))
                    throw new NotSupportPlatformException("not support platform");
                waterFall.add(adModel.getName());
                saveAdModel(adModel);
            }
            saveWaterFall(waterFall);
        }

        for (int i = 0; i < waterFall.size(); i++) {
            LogUtil.i(TAG, "waterfall: " + waterFall.get(i));
            mAdsPlatform.add(getAdsPlatformByName(waterFall.get(i), testMode));
        }
        for (AdsPlatform platform : mAdsPlatform) {
            platform.init(context, testMode);
        }
    }

    @Override
    public void showBanner(ViewGroup banner) {
        if (canShowBanner()) {
            try {
                switch (mAdsPlatform.size()) {
                    case 1:
                        mAdsPlatform.get(0).showBanner(banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {

                            }
                        });
                        return;
                    case 2:
                        mAdsPlatform.get(0).showBanner(banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {
                                mAdsPlatform.get(1).showBanner(banner, new BannerAdsListener() {
                                    @Override
                                    public void OnLoadFail() {

                                    }
                                });
                            }
                        });
                        return;
                    case 3:
                        mAdsPlatform.get(0).showBanner(banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {
                                mAdsPlatform.get(1).showBanner(banner, new BannerAdsListener() {
                                    @Override
                                    public void OnLoadFail() {
                                        mAdsPlatform.get(2).showBanner(banner, new BannerAdsListener() {
                                            @Override
                                            public void OnLoadFail() {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                        return;
                    default:
                        mAdsPlatform.get(0).showBanner(banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {
                                mAdsPlatform.get(1).showBanner(banner, new BannerAdsListener() {
                                    @Override
                                    public void OnLoadFail() {
                                        mAdsPlatform.get(2).showBanner(banner, new BannerAdsListener() {
                                            @Override
                                            public void OnLoadFail() {
                                                mAdsPlatform.get(3).showBanner(banner, new BannerAdsListener() {
                                                    @Override
                                                    public void OnLoadFail() {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        return;
                }
            } catch (Exception e) {
                //LogUtil.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void showPopup(PopupAdsListener listener) {
        LogUtil.i(TAG, "showPopup()");
        if (canShowPopup()) {
            try {
                LogUtil.i(TAG, "mAdsPlatform size: " + mAdsPlatform.size());
                switch (mAdsPlatform.size()) {
                    case 1:
                        mAdsPlatform.get(0).showPopup(new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                listener.OnShowFail();
                            }
                        });
                        mPreviousTime = System.currentTimeMillis();
                        return;
                    case 2:
                        mAdsPlatform.get(0).showPopup(new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).showPopup(new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        listener.OnShowFail();
                                    }
                                });
                            }
                        });
                        mPreviousTime = System.currentTimeMillis();
                        return;
                    case 3:
                        mAdsPlatform.get(0).showPopup(new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                LogUtil.i(TAG, "OnClose");
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                LogUtil.i(TAG, "OnShowFail: " + mAdsPlatform.get(0).mAdModel.getName());
                                mAdsPlatform.get(1).showPopup(new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        LogUtil.i(TAG, "OnShowFail: " + mAdsPlatform.get(0).mAdModel.getName());
                                        mAdsPlatform.get(2).showPopup(new PopupAdsListener() {
                                            @Override
                                            public void OnClose() {
                                                listener.OnClose();
                                            }

                                            @Override
                                            public void OnShowFail() {
                                                listener.OnShowFail();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        mPreviousTime = System.currentTimeMillis();
                        return;
                    default:
                        mAdsPlatform.get(0).showPopup(new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).showPopup(new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        mAdsPlatform.get(2).showPopup(new PopupAdsListener() {
                                            @Override
                                            public void OnClose() {
                                                listener.OnClose();
                                            }

                                            @Override
                                            public void OnShowFail() {
                                                mAdsPlatform.get(3).showPopup(new PopupAdsListener() {
                                                    @Override
                                                    public void OnClose() {
                                                        listener.OnClose();
                                                    }

                                                    @Override
                                                    public void OnShowFail() {
                                                        listener.OnShowFail();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        mPreviousTime = System.currentTimeMillis();
                        return;
                }

            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                listener.OnShowFail();
            }
        } else {
            listener.OnShowFail();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
        if (canShowReward()) {
            try {
                switch (mAdsPlatform.size()) {
                    case 1:
                        mAdsPlatform.get(0).showReward(new RewardAdListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                listener.OnShowFail();
                            }

                            @Override
                            public void OnRewarded() {
                                listener.OnRewarded();
                            }
                        });
                        return;
                    case 2:
                        mAdsPlatform.get(0).showReward(new RewardAdListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).showReward(new RewardAdListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        listener.OnShowFail();
                                    }

                                    @Override
                                    public void OnRewarded() {
                                        listener.OnRewarded();
                                    }
                                });
                            }

                            @Override
                            public void OnRewarded() {
                                listener.OnRewarded();
                            }
                        });
                        return;
                    case 3:
                        mAdsPlatform.get(0).showReward(new RewardAdListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).showReward(new RewardAdListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        mAdsPlatform.get(2).showReward(new RewardAdListener() {
                                            @Override
                                            public void OnClose() {
                                                listener.OnClose();
                                            }

                                            @Override
                                            public void OnShowFail() {
                                                listener.OnShowFail();
                                            }

                                            @Override
                                            public void OnRewarded() {
                                                listener.OnRewarded();
                                            }
                                        });
                                    }

                                    @Override
                                    public void OnRewarded() {
                                        listener.OnRewarded();
                                    }
                                });
                            }

                            @Override
                            public void OnRewarded() {
                                listener.OnRewarded();
                            }
                        });
                        return;
                    default:
                        mAdsPlatform.get(0).showReward(new RewardAdListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).showReward(new RewardAdListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        mAdsPlatform.get(2).showReward(new RewardAdListener() {
                                            @Override
                                            public void OnClose() {
                                                listener.OnClose();
                                            }

                                            @Override
                                            public void OnShowFail() {
                                                mAdsPlatform.get(3).showReward(new RewardAdListener() {
                                                    @Override
                                                    public void OnClose() {
                                                        listener.OnClose();
                                                    }

                                                    @Override
                                                    public void OnShowFail() {
                                                        listener.OnShowFail();
                                                    }

                                                    @Override
                                                    public void OnRewarded() {
                                                        listener.OnRewarded();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void OnRewarded() {
                                                listener.OnRewarded();
                                            }
                                        });
                                    }

                                    @Override
                                    public void OnRewarded() {
                                        listener.OnRewarded();
                                    }
                                });
                            }

                            @Override
                            public void OnRewarded() {
                                listener.OnRewarded();
                            }
                        });
                        return;
                }
            } catch (Exception e) {
                listener.OnShowFail();
                //LogUtil.e(TAG, e.getMessage());
            }
        } else {
            listener.OnShowFail();
        }
    }

    @Override
    public void setLimitTime(long limitTime) {

    }

    @Override
    public void enableAd() {
        mEnableAd = true;
        mPref.edit().putBoolean(AdsConstants.PREF_ENABLE_AD, mEnableAd);
    }

    @Override
    public void muteAdsForever() {
        mEnableAd = false;
        mPref.edit().putBoolean(AdsConstants.PREF_ENABLE_AD, mEnableAd);
    }

    @Override
    public AdModel getAdModelByName(String name) {
        Gson gson = new Gson();
        String json = mPref.getString(name, null);
        Type type = new TypeToken<AdModel>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void saveAdModel(AdModel adModel) {
        Gson gson = new Gson();
        String json = gson.toJson(adModel);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(adModel.getName(), json);
        editor.apply();
    }

    @Override
    public ArrayList<String> getWaterfall() {
        Gson gson = new Gson();
        String json = mPref.getString(AdsConstants.PREF_AD_WATERFALL, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void saveWaterFall(ArrayList<String> inputPlatforms) {
        Gson gson = new Gson();
        String json = gson.toJson(inputPlatforms);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(AdsConstants.PREF_AD_WATERFALL, json);
        editor.apply();
    }


    private boolean canShowBanner() {
        //check vip
        return mEnableAd;
    }

    private boolean canShowPopup() {
        //prevent show 2 time continuously
        if (!mShow) mShow = true;
        else mShow = false;
        //check period to show
        long currentTime = System.currentTimeMillis();
        LogUtil.i(TAG, "canShowPopup: " + mShow + " " + (currentTime - mPreviousTime));
        return (mEnableAd && mShow && (currentTime - mPreviousTime >= getLimitTime()));
    }

    private boolean canShowReward() {
        return mEnableAd;
    }

    private long getLimitTime() {
        long interval = mPref.getLong(AdsConstants.PREF_AD_TIME, -1); //in second
        return interval * 1000;
    }

    private AdsPlatform getAdsPlatformByName(String name, boolean testMode) {
            AdModel adModel = getAdModelByName(name);
        switch (name) {
            case AdsConstants.ADMOB:
                return new AdmobAdsManager(adModel);
            case AdsConstants.UNITY:
                return new UnityAdsManager(adModel);
            case AdsConstants.MOPUB:
                return new MopubManager(adModel);
            case AdsConstants.IRONSOURCE:
                return new IronSourceManager(adModel);
            case AdsConstants.FACEBOOK:
                return new FanManager(adModel);
            default:
                return new AdmobAdsManager(adModel);
        }
    }
}
