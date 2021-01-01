package com.dip.squadsecurity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class UserProfile extends AppCompatActivity {
ImageButton profile;
EditText name;
String phoneno,currentPhotoPath;
Button done;
int PICK_IMAGE_CAMERA=1,PICK_IMAGE_GALLERY=2;
private Bitmap bitmap;
File destination;
InputStream inputStreamImg;
byte[] cmpimg;
Uri imgPath,selectedImage;
    FirebaseDatabase df = FirebaseDatabase.getInstance();
    DatabaseReference tbl_user;

private FirebaseStorage storage;
private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profile = (ImageButton) findViewById(R.id.imageButton);
        name = (EditText) findViewById(R.id.editTextName);
        done = (Button) findViewById(R.id.btn_done);
        ChechUser();
        if (!name.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "helllooo", Toast.LENGTH_SHORT).show();
        }

        final SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCE", Context.MODE_PRIVATE);
        phoneno = sharedPreferences.getString("num", "8005634848");


        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_CAMERA);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_GALLERY);
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        try {
            done.setOnClickListener(new View.OnClickListener() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("UserDetails");

                @Override
                public void onClick(View v) {
                    if (!name.getText().toString().isEmpty()) {
                        if (!(profile.getDrawable() == null)) {
                            try {
                                UserInfo ui = new UserInfo(name.getText().toString(), phoneno);
                                myRef.child(phoneno).setValue(ui);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name", name.getText().toString());
                                editor.putString("profile", "Done");
                                editor.apply();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Intent i = new Intent(UserProfile.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                            finish();
                        } else {
                            showAlertDialog("Select your Profile", "OK");
                        }
                    } else {
                        name.requestFocus();
                        name.setError("Invalid Input");
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

        }

    public void ChechUser(){
        tbl_user = df.getReference("Admin");
        tbl_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserInfo ui = dataSnapshot.getValue(UserInfo.class);
                    if (phoneno.equals(ui.phno)) {
                        name.setText(ui.name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Select image from camera and gallery
    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserProfile.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            openCamera(PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openCamera(int reqCode) {
        if ((ContextCompat.checkSelfPermission(UserProfile.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(UserProfile.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserProfile.this,
                    Manifest.permission.CAMERA)) {
                //Denied request
                ActivityCompat.requestPermissions(UserProfile.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
            }
            else {
                //Checked don't ask again
                Toast.makeText(UserProfile.this, "Permission is required!",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(UserProfile.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
            }
        }
        else {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, PICK_IMAGE_CAMERA);
            if (camera.resolveActivity(getPackageManager()) != null) {
                try {
                    destination = createImageFile();
                }
                catch (IOException ex) {
                }
                if (destination != null) {
                    Uri photoUri = FileProvider.getUriForFile(UserProfile.this,
                            "com.dip.squadsecurity", destination);
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    //startActivityForResult(camera,PICK_IMAGE_CAMERA);
                }
            }
        }
    }

    //Stores the photo to a common directory
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Security" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        //Toast.makeText(getApplicationContext(), currentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {

                imgPath = Uri.fromFile(new File(currentPhotoPath));
                performCrop();
                //compress image
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                cmpimg = baos.toByteArray();
                //***********
                //Toast.makeText(getApplicationContext(), "Check notification for progress", Toast.LENGTH_SHORT).show();
                galleryAddPic();
                profile.setImageBitmap(getResizedBitmap(bitmap,300,250));
                UploadImage();


            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Upload failed! Contact the developer.", Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }

        }
        else if (requestCode == PICK_IMAGE_GALLERY) {
            try {
                selectedImage=data.getData();
                //compress image
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                cmpimg = baos.toByteArray();

                //***********
                Log.e("Activity", "Pick from Gallery::>>> ");
                imgPath = selectedImage;
                //Toast.makeText(getApplicationContext(), "Check notification for progress", Toast.LENGTH_SHORT).show();
                profile.setImageBitmap(getResizedBitmap(bitmap,300,250));
                UploadImage();

            } catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(e.getStackTrace());
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    void UploadImage(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference ref = storageReference.child("image/"+phoneno);
        ref.putBytes(cmpimg)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Photo successfully uploaded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to upload!");
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_CAMERA && requestCode == PICK_IMAGE_GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Show a modular alertDialog with options in arguments
    private void showAlertDialog(final String message, String positive) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //uiid= UUID.randomUUID().toString();
                        //Clear();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * this function does the crop operation.
     */
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent camera = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            camera.setDataAndType(imgPath, "image/*");
            // set crop properties
            camera.putExtra("crop", "true");
            // indicate aspect of desired crop
            camera.putExtra("aspectX", 2);
            camera.putExtra("aspectY", 1);
            // indicate output X and Y
            camera.putExtra("outputX", 256);
            camera.putExtra("outputY", 256);
            // retrieve data on return
            camera.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(camera,PICK_IMAGE_CAMERA);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
