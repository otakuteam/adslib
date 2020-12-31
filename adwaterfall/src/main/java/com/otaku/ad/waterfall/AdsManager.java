package com.otaku.ad.waterfall;

import android.content.Context;
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
import com.otaku.ad.waterfall.util.AdsLog;
import com.otaku.ad.waterfall.util.AdsPreferenceUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdsManager implements IAdManager {
    private final String TAG = getClass().getSimpleName();
    private boolean mEnableAd = true;
    private long mPreviousTime = 0;
    private boolean mShow = false;
    private ArrayList<AdsPlatform> mAdsPlatform = new ArrayList<>();
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
        AdsPreferenceUtil.getInstance().init(context);
        AdsLog.isDebug = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        mEnableAd = AdsPreferenceUtil.getInstance().getBoolean(AdsConstants.PREF_ENABLE_AD, true);
        List<String> supportPlatforms = Arrays.asList(new String[]{AdsConstants.ADMOB, AdsConstants.UNITY,
                AdsConstants.FACEBOOK, AdsConstants.MOPUB, AdsConstants.IRONSOURCE});
        ArrayList<String> waterFall = getWaterfall();
        if (waterFall == null || waterFall.isEmpty()) {
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
            for (int i = 0; i < waterFall.size(); i++) {
                AdsLog.i(TAG, "waterfall: " + waterFall.get(i));
                mAdsPlatform.add(getAdsPlatformByName(waterFall.get(i), testMode));
            }
            for (AdsPlatform platform : mAdsPlatform) {
                platform.init(context, testMode);
            }
        }

        for (AdsPlatform platform : mAdsPlatform) {
            AdsLog.i(TAG, "log_platform" + platform.mAdModel.getName());
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
        AdsLog.i(TAG, "showPopup()");
        if (canShowPopup()) {
            try {
                AdsLog.i(TAG, "mAdsPlatform size: " + mAdsPlatform.size());
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
                                AdsLog.i(TAG, "OnShowFail_0");
                                mAdsPlatform.get(1).showPopup(new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        AdsLog.i(TAG, "OnShowFail_1");
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
                                AdsLog.i(TAG, "OnClose");
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                AdsLog.i(TAG, "OnShowFail: " + mAdsPlatform.get(0).mAdModel.getName());
                                mAdsPlatform.get(1).showPopup(new PopupAdsListener() {
                                    @Override
                                    public void OnClose() {
                                        listener.OnClose();
                                    }

                                    @Override
                                    public void OnShowFail() {
                                        AdsLog.i(TAG, "OnShowFail: " + mAdsPlatform.get(0).mAdModel.getName());
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
                AdsLog.e(TAG, e.getMessage());
                listener.OnShowFail();
            }
        } else {
            listener.OnShowFail();
        }
    }

    @Override
    public void showReward(RewardAdListener listener) {
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
    }

    @Override
    public void setLimitTime(long limitTime) {
        AdsLog.i(TAG, "setLimitTime: " + limitTime);
        AdsPreferenceUtil.getInstance().putLong(AdsConstants.PREF_AD_TIME, limitTime);
        AdsLog.i(TAG, "setLimitTime: " + AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_AD_TIME, -1));
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
        if (!mShow) mShow = true;
        else mShow = false;
        //check period to show
        long currentTime = System.currentTimeMillis();
        AdsLog.i(TAG, "canShowPopup: " + mShow + " " + getLimitTime() + " " + (currentTime - mPreviousTime));
        return mEnableAd && mShow && (currentTime - mPreviousTime >= getLimitTime());
    }

    private long getLimitTime() {
        long interval = AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_AD_TIME, -1); //in second
        return interval * 1000;
    }

    private AdsPlatform getAdsPlatformByName(String name, boolean testMode) {
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
            case AdsConstants.MOPUB:
                if (adModel == null) {
                    adModel = new AdModel(AdsConstants.MOPUB, mContext.getString(R.string.app_id),
                            mContext.getString(R.string.banner_id),
                            mContext.getString(R.string.popup_id),
                            mContext.getString(R.string.reward_id)
                    );
                }
                return new MopubManager(adModel);
            case AdsConstants.IRONSOURCE:
                if (adModel == null) {
                    adModel = new AdModel(AdsConstants.IRONSOURCE, mContext.getString(R.string.app_id),
                            mContext.getString(R.string.banner_id),
                            mContext.getString(R.string.popup_id),
                            mContext.getString(R.string.reward_id)
                    );
                }
                return new IronSourceManager(adModel);
            case AdsConstants.FACEBOOK:
                if (adModel == null) {
                    adModel = new AdModel(AdsConstants.FACEBOOK, mContext.getString(R.string.app_id),
                            mContext.getString(R.string.banner_id),
                            mContext.getString(R.string.popup_id),
                            mContext.getString(R.string.reward_id)
                    );
                }
                return new FanManager(adModel);
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
