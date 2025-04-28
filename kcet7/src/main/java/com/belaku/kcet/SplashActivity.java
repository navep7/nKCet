package com.belaku.kcet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class SplashActivity extends AppCompatActivity {

    public static boolean prodFlag = true;

    Handler handler = new Handler();
    private boolean internetConnectivity = false;
    private static Context context;

    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("com.belaku.kcet", MODE_PRIVATE);
        context = getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Initialize the Google Mobile Ads SDK on a background thread.
                MobileAds.initialize(context, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        // Handle initialization status if needed
                        makeToast("initializationStatus");
                    }
                });
            }
        }).start();





        WebView wbv = findViewById(R.id.webv);
        wbv.loadUrl("file:///android_asset/cet_splash.gif");

        wbv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


    }


    public static void makeToast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    private Boolean checkNet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkNet())
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPreferences.getBoolean("firstRun", true)) {
                        startActivity(new Intent(SplashActivity.this, ContactUsActivity.class));
                        //You can perform anything over here. This will call only first time
                        editor = sharedPreferences.edit();
                        editor.putBoolean("firstRun", false);
                        editor.commit();

                    } else startActivity(new Intent(SplashActivity.this, SubjectActivity.class));
                }
            }, 3000);
        else makeToast("Poor Connectivity, try after sometime.");

    }
}


