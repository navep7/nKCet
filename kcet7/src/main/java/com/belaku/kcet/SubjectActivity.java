package com.belaku.kcet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.belaku.kcet.SplashActivity.makeToast;
import static com.belaku.kcet.SplashActivity.prodFlag;

public class SubjectActivity extends AppCompatActivity {

    private Button buttonP, buttonC, buttonM, buttonB;
    private TextView textViewCet;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int totalCount;

    private ReviewManager reviewManager;
    private boolean reviewSuccessfull = false;

    private ReviewInfo reviewInfo;
    private ReviewManager manager;

    private TemplateView templateView;
    private boolean adLoaded = false;
    private AdLoader adLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);


        templateView = findViewById(R.id.nativeTemplateView);

        adLoader = new AdLoader.Builder(getApplicationContext(), getResources().getString(R.string.actual_native_id4))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable background = new ColorDrawable();
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                        templateView.setVisibility(View.VISIBLE);
                        templateView.setStyles(styles);
                        templateView.setNativeAd(nativeAd);
                        adLoaded = true;
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        makeToast("" + adError.getCode());
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                }).build();


        showNativeAd();
        reviewManager = ReviewManagerFactory.create(this);

        initReview();

        CountLaunches();


        textViewCet = findViewById(R.id.tx_cet);
        textViewCet.setPaintFlags(textViewCet.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        buttonP = findViewById(R.id.btn_phy);
        buttonC = findViewById(R.id.btn_che);
        buttonM = findViewById(R.id.btn_math);
        buttonB = findViewById(R.id.btn_bio);

        buttonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubjectActivity.this, YearActivity.class).putExtra("subject", "Physics"));
            }
        });
        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubjectActivity.this, YearActivity.class).putExtra("subject", "Chemistry"));
            }
        });
        buttonM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubjectActivity.this, YearActivity.class).putExtra("subject", "Maths"));
            }
        });
        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubjectActivity.this, YearActivity.class).putExtra("subject", "Biology"));
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Reach us @ ~ Naveen Prakash \n karnataka.cet7@gmail.com / +918884846307", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
                startActivity(new Intent(SubjectActivity.this, ContactUsActivity.class));
            }
        });
    }



    private void loadNativeAd() {
        // Creating an Ad Request
        AdRequest adRequest = new AdRequest.Builder().build();

        // load Native Ad with the Request
        adLoader.loadAd(adRequest);
    }

    private void showNativeAd() {
        if (prodFlag) {
            if (adLoaded) {
                templateView.setVisibility(View.VISIBLE);
                // Showing a simple Toast message to user when an Native ad is shown to the user

            } else {
                // Load the Native ad if it is not loaded
                loadNativeAd();

                // Showing a simple Toast message to user when Native ad is not loaded

            }
        }
    }


    private void CountLaunches() {
        prefs = getPreferences(Context.MODE_PRIVATE);
        editor = prefs.edit();
        totalCount = prefs.getInt("counter", 0);
        totalCount++;
        editor.putInt("counter", totalCount);
        editor.commit();

        if (totalCount > 3)
            askForReview();
    }


    private void initReview() {
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();

            } else {
                showRateAppFallbackDialog();
                // There was some problem, continue regardless of the result.
                // Log error

            }
        });
    }

    private void askForReview() {
        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("ReviewDone", "Successful");
                        Toast.makeText(getApplicationContext(), "ReviewDone - Successful", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> {

                })
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }


}