package com.example.detectiontext.loginfragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.detectiontext.DashboardActivity;
import com.example.detectiontext.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterFragment extends Fragment {
    Button btnregister;
    EditText edmail,edusername,edpswd;
    ProgressDialog progressDialog;


    private FirebaseAuth mAuth;



    public RegisterFragment() {
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
        View view=inflater.inflate(R.layout.fragment_register, container, false);
        btnregister=view.findViewById(R.id.btnregister);
        edusername=view.findViewById(R.id.edusername_register);
        edpswd=view.findViewById(R.id.edpswd_register);
        edmail=view.findViewById(R.id.edmail_register);
        progressDialog=new ProgressDialog(getContext());

        mAuth = FirebaseAuth.getInstance();



        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edmail.getText().toString().trim();
                String pswd=edpswd.getText().toString().trim();
                String username=edusername.getText().toString().trim();
                //validate
                Boolean valid=true;
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focus to email edit text
                    edmail.setError("Invalid Email");
                    edmail.setFocusable(true);
                    valid=false;
                }
                if(pswd.length()<6){
                    //set error and focus to password edit text
                    edpswd.setError("Password must be at least 6 characters");
                    edpswd.setFocusable(true);
                    valid=false;
                }
                if(username.isEmpty()){
                    //set error and focus to password edit text
                    edpswd.setError("Username cannot be empty");
                    edpswd.setFocusable(true);
                    valid=false;
                }
                
                if (valid){
                    registerUser(username,email,pswd);
                }

            }
        });

        return view;
    }

    private void registerUser(String username, String email, String pswd) {
        progressDialog.setMessage("Signing Up...");
            progressDialog.show();
            //creating a user
            mAuth.createUserWithEmailAndPassword(email, pswd)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String email=user.getEmail();
                                String uid=user.getUid();
                                //when user is registered store user info in firebase realtime fb
                                HashMap<Object,String> hashMap=new HashMap<>();

                                hashMap.put("email",email);
                                hashMap.put("name",username);
                                hashMap.put("password",pswd);


                                //firebase database instance
                                FirebaseDatabase database =FirebaseDatabase.getInstance();
                                //path to store user data named "Users"
                                DatabaseReference reference=database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                Toast.makeText(getActivity(),
                                        "Successfully Registered.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), DashboardActivity.class));
                                getActivity().finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Something Wrong happened, please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //error, dismiss progress dialog and get and show the error message
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
