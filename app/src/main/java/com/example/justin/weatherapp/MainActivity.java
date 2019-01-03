package com.example.justin.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private static final String API_KEY = "64355b35be02c96771538526be272293";
    private TextView tempTV;
    private TextView feelsLikeTV;
    private ImageView iconImg;

    HashMap<String, String> jsonFieldVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        tempTV = (TextView)findViewById(R.id.temp);
        feelsLikeTV = (TextView)findViewById(R.id.feels_like);
        iconImg = (ImageView)findViewById(R.id.weather_icon);

        jsonFieldVals = new HashMap<>();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place
                Log.i(TAG, "Place: " + place.getName());
                new UpdateWeather().execute(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private class UpdateWeather extends AsyncTask<Place, Void, Void> {
        protected Void doInBackground(Place... place) {
            HttpHandler conHandler = new HttpHandler();
            String url = String.format("https://api.darksky.net/forecast/%s/%f,%f", API_KEY,
                    place[0].getLatLng().latitude, place[0].getLatLng().longitude);
            Log.e(TAG, "URL: " + url);
            String jsonStr = conHandler.makeServiceCall(url);

            Log.e(TAG, "JSON response: " + jsonStr);

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject curr = jsonObj.getJSONObject("currently");

                jsonFieldVals.put("currTemp", curr.getString("temperature"));
                jsonFieldVals.put("feelsLike", curr.getString("apparentTemperature"));
                jsonFieldVals.put("icon", curr.getString("icon"));

            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            tempTV.setText(strOfDoubleToRoundedIntStr(jsonFieldVals.get("currTemp")));
            feelsLikeTV.setText("Feels like: " + strOfDoubleToRoundedIntStr(jsonFieldVals.get("feelsLike")));

            determineWeatherIMG();
        }

        // @TODO: implement cases instead of else-if
        private void determineWeatherIMG() {
            String iconType = jsonFieldVals.get("icon");

            if (iconType.equals("clear-day")) {
                iconImg.setImageResource(R.drawable.clear_day);
            }
            else if (iconType.equals("clear-night")) {
                iconImg.setImageResource(R.drawable.clear_night);
            }
            else if (iconType.equals("rain")) {
                iconImg.setImageResource(R.drawable.rain);
            }
            else if (iconType.equals("snow")) {
                iconImg.setImageResource(R.drawable.snow);
            }
            else if (iconType.equals("sleet")) {
                iconImg.setImageResource(R.drawable.sleet);
            }
            else if (iconType.equals("wind")) {
                iconImg.setImageResource(R.drawable.wind);
            }
            else if (iconType.equals("fog")) {
                iconImg.setImageResource(R.drawable.fog);
            }
            else if (iconType.equals("cloudy")) {
                iconImg.setImageResource(R.drawable.cloudy);
            }
            else if (iconType.equals("partly-cloudy-day")) {
                iconImg.setImageResource(R.drawable.partly_cloudy_day);
            }
            else if (iconType.equals("partly-cloudy-night")) {
                iconImg.setImageResource(R.drawable.partly_cloudy_night);
            }
            else {
                // @TODO: implement default img
            }
        }

        private String strOfDoubleToRoundedIntStr(String dub) {
            return String.format("%d", Math.round(Double.parseDouble(dub)));
        }
    }
}
