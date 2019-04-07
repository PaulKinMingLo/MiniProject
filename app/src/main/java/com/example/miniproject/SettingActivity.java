package com.example.miniproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private String TAG = SettingActivity.class.getSimpleName();
    private TextView latitudeText;
    private TextView longitudeText;
    private String latitudeLabel;
    private String longitudeLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        latitudeText = findViewById(R.id.setting_lat_msg);
        longitudeText = findViewById(R.id.setting_lng_msg);
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
    }
}
