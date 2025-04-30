package com.belaku.kcet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class SplashActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "SplashActivity";
    private static final int REQUEST_CODE_EMAIL = 1;
    public static boolean prodFlag = true;

    Handler handler = new Handler();
    private boolean internetConnectivity = false;
    private static Context context;

    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor sharedPreferencesEditor;
    private GoogleSignInAccount account;

    private SignInButton gSignInBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("com.belaku.kcet", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
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


    }


    private void saveSignIn(String displayName) {

        sharedPreferencesEditor.putString("accSignedIn", displayName);
        sharedPreferencesEditor.commit();
    }


    public static void makeToast(String str) {
     //   Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    private Boolean checkNet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            makeToast(accName);
            startActivity(new Intent(SplashActivity.this, SubjectActivity.class).putExtra("accName", accName));
            saveSignIn(accName);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPreferences.getString("accSignedIn", "no").equals("no")) {
            gSignInBtn = findViewById(R.id.sign_in_button);
            gSignInBtn.setVisibility(View.VISIBLE);
            gSignInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent = AccountPicker.newChooseAccountIntent(
                                null, null,
                                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null
                        );
                        startActivityForResult(intent, REQUEST_CODE_EMAIL);
                    } catch (ActivityNotFoundException e) {
                        // TODO
                    }
                }
            });
        } else {
            startActivity(new Intent(SplashActivity.this, SubjectActivity.class).putExtra("accName", sharedPreferences.getString("accSignedIn", "no")));
        }


    }
}


