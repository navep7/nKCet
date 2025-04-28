package com.belaku.kcet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactUsActivity extends AppCompatActivity {

    Handler handler = new Handler();
    private ProgressBar progressBar;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        TextView textView = findViewById(R.id.tx_more_apps);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

     /*   TextView mailTo = (TextView) findViewById(R.id.tx_mailto);
        mailTo.setText(Html.fromHtml("<a href=\"mailto:karnataka.cet7@gmail.com\">karnataka.cet7@gmail.com</a>"));
        mailTo.setMovementMethod(LinkMovementMethod.getInstance());*/

        TextView phone = (TextView) findViewById(R.id.tx_phno);
        phone.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel://"+ phone.getText().toString().trim()))));


    }

    @Override
    protected void onResume() {
        super.onResume();

        progressStatus = 0;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ContactUsActivity.this, SubjectActivity.class));
            }
        }, 10000);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            //    textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}