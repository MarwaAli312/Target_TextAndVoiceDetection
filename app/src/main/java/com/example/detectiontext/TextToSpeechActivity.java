package com.example.detectiontext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.detectiontext.historyfragments.HistoryTTSFragment;
import com.example.detectiontext.loginfragments.LoginFragment;
import com.example.detectiontext.models.DataManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class TextToSpeechActivity extends AppCompatActivity {
    //private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    Button btnConvert,btnClear;
    ExtendedFloatingActionButton fab;
    EditText edInput;
    TextToSpeech textToSpeech;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;






    String text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);
        btnClear=findViewById(R.id.btnclear_tts);
        btnConvert=findViewById(R.id.btnconvert_tts);
        edInput=findViewById(R.id.edinput_tts);

        progressDialog=new ProgressDialog(this);



        fab=findViewById(R.id.history_tts);

        firebaseAuth=FirebaseAuth.getInstance();

        text=edInput.getText().toString().trim();



        textToSpeech= new TextToSpeech(TextToSpeechActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS) {
                    textToSpeech.setSpeechRate(1);
                }
                else{
                    Toast.makeText(TextToSpeechActivity.this,"Could not initialize TTS service",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(edInput.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edInput.setText("");
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TextToSpeechActivity.this,HistoryActivity.class);
                i.putExtra("dest","tts");
                startActivity(i);
                TextToSpeechActivity.this.finish();
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activities,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout){
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(TextToSpeechActivity.this, gso);
            mGoogleSignInClient.signOut();
            firebaseAuth.signOut();

            //checkUserStatus();
            FirebaseAuth.getInstance().signOut();
            progressDialog.dismiss();

            Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(TextToSpeechActivity.this, MainActivity.class);
            startActivity(i);


        }
        if(id==R.id.action_save){
            DataManager dataManager=new DataManager();
            text=edInput.getText().toString();
            if (!text.isEmpty()){
                dataManager.saveTTSData(text);
                Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Empty text box",Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            //user is signed in, stay in profile
        }
        else{
            //user not signed in, go to main activity
                startActivity(new Intent(TextToSpeechActivity.this, LaunchScreen.class));
            finish();
        }
    }


}