package com.rounak.covid19india;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class FetchData extends AsyncTask<Void, Void, Void> {
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
            while(line != null){
                line = bufferedReader.readLine();
                data = data+line;
            }

            data = data.substring(4 , data.length() - 1);
            JSONObject jsonObject = new JSONObject(data);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");

            try {
                JSONArray jsonArray = jsonObject1.getJSONArray("regional");

                cases = new String[4][jsonArray.length()];
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object=jsonArray.getJSONObject(i);

                    cases[0][i] = object.getString("loc");
                    cases[1][i] = String.valueOf(object.getInt("totalConfirmed"));
                    cases[2][i] = String.valueOf(object.getInt("discharged"));
                    cases[3][i] = String.valueOf(object.getInt("deaths"));

                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }


        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        MainActivity.cases = Arrays.copyOf(this.cases, this.cases.length);

    }
}
