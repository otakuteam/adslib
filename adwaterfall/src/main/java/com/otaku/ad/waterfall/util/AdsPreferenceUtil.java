package com.otaku.ad.waterfall.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AdsPreferenceUtil {
    private static AdsPreferenceUtil instance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private AdsPreferenceUtil() {

    }

    public static AdsPreferenceUtil getInstance() {
        if (instance == null) {
            instance = new AdsPreferenceUtil();
        }
        return instance;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("ads_config", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public void putLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public String getString(String key, String defVal) {
        return mSharedPreferences.getString(key, defVal);
    }

    public int getInt(String key, int defVal) {
        return mSharedPreferences.getInt(key, defVal);
    }

    public long getLong(String key, long defVal) {
        return mSharedPreferences.getLong(key, defVal);
    }

    public boolean getBoolean(String key, boolean defVal) {
        return mSharedPreferences.getBoolean(key, defVal);
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }


}

