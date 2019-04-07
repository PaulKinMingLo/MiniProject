package com.example.miniproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class InfoActivity extends AppCompatActivity {
    private TextView textView;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        textView = findViewById(R.id.info_text);
        textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.info_text));
        textView.setText(String.format(Locale.ENGLISH, "%d.%s", versionCode, versionName));
    }
}
