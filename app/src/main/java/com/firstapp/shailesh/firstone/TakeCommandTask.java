package com.firstapp.shailesh.firstone;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by _SHAILESH on 1/16/2016.
 */
public class TakeCommandTask extends AsyncTask<String,String,String> {
    public IAyncTaskCallBack delegate = null;
    private String data;
    private Context commandContext;

    public TakeCommandTask(Context context)
    {
        commandContext = context;
    }

    @Override
    protected String doInBackground(String[] params) {

        return data;
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.TaskCallBack(result);
    }


}
