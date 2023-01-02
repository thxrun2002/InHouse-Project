package com.example.quikcar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.MyViewHolder> {
    private StorageReference carstorageReference, facestorageReference;
    Context context;
    ArrayList<driver> driverArrayList;

    public adapter(Context context, ArrayList<driver> driverArrayList) {
        this.context = context;
        this.driverArrayList = driverArrayList;
    }

    @NonNull
    @Override
    public adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.MyViewHolder holder, int position) {

        driver d = driverArrayList.get(position);
        holder.name.setText(d.getName());
        holder.car.setText(d.getCarname());
        holder.phone.setText(d.getNumber());
        carstorageReference = FirebaseStorage.getInstance().getReference().child(d.getCarimgref());
        try {
            File file = File.createTempFile("img", "png");
            carstorageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "IMAGE RETRIEVED", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.carImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "IMAGE NOT RETRIEVED", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        facestorageReference = FirebaseStorage.getInstance().getReference().child(d.getFaceimgref());
        try {
            File file = File.createTempFile("img", "png");
            facestorageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "IMAGE RETRIEVED", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.faceImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "IMAGE NOT RETRIEVED", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return driverArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,phone,car;
        ImageView carImage, faceImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameText);
            phone = itemView.findViewById(R.id.contactText);
            car = itemView.findViewById(R.id.carText);
            carImage = itemView.findViewById(R.id.carPicture);
            faceImage = itemView.findViewById(R.id.facePicture);
        }
    }
}
