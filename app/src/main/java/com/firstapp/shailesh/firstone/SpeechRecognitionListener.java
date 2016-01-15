package com.firstapp.shailesh.firstone;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

/**
 * Created by shailesh.bagade on 1/14/2016.
 */
public class SpeechRecognitionListener implements RecognitionListener
{
    private SpeechRecognizer mSpeechRecognizer;
    private boolean mIslistening;
    private Intent mSpeechRecognizerIntent;
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
        //message.setText(matches.get(0).toString());
        // matches are the return values of speech recognition engine
        // Use these values for whatever you wish to do
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
    }
}
