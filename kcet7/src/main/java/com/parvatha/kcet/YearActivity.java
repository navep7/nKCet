package com.parvatha.kcet;

import static com.parvatha.kcet.SplashActivity.makeToast;
import static com.parvatha.kcet.SplashActivity.prodFlag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class YearActivity extends AppCompatActivity {

    private TextView textViewSubject;
    private ArrayList<String> listYears;
    Handler handler = new Handler();
    private LinearLayout examYears, readYears;
    private Intent intentQs;
    private String subject;
    private WebView wbv;
    private RelativeLayout relativeLayoutYear;

    private TemplateView templateView1, templateView2;
    private boolean adLoaded1 = false, adLoaded2 = false;
    private AdLoader adLoader1, adLoader2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        templateView1 = findViewById(R.id.nativeTemplateView1);
        templateView2 = findViewById(R.id.nativeTemplateView2);

        adLoader1 = new AdLoader.Builder(getApplicationContext(), getResources().getString(R.string.actual_native_id3))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable background = new ColorDrawable();
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                        templateView1.setVisibility(View.VISIBLE);
                        templateView1.setStyles(styles);
                        templateView1.setNativeAd(nativeAd);
                        adLoaded1 = true;
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        makeToast("" + adError.getCode());
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                }).build();

        adLoader2 = new AdLoader.Builder(getApplicationContext(), getResources().getString(R.string.actual_native_id4))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable background = new ColorDrawable();
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                        templateView2.setVisibility(View.VISIBLE);
                        templateView2.setStyles(styles);
                        templateView2.setNativeAd(nativeAd);
                        adLoaded2 = true;
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

        relativeLayoutYear = findViewById(R.id.year_layout);
    //    relativeLayoutYear.setBackgroundResource(R.drawable.cet_splash);

        wbv = findViewById(R.id.webv);
        wbv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        wbv.loadUrl("file:///android_asset/cet_splash.gif");


        textViewSubject = findViewById(R.id.tx_sub);
        examYears = findViewById(R.id.year_exam);
        readYears = findViewById(R.id.year_read);

        Intent intentSub = getIntent();
        if (intentSub != null) {
            subject = intentSub.getExtras().get("subject").toString();
            textViewSubject.setText(subject);

            new GetYears().execute();
        }


    }

    private void loadNativeAd1() {
        // Creating an Ad Request
        AdRequest adRequest = new AdRequest.Builder().build();

        // load Native Ad with the Request
        adLoader1.loadAd(adRequest);
    }

    private void loadNativeAd2() {
        // Creating an Ad Request
        AdRequest adRequest = new AdRequest.Builder().build();

        // load Native Ad with the Request
        adLoader2.loadAd(adRequest);
    }

    private void showNativeAd() {
        if (prodFlag) {
            if (adLoaded1) {
                templateView1.setVisibility(View.VISIBLE);
                // Showing a simple Toast message to user when an Native ad is shown to the user

            } else {
                // Load the Native ad if it is not loaded
                loadNativeAd1();
            }

            if (adLoaded2) {
                templateView2.setVisibility(View.VISIBLE);
                // Showing a simple Toast message to user when an Native ad is shown to the user

            } else {
                // Load the Native ad if it is not loaded
                loadNativeAd2();
            }
        }
    }

    @SuppressLint("ResourceType")
    private void createYears(ArrayList<String> listYears) {

        for (int i = listYears.size() -1 ; i >= 0 ; i--) {
            Button buttonExam = new Button(getApplicationContext());
            buttonExam.setTypeface(null, Typeface.BOLD);
            buttonExam.setTextSize(50);
            buttonExam.setText(listYears.get(i));
            buttonExam.bringToFront();

            buttonExam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentQs = new Intent(YearActivity.this, QuestionsActivity.class);
                    intentQs.putExtra("year", buttonExam.getText());
                    intentQs.putExtra("subject", subject);
                    intentQs.putExtra("type", "exam");
                    startActivity(intentQs);
                }
            });

            examYears.addView(buttonExam);


            Button buttonRead = new Button(getApplicationContext());
            buttonRead.setTypeface(null, Typeface.BOLD);
            buttonRead.setTextSize(50);
            buttonRead.setText(listYears.get(i));
            buttonRead.bringToFront();

            buttonRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentQs = new Intent(YearActivity.this, QuestionsActivity.class);
                    String year = buttonRead.getText().toString();
                    intentQs.putExtra("year", year);
                    intentQs.putExtra("subject", subject);
                    intentQs.putExtra("type", "paper");
                    startActivity(intentQs);
                }
            });

            readYears.addView(buttonRead);


        }
    }






    public class GetYears extends AsyncTask<String, Context, ArrayList<String>> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference subRef = rootRef.child(subject);
            listYears = new ArrayList<>();


            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String year;
                        year = ds.getKey();


                        {

                            DatabaseReference ref = rootRef.child(subject).child(year).child("Questions").getRef();
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long count = dataSnapshot.getChildrenCount();
                                    Log.d("TAAAG", "count= " + count);

                                    if (count == 60)
                                        listYears.add(year);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                            ref.addListenerForSingleValueEvent(valueEventListener);

                        }
                        //   listYears.add(year);

                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            subRef.addListenerForSingleValueEvent(valueEventListener);




            return listYears;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            //   progressBar.setVisibility(View.GONE);


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(result.size() == 0)
                        handler.postDelayed(this, 1000);
                    else {
                        // do actions
                        createYears(result);
                        wbv.setVisibility(View.GONE);
                    //    relativeLayoutYear.setBackgroundResource(android.R.color.white);
                    }
                }
            }, 1000);


        }
    }

}


