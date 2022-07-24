package com.otaku.ad.waterfall;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otaku.ad.waterfall.admob.AdmobAdsManager;
import com.otaku.ad.waterfall.listener.BannerAdsListener;
import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;
import com.otaku.ad.waterfall.unity.UnityAdsManager;
import com.otaku.ad.waterfall.util.AdsLog;
import com.otaku.ad.waterfall.util.AdsPreferenceUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdsManager implements IAdManager {
    private final String TAG = "Main_" + getClass().getSimpleName();
    private boolean mEnableAd = true;
    private long mPreviousTime = 0;
    private int mShowPopup = 0;
    private ArrayList<AdsPlatform> mAdsPlatform;
    private Context mContext;

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
        mContext = context;
        mAdsPlatform = new ArrayList<>();
        AdsPreferenceUtil.getInstance().init(context);
        AdsLog.isDebug = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        mEnableAd = AdsPreferenceUtil.getInstance().getBoolean(AdsConstants.PREF_ENABLE_AD, true);
        List<String> supportPlatforms = Arrays.asList(new String[]{AdsConstants.ADMOB, AdsConstants.UNITY});
        ArrayList<String> waterFall = getWaterfall();

        if (waterFall == null || waterFall.isEmpty()) {
            AdsLog.d(TAG, "waterfall_size: null");
            waterFall = new ArrayList<>();
            for (AdModel adModel : models) {
                if (!supportPlatforms.contains(adModel.getName()))
                    throw new NotSupportPlatformException("not support platform");
                waterFall.add(adModel.getName());
                saveAdModel(adModel);
            }
            saveWaterFall(waterFall);
        }

        if (waterFall != null && waterFall.size() > 0) {
            AdsLog.d(TAG, "waterfall_size: " + waterFall.size());
            for (int i = 0; i < waterFall.size(); i++) {
                AdsLog.d(TAG, "waterfall: " + waterFall.get(i));
                mAdsPlatform.add(getAdsPlatformByName(waterFall.get(i)));
            }
            for (AdsPlatform platform : mAdsPlatform) {
                platform.init(context, testMode);
            }
        }

        AdsLog.d(TAG, "log_platform_size: " + mAdsPlatform.size());
        for (AdsPlatform platform : mAdsPlatform) {
            AdsLog.d(TAG, "log_platform" + platform.mAdModel.getName());
        }
    }

    @Override
    public void showBanner(Activity activity, ViewGroup banner) {
        if (canShowBanner()) {
            try {
                switch (mAdsPlatform.size()) {
                    case 1:
                        mAdsPlatform.get(0).showBanner(activity, banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {

                            }
                        });
                        return;
                    case 2:
                        mAdsPlatform.get(0).showBanner(activity, banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {
                                mAdsPlatform.get(1).showBanner(activity, banner, new BannerAdsListener() {
                                    @Override
                                    public void OnLoadFail() {

                                    }
                                });
                            }
                        });
                        return;
                    default:
                        mAdsPlatform.get(0).showBanner(activity, banner, new BannerAdsListener() {
                            @Override
                            public void OnLoadFail() {
                                mAdsPlatform.get(1).showBanner(activity, banner, new BannerAdsListener() {
                                    @Override
                                    public void OnLoadFail() {

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
    public void showPopup(Activity activity, PopupAdsListener listener) {
        AdsLog.d(TAG, "showPopup()");
        if (canShowPopup()) {
            try {
                AdsLog.d(TAG, "mAdsPlatform size: " + mAdsPlatform.size());
                switch (mAdsPlatform.size()) {
                    case 1:
                        mAdsPlatform.get(0).showPopup(activity, new PopupAdsListener() {
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
                        mAdsPlatform.get(0).showPopup(activity, new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                AdsLog.d(TAG, "OnShowFail_0");
                                mAdsPlatform.get(1).showPopup(activity, new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        AdsLog.d(TAG, "OnShowFail_1");
                                        listener.OnShowFail();
                                    }
                                });
                            }
                        });
                        mPreviousTime = System.currentTimeMillis();
                        return;
                    default:
                        mAdsPlatform.get(0).showPopup(activity, new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).showPopup(activity, new PopupAdsListener() {
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
                }

            } catch (Exception e) {
                AdsLog.e(TAG, e.getMessage());
                listener.OnShowFail();
            }
        } else {
            listener.OnShowFail();
        }
    }

    @Override
    public void forceShowPopup(Activity activity, PopupAdsListener listener) {
        AdsLog.d(TAG, "forceShowPopup()");
        if (mEnableAd) {
            try {
                AdsLog.d(TAG, "mAdsPlatform size: " + mAdsPlatform.size());
                switch (mAdsPlatform.size()) {
                    case 1:
                        mAdsPlatform.get(0).forceShowPopup(activity, new PopupAdsListener() {
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
                        mAdsPlatform.get(0).forceShowPopup(activity, new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                AdsLog.d(TAG, "OnShowFail_0");
                                mAdsPlatform.get(1).forceShowPopup(activity, new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        AdsLog.d(TAG, "OnShowFail_1");
                                        listener.OnShowFail();
                                    }
                                });
                            }
                        });
                        mPreviousTime = System.currentTimeMillis();
                        return;
                    default:
                        mAdsPlatform.get(0).forceShowPopup(activity, new PopupAdsListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                mAdsPlatform.get(1).forceShowPopup(activity, new PopupAdsListener() {
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
                }

            } catch (Exception e) {
                AdsLog.e(TAG, e.getMessage());
                listener.OnShowFail();
            }
        } else {
            listener.OnShowFail();
        }
    }

    @Override
    public void forceShowAdModelPopup(Activity activity, String admodelName, PopupAdsListener listener) {
        if(mAdsPlatform.get(0).mAdModel.getName().equals(admodelName)) mAdsPlatform.get(0).forceShowPopup(activity, listener);
        if(mAdsPlatform.get(1).mAdModel.getName().equals(admodelName)) mAdsPlatform.get(1).forceShowPopup(activity, listener);
        mPreviousTime = System.currentTimeMillis();
    }

    @Override
    public void showReward(Activity activity, RewardAdListener listener) {
        try {
            switch (mAdsPlatform.size()) {
                case 1:
                    mAdsPlatform.get(0).showReward(activity, new RewardAdListener() {
                        @Override
                        public void OnClose() {
                            listener.OnClose();
                            mPreviousTime = System.currentTimeMillis();
                            mShowPopup++;
                        }

                        @Override
                        public void OnShowFail() {
                            listener.OnShowFail();
                        }

                        @Override
                        public void OnRewarded() {
                            listener.OnRewarded();
                            mPreviousTime = System.currentTimeMillis();
                            mShowPopup++;
                        }
                    });
                    return;
                case 2:
                    mAdsPlatform.get(0).showReward(activity, new RewardAdListener() {
                        @Override
                        public void OnClose() {
                            listener.OnClose();
                            mPreviousTime = System.currentTimeMillis();
                            mShowPopup++;
                        }

                        @Override
                        public void OnShowFail() {
                            mAdsPlatform.get(1).showReward(activity, new RewardAdListener() {
                                @Override
                                public void OnClose() {
                                    listener.OnClose();
                                    mPreviousTime = System.currentTimeMillis();
                                    mShowPopup++;
                                }

                                @Override
                                public void OnShowFail() {
                                    listener.OnShowFail();
                                }

                                @Override
                                public void OnRewarded() {
                                    listener.OnRewarded();
                                    mPreviousTime = System.currentTimeMillis();
                                    mShowPopup++;
                                }
                            });
                        }

                        @Override
                        public void OnRewarded() {
                            listener.OnRewarded();
                            mPreviousTime = System.currentTimeMillis();
                            mShowPopup++;
                        }
                    });
                    return;
                default:
                    mAdsPlatform.get(0).showReward(activity, new RewardAdListener() {
                        @Override
                        public void OnClose() {
                            listener.OnClose();
                            mPreviousTime = System.currentTimeMillis();
                            mShowPopup++;
                        }

                        @Override
                        public void OnShowFail() {
                            mAdsPlatform.get(1).showReward(activity, new RewardAdListener() {
                                @Override
                                public void OnClose() {
                                    listener.OnClose();
                                    mPreviousTime = System.currentTimeMillis();
                                    mShowPopup++;
                                }

                                @Override
                                public void OnShowFail() {
                                    listener.OnShowFail();
                                }

                                @Override
                                public void OnRewarded() {
                                    listener.OnRewarded();
                                    mPreviousTime = System.currentTimeMillis();
                                    mShowPopup++;
                                }
                            });
                        }

                        @Override
                        public void OnRewarded() {
                            listener.OnRewarded();
                            mPreviousTime = System.currentTimeMillis();
                            mShowPopup++;
                        }
                    });
                    return;
            }
        } catch (Exception e) {
            listener.OnShowFail();
            //LogUtil.e(TAG, e.getMessage());
        }
    }

    @Override
    public void setPopupLimitShow(int click) {
        AdsLog.d(TAG, "setPopupLimitShow: " + click);
        AdsPreferenceUtil.getInstance().putInt(AdsConstants.PREF_POPUP_SPACING, click);
    }

    @Override
    public void setLimitTime(long limitTime) {
        AdsLog.d(TAG, "setLimitTime: " + limitTime);
        AdsPreferenceUtil.getInstance().putLong(AdsConstants.PREF_AD_TIME, limitTime);
        AdsLog.d(TAG, "setLimitTime: " + AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_AD_TIME, -1));
    }

    @Override
    public void enableAd() {
        mEnableAd = true;
        AdsPreferenceUtil.getInstance().putBoolean(AdsConstants.PREF_ENABLE_AD, mEnableAd);
    }

    @Override
    public void muteAdsForever() {
        mEnableAd = false;
        AdsPreferenceUtil.getInstance().putBoolean(AdsConstants.PREF_ENABLE_AD, mEnableAd);
    }

    @Override
    public AdModel getAdModelByName(String name) {
        Gson gson = new Gson();
        String json = AdsPreferenceUtil.getInstance().getString(name, null);
        Type type = new TypeToken<AdModel>() {
        }.getType();
        if (json != null) {
            return gson.fromJson(json, type);
        }
        return null;
    }

    @Override
    public void setAdModelPopupLimitTime(String admodelName, long limitTime) {

    }

    @Override
    public void saveAdModel(AdModel adModel) {
        Gson gson = new Gson();
        String json = gson.toJson(adModel);
        AdsPreferenceUtil.getInstance().putString(adModel.getName(), json);
    }

    @Override
    public ArrayList<String> getWaterfall() {
        Gson gson = new Gson();
        String json = AdsPreferenceUtil.getInstance().getString(AdsConstants.PREF_AD_WATERFALL, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        if (json != null)
            return gson.fromJson(json, type);
        return null;
    }

    @Override
    public void saveWaterFall(ArrayList<String> inputPlatforms) {
        Gson gson = new Gson();
        String json = gson.toJson(inputPlatforms);
        AdsPreferenceUtil.getInstance().putString(AdsConstants.PREF_AD_WATERFALL, json);
    }


    private boolean canShowBanner() {
        //check vip
        return mEnableAd;
    }

    private boolean canShowPopup() {
        //prevent show 2 time continuously
        int limitShow = getPopupLimitShow();
        mShowPopup++;
        //check period to show
        long currentTime = System.currentTimeMillis();
        AdsLog.d(TAG, "canShowPopup: " + mShowPopup + " " + getLimitTime() + " " + (currentTime - mPreviousTime));
        return mEnableAd && (mShowPopup % limitShow == 0) && (currentTime - mPreviousTime >= getLimitTime());
    }

    private long getLimitTime() {
        long interval = AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_AD_TIME, 15); //in second
        return interval * 1000;
    }

    private int getPopupLimitShow() {
        return AdsPreferenceUtil.getInstance().getInt(AdsConstants.PREF_POPUP_SPACING, 2); //in second
    }

    private AdsPlatform getAdsPlatformByName(String name) {
        AdModel adModel = getAdModelByName(name);
        switch (name) {
            case AdsConstants.ADMOB:
                if (adModel == null) {
                    adModel = new AdModel(AdsConstants.ADMOB, mContext.getString(R.string.app_id),
                            mContext.getString(R.string.banner_id),
                            mContext.getString(R.string.popup_id),
                            mContext.getString(R.string.reward_id)
                    );
                }
                return new AdmobAdsManager(adModel);
            case AdsConstants.UNITY:
                if (adModel == null) {
                    adModel = new AdModel(AdsConstants.UNITY, mContext.getString(R.string.app_id),
                            mContext.getString(R.string.banner_id),
                            mContext.getString(R.string.popup_id),
                            mContext.getString(R.string.reward_id)
                    );
                }
                return new UnityAdsManager(adModel);
            default:
                if (adModel == null) {
                    adModel = new AdModel(AdsConstants.ADMOB, mContext.getString(R.string.app_id),
                            mContext.getString(R.string.banner_id),
                            mContext.getString(R.string.popup_id),
                            mContext.getString(R.string.reward_id)
                    );
                }
                return new AdmobAdsManager(adModel);
        }
    }
}
