package com.example.detectiontext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SpeechToTextActivity_Google extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    ImageButton btnspeak;
    EditText edtext;
    Button btnlang;
    AlertDialog.Builder builder;
    final String[] languages=new String[]{"English","French"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text_google);
        btnspeak=findViewById(R.id.btnspeak_stt);
        edtext=findViewById(R.id.edtext_stt);
        btnlang=findViewById(R.id.btnlang_stt);


        btnlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder=new AlertDialog.Builder(SpeechToTextActivity_Google.this);
                builder.setTitle("Choose a Language");
                builder.setSingleChoiceItems(languages, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s= Arrays.asList(languages).get(which);
                        Boolean success=updateSpeechLanguage(s);
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });


        btnspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }

    private Boolean updateSpeechLanguage(String s) {
        /*switch (s){
            case "French":
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                Toast.makeText(TextToSpeechActivity.this,"French",Toast.LENGTH_SHORT).show();
                return true;
            case "English":
                textToSpeech.setLanguage(Locale.US);
                Toast.makeText(TextToSpeechActivity.this,"English",Toast.LENGTH_SHORT).show();
                return true;
        }*/

        return false;
    }

    private void speak() {
        //show speech dialog
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi speak something");

        try{
            //start voice detection
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);

        }
        catch (Exception e){
            System.out.println(e.getMessage());

        }
    }

    //receive voice

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
            {
                if(resultCode == RESULT_OK && data !=null){
                    //get text from voice intent
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //show
                    edtext.setText(result.get(0));
                }
            }
        }
    }
}