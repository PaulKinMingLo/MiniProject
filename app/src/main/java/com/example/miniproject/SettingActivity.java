package com.example.miniproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
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
    }
}
