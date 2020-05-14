package com.rounak.covid19india;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {
    CardView cvRed, cvOrange, cvGreen;
    Animation fade, fade_in, fade_out, move_down, move_right_in, move_right_out;
    TextView tvTotal, tvDischarged, tvDeaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDeaths = findViewById(R.id.tvDeaths);
        tvDischarged = findViewById(R.id.tvDischarged);
        tvTotal = findViewById(R.id.tvTotal);

        new Fetch().execute();

        cvGreen = findViewById(R.id.cvGreen);
        cvRed = findViewById(R.id.cvRed);
        cvOrange = findViewById(R.id.cvOrange);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        move_down = AnimationUtils.loadAnimation(this, R.anim.move_down);
        move_right_in = AnimationUtils.loadAnimation(this, R.anim.move_right_in);
        move_right_out = AnimationUtils.loadAnimation(this, R.anim.move_right_out);

        cvGreen.setAlpha(0);
        cvRed.setAlpha(0);
        cvOrange.setAlpha(0);

    }

    class Fetch extends AsyncTask<Void, Void, Void> {
        String data;
        String[][] cases;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL("https://api.rootnet.in/covid19-in/stats/latest");

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = String.format("%s%s", data, line);
                }

                data = data.substring(4, data.length() - 1);
                JSONObject jsonObject = new JSONObject(data);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");


                try {
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("regional");
                    cases = new String[4][jsonArray1.length() + 1];

                    JSONArray jsonArray = jsonObject1.getJSONArray("unofficial-summary");
                    JSONObject overallObject = jsonArray.getJSONObject(0);

                    cases[0][0] = "Overall";
                    cases[1][0] = String.valueOf(overallObject.getInt("total"));
                    cases[2][0] = String.valueOf(overallObject.getInt("recovered"));
                    cases[3][0] = String.valueOf(overallObject.getInt("deaths"));

                    for (int i = 1; i <= jsonArray1.length(); i++) {
                        JSONObject object = jsonArray1.getJSONObject(i-1);

                        cases[0][i] = object.getString("loc");
                        cases[1][i] = String.valueOf(object.getInt("totalConfirmed"));
                        cases[2][i] = String.valueOf(object.getInt("discharged"));
                        cases[3][i] = String.valueOf(object.getInt("deaths"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final String[] items = new String[35];
            items[0] = "Choose Location";
            System.arraycopy(cases[0], 0, items, 1, 34);



            final Spinner dropdown = findViewById(R.id.spinner1);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(adapter);

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                int c = 0;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (dropdown.getItemAtPosition(position).toString().equals(items[0])) {
                        cvGreen.startAnimation(move_right_out);
                        cvRed.startAnimation(move_right_out);
                        cvOrange.startAnimation(move_right_out);

                        cvOrange.setVisibility(View.INVISIBLE);
                        cvGreen.setVisibility(View.INVISIBLE);
                        cvRed.setVisibility(View.INVISIBLE);

                        c = 0;
                    } else {
                        cvGreen.setVisibility(View.VISIBLE);
                        cvRed.setVisibility(View.VISIBLE);
                        cvOrange.setVisibility(View.VISIBLE);

                        if(c==0) {
                            c=1;
                            cvOrange.setAlpha(1);
                            cvOrange.startAnimation(fade);
                            cvRed.setAlpha(1);
                            cvRed.startAnimation(fade);
                            cvGreen.setAlpha(1);
                            cvGreen.startAnimation(fade);
                        }
                        else{
                            cvOrange.setAlpha(1);
                            cvOrange.startAnimation(move_right_in);
                            cvRed.setAlpha(1);
                            cvRed.startAnimation(move_right_in);
                            cvGreen.setAlpha(1);
                            cvGreen.startAnimation(move_right_in);
                        }


                        tvDeaths.setText(cases[3][position - 1]);
                        tvTotal.setText(cases[1][position - 1]);
                        tvDischarged.setText(cases[2][position - 1]);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


}
