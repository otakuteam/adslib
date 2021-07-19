package com.otaku.ad.waterfall.inhouse;

import android.os.Parcel;
import android.os.Parcelable;

public class InhouseAds implements Parcelable {
    public static int POPUP = 1;
    public static int REWARD = 2;
    public String id;
    public String resourceUrl;
    public String actionUrl;

    public InhouseAds(String id, String resourceUrl, String actionUrl) {
        this.id = id;
        this.resourceUrl = resourceUrl;
        this.actionUrl = actionUrl;
    }

    protected InhouseAds(Parcel in) {
        id = in.readString();
        resourceUrl = in.readString();
        actionUrl = in.readString();
    }

    public static final Creator<InhouseAds> CREATOR = new Creator<InhouseAds>() {
        @Override
        public InhouseAds createFromParcel(Parcel in) {
            return new InhouseAds(in);
        }

        @Override
        public InhouseAds[] newArray(int size) {
            return new InhouseAds[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(resourceUrl);
        parcel.writeString(actionUrl);
    }
}
