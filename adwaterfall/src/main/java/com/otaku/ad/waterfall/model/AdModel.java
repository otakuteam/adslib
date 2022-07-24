package com.otaku.ad.waterfall.model;

public class AdModel {
    private String name;
    private String appId;
    private String bannerId;
    private String popupId;
    private String rewardId;
    private long popupLimitTime;

    public AdModel() {
    }

    public AdModel(String name, String appId, String bannerId, String popupId, String rewardId) {
        this.name = name;
        this.appId = appId;
        this.bannerId = bannerId;
        this.popupId = popupId;
        this.rewardId = rewardId;
        this.popupLimitTime = 0;
    }

    public AdModel(String name, String appId, String bannerId, String popupId, String rewardId, long popupLimitTime) {
        this.name = name;
        this.appId = appId;
        this.bannerId = bannerId;
        this.popupId = popupId;
        this.rewardId = rewardId;
        this.popupLimitTime = popupLimitTime;
    }

    public long getPopupLimitTime() {
        return popupLimitTime;
    }

    public AdModel setPopupLimitTime(long popupLimitTime) {
        this.popupLimitTime = popupLimitTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public AdModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public AdModel setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getBannerId() {
        return bannerId;
    }

    public AdModel setBannerId(String bannerId) {
        this.bannerId = bannerId;
        return this;
    }

    public String getPopupId() {
        return popupId;
    }

    public AdModel setPopupId(String popupId) {
        this.popupId = popupId;
        return this;
    }

    public String getRewardId() {
        return rewardId;
    }

    public AdModel setRewardId(String rewardId) {
        this.rewardId = rewardId;
        return this;
    }
}
