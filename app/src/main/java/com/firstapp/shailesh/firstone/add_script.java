package com.firstapp.shailesh.firstone;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

public class add_script extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText scriptName;
    private EditText message;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addscript);
        scriptName = (EditText)findViewById(R.id.scriptName);
        //scriptName.setText("Vedanta");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_bye)
        {
            String mymessage ="Good bye";
            //message.setText(mymessage);
            Speakup(mymessage);
        }
        else{
            //setContentView(R.layout.activity_addscript);
            Intent intent = new Intent(this, add_script.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void Speakup(String myMessage)
    {
        textToSpeech.speak(myMessage, TextToSpeech.QUEUE_FLUSH, null);

    }

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

    public void onAddClick(View v)
    {
        Speakup(scriptName.getText().toString());
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
