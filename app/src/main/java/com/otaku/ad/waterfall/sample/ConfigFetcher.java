package com.otaku.ad.waterfall.sample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.model.AdModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfigFetcher extends AsyncTask<Void, Void, Void> {
    private final String TAG = getClass().getSimpleName();

    private Context mContext;

    public ConfigFetcher(Context context) {
        mContext = context;
    }

    public String getLink() {
        return "https://otakuteam.github.io/config/adstest.json";
    }

    @Override
    protected Void doInBackground(Void... voids) {
        fetchConfigs();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void fetchConfigs() {
        String url = getLink();
        Log.i(TAG, "fetchConfig_url:" + url);
        JSONObject jsonObject = fetchConfig(url);
        if (jsonObject != null) {
            try {
                Log.i(TAG, "fetchConfig: " + jsonObject.toString());
                JSONObject jAdsConfig = jsonObject.getJSONObject("ads_config");
                fetchAdConfig(jAdsConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchAdConfig(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                //  LogDebug.i(TAG, jsonObject.toString());
                JSONArray adWaterFall = jsonObject.getJSONArray("ad_waterfall");
                ArrayList<String> adPlatforms = new ArrayList<>();
                for (int i = 0; i < adWaterFall.length(); i++) {
                    adPlatforms.add(adWaterFall.getString(i));
                }
                AdsManager.getInstance().saveWaterFall(adPlatforms);
                long ad_time = jsonObject.getLong("ad_time");
                AdsManager.getInstance().setLimitTime(ad_time);

                JSONArray adModels = jsonObject.getJSONArray("ad_model");
                for (int i = 0; i < adModels.length(); i++) {
                    JSONObject obj = adModels.getJSONObject(i);
                    String name = obj.getString("name");
                    String app_id = obj.getString("app_id");
                    String banner_id = obj.getString("banner_id");
                    Log.i(TAG, "banner_id_test: " + banner_id);
                    String popup_id = obj.getString("popup_id");
                    String reward_id = obj.getString("reward_id");
                    AdsManager.getInstance().saveAdModel(new AdModel(name, app_id, banner_id, popup_id, reward_id));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public JSONObject fetchConfig(String loadUrl) {
        Response response;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(loadUrl).build();
            response = client.newCall(request).execute();
            String result = response.body().string();
            Log.i(TAG, "result_: " + result);
            return new JSONObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
