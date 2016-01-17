package com.firstapp.shailesh.firstone;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by shailesh.bagade on 1/14/2016.
 */
public class SpeechRecognitionListener implements RecognitionListener
{
    private SpeechRecognizer mSpeechRecognizer;
    private boolean mateIslistening;
    private Intent mSpeechRecognizerIntent;
    private boolean isOperationComplete;
    private String data;
    @Override
    public void onBeginningOfSpeech()
    {
        mateIslistening = true;
    }

    public boolean isMateListing(){
        return mateIslistening;
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {

    }

    @Override
    public void onEndOfSpeech()
    {
        //Log.d(TAG, "onEndOfSpeech");
        mateIslistening = false;
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
    }

    @Override
    public void onResults(Bundle results)
    {
        //Log.d(TAG, "onResults"); //$NON-NLS-1$
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        data = matches.get(0).toString();
        isOperationComplete = true;
        //message.setText(matches.get(0).toString());
        // matches are the return values of speech recognition engine
        // Use these values for whatever you wish to do
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
    }

    public boolean isRecognitionComplete()
    {
        return isOperationComplete;
    }
    public String command()
    {
        return data;
    }
}
