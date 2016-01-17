package com.firstapp.shailesh.firstone;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by _SHAILESH on 1/17/2016.
 */
public class ShareMarketRequests extends AsyncTask<String,String,String> {
    public IAyncTaskCallBack delegate = null;
    private String response = null;
    HttpURLConnection urlConnection = null;
    URL url;

    @Override
    protected String doInBackground(String[] params) {
        String command = params[0];
        String scriptCode = getScriptNameToCode(command);
        if(scriptCode.isEmpty())
            response = "Sorry sir, unable to identify script.";

        if(command.contains("market today")  || command.contains("nifty")
                || command.contains("sensex"))
        {
            //pass to IndexRequest
            IndexRequest(scriptCode);
        }
        if(command.contains("share price")){
            //pass to SharePriceRequest
            SharePriceRequest(scriptCode);
        }

        return response;
    }

    private void IndexRequest(String requestData)
    {
        Script scriptDetail = SendRequest(requestData);
        if(scriptDetail == null)
            response = "Sorry sir, unable to process request";
        response = "Sir, " + scriptDetail.getScriptName() + " is " + scriptDetail.getPrice();
        if(scriptDetail.Change.contains("-"))
            response = response + ". It's down by " + scriptDetail.Change.replace("-","") + "points";
        else
            response = response + ". It's up by " + scriptDetail.Change.replace("+","") + "points";
    }

    private void SharePriceRequest(String requestData)
    {
        Script scriptDetail = SendRequest(requestData);
        if(scriptDetail == null)
            response = "Sorry sir, unable to process request";
        response = "Sir, " + "price of " + scriptDetail.getScriptName() + " is " + scriptDetail.getPrice();

    }

    private Script SendRequest(String requestData)
    {
        try {
            String scriptUrl = "http://finance.yahoo.com/webservice/v1/symbols/" + requestData + "/quote?format=json&view=detail";
            url = new URL(scriptUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();

            if(responseCode != -1){
                String data = readStream(urlConnection.getInputStream());
                Script scriptDetail = processScriptResponse(data);
                return scriptDetail;
            }else{
                Log.v("CatalogClient", "Response code:" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    //process share price json response
    private Script processScriptResponse(String data)
    {
        Script scriptObj = null;
        data = "[" + data + "]";
        try {
            JSONArray ja1 = new JSONArray(data);
            JSONObject jsonObj = ja1.getJSONObject(0);
            String scriptDetail = jsonObj.getString("list");
            JSONObject jsonObj1 = jsonObj.getJSONObject("list");
            JSONArray jsonA1 = jsonObj1.getJSONArray("resources");
            JSONObject jsonObj3 = jsonA1.getJSONObject(0);
            JSONObject jsonObj4 = jsonObj3.getJSONObject("resource");
            JSONObject jsonObj5 = jsonObj4.getJSONObject("fields");
            String scriptName = jsonObj5.getString("name");
            String scriptPrice = jsonObj5.getString("price");
            String scriptDayHigh = jsonObj5.getString("day_high");
            String scriptDayLow = jsonObj5.getString("day_low");
            String scriptChange = jsonObj5.getString("change");
            scriptObj = new Script(scriptName,scriptPrice,scriptDayHigh,scriptDayLow,scriptChange);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return scriptObj;
    }

    //process response
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

    //script name to code
    private String getScriptNameToCode(String data)
    {
        //connect to sql to
        if(data.contains("vedanta"))
            data = "VEDL.BO";
        if(data.contains("reliance communication"))
            data = "RCOM.BO";
        if(data.contains("nifty"))
            data = "%5ENSEI";
        if(data.contains("sensex"))
            data = "%5EBSESN";
        if(data.contains("market today"))
        {
            data = "%5ENSEI";
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.TaskCallBack(result);
    }
}
