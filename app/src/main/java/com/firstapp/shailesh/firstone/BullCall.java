package com.firstapp.shailesh.firstone;

import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by _SHAILESH on 1/7/2016.
 */

public class BullCall extends AsyncTask<String,String, String>  {

    public IBullCallResponse delegate = null;
    @Override
    protected String doInBackground(String[] params)
    {
        //http://finance.yahoo.com/webservice/v1/symbols/VEDL.BO/quote?format=json
        //http://finance.yahoo.com/webservice/v1/symbols/VEDL.BO/quote?format=json&view=detail
        URL url;
        HttpURLConnection urlConnection = null;
        String response = null;

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();

            if(responseCode != -1){
                response = readStream(urlConnection.getInputStream());
            }else{
                Log.v("CatalogClient", "Response code:"+ responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.postResult(result);
    }
}
