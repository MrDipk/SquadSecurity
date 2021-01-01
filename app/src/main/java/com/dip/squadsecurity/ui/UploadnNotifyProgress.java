package com.dip.squadsecurity.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dip.squadsecurity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class UploadnNotifyProgress implements Runnable {
    private Context context;
    private String CHANNEL_ID = "123456";
    private int notificationId = 12121;
    private Uri filePath;
    String imgname,id;
    private static int fileNos = 0;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    byte[] imgbyte;
    // Issue the initial notification with zero progress
    private int PROGRESS_MAX = 100;
    private int PROGRESS_CURRENT = 0;
    public  UploadnNotifyProgress() {}
    public  UploadnNotifyProgress(Context currentContext) {
        context = currentContext;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.channel_name);
            String description = context.getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void uploadFile(Uri filePath1,byte[] cmp, int flag, String uiid) {
        imgbyte = cmp;
        filePath=filePath1;
        id=uiid;
        fileNos+=1;
        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        createNotificationChannel();
        builder.setContentTitle("Picture Upload")
                .setContentText("Uploading " + fileNos + " file(s)")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        if(filePath1 != null)
        {
            if (flag == 1) {
                imgname="Profile";
            }
            if (flag == 2) {
                imgname="AdharFront";
            }
            if (flag == 3) {
                imgname="AdharBack";
            }
            run();
        }
    }
    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference ref = storageReference.child("GuardIdentity").child(id).child(imgname);
        ref.putBytes(imgbyte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "Photo successfully uploaded!", Toast.LENGTH_SHORT).show();
                        fileNos-= 1;
                        builder.setContentText("Upload Complete")
                                .setProgress(0,0,false);
                        notificationManager.notify(notificationId, builder.build());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to upload!");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        PROGRESS_CURRENT = (int)(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        notificationManager.notify(notificationId, builder.build());
                    }
                });
    }




}

