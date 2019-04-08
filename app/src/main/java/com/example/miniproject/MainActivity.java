package com.example.miniproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String EXTRA_MESSAGE = "com.example.assignment.MESSAGE";

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    private String url = "";
    private String myLat = "";
    private String myLng = "";
    private String tempUrl = "";

    ArrayList<HashMap<String, String>> toiletList;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private FusedLocationProviderClient mFusedLocationClient;

    protected Location mLastLocation;

    private String currentLanguage;
    private String currentCountry;

    private String numOfRowPref = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        numOfRowPref = sharedPref.getString(SettingActivity.KEY_PREF_NUMOFROW, "pref_setting_numOfRow_default");
        Log.i(TAG, "default number of row : " + numOfRowPref);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        currentLanguage = Locale.getDefault().getLanguage();
        currentCountry = Locale.getDefault().getCountry();

        PreferenceManager.setDefaultValues(this, R.xml.setting_preference, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getResult();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getResult() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            myLat = String.valueOf(mLastLocation.getLatitude());
                            myLng = String.valueOf(mLastLocation.getLongitude());

                            tempUrl = "lat=";
                            tempUrl = tempUrl.concat(myLat);
                            tempUrl = tempUrl.concat("&lng=");
                            tempUrl = tempUrl.concat(myLng);

                            url = url.concat(getResources().getString(R.string.linkToServer));

                            Log.i(TAG, "The default language is " + currentCountry + " " + currentLanguage);

                            if (currentLanguage == "zh") {
                                tempUrl = tempUrl.concat("&lang=");
                                //TODO localization the app
                                if (currentCountry == "HK") {
                                    tempUrl = tempUrl.concat("zh");
                                }
                                if (currentCountry == "CN") {
                                    tempUrl = tempUrl.concat("zh_cn");
                                }
                            }

                            tempUrl = tempUrl.concat("&row_index=0&display_row=");
                            tempUrl = tempUrl.concat(numOfRowPref);

                            url = url.concat(tempUrl);

                            Log.i(TAG, url);
                            toiletList = new ArrayList<>();
                            lv = (ListView) findViewById(R.id.list);

                            ToiletListTask getToiletList = new ToiletListTask(MainActivity.this, lv);
                            getToiletList.setDistanceLabel(getResources().getString(R.string.distance_label));
                            getToiletList.setDistanceUnitLabel(getResources().getString(R.string.distanceUnit_label));
                            getToiletList.execute(url);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    ListView listView = (ListView) adapterView;
                                    Toast.makeText(MainActivity.this,
                                            listView.getItemAtPosition(i).toString(),
                                            Toast.LENGTH_SHORT).show();

                                    //TODO adding map activity
                                    //Intent intent_Map = new Intent();

                                    //String data = listView.getItemAtPosition(i).toString();
                                    //intent_Map.putExtra("MyLat", )
                                }
                            });

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startLocationPermissionRequest();
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //getLastLocation();
                getResult();
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent4 = new Intent();
                                intent4.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent4.setData(uri);
                                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent4);
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_retry) {
            Intent intent = new Intent(this, MainActivity.class);
            //String message = myLat;
            //intent.putExtra(EXTRA_MESSAGE, message);

            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        String extraMessageKey1 = "latitude";
        String extraMessageKey2 = "longitude";

        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_setting:
                Intent intent2 = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.nav_info:
                Intent intent3 = new Intent(getApplicationContext(), InfoActivity.class);
                if (myLat != null) {
                    intent3.putExtra(extraMessageKey1, myLat);
                }
                if (myLng != null) {
                    intent3.putExtra(extraMessageKey2, myLng);
                }
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
