package com.belaku.kcet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static com.belaku.kcet.SplashActivity.makeToast;

public class QuestionsActivity extends AppCompatActivity {

    public static TemplateView templateView;
    private static ArrayList<QnA> listQnAs = new ArrayList<>();
    private static DatabaseReference QsRef;

    private Button buttonShowCorrectAnswer;
    private FloatingActionButton floatingActionButtonNext, floatingActionButtonPrev;
    private TextView textViewMain, textViewQuestion;
    private RadioGroup radioGroupOptions;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    private int count = 0;
    private ImageView imageViewQuestion, imageViewAnswer;
    private int Score = 0;
    private String year, subject, type;
    private Handler handler;
    public static InterstitialAd mInterstitialAd;
    private boolean correctAnswer, answerShown;
    private String answer;

    private static QnA qnA;
    private RecyclerView recyclerView;
    private WebView wbv;
    public static AdView mAdView;
    private AdRequest adRequest;
    private LinearLayoutManager llm;
    private boolean adLoaded = false;
    private AdLoader adLoader;
    private FrameLayout frameLayoutAnswer;
    private Button buttonSubmitAnswer;
    private EditText edtxAnswer;
    private static String strAE;


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        findViewByIds();
        listeners();

        adLoader = new AdLoader.Builder(getApplicationContext(), getResources().getString(R.string.actual_native_id2)).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                ColorDrawable background = new ColorDrawable();
                NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                templateView.setVisibility(View.VISIBLE);
                templateView.setStyles(styles);
                templateView.setNativeAd(nativeAd);
                adLoaded = true;
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                makeToast("" + adError.getCode());
                // Handle the failure by logging, altering the UI, and so on.
            }
        }).build();


        Handler handler1 = new Handler();


        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                //Your function
                showNativeAd();
                showBannerAd();

                //restarting the runnable
                handler1.postDelayed(this, 60000);

            }

        };

        handler1.post(runnable);


        loadIAd();


        wbv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        wbv.loadUrl("file:///android_asset/cet_splash.gif");

        Intent intentYear = getIntent();

        if (intentYear != null) {
            year = intentYear.getExtras().get("year").toString();
            subject = intentYear.getExtras().get("subject").toString();

            SpannableString mSpannableString = new SpannableString(subject + " - " + year);
            mSpannableString.setSpan(new UnderlineSpan(), 1, mSpannableString.length(), 0);
            textViewMain.setText(mSpannableString);

            QuestionsActivity.this.setTitle(subject + " ~ " + year);
            FirebaseApp.initializeApp(getApplicationContext());
            listQnAs = getData(subject, year);

            type = intentYear.getExtras().get("type").toString();

            {

                if (type.equals("paper")) {
                    recyclerView.setVisibility(View.VISIBLE);
                    floatingActionButtonNext.setVisibility(View.GONE);
                    floatingActionButtonPrev.setVisibility(View.GONE);
                    textViewQuestion.setVisibility(View.GONE);
                    radioGroupOptions.setVisibility(View.GONE);
                    imageViewQuestion.setVisibility(View.GONE);
                    imageViewAnswer.setVisibility(View.GONE);
                } else if (type.equals("exam")) {
                    recyclerView.setVisibility(View.GONE);
                    floatingActionButtonNext.setVisibility(View.VISIBLE);
                    floatingActionButtonPrev.setVisibility(View.VISIBLE);
                    textViewQuestion.setVisibility(View.VISIBLE);
                    radioGroupOptions.setVisibility(View.VISIBLE);
                    imageViewQuestion.setVisibility(View.VISIBLE);
                    imageViewAnswer.setVisibility(View.VISIBLE);
                }
            }


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //    if(listQnAs.size() != 60)
                    //      handler.postDelayed(this, 1000);
                    //   else {
                    // do actions
                    wbv.setVisibility(View.GONE);

                    if (type.equals("exam")) {
                        mAdView.setVisibility(View.VISIBLE);
                        loadQnAsforExam(0);
                    } else if (type.equals("paper")) {
                        mAdView.setVisibility(View.GONE);
                        recyclerView.setAdapter(new RvAdapter(listQnAs, getApplicationContext()));
                    }
                    //   }
                }
            }, 1000);


        }


        radioGroupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == -1) {
                    radioButton1.setEnabled(true);
                    radioButton2.setEnabled(true);
                    radioButton3.setEnabled(true);
                    radioButton4.setEnabled(true);
                } else {
                    radioButton1.setEnabled(false);
                    radioButton2.setEnabled(false);
                    radioButton3.setEnabled(false);
                    radioButton4.setEnabled(false);
                }

                boolean cAnswer = Validate(count);
                int radioButtonID = radioGroupOptions.getCheckedRadioButtonId();

                View radioButton = radioGroupOptions.findViewById(radioButtonID);
                int idx = radioGroupOptions.indexOfChild(radioButton);
                switch (idx) {

                    case 0:
                        if (cAnswer)
                            radioButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        else {
                            radioButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            buttonShowCorrectAnswer.setVisibility(View.VISIBLE);
                            //    highlightCorrectAns();
                        }
                        break;
                    case 1:
                        if (cAnswer)
                            radioButton2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        else {
                            radioButton2.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            buttonShowCorrectAnswer.setVisibility(View.VISIBLE);
                            //    highlightCorrectAns();
                        }
                        break;
                    case 2:
                        if (cAnswer)
                            radioButton3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        else {
                            radioButton3.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            buttonShowCorrectAnswer.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 3:
                        if (cAnswer)
                            radioButton4.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        else {
                            radioButton4.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            buttonShowCorrectAnswer.setVisibility(View.VISIBLE);
                        }
                        break;

                }


            }
        });


    }

    private void listeners() {


        edtxAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (edtxAnswer.getText().toString().equals("Explain Briefly...!")) {
                        edtxAnswer.setText("");
                    }
                }
            }
        });


        buttonShowCorrectAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadIAd();
                buttonShowCorrectAnswer.setVisibility(View.INVISIBLE);
                showFSAd();
                highlightCorrectAns();
                frameLayoutAnswer.setVisibility(View.VISIBLE);
                edtxAnswer.setText(listQnAs.get(count).stringAnsExplained);
            }
        });

        buttonSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitAnswer(edtxAnswer.getText().toString());
            }
        });

        floatingActionButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frameLayoutAnswer.setVisibility(View.INVISIBLE);
                radioGroupOptions.setActivated(true);
                radioGroupOptions.clearCheck();
                radioButton1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                radioButton2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                radioButton3.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                radioButton4.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                if (count < listQnAs.size() - 1) {


                    if (correctAnswer || answerShown) {
                        answerShown = false;
                        radioGroupOptions.clearCheck();
                        count = count + 1;
                        loadQnAsforExam(count);
                    } else answerShown = true;

                } else
                    startActivity(new Intent(QuestionsActivity.this, ResultActivity.class).putExtra("score", Score));

            }
        });

        floatingActionButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frameLayoutAnswer.setVisibility(View.INVISIBLE);
                radioGroupOptions.setActivated(true);
                radioGroupOptions.clearCheck();
                radioButton1.setTextColor(getResources().getColor(R.color.black));
                radioButton2.setTextColor(getResources().getColor(R.color.black));
                radioButton3.setTextColor(getResources().getColor(R.color.black));
                radioButton4.setTextColor(getResources().getColor(R.color.black));

                if (count > 0) {

                    if (correctAnswer || answerShown) {
                        answerShown = false;
                        radioGroupOptions.clearCheck();
                        count = count - 1;
                        loadQnAsforExam(count);
                    } else answerShown = true;

                } else
                    startActivity(new Intent(QuestionsActivity.this, ResultActivity.class).putExtra("score", Score));

            }
        });


    }


    private void SubmitAnswer(String string) {
        makeToast("Ref - " + QsRef.child(String.valueOf(count + 1)).child("AE").setValue(string));
    }


    private void showBannerAd() {
        if (SplashActivity.prodFlag) {
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }


    private void showNativeAd() {
        if (SplashActivity.prodFlag) if (adLoaded) {
            templateView.invalidate();
            templateView.setVisibility(View.VISIBLE);
        } else {
            // Load the Native ad if it is not loaded
            loadNativeAd();
        }
    }

    private void loadNativeAd() {
        // Creating an Ad Request
        AdRequest adRequest = new AdRequest.Builder().build();

        // load Native Ad with the Request
        adLoader.loadAd(adRequest);
    }

    private void loadIAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.admob_interst_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });

    }

    private void highlightCorrectAns() {
        switch (Integer.parseInt(listQnAs.get(count).getStringA())) {
            case 0:
                radioButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                break;
            case 1:
                radioButton2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                break;
            case 2:
                radioButton3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                break;
            case 3:
                radioButton4.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                break;
        }
    }

    private void findViewByIds() {

        frameLayoutAnswer = findViewById(R.id.framelayout_answer);
        buttonSubmitAnswer = findViewById(R.id.btn_edtxanswer_submit);
        edtxAnswer = findViewById(R.id.edtx_answer_explained);

        mAdView = findViewById(R.id.adView);
        templateView = findViewById(R.id.nativeTemplateView);
        buttonShowCorrectAnswer = findViewById(R.id.btn_show_the_correct_answer);


        textViewMain = findViewById(R.id.tx_main);


        wbv = findViewById(R.id.webv);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        imageViewQuestion = findViewById(R.id.imgv_q);
        imageViewAnswer = findViewById(R.id.imgv_a);


        floatingActionButtonNext = findViewById(R.id.fab_next);
        floatingActionButtonPrev = findViewById(R.id.fab_prev);

        textViewQuestion = findViewById(R.id.tx_question);
        radioGroupOptions = findViewById(R.id.radio_group_options);
        radioButton1 = findViewById(R.id.radio_btn1);
        radioButton2 = findViewById(R.id.radio_btn2);
        radioButton3 = findViewById(R.id.radio_btn3);
        radioButton4 = findViewById(R.id.radio_btn4);


        /*recyclerView.setVisibility(View.GONE);
        textViewQuestion.setVisibility(View.GONE);
        radioGroupOptions.setVisibility(View.GONE);
        imageViewQuestion.setVisibility(View.GONE);
        imageViewAnswer.setVisibility(View.GONE);*/
    }

    private boolean Validate(int count) {

        if (count != 0) if (count % 5 == 0) {
            showFSAd();
        }

        int radioButtonID = radioGroupOptions.getCheckedRadioButtonId();
        View radioButton = radioGroupOptions.findViewById(radioButtonID);
        int idx = radioGroupOptions.indexOfChild(radioButton);

        if (Integer.parseInt(listQnAs.get(count).getStringA()) == idx) {
            correctAnswer = true;
            //    makeToast("Correct Answer");
            Score = Score + 1;
            return true;
        } else if (idx == -1) {
            correctAnswer = true;
        } else {
            correctAnswer = false;

            if (Integer.parseInt(listQnAs.get(count).getStringA()) == 0)
                answer = radioButton1.getText().toString();
            else if (Integer.parseInt(listQnAs.get(count).getStringA()) == 1)
                answer = radioButton2.getText().toString();
            else if (Integer.parseInt(listQnAs.get(count).getStringA()) == 2)
                answer = radioButton3.getText().toString();
            else answer = radioButton4.getText().toString();

            //    makeToast("Wrong Answer");


            Score = Score - 1;
            return false;
        }
        return false;
    }

    private void showFSAd() {
        if (SplashActivity.prodFlag) if (mInterstitialAd != null) {
            mInterstitialAd.show(QuestionsActivity.this);
        }
    }

    private void loadQnAsforExam(int i) {

        //    diagrams(subject,year,i);

        makeToast(count + "");

        Animation animTimeChange = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        textViewQuestion.startAnimation(animTimeChange);
        radioButton1.startAnimation(animTimeChange);
        radioButton2.startAnimation(animTimeChange);
        radioButton3.startAnimation(animTimeChange);
        radioButton4.startAnimation(animTimeChange);

        textViewQuestion.setText(Html.fromHtml(listQnAs.get(i).getStringQ()));
        radioButton1.setText(Html.fromHtml(listQnAs.get(i).getArrayListOptions().get(0)));
        radioButton2.setText(Html.fromHtml(listQnAs.get(i).getArrayListOptions().get(1)));
        radioButton3.setText(Html.fromHtml(listQnAs.get(i).getArrayListOptions().get(2)));
        radioButton4.setText(Html.fromHtml(listQnAs.get(i).getArrayListOptions().get(3)));

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);


        if (listQnAs.get(i).getStringQimg().length() > 1) {
            imageViewQuestion.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(listQnAs.get(i).getStringQimg()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    makeToast("Poor connectivity!");
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageViewQuestion);
        } else imageViewQuestion.setVisibility(View.GONE);

        if (listQnAs.get(i).getStringAimg().length() > 1) {
            imageViewAnswer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(listQnAs.get(i).getStringAimg()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageViewAnswer);
        } else imageViewAnswer.setVisibility(View.GONE);

        buttonShowCorrectAnswer.setVisibility(View.INVISIBLE);

    }


    public static ArrayList<QnA> getData(String subject, String year) {

        //   FirebaseApp.initializeApp(getApplicationContext());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference subRef = rootRef.child(subject);
        DatabaseReference yearRef = subRef.child(year);
        QsRef = yearRef.child("Questions");


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listQnAs = new ArrayList<>();
                String imgUrlQ = "", imgUrlA = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String strQ, strA, opt1, opt2, opt3, opt4;
                    ArrayList<String> arrayListOptions = new ArrayList<>();
                    if (subject.equals("Physics") && year.equals("2020")) {
                        strQ = ds.child("Question").getValue(String.class);
                        strA = String.valueOf(ds.child("Answer").getValue(Long.class));
                        opt1 = ds.child("Options").child("O1").getValue().toString();
                        opt2 = ds.child("Options").child("O2").getValue().toString();
                        opt3 = ds.child("Options").child("O3").getValue().toString();
                        opt4 = ds.child("Options").child("O4").getValue().toString();
                    } else {
                        strQ = ds.child("Q").getValue(String.class);
                        strA = String.valueOf(ds.child("A").getValue(Long.class));
                        opt1 = ds.child("O").child("O1").getValue().toString();
                        opt2 = ds.child("O").child("O2").getValue().toString();
                        opt3 = ds.child("O").child("O3").getValue().toString();
                        opt4 = ds.child("O").child("O4").getValue().toString();

                    }

                    if (ds.child("Dq") != null) if (ds.child("Dq").getValue() != null) {
                        if (ds.child("Dq").getValue().toString().length() > 0) {
                            imgUrlQ = ds.child("Dq").getValue().toString();
                        }
                    }
                    if (ds.child("Da") != null) if (ds.child("Da").getValue() != null) {
                        imgUrlA = ds.child("Da").getValue().toString();
                    }

                    arrayListOptions.add(opt1);
                    arrayListOptions.add(opt2);
                    arrayListOptions.add(opt3);
                    arrayListOptions.add(opt4);


                    if (imgUrlQ == null) {
                        if (imgUrlA == null) qnA = new QnA(strQ, strA, arrayListOptions, "", "");
                        else qnA = new QnA(strQ, strA, arrayListOptions, "", imgUrlA);
                    } else {
                        if (imgUrlA == null)
                            qnA = new QnA(strQ, strA, arrayListOptions, imgUrlQ, "");
                        else {
                            qnA = new QnA(strQ, strA, arrayListOptions, imgUrlQ, imgUrlA);
                            imgUrlA = "";
                            imgUrlQ = "";
                        }
                    }

                    if (ds.child("AE") != null)
                        if (ds.child("AE").getValue() != null) {
                            strAE = ds.child("AE").getValue().toString();
                        } else strAE = "Explain Briefly...!";

                    qnA.setStringAnsExplained(strAE);

                    listQnAs.add(qnA);


                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        QsRef.addListenerForSingleValueEvent(valueEventListener);


        return listQnAs;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(QuestionsActivity.this, ContactUsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}