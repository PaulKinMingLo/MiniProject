package com.example.miniproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class InfoActivity extends AppCompatActivity {
    private TextView textView;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    private String TAG = InfoActivity.class.getSimpleName();
    private TextView latitudeText;
    private TextView longitudeText;
    private String latitudeLabel;
    private String longitudeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        latitudeText = findViewById(R.id.latitude_text);
        longitudeText = findViewById(R.id.longitude_text);
        latitudeLabel = getResources().getString(R.string.latitude_label);
        longitudeLabel = getResources().getString(R.string.longitude_label);

        Intent intent = getIntent();
        String messageLat = intent.getStringExtra("latitude");
        String messageLng = intent.getStringExtra("longitude");

        try {
            latitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                    latitudeLabel, Double.valueOf(messageLat)));
        } catch (final NumberFormatException e) {
            Log.e(TAG, "Can not get latitude...");
            latitudeText.setText(String.format(Locale.ENGLISH, "%s: %s",
                    latitudeLabel, "null"));
        }

        try {
            longitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                    longitudeLabel, Double.valueOf(messageLng)));
        } catch (final NumberFormatException e) {
            Log.e(TAG, "Can not get longitude...");
            longitudeText.setText(String.format(Locale.ENGLISH, "%s: %s",
                    longitudeLabel, "null"));
        }

        latitudeText.setTextSize(getResources().getDimensionPixelSize(R.dimen.setting_text));
        longitudeText.setTextSize(getResources().getDimensionPixelSize(R.dimen.setting_text));

        textView = findViewById(R.id.info_text);
        textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.info_text));
        textView.setText(String.format(Locale.ENGLISH, "%d.%s", versionCode, versionName));
    }
}
