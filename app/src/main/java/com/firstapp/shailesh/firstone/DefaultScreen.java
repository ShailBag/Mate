package com.firstapp.shailesh.firstone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by shailesh.bagade on 1/14/2016.
 * Main class for mate
 * IAsynTaskCallBack is used in AsynTask as delegate whose function is CallBack function on Taskcompletion providing us task output
 *
 */
public class DefaultScreen extends AppCompatActivity implements TextToSpeech.OnInitListener,IAyncTaskCallBack {
    private SpeechRecognizer mateSpeechRecognizer;
    private TextToSpeech textToSpeech;
    private Intent mSpeechRecognizerIntent;
    protected boolean mIslistening;
    protected SpeechRecognitionListener listener;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private boolean playedStartupUpMessage;
    private final static int interval = 60000;
    Handler handler;
    Runnable mateHandlerTask;
    boolean isRepeatTaskAssigned = false;
    String repeatTaskName= null;
    boolean isSpeakingNow =false;
    boolean isSpeechRecongnitionInitialed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_screen);
        //initialize speak ability
        textToSpeech= new TextToSpeech(this,this);
        registerReapeatTask();
    }

    private void registerReapeatTask()
    {
        //repeat task
        final Handler handler = new Handler();
        mateHandlerTask = new Runnable() {
            @Override
            public void run() {
                //startConversation();
                repeatTask();
                handler.postDelayed(this, interval);
            }
        };
        mateHandlerTask.run();
    }

    private void startConversation()
    {
        if(isSpeechRecongnitionInitialed)
            takeCommand();
    }

    private void stopConversation()
    {
        isRepeatTaskAssigned = false;
        handler.removeCallbacks(mateHandlerTask);
    }

    private void repeatTask()
    {
        if(isRepeatTaskAssigned)
            identifyAndExecuteCommand(repeatTaskName);
    }

    //Application Start event
    public void onMateStartButtonClick(View v)
    {
        if(!isSpeechRecongnitionInitialed)
        {
            //initialize listening ability
            initializeSpeechRecognition();
            isSpeechRecongnitionInitialed = true;
        }
        if(!playedStartupUpMessage)
        {
            speakUp(greetMessage());
            playedStartupUpMessage =true;
        }

        EditText MateMessage =(EditText)findViewById(R.id.MateMessage);
        identifyAndExecuteCommand(MateMessage.getText().toString());
        takeCommand();
    }

    //text to speech event
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)
        {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TextToSpeech", "Language not supported.");
            }
        }
    }

    private void initializeSpeechRecognition()
    {
        //for inline speech
        mateSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
        //intent.putExtra("android.speech.extra.DICTATION_MODE", true);
        listener = new SpeechRecognitionListener();
        mateSpeechRecognizer.setRecognitionListener(listener);
    }

    private void takeCommand()
    {
        if (!mIslistening && !isSpeakingNow )
        {
            mateSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
        if(mIslistening){
            speakUp("its still listening");
        }
    }

    private void speakUp(String message)
    {
        isSpeakingNow = true;
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        int messageLength = message.length();
        try{
            Thread.sleep(4000);
            isSpeakingNow =false;
            takeCommand();
        }
        catch(Exception e){
        }
    }

    private String greetMessage()
    {
        Calendar c = Calendar.getInstance();
        String greet = "";

        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentHour < 12)
            greet = "Good morning sir";
        else if(currentHour > 12 && currentHour <17 )
            greet = "Good afternoon sir";
        else
            greet = "Good evening sir";

        Random random = new Random();
        int randomNum = random.nextInt(3);
        String[] greetMessages = {"how may I assist you?","How can I help you?"," what is up?"};
        greet += greetMessages[randomNum];
        return greet;
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (mateSpeechRecognizer != null)
        {
            mateSpeechRecognizer.destroy();
        }
        super.onDestroy();
    }

    //Aync task response
    @Override
    public void TaskCallBack(String data) {
        speakUp(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EditText MateMessage =(EditText)findViewById(R.id.MateMessage);
        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT: {
                if( resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> myInput = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    MateMessage.setText(myInput.get(0));
                }
            }
        }
    }

    private void identifyAndExecuteCommand(String command)
    {
        if(command.isEmpty())
            return;
        command = command.toLowerCase();

        EditText MateMessage =(EditText)findViewById(R.id.MateMessage);
        if(command.contains("clear screen"))
        {
            MateMessage.setText("");
        }

        if(command.contains("stop assigned task") || command.contains("stop repeating"))
        {
            isRepeatTaskAssigned = false;
            speakUp("ok sir cancelling it.");
        }

        if(command.contains("stop conversation") || command.contains("go to sleep"))
            stopConversation();

        //repeat task command
        String repeatCommand1 ="keep telling me";
        String repeatCommand2 ="keep informing me";
        String repeatCommand3 ="keep me informed";
        String repeatCommand4 ="keep me updated";
        if(command.contains(repeatCommand1) || command.contains(repeatCommand2)|| command.contains(repeatCommand3)
                ||command.contains(repeatCommand4))
        {
            String repeatCommand;
            repeatCommand = command.replace(repeatCommand1,"");
            repeatCommand = command.replace(repeatCommand2,"");
            repeatCommand = command.replace(repeatCommand3,"");
            repeatCommand = command.replace(repeatCommand4,"");
            repeatCommand = command.replace("about","");
            repeatCommand = command.replace("on","");

            isRepeatTaskAssigned = true;
            repeatTaskName = repeatCommand;
            speakUp("ok sir, i'll keep to updated on " + repeatCommand);
            return;
        }

        //share market commands
        if(command.contains("market today") || command.contains("share price") || command.contains("nifty")
                || command.contains("sensex"))
        {
            ShareMarketRequests request = new ShareMarketRequests();
            request.delegate = this;
            request.execute(command);
        }

    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            mIslistening = true;
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            mIslistening = false;
        }

        @Override
        public void onError(int error)
        {
            mateSpeechRecognizer.startListening(mSpeechRecognizerIntent);
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
            EditText MateMessage =(EditText)findViewById(R.id.MateMessage);
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            MateMessage.setText("");
            MateMessage.setText(matches.get(0).toString());
            identifyAndExecuteCommand(matches.get(0).toString());
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }//speech recognizer end

}
