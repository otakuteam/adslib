package com.otaku.ad.waterfall;

import android.content.Context;
import android.view.ViewGroup;

import com.otaku.ad.waterfall.listener.PopupAdsListener;
import com.otaku.ad.waterfall.listener.RewardAdListener;
import com.otaku.ad.waterfall.model.AdModel;

import java.util.ArrayList;

public interface IAdManager {
    void init(Context context, boolean testMode, AdModel... models)  throws NotSupportPlatformException;

    void showBanner(ViewGroup banner);

    void showPopup(PopupAdsListener listener);

    void showReward(RewardAdListener listener);

    void muteAdsForever(); //sometime need to mute ads to capture screenshot or in premium version

    void enableAd();

    void saveWaterFall(ArrayList<String> platforms); // save water fall in order of element in array to sharedprefences

    ArrayList<String> getWaterfall(); // get water fall in order of element in array


    void setPopupLimitShow(int click); //popup ad show spacing

    void setLimitTime(long limitTime); //set time between 2 continuously ads showed

    void saveAdModel(AdModel adModel); //save app_id, banner_id, popup_id, reward_id of an Ad platform

    AdModel getAdModelByName(String name); //get AdModel
}
