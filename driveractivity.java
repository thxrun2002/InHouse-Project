package com.example.quikcar;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.widget.EditText;
import android.view.View;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class driveractivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveractivity);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }
    FirebaseFirestore db;
    String carphotoref, facephotoref;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    driver d;
    ImageView img,image;
    EditText name,phone,email,carmodel, password;
    String dname,dphone,demail,dcarmodel, pass;
    Bitmap facephoto,carphoto;
    ActivityResultLauncher<Intent> startforResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result!=null && result.getResultCode() == RESULT_OK){
                if(result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    facephoto = (Bitmap) bundle.get("data");
                    img.setImageBitmap(facephoto);
                    facephotoref = UUID.randomUUID().toString();
                    uploadPicture(facephoto,facephotoref);

                }
            }
        }
    });

    ActivityResultLauncher<Intent> carstartforResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    carphoto = (Bitmap) bundle.get("data");
                    image.setImageBitmap(carphoto);
                    carphotoref = UUID.randomUUID().toString();
                    uploadPicture(carphoto,carphotoref);
                }
            }
        }
    });

    private void uploadPicture(Bitmap photo, String referenceKey) {

        File tempFile = new File(getCacheDir(), "temp.png");
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            photo.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
            fileOutputStream.close();
        }catch (IOException e){
            Log.d("ERROR", "PHOTO FILE ERROR " + e);
            Toast.makeText(driveractivity.this, "Error: "+e, Toast.LENGTH_SHORT).show();
        }
        Uri imgUri = Uri.fromFile(tempFile);
        StorageReference photoRef = storageReference.child(referenceKey);
        photoRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(driveractivity.this, "IMAGE SUCCESSFULLY SAVED", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(driveractivity.this, "IMAGE SAVING UNSUCCESSFUL", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void register(View v) {
        db = FirebaseFirestore.getInstance();
        name = findViewById(R.id.nameText);
        phone = findViewById(R.id.phoneText);
        email = findViewById(R.id.emailText);
        carmodel = findViewById(R.id.carmodelText);
        password = findViewById(R.id.passwordText);
        dname = name.getText().toString();
        dphone = phone.getText().toString();
        demail = email.getText().toString();
        pass = password.getText().toString();
        dcarmodel = carmodel.getText().toString();
        if(TextUtils.isEmpty(dname) || TextUtils.isEmpty(dphone) || TextUtils.isEmpty(demail) || TextUtils.isEmpty(dcarmodel) || TextUtils.isEmpty(pass)){
            Toast.makeText(driveractivity.this,"ENTER ALL DETAILS", Toast.LENGTH_SHORT).show();
        }
        else{
            addDatatoFirestore(dname,dphone,demail,dcarmodel,pass,carphotoref,facephotoref);
        }
    }
    public void addDatatoFirestore(String dname, String dphone,String demail,String dcarmodel,
                                   String password, String carphotoref, String facephotoref){
        CollectionReference cr = db.collection("DRIVERS");
         d = new driver(dname,dphone,demail,dcarmodel,password, carphotoref, facephotoref);
        cr.add(d).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(driveractivity.this,"DATA SUCCESSFULLY ADDED! YOU CAN NOW LOGIN",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(driveractivity.this,driverlogin.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(driveractivity.this,"FAILED TO ADD DATA! RETRY!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(driveractivity.this,driveractivity.class);
                startActivity(i);
            }
        });
    }
    public void photoclick(View v){
        img = findViewById(R.id.faceimg);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startforResult.launch(intent);
        }
        else
        {
            Toast.makeText(driveractivity.this,"No camera to support action",Toast.LENGTH_SHORT).show();
        }
    }
    public void carclick(View v) {
        image = findViewById(R.id.carimg);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            carstartforResult.launch(intent);
        } else {
            Toast.makeText(driveractivity.this, "No camera to support action", Toast.LENGTH_SHORT).show();
        }
    }
}