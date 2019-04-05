package com.example.miniproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ToiletListTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
    private String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private ProgressDialog pDialog;
    private ListView lv;

    private String distanceLabel = null;
    private String distanceUnitLabel = null;

    ArrayList<HashMap<String, String>> toiletList = new ArrayList<>();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public ToiletListTask(Context cxt, ListView listView) {
        context = cxt;
        pDialog = new ProgressDialog(context);

        lv = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        //TODO: fixed the problem about windowLeaked
        pDialog.show();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
        int count = strings.length;
        HttpHandler sh = new HttpHandler();

        String jsonStr = null;
        for (int i = 0; i < count; i++) {
            jsonStr = sh.makeServiceCall(strings[i]);
        }

        Log.i(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray toiletResults = jsonObj.getJSONArray("results");

                for (int j = 0; j < toiletResults.length(); j++) {
                    JSONObject c = toiletResults.getJSONObject(j);

                    String name = c.getString("name");
                    String address = c.getString("address");
                    String latitude = c.getString("lat");
                    String longitude = c.getString("lng");
                    String distance = c.getString("distance");

                    HashMap<String, String> toilet = new HashMap<>();

                    DecimalFormat df = new DecimalFormat("#.##");
                    double distanceValue = Double.valueOf(distance);

                    distance = String.format(Locale.ENGLISH, "%s: %s %s",
                            distanceLabel, df.format(distanceValue), distanceUnitLabel);

                    toilet.put("name", name);
                    toilet.put("address", address);
                    toilet.put("distance", distance);

                    toiletList.add(toilet);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());

            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");

        }
        return toiletList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> list) {
        super.onPostExecute(list);
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

        ListAdapter adapter = new SimpleAdapter(
                context, list,
                R.layout.list_item, new String[]{"name", "address", "distance"},
                new int[]{R.id.name, R.id.address, R.id.distance});
        lv.setAdapter(adapter);
    }

    public void setDistanceLabel(String label) {
        distanceLabel = label;
    }

    public void setDistanceUnitLabel(String label) {
        distanceUnitLabel = label;
    }

}