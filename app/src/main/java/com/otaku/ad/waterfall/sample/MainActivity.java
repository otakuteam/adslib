package com.otaku.ad.waterfall.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ad.waterfall.AdsConstants;
import com.otaku.ad.waterfall.AdsManager;
import com.otaku.ad.waterfall.listener.PopupAdsListener;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdsManager.getInstance().showBanner(this, (findViewById(R.id.banner)));
        ((Button) findViewById(R.id.btn_show_popup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().showPopup(MainActivity.this, new PopupAdsListener() {
                    @Override
                    public void OnClose() {

                    }

                    @Override
                    public void OnShowFail() {

                    }
                });
            }
        });
        ((Button) findViewById(R.id.btn_force_show_popup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().forceShowPopup(MainActivity.this, new PopupAdsListener() {
                    @Override
                    public void OnClose() {

                    }

                    @Override
                    public void OnShowFail() {

                    }
                });
            }
        });
        ((Button) findViewById(R.id.btn_force_show_admob_popup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().forceShowAdModelPopup(MainActivity.this, AdsConstants.ADMOB, new PopupAdsListener() {
                    @Override
                    public void OnClose() {

                    }

                    @Override
                    public void OnShowFail() {

                    }
                });
            }
        });
        ((Button) findViewById(R.id.btn_force_show_unity_popup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().forceShowAdModelPopup(MainActivity.this, AdsConstants.UNITY, new PopupAdsListener() {
                    @Override
                    public void OnClose() {

                    }

                    @Override
                    public void OnShowFail() {

                    }
                });
            }
        });
        ((Button) findViewById(R.id.btn_test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
    }
}
