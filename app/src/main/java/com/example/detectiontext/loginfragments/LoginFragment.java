package com.example.detectiontext.loginfragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.detectiontext.DashboardActivity;
import com.example.detectiontext.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginFragment extends Fragment {
    Button btnlogin;
    EditText edemail,edpswd;
    TextView tvrecoverpswd;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton googlelogin;
    final int GOOGLE_SIGN_IN=10;
    public static String NameOfUser;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_login, container, false);
        btnlogin=view.findViewById(R.id.btnlogin);
        edemail=view.findViewById(R.id.edmail_login);
        edpswd=view.findViewById(R.id.edpswd_login);
        googlelogin=view.findViewById(R.id.googleLoginBtn);
        tvrecoverpswd=view.findViewById(R.id.tvrecoverpswd);


        progressDialog=new ProgressDialog(getContext());


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(getContext(),gso);

        mAuth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edemail.getText().toString().trim();
                String pswd=edpswd.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //invalid email pattern
                    edemail.setError("Invalid Email");
                    edemail.setFocusable(true);
                }
                else {
                    //valid email pattern
                    loginUser(email,pswd);
                }
            }
        });

        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignIn();
            }
        });

        tvrecoverpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        return view;
    }

    private void showRecoverPasswordDialog() {

        //views to set in the dialog
        EditText edmail=new EditText(getContext());
        //edmail.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        edmail.setHint("Enter your Email");
        edmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //sets the min width of editview to fit a text of M letters
        //edmail.setMinEms(16);

        //set layout
        LinearLayout linearLayout=new LinearLayout(getContext());
        linearLayout.addView(edmail);
        linearLayout.setPadding(10,10,10,10);

        //alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Recover Password");
        builder.setView(linearLayout);

        //button recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email=edmail.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();

            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending Email...");
        progressDialog.show();


        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){

                            Toast.makeText(getContext(),"Email sent",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(),"Sending failed, Please try again...",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                // get and show proper error message

            }
        });
    }

    private void loginUser(String email, String pswd) {
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success
                            startActivity(new Intent(getActivity(), DashboardActivity.class));
                            getActivity().finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss the progress dialog
                progressDialog.dismiss();
                //error message
                Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void GoogleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                 Log.w("Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken)  {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //if user is signing in first time then create new user
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                String email=user.getEmail();
                                String uid=user.getUid();
                                String name=user.getDisplayName();
                                HashMap<Object,String> hashMap=new HashMap<>();
                                //put info in hashmap
                                hashMap.put("name",name);
                                hashMap.put("email",email);
                                //firebase database instance
                                FirebaseDatabase database =FirebaseDatabase.getInstance();
                                //path to store user data named "Users"
                                DatabaseReference reference=database.getReference("Users");
                                //out data within hashmap in db
                                reference.child(uid).setValue(hashMap);

                            }

                            Toast.makeText(getActivity(), ""+user.getEmail(),Toast.LENGTH_SHORT).show();
                            //go to user profile
                            startActivity(new Intent(getActivity(), DashboardActivity.class));
                            getActivity().finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(),"Failed to login",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}