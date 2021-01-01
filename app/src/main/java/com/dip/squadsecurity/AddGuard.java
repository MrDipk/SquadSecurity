package com.dip.squadsecurity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.dip.squadsecurity.ui.UploadnNotifyProgress;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class AddGuard extends AppCompatActivity {
Spinner CName;
Button Savebtn,Dobbtn;
ImageButton ProfilePhoto,AdharFront,AdharBack;
RadioButton Married,UnMarried;
RadioGroup groupbtn;
TextView Dob;
EditText GName,GPhno,GFName,GFPhno,GPAddress,GWName,GQualification,GPCDetails,GExprience;
int year,month,dayOfMonth,flag=0;
ProgressDialog pd;
Uri selectedImage,imgPath;
String Company,Status,currentPhotoPath;
private Bitmap bitmap;
File destination;
InputStream inputStreamImg;
byte[] cmpimg;
private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
DatePickerDialog datePickerDialog;
String uiid=UUID.randomUUID().toString();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addguard);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        CName = (Spinner) findViewById(R.id.spinner);
        CName.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CompanySpinner.CompanyName));

        Savebtn = (Button) findViewById(R.id.buttonsave);
        Dobbtn = (Button) findViewById(R.id.buttonDOB);
        ProfilePhoto = (ImageButton) findViewById(R.id.imageButtonProfile);
        AdharFront = (ImageButton) findViewById(R.id.imageButtonfront);
        AdharBack = (ImageButton) findViewById(R.id.imageButtonback);
        Dob = (TextView) findViewById(R.id.textboxdob);
        Married = (RadioButton) findViewById(R.id.radiobtnMarried);
        UnMarried = (RadioButton) findViewById(R.id.radiobtnUnMarried);
        groupbtn = (RadioGroup) findViewById(R.id.radioGroup);
        GName = (EditText) findViewById(R.id.editTextGuardName);
        GPhno = (EditText) findViewById(R.id.editTextGphoneno);
        GFName = (EditText) findViewById(R.id.editTextFname);
        GFPhno = (EditText) findViewById(R.id.editTextFphoneno);
        GPAddress = (EditText) findViewById(R.id.editTextAddress);
        GWName = (EditText) findViewById(R.id.editTextWifename);
        GQualification = (EditText) findViewById(R.id.editTexQualification);
        GPCDetails = (EditText) findViewById(R.id.editTextprecompany);
        GExprience = (EditText) findViewById(R.id.editTextExprience);

        //Radio Button****************************
        groupbtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radiobtnMarried) {
                    GWName.setEnabled(true);
                    Status = "Married";
                }
                if (checkedId == R.id.radiobtnUnMarried) {
                    GWName.setEnabled(false);
                    GWName.setText("");
                    GWName.clearFocus();
                    GWName.setError(null);
                    Status = "UnMarried";
                }
            }
        });

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Guard");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddGuard.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();

            }
        });


        Dobbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddGuard.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Dob.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(1950);
                datePickerDialog.show();
                Dob.setError(null);
            }
        });

        // for sending flag value
        ProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                selectImage();
            }
        });
        AdharFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                selectImage();
            }
        });

        AdharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 3;
                selectImage();
            }
        });


        Savebtn.setOnClickListener(new View.OnClickListener() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("GuardDetails");

            @Override
            public void onClick(View v) {
                boolean v1, v2, v3, v4, v5, v7, v8, v9, v10, v11,v13;
                boolean v6=false;
                boolean v12=false;
                v1 = ValidationImage(ProfilePhoto);
                v13=ValidatoinText(GName);
                v2 = ValidationNum(GPhno);
                v3 = ValidatoinText(GFName);
                if(Dob.getText().toString().isEmpty()){
                    Dob.requestFocus();
                    Dob.setError("required");
                }
                else{
                    v12=true;
                }
                v4 = ValidationNum(GFPhno);
                v5 = ValidatoinText(GPAddress);
                if(GWName.isEnabled()==true) {
                    v6 = ValidatoinText(GWName);
                }
                else {
                    v6=true;

                }
                v7 = ValidatoinText(GQualification);
                v8 = ValidatoinText(GPCDetails);
                v9 = ValidatoinText(GExprience);
                v10 = ValidationImage(AdharFront);
                v11 = ValidationImage(AdharBack);

                if (v1 == true && v2 == true && v3 == true && v4 == true && v5 == true && v6 == true && v7 == true && v8 == true && v9 == true && v10 == true && v11 == true && v12 == true && v13 ==true) {
                    String id = uiid;
                    Company = CompanySpinner.CompanyName[CName.getSelectedItemPosition()];
                    GuardInfo obj = new GuardInfo(id, Company, GName.getText().toString().toLowerCase(),
                            GPhno.getText().toString(), Dob.getText().toString(), GFName.getText().toString(),
                            GFPhno.getText().toString(), GPAddress.getText().toString(), Status, GWName.getText().toString(),
                            GQualification.getText().toString(), GPCDetails.getText().toString(), GExprience.getText().toString());
                    myRef.child(id).setValue(obj);
                    Clear();
                    showAlertDialog("Data Insert Successful", "OK");
                } else {
                    showAlertDialog("Please Fill all Details", "OK");
                }
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddGuard.this);
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
        if ((ContextCompat.checkSelfPermission(AddGuard.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(AddGuard.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddGuard.this,
                    Manifest.permission.CAMERA)) {
                //Denied request
                ActivityCompat.requestPermissions(AddGuard.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
            }
            else {
                //Checked don't ask again
                Toast.makeText(AddGuard.this, "Permission is required!",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(AddGuard.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
            }
        }
        else {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (camera.resolveActivity(getPackageManager()) != null) {
                try {
                    destination = createImageFile();
                }
                catch (IOException ex) {
                }
                if (destination != null) {
                    Uri photoUri = FileProvider.getUriForFile(AddGuard.this,
                            "com.dip.squadsecurity", destination);
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(camera,PICK_IMAGE_CAMERA);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {

                imgPath = Uri.fromFile(new File(currentPhotoPath));
                //compress image
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                cmpimg = baos.toByteArray();
                //***********
                Toast.makeText(getApplicationContext(), "Check notification for progress", Toast.LENGTH_SHORT).show();
                UploadnNotifyProgress unn = new UploadnNotifyProgress(this);
                unn.uploadFile(imgPath,cmpimg, flag, uiid);
                showPhoto();
                galleryAddPic();
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
                showPhoto();
                imgPath = selectedImage;
                Toast.makeText(getApplicationContext(), "Check notification for progress", Toast.LENGTH_SHORT).show();
                UploadnNotifyProgress Unp= new UploadnNotifyProgress(this);
                Unp.uploadFile(imgPath,cmpimg,flag, uiid);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(e.getStackTrace());
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
        // Toast.makeText(getApplicationContext(), currentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }


    void showPhoto() {
            if(flag==1){
                ProfilePhoto.setImageBitmap(getResizedBitmap(bitmap,250,250));
            }
            if(flag==2){
                AdharFront.setImageBitmap(getResizedBitmap(bitmap,250,250));
            }
            if(flag==3){
                AdharBack.setImageBitmap(getResizedBitmap(bitmap,250,250));
            }
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


    //Show a modular alertDialog with options in arguments
    private void showAlertDialog(final String message, String positive) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        uiid=UUID.randomUUID().toString();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void Clear(){
        Company="";
        GName.setText("");
        GPhno.setText("");
        Dob.setText("Date of Birth");
        GFName.setText("");
        GFPhno.setText("");
        GPAddress.setText("");
        Status="";
        GWName.setText("");
        GQualification.setText("");
        GPCDetails.setText("");
        GExprience.setText("");
        flag=0;
        groupbtn.clearCheck();
        ProfilePhoto.clearAnimation();
        AdharFront.clearAnimation();
        AdharBack.clearAnimation();
        ProfilePhoto.setImageDrawable(null);
        AdharBack.setImageDrawable(null);
        AdharFront.setImageDrawable(null);
        ProfilePhoto.setBackgroundResource(R.mipmap.ic_launcher_person_round);
        AdharFront.setBackgroundResource(R.mipmap.ic_launcher_adhar_round);
        AdharBack.setBackgroundResource(R.mipmap.ic_launcher_adhar_round);

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(AddGuard.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    public boolean ValidatoinText( EditText ed){
        Boolean flag=false;
        if(ed.getText().toString().isEmpty()){
            ed.requestFocus();
            ed.setError("required");
        }
        else{
            flag=true;
        }
        return  flag;
    }

    public boolean ValidationImage(ImageView iv){
        Boolean flag=false;
        if(iv.getDrawable()==null){
            showAlertDialog("Please Select image","OK");
        }
        else {
            flag=true;
        }
        return flag;
    }

    public boolean ValidationNum(EditText edn){
        Boolean flag=false;
        if(edn.getText().toString().isEmpty() || edn.length()<10){
            edn.requestFocus();
            edn.setError("Invalid Input");
        }
        else {
            flag=true;
        }
        return flag;
    }
}

