package com.example.apitesting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView text;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ApiRequestTask().execute();
            }
        });

    }

    public class ApiRequestTask extends AsyncTask<Void, Void, JSONObject> {
        private final String apiEndPoint = "advanced_panchang";
        private final String userId = "{YourUserID}";
        private final String apiKey = "{YourApiKey}";
        private final String language = "en";
        private final int day = 15;
        private final int month = 9;
        private final int year = 1994;
        private final int hour = 12;
        private final int min = 30;
        private final double lat = 28.6139;
        private final double lon = 77.1025;
        private final double tzone = 5.5;

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL("https://json.apireports.com/v1/" + apiEndPoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                String api = "69908a3ae9144cf4918985a785114a46";
                connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((666 + ":" + api).getBytes(), Base64.NO_WRAP));
                connection.setRequestProperty("Accept-Language", language);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject data = new JSONObject();
                data.put("day", day);
                data.put("month", month);
                data.put("year", year);
                data.put("hour", hour);
                data.put("min", min);
                data.put("lat", lat);
                data.put("lon", lon);
                data.put("tzone", tzone);

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(data.toString());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return new JSONObject(response.toString());
                } else {
                    Log.e("ApiRequest", "HTTP error code: " + responseCode);
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String date = data.getString("date");
                    String month = data.getString("month");
                    String year = data.getString("year");
                    String day = data.getString("day");

                    JSONObject sun = data.getJSONObject("sun");
                    String sunrise = sun.getString("sunrise");
                    String sunset = sun.getString("sunset");

                    JSONObject ayana = data.getJSONObject("ayana");
                    String ayana_number = ayana.getString("ayana_number");
                    String ayana_name = ayana.getString("ayana_name");
                    String meaning = ayana.getString("meaning");

                    // Update your TextView with the extracted data.
                    String displayText = "Date: " + date + "\nMonth: " + month + "\nYear: " + year +
                            "\nDay: " + day + "\nSunrise: " + sunrise + "\nSunset: " + sunset + "\n \n \n ayana_number: " + ayana_number + "\nayana_name: " + ayana_name + "\nmeaning: " + meaning;
                    text.setText(displayText);

                } catch (JSONException e) {
                    e.printStackTrace();
                    text.setText("Error parsing JSON");
                }
            } else {
                //Log.e("ApiRequest", "Failed to fetch data.");
                text.setText("Error...");
                // Handle the error here
            }
        }
    }
}
