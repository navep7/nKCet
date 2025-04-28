package com.belaku.kcet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    private EditText editextScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        editextScore = findViewById(R.id.edtx_score);

        Intent intent = getIntent();

        if (intent != null) {
            Toast.makeText(getApplicationContext(), intent.getExtras().get("score") + " Score", Toast.LENGTH_LONG).show();
            editextScore.setText( intent.getExtras().get("score").toString());
        }

    }
}