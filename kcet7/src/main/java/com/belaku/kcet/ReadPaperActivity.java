package com.belaku.kcet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import static com.belaku.kcet.SplashActivity.makeToast;

public class ReadPaperActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private String year, subject;
    private ArrayList<QnA> listQnAs = new ArrayList<>();
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


                listQnAs = QuestionsActivity.getData(subject, year);
                recyclerView.setAdapter(new RvAdapter(listQnAs, getApplicationContext()));


              /*  while (true) {
                    if (listQnAs.size() > 0) {

                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                                    makeToast("-----" + "end");

                                }
                            }
                        });                        recyclerView.setAdapter(new RvAdapter(listQnAs, getApplicationContext()));
                        break;
                    }
                }*/



            }
        }
    }


}