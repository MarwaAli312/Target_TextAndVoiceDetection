package com.example.detectiontext.models;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class DataManager {
    FirebaseAuth auth;
    FirebaseUser user;
    String uid;

    FirebaseDatabase database;


    public DataManager() {
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid=user.getUid();
        database =FirebaseDatabase.getInstance();

    }


    //saving data

    public void saveSTTData(String text){


        /*DatabaseReference reference=database.getReference("Users").child(uid).child("speechtotext");


        String timestamp = String.valueOf(System.currentTimeMillis());
        STT item=new STT(text,timestamp);
        reference.child(timestamp).setValue(item);*/

        DatabaseReference reference=database.getReference("SpeechToText").child(uid);
        String timestamp = String.valueOf(System.currentTimeMillis());
        STT item=new STT(text,timestamp);
        reference.child(timestamp).setValue(item);


    }

    public void saveTTSData(String text){


        /*DatabaseReference reference=database.getReference("Users").child(uid).child("texttospeech");
        String timestamp = String.valueOf(System.currentTimeMillis());
        TTS item=new TTS(text,timestamp);
        reference.child(timestamp).setValue(item);*/
        DatabaseReference reference=database.getReference("TextToSpeech").child(uid);
        String timestamp = String.valueOf(System.currentTimeMillis());
        TTS item=new TTS(text,timestamp);
        reference.child(timestamp).setValue(item);

    }

    public void saveImgTTData(Uri img, String text){

        DatabaseReference reference=database.getReference("ImageToText").child(uid);

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference=storage.getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String photoStringLink;

        StorageReference riversRef = storageReference.child("images/"+timestamp);


        riversRef.putFile(img)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoStringLink = uri.toString();
                                ImgTT item=new ImgTT(text,photoStringLink,timestamp);
                                reference.child(timestamp).setValue(item);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });


        /*DatabaseReference reference=database.getReference("Users").child(uid).child("imagetotext");

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference=storage.getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String photoStringLink;

        StorageReference riversRef = storageReference.child("images/"+timestamp);


        riversRef.putFile(img)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoStringLink = uri.toString();
                                ImgTT item=new ImgTT(text,photoStringLink,timestamp);
                                reference.child(timestamp).setValue(item);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });*/





        }


        //deleting data

    public void deleteTTSitem(TTS item){
        DatabaseReference reference=database.getReference("TextToSpeech");
        Query query = reference.child(uid);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    TTS res = snapshot.getValue(TTS.class);
                    if(res.getTimestamp().equals(item.getTimestamp()) && res.getText().equals(item.getText())){
                        dataSnapshot.getRef().child(item.getTimestamp()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

        /*DatabaseReference reference=database.getReference("Users");
        Query query = reference.child("texttospeech");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    TTS res = snapshot.getValue(TTS.class);
                    if(res.getTimestamp().equals(item.getTimestamp()) && res.getText().equals(item.getText())){
                        dataSnapshot.getRef().child(item.getTimestamp()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });*/


    }

    public void deleteSTTitem(STT item){
       /* DatabaseReference reference=database.getReference("Users").child(uid);
        Query query = reference.child("speechtotext");*/

        DatabaseReference reference=database.getReference("SpeechToText");
        Query query = reference.child(uid);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    STT res = snapshot.getValue(STT.class);
                    if(res.getTimestamp().equals(item.getTimestamp())){
                        dataSnapshot.getRef().child(item.getTimestamp()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });
    }


    public void deleteImgTTitem(ImgTT item){
       /* DatabaseReference reference=database.getReference("Users").child(uid);
        Query query = reference.child("imagetotext");*/

        DatabaseReference reference=database.getReference("ImageToText");
        Query query = reference.child(uid);



        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ImgTT res = snapshot.getValue(ImgTT.class);
                    if(res.getTimestamp().equals(item.getTimestamp()) && res.getText().equals(item.getText()) && res.getImage().equals(item.getImage()) ){
                        dataSnapshot.getRef().child(item.getTimestamp()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference photoRef =storage.getReferenceFromUrl(item.getImage());





        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Picture","#deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }


    public void deleteAll(String which){

        DatabaseReference reference=database.getReference(which);
        reference.child(uid).removeValue();

    }





}


