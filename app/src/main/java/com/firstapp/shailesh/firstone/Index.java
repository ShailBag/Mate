package com.firstapp.shailesh.firstone;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.RemoteConference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Index extends AppCompatActivity implements TextToSpeech.OnInitListener,IBullCallResponse {

    EditText message;
    private TextToSpeech textToSpeech;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //for inline speech
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    protected boolean mIslistening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        message = (EditText)findViewById(R.id.message);
        textToSpeech= new TextToSpeech(this,this);
        //for inline speech
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
    }

    @Override
     protected void onStart() {
        super.onStart();
        //Speakup("This is Start event");
    }

    public  void onTellClick(View v)
    {
        TextView userName = (TextView)findViewById(R.id.UserName);
        String myMessage = userName.getText().toString();
        //script call to fetch BSE details
        if (myMessage.isEmpty() || myMessage == null)
            myMessage = "VEDL";
        BullCall bullCall = new BullCall();
        bullCall.delegate = this;
        String scriptUrl = "http://finance.yahoo.com/webservice/v1/symbols/" + myMessage + ".BO/quote?format=json&view=detail";
        bullCall.execute(scriptUrl);

    }

    public  void onUserSaveClick(View v)
    {
        TextView userName = (TextView)findViewById(R.id.UserName);
        String myMessage = userName.getText().toString();
        message.setText(myMessage);

        //inline speech
        if (!mIslistening)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_bye)
        {
            String mymessage ="Good bye";
            message.setText(mymessage);
        }
        else{
            //setContentView(R.layout.activity_addscript);
            Intent intent = new Intent(this, add_script.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)
        {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TextToSpeech","Language not supported.");
            }
        }
    }

    private void Speakup(String myMessage) {
        textToSpeech.speak(myMessage, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT: {
                if( resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> myInput = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    message.setText(myInput.get(0));
                }
            }
        }
    }

    private void SpeechToText()
    {
        Intent speechInput = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechInput.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechInput.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        speechInput.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try{
            startActivityForResult(speechInput,REQ_CODE_SPEECH_INPUT);
        }
        catch (Exception ex) {
            Log.e("Parker","Some error.");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void postResult(String asyncresult) {
        message = (EditText)findViewById(R.id.message);
        asyncresult = "[" + asyncresult + "]";


        try {
            JSONArray ja1 = new JSONArray(asyncresult);
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
            Script scriptObj = new Script(scriptName,scriptPrice,scriptDayHigh,scriptDayLow);
            message.setText(scriptObj.getScriptName());
            String scriptMessage = "Sir " + "price of " + scriptObj.getScriptName() + " is " + scriptObj.getPrice();
            Speakup(scriptMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
            mIslistening = true;
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech");
            mIslistening = false;
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            //Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
            String some = "ready for instructions";
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            message.setText(matches.get(0).toString());
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
}
