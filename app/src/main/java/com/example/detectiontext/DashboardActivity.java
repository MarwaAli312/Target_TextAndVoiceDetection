package com.example.detectiontext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.detectiontext.models.ImgTT;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    CardView profile,imgtt,tts,stt;
    TextView editText;
    TextView tvnameuser;
    String user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserStatus();
        setContentView(R.layout.activity_dashboard);
        profile=findViewById(R.id.profile_dashboard);
        imgtt=findViewById(R.id.imgtt_dashboard);
        tts=findViewById(R.id.tts_dashboard);
        stt=findViewById(R.id.stt_dashboard);
        editText=findViewById(R.id.tvtop_dashboard);
        tvnameuser=findViewById(R.id.tvnameuser);




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashboardActivity.this, HistoryActivity.class);
                startActivity(i);
            }
        });

        imgtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashboardActivity.this, ImageToTextActivity.class);
                startActivity(i);
            }
        });

        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashboardActivity.this,TextToSpeechActivity.class);
                startActivity(i);
            }
        });

        stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashboardActivity.this,SpeechToTextActivity.class);
                startActivity(i);
            }
        });


        firebaseAuth= FirebaseAuth.getInstance();

        setUserName();

      //System.out.println(user);

    }

    private void setUserName() {
        try {
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            DatabaseReference reference=database.getReference("Users");
            Query query = reference.child(firebaseAuth.getCurrentUser().getUid()).child("name");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user= dataSnapshot.getValue().toString();
                    String welcome=" Welcome, "+user+"!";
                    tvnameuser.setText(welcome);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException());
                }
            });
        }
        catch (Exception e){
            //Toast.makeText(DashboardActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
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
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(DashboardActivity.this, gso);
            mGoogleSignInClient.signOut();
            firebaseAuth.signOut();

            //checkUserStatus();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


    private void checkUserStatus(){
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                    finish();
                }
                else System.out.println("User connected");

            }
        };

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            //user is signed in, stay in profile
        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this, LaunchScreen.class));
            finish();
        }
    }
}