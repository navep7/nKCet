package com.parvatha.kcet;

import static com.parvatha.kcet.SplashActivity.makeToast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReadPaperActivity extends AppCompatActivity {

    private static DatabaseReference QsRef;
    private static ArrayList<QnA> listQnAs = new ArrayList<>();
    private static QnA qnA;
    private static String strAE;

    private RecyclerView recyclerView;
    private String year, subject;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_paper);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();

        if (intent != null) {
            if (intent.getExtras() != null) {

                year = intent.getExtras().get("year").toString();
                subject = intent.getExtras().get("subject").toString();
                FirebaseApp.initializeApp(getApplicationContext());

                listQnAs = getData(subject, year);
                recyclerView.setAdapter(new RvAdapter(listQnAs, getApplicationContext()));


            }
        }
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

                    String strQ, strA = "", opt1, opt2, opt3, opt4;
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
                        try {
                            strA = String.valueOf(ds.child("A").getValue(Long.class));
                        } catch (Exception ex) {
                            makeToast(ex.getStackTrace().toString());
                        }
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


}