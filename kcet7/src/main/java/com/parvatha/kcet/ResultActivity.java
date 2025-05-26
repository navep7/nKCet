package com.parvatha.kcet;

import static com.parvatha.kcet.SplashActivity.makeToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class ResultActivity extends AppCompatActivity {

    private EditText editextScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        editextScore = findViewById(R.id.edtx_score);

        Intent intent = getIntent();

        if (intent != null) {
           makeToast(intent.getExtras().get("score") + " Score");
            editextScore.setText( intent.getExtras().get("score").toString());
        }

    }
}