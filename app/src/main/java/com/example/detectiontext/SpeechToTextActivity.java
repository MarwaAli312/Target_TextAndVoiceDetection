package com.example.detectiontext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.detectiontext.models.DataManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageView micButton;
    Button clearTxt;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch;
    ExtendedFloatingActionButton fab;
    ProgressDialog progressDialog;




    FirebaseAuth firebaseAuth;
    DataManager dataManager;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        progressDialog=new ProgressDialog(this);

        editText = findViewById(R.id.text);
        micButton = findViewById(R.id.button_stt);
        fab=findViewById(R.id.history_stt);
        aSwitch=findViewById(R.id.switchSpeak);
        clearTxt=findViewById(R.id.btnclear_stt);



        firebaseAuth=FirebaseAuth.getInstance();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        clearTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SpeechToTextActivity.this,HistoryActivity.class);
                i.putExtra("dest","stt");
                startActivity(i);
                SpeechToTextActivity.this.finish();
            }
        });


        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500000000);


        dataManager=new DataManager();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    micButton.setImageResource(R.drawable.ic_mic_red);
                    speechRecognizer.startListening(speechRecognizerIntent);


                }
                else {
                    micButton.setImageResource(R.drawable.ic_mic);
                    speechRecognizer.stopListening();

                }
            }
        });




        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {


            }

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(SpeechToTextActivity.this,"End of Speech",Toast.LENGTH_SHORT).show();
                aSwitch.setChecked(false);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //micButton.setImageResource(R.drawable.ic_mic);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));


            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));


            }

            @Override
            public void onEvent(int i, Bundle bundle) {

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
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }

            else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();

            }
        }
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
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(SpeechToTextActivity.this, gso);

            mGoogleSignInClient.signOut();
            firebaseAuth.signOut();

            //checkUserStatus();
            FirebaseAuth.getInstance().signOut();
            progressDialog.dismiss();
            Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(SpeechToTextActivity.this, MainActivity.class);
            startActivity(i);
        }
        if(id==R.id.action_save){
            if (!editText.getText().toString().isEmpty()){
             DataManager dataManager=new DataManager();
            dataManager.saveSTTData(editText.getText().toString());
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
            startActivity(new Intent(SpeechToTextActivity.this, LaunchScreen.class));
            finish();
        }
    }
}