package com.example.martin.android_kehitys;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchSportData extends AsyncTask<Object, Void, String> {
    private int i;
    private Object clazz;

    @Override
    protected String doInBackground(Object... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        i = (int) params[0];
        clazz = params[1];

        String urlS;

        switch (i) {
            case 0:
                if (params.length == 2) {
                    urlS = "schedule";
                } else {
                    urlS = "schedule?startDate=" + ((String) params[2]) + "&endDate=" + ((String) params[3]);
                }

                break;
            case 1:
                urlS = "teams"; break;
            case 2:
                urlS = "teams?expand=team.roster"; break;
            case 3:
                urlS = "game/" + params[2] + "/feed/live"; break;
            case 4:
                urlS = "teams/" + params[2] + "?expand=team.roster"; break;
            case 5:
                urlS = "people/" + ((int) params[2]); break;
            default:
                urlS = null; break;
        }

        try {
            URL url = new URL("https://statsapi.web.nhl.com/api/v1/" + urlS);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonStr = buffer.toString();
            return jsonStr;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);

        try {
            final JSONObject data = new JSONObject(s);

            switch (i) {
                case 0: ((GamesFragment) clazz).setData(data); break;
                case 1: ((TeamsFragment) clazz).setData(data); break;
                case 2: ((PlayersFragment) clazz).setData(data); break;
                case 3: ((GameActivity) clazz).setData(data); break;
                case 4: ((TeamActivity) clazz).setData(data); break;
                case 5: ((PlayerActivity) clazz).setData(data); break;
                default: return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
