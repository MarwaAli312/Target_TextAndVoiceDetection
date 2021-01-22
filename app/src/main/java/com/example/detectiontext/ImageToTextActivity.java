package com.example.detectiontext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.detectiontext.models.DataManager;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ImageToTextActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int CAMERA_PICKED_CODE=1001;
    private static final int GALLERY_PICKED_CODE=1000;


    FirebaseAuth firebaseAuth;


    String[] cameraPermission;
    String[] storagePermission;

    Button previewbtn,bt;
    EditText edresult;
    ImageView imgpreview;
    ExtendedFloatingActionButton fab;
    ImageView photoView;
    ProgressDialog progressDialog;
    PhotoView photoPreview;



    String text;

    Bitmap bitmap;

    Uri image_uri,rsltUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);
        //btnaddimg=findViewById(R.id.btnaddimg_detection);
        edresult=findViewById(R.id.edresult_detection);
        imgpreview=findViewById(R.id.imgpreview_detection);
        fab=findViewById(R.id.history_imgtt);
        previewbtn=findViewById(R.id.preview);

        previewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rsltUri == null){
                    Toast.makeText(ImageToTextActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    Dialog d = new Dialog(ImageToTextActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    d.setCancelable(true);
                    PhotoView photoView=new PhotoView(ImageToTextActivity.this);
                    Glide.with(ImageToTextActivity.this)
                            .load(rsltUri)
                            .into(photoView);
                    d.setContentView(photoView);
                    d.show();
                }
            }
        });



        progressDialog=new ProgressDialog(this);



        bt=findViewById(R.id.detect_detection);
        //when first opening the activity
         showImageImportDialog();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ImageToTextActivity.this,"yoy",Toast.LENGTH_SHORT).show();
                showImageImportDialog();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ImageToTextActivity.this,HistoryActivity.class);
                i.putExtra("dest","imgtt");
                startActivity(i);
                ImageToTextActivity.this.finish();
            }
        });


        imgpreview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog d = new Dialog(ImageToTextActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                d.setCancelable(true);
                PhotoView photoView=new PhotoView(ImageToTextActivity.this);
                Glide.with(ImageToTextActivity.this)
                        .load(rsltUri)
                        .into(photoView);

                d.setContentView(photoView);
                d.show();
                return false;
            }
        });



        firebaseAuth= FirebaseAuth.getInstance();


        //camera permission
        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void showImageImportDialog() {
        String [] items={"Camera","Gallery"};
        AlertDialog.Builder dialog=new AlertDialog.Builder(ImageToTextActivity.this);
        dialog.setTitle("Select image from: ");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //camera option is clicked
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickCamera();
                    }
                }
                if(which==1){
                    // gallery
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickGallery();
                    }
                }
            }
        });

        dialog.create().show();
    }

    private void pickGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_PICKED_CODE);
    }

    private void pickCamera() {


        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture"); //title of the image
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");
        image_uri= ImageToTextActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,CAMERA_PICKED_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(ImageToTextActivity.this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean permission_storage= ContextCompat.checkSelfPermission(ImageToTextActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return permission_storage;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(ImageToTextActivity.this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean permission_camera= ContextCompat.checkSelfPermission(ImageToTextActivity.this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean permission_storage=ContextCompat.checkSelfPermission(ImageToTextActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return permission_camera && permission_storage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(ImageToTextActivity.this,"Permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickGallery();
                    }
                    else {
                        Toast.makeText(ImageToTextActivity.this,"Permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_PICKED_CODE) {


                assert data != null;
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(ImageToTextActivity.this);
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(result==null){
                    Toast.makeText(ImageToTextActivity.this, "Something went wrong, Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == CAMERA_PICKED_CODE) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(ImageToTextActivity.this);
            }
        }

        //get cropped img
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                assert result != null;
                 rsltUri = result.getUri();
                 imgpreview.setImageURI(rsltUri);

                //get drawable bitmap
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgpreview.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(ImageToTextActivity.this).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(ImageToTextActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    //get text from sb until there is no next
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    text=sb.toString();
                    if(text.trim().isEmpty()){
                        Toast.makeText(ImageToTextActivity.this,"Unable to detect Text",Toast.LENGTH_SHORT).show();
                    }
                    edresult.setText(sb.toString());


                }
            }
            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception err = result.getError();
                System.out.println(err);
                Toast.makeText(ImageToTextActivity.this, "An error Ocuured", Toast.LENGTH_SHORT).show();
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
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(ImageToTextActivity.this, gso);
            mGoogleSignInClient.signOut();
            firebaseAuth.signOut();

            FirebaseAuth.getInstance().signOut();
            progressDialog.dismiss();
            Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(ImageToTextActivity.this, MainActivity.class);
            startActivity(i);


        }
        if(id==R.id.action_save){
            DataManager dataManager=new DataManager();
            if (edresult.getText().toString().isEmpty()){
                text="No text Detected";
            }

            dataManager.saveImgTTData(rsltUri,text);
            Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();



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
            startActivity(new Intent(ImageToTextActivity.this, LaunchScreen.class));
            finish();
        }
    }

}