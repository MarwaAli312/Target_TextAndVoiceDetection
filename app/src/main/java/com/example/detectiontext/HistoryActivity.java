package com.example.detectiontext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.detectiontext.historyfragments.HistoryImgTTFragment;
import com.example.detectiontext.historyfragments.HistorySTTFragment;
import com.example.detectiontext.historyfragments.HistoryTTSFragment;
import com.example.detectiontext.models.DataManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HistoryActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    BottomNavigationView navigationView;
    ActionBar actionBar;

    String frag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        actionBar=getSupportActionBar();
        actionBar.setTitle("Image to Text History");
        frag="ImageToText";


        firebaseAuth=FirebaseAuth.getInstance();
        navigationView=findViewById(R.id.bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        Boolean b=false;


        try{
            Intent i=getIntent();
            String f=i.getExtras().getString("dest");
            b=true;
            switch (f){
                case "tts":
                    frag="TextToSpeech";
                    actionBar.setTitle("Text to Speech History");
                    HistoryTTSFragment fragment2=new HistoryTTSFragment();
                    FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.container_hisory,fragment2,"");
                    navigationView.setSelectedItemId(R.id.tts_nav);
                    ft2.commit();

                    break;
                case "stt":
                    frag="SpeechToText";
                    actionBar.setTitle("Speech to Text History");
                    HistorySTTFragment fragment3=new HistorySTTFragment();
                    FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.container_hisory,fragment3,"");
                    navigationView.setSelectedItemId(R.id.stt_nav);
                    ft3.commit();
                    break;
                case "imgtt":
                    frag="ImageToText";
                    actionBar.setTitle("Image to Text History");
                    HistoryImgTTFragment fragment1=new HistoryImgTTFragment();
                    FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                    navigationView.setSelectedItemId(R.id.imgtt_nav);
                    ft1.replace(R.id.container_hisory,fragment1,"");
                    ft1.commit();
                    break;
            }
        }
        catch (Exception e){

        }
        if(b==false){
            //default fragment transaction: Home
            HistoryImgTTFragment fragmentImgtt=new HistoryImgTTFragment();
            FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.container_hisory,fragmentImgtt,"");
            ft1.commit();
        }





    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HistoryActivity.this.finish();    }

    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //handle item clicks
                    switch (item.getItemId()){
                        case R.id.imgtt_nav:
                            //home fragment transaction
                            frag="ImageToText";
                            actionBar.setTitle("Image to Text History");
                            HistoryImgTTFragment fragment1=new HistoryImgTTFragment();
                            FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.container_hisory,fragment1,"");
                            ft1.commit();
                            return true;
                        case R.id.tts_nav:
                            frag="TextToSpeech";
                            actionBar.setTitle("Text to Speech History");
                            HistoryTTSFragment fragment2=new HistoryTTSFragment();
                            FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.container_hisory,fragment2,"");
                            ft2.commit();
                            return true;
                        case R.id.stt_nav:
                            frag="SpeechToText";
                            actionBar.setTitle("Speech to Text History");
                            HistorySTTFragment fragment3=new HistorySTTFragment();
                            FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.container_hisory,fragment3,"");
                            ft3.commit();
                            return true;
                    }
                    return false;
                }
            };


    private void checkUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            //user is signed in, stay in profile
        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(HistoryActivity.this, LaunchScreen.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history_fragments,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(HistoryActivity.this, gso);
            mGoogleSignInClient.signOut();
            firebaseAuth.signOut();

            //checkUserStatus();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(i);
        }

        if(id==R.id.action_delete_all){
            AlertDialog.Builder builder=new AlertDialog.Builder(HistoryActivity.this);
            builder.setTitle("Confirmation Required");
            builder.setMessage("Delete All items?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataManager dataManager=new DataManager();
                    dataManager.deleteAll(frag);
                    refreshFragment();
                    Toast.makeText(HistoryActivity.this,"All items are deleted",Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.create();
            //alert.setTitle("AlertDialogExample");
            alert.show();
        }


        return super.onOptionsItemSelected(item);
    }

    private void refreshFragment() {
        switch (frag){
            case "ImageToText":
                HistoryImgTTFragment fragment1=new HistoryImgTTFragment();
                FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.container_hisory,fragment1,"");
                ft1.commit();
                break;
            case "SpeechToText":
                HistorySTTFragment fragment3=new HistorySTTFragment();
                FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.container_hisory,fragment3,"");
                ft3.commit();
                break;
            case "TextToSpeech":
                HistoryTTSFragment fragment2=new HistoryTTSFragment();
                FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.container_hisory,fragment2,"");
                ft2.commit();
                break;
        }
    }
}