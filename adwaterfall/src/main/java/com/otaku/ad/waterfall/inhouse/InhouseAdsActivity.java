package com.otaku.ad.waterfall.inhouse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ad.waterfall.R;

import java.io.File;
import java.util.Calendar;

import ir.samanjafari.easycountdowntimer.CountDownInterface;
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview;

public class InhouseAdsActivity extends AppCompatActivity {

    private String actionUrl;
    private String resourcePath;
    private int adsType;
    private boolean isRewared = false;
    private Context mContext;

    ImageView btnClose, imageView;
    VideoView videoView;
    Button btnInstall;
    EasyCountDownTextview counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inhouse_ads);
        mContext = this;

        RelativeLayout rootLayout = findViewById(R.id.root_layout);
        btnClose = findViewById(R.id.btn_close);
        imageView = findViewById(R.id.img_popup);
        videoView = findViewById(R.id.video_view);
        btnInstall = findViewById(R.id.btn_install);
        counter = findViewById(R.id.counter);

        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionUrl != null)
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(actionUrl)));
                finish();
            }
        });
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionUrl != null)
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(actionUrl)));
                finish();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adsType == InhouseAds.POPUP) {
                    sendBroadcast(new Intent(InhouseAdsManager.CLOSE_POPUP_ACTION));
                    finish();
                } else if (adsType == InhouseAds.REWARD) {
                    if (isRewared) {
                        finish();
                    } else {
                        //videoView.pause();
                        new CloseAdsDialog(new CloseAdsDialog.Listener() {
                            @Override
                            public void OnResume() {
                                //videoView.resume();
                            }

                            @Override
                            public void OnClose() {
                                sendBroadcast(new Intent(InhouseAdsManager.CLOSE_REWARD_ACTION));
                                finish();
                            }
                        }).show(getSupportFragmentManager(), "close_ads");
                    }

                }
            }
        });

        resourcePath = getIntent().getStringExtra("resource");
        actionUrl = getIntent().getStringExtra("action");
        adsType = getIntent().getIntExtra("type", InhouseAds.POPUP);
        if (adsType == InhouseAds.POPUP) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            counter.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeFile(resourcePath);
            imageView.setImageBitmap(bitmap);
        } else if (adsType == InhouseAds.REWARD) {
            //imageView.setVisibility(View.GONE);
            btnClose.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnClose.setVisibility(View.VISIBLE);
                }
            }, 3000);
            counter.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(this, Uri.fromFile(new File(resourcePath)));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            retriever.release();
            calendar.add(Calendar.MILLISECOND, (int) timeInMillisec);
            counter.startTimer(calendar);
            counter.setOnTick(new CountDownInterface() {
                @Override
                public void onTick(long time) {

                }

                @Override
                public void onFinish() {

                }
            });
            videoView.setVisibility(View.VISIBLE);
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(resourcePath,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(thumb);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(resourcePath);
            videoView.requestFocus();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.setVisibility(View.GONE);
                    isRewared = true;
                    sendBroadcast(new Intent(InhouseAdsManager.REWARDED_ACTION));
                }
            });
            videoView.start();
        }


    }

    @Override
    public void onBackPressed() {

    }
}