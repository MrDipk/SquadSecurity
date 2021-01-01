package com.dip.squadsecurity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dip.squadsecurity.ui.ImageURI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DetailViewForm extends AppCompatActivity {
TextView cname,gname,gphno,gdob,gfname,gfphno,gpaddress,status,gwname,qualification,pcdetails,exprience;
ImageButton profile,adharfront,adharback;
String uidd,Name,Phno;
LinearLayout wnamelayout;
Toolbar tbar;
WebView view;
String cnam,gnam,gpno,gdb,gfnam,gfpno,gpadd,gmst,gwnam,gque,pcdetail,gexp;
ImageURI url =new ImageURI();
ProgressBar PBar;

//String img1 ="https://firebasestorage.googleapis.com/v0/b/squad-security-99d85.appspot.com/o/GuardIdentity%2F25dbf853-f8e8-48ab-b950-c728445c104b%2FProfile?alt=media&token=04176495-c389-4894-b783-1cf1ad2852d9";

    public FirebaseStorage storage;
    public StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view_form);

        view = (WebView) findViewById(R.id.webview);

        cname = (TextView) findViewById(R.id.tvcompanyname);
        gname = (TextView) findViewById(R.id.tvGuardName);
        gphno = (TextView) findViewById(R.id.tvGphoneno);
        gdob = (TextView) findViewById(R.id.tvdob);
        gfname = (TextView) findViewById(R.id.tvFname);
        gfphno = (TextView) findViewById(R.id.tvFphoneno);
        gpaddress = (TextView) findViewById(R.id.tvAddress);
        status = (TextView) findViewById(R.id.tvstatus);
        gwname = (TextView) findViewById(R.id.tvWifename);
        qualification = (TextView) findViewById(R.id.tvQualification);
        pcdetails = (TextView) findViewById(R.id.tvprecompany);
        exprience = (TextView) findViewById(R.id.tvExprience);
        PBar=(ProgressBar)findViewById(R.id.progressBarDV);

        profile = (ImageButton) findViewById(R.id.imageButtonProfile);
        adharfront = (ImageButton) findViewById(R.id.imageBtnfront);
        adharback = (ImageButton) findViewById(R.id.imageBtnback);

        wnamelayout = (LinearLayout) findViewById(R.id.layoutwname);
        // Find the toolbar view inside the activity layout
        tbar = (Toolbar) findViewById(R.id.toolbardetail);
        tbar.setTitle("Guard Details");
        setSupportActionBar(tbar);
        Toast.makeText(getApplicationContext(),"Loading..",Toast.LENGTH_SHORT).show();

        Intent i = getIntent();
        Name = i.getStringExtra("name");
        Phno = i.getStringExtra("contact");
        uidd = i.getStringExtra("uid");

        if (!Name.isEmpty() && !Phno.isEmpty()) {
            RetriveImage(profile,"Profile",uidd);
            RetriveImage(adharfront, "AdharFront", uidd);
            RetriveImage(adharback, "AdharBack", uidd);

            FirebaseDatabase df = FirebaseDatabase.getInstance();
            final DatabaseReference tbl_guard;
            //Toast.makeText(getApplicationContext(),Name + Phno,Toast.LENGTH_SHORT).show();
            tbl_guard = df.getReference("GuardDetails");
            tbl_guard.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        final GuardInfo gf = dataSnapshot.getValue(GuardInfo.class);
                        //gf.GuardName.equals(Name) &&  gf.GuardPhno.equals(Phno)
                        if (uidd.equals(gf.Gid)) {

                            uidd = gf.Gid;
                            cname.setText(gf.CompanyName);
                            gname.setText(gf.GuardName);
                            gphno.setText(gf.GuardPhno);
                            gdob.setText(gf.GuardDOB);
                            gfname.setText(gf.GuardFName);
                            gfphno.setText(gf.GuardFPhno);
                            gpaddress.setText(gf.GuardPAddress);
                            status.setText(gf.MarriedStatus);
                            if (gf.MarriedStatus.equals("Married")) {
                                wnamelayout.setVisibility(View.VISIBLE);
                            }
                            gwname.setText(gf.GuardWName);
                            qualification.setText(gf.GuardQualification);
                            pcdetails.setText(gf.GuardPCDetails);
                            exprience.setText(gf.GuardExprience);

                            // for PDF View
                            cnam=gf.CompanyName;
                            gnam=gf.GuardName;
                            gpno=gf.GuardPhno;
                            gdb=gf.GuardDOB;
                            gfnam=gf.GuardFName;
                            gfpno=gf.GuardFPhno;
                            gpadd=gf.GuardPAddress;
                            gmst=gf.MarriedStatus;
                            gwnam=gf.GuardWName;
                            gque=gf.GuardQualification;
                            pcdetail=gf.GuardPCDetails;
                            gexp=gf.GuardExprience;
                         }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }


    public void RetriveImage(final ImageButton img, final String str, String uidd) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("GuardIdentity/" + uidd + "/" + str);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with((getApplicationContext()))
                        .load(uri)
                        .into(img);
                //ImageURI ui =new ImageURI();
                url.URL(uri,str,getApplicationContext());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Handler wait;
        Runnable delay;
        wait = new Handler();
        delay = new Runnable() {
            @Override
            public void run() {
                WebViewDetails();
                PBar.setVisibility(View.INVISIBLE);
            }
        };
        wait.postDelayed(delay,3000);

    }



    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_view_menu, menu);
        menu.findItem(R.id.print).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
               CreatePdf(view);
                return false;
            }
        });
        return true;
    }

    public void CreatePdf(View v){

        Context context=DetailViewForm.this;
        PrintManager printManager=(PrintManager)DetailViewForm.this.getSystemService(context.PRINT_SERVICE);
        PrintDocumentAdapter adapter=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
           adapter=view.createPrintDocumentAdapter();
        }
        String JonName=getString(R.string.app_name)+"Document";
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT){
            PrintJob printJob=printManager.print(JonName,adapter,new PrintAttributes.Builder().build());
        }

    }

    public void WebViewDetails() {

        GuardInfo gf = new GuardInfo();
        //for webview Pdf

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "<style type=\"text/css\">\n" +
                "    .profileimg{\n" +
                "        height: 90px;\n" +
                "        width: 70px;\n" +
                "    }\n" +
                "\n" +
                "body{" +
                " border:2px solid black;\n"+
                "       padding-bottom:10px;\n" +
                "}\n"+
                "    .adhartitle{\n" +
                "        height: 40px;\n" +
                 "       padding-Top:10px;\n"+
                "    }\n" +
                "    .adharimg{\n" +
                "        height: 240px;\n" +
                "        width:320px;\n" +
                "    }\n" +
                "    .padding{\n" +
                "        padding-left: 200px;\n" +
                "        width: 50%;\n" +
                "        font-size: 24px;\n" +
                "    }\n" +
                "    table{\n" +
                "      border: 0px solid black;\n" +
                "    }\n" +
                "    .p{\n" +
                "        font-size: 30px;\n" +
                "        margin-top:20px"+
                "        font-weight: 700;\n" +
                "    }\n" +
                "    .data{\n" +
                "        font-size: 22px;\n" +
                "        color: gray;\n" +
                "    }\n" +
                "    .pad{\n" +
                "        padding-top: 20px;\n" +
                "        padding-bottom: 10px;\n" +
                "    }\n" +
                "    .fontbio{\n" +
                "        font-size: 24px;\n" +
                "        font-weight: 500px;\n" +
                "    }\n" +
                "    \n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<center>\n" +
                "<div>\n" +
                "<table width=\"100%\">\n" +
                "    <tr><td align=\"center\" class=\"p\" colspan=\"2\">\n" + cnam + " System Pvt. Ltd.</td></tr>\n" +
                "<tr><td align=\"center\" colspan=\"2\">\n" +
                "    #62,Oop. Maithri Nursing Home,Netravathi Extn. Main Road,<br> K.R. Puram,Bangalore-562114,<br>\n" +
                "    Mob : 9845200081, 9980543931\n" +
                "</td></tr>\n" +
                "<tr><td colspan=\"2\" align=\"center\" class=\"fontbio\">Bio-Data</td></tr>\n" +

                "    <tr><td align=\"center\" colspan=\"2\" class=\"pad\" ><img class=\"profileimg\" alt=\"Profile\" src=\"\n" + url.img1 + "\"/>\n" +
                "</td></tr>\n" +
                "<tr><td class=\"padding\">Guard Name:</td><td class=\"data\" id=\"gname\">\n" + gnam + "</td></tr>\n" +
                "<tr><td class=\"padding\">Contact No:</td><td class=\"data\" id=\"gphone\">\n" + gpno + "</td></tr>\n" +
                "<tr><td class=\"padding\">Date of Birth:</td><td class=\"data\" id=\"gdob\">\n" + gdb + "</td></tr>\n" +
                "<tr><td class=\"padding\">Father Name:</td><td class=\"data\" id=\"gfname\">\n" + gfnam + "</td></tr>\n" +
                "<tr><td class=\"padding\">Contact No:</td><td class=\"data\" id=\"gfphno\">\n" + gfpno + "</td></tr>\n" +
                "<tr><td class=\"padding\">Address:</td><td class=\"data\" id=\"gaddress\">\n" + gpadd + "</td></tr>\n" +
                "<tr><td class=\"padding\">Status:</td><td class=\"data\" id=\"gstatus\">\n" + gmst + "</td></tr>\n" +
                "<tr><td class=\"padding\">Wife's Name:</td><td class=\"data\" id=\"gwname\">\n" + gwnam + "</td></tr>\n" +
                "<tr><td class=\"padding\">Qualification:</td><td class=\"data\" id=\"gque\">\n" + gque + "</td></tr>\n" +
                "<tr><td class=\"padding\">Pre.Company:</td><td class=\"data\" id=\"gprecom\">\n" + pcdetail + "</td></tr>\n" +
                "<tr><td class=\"padding\">Exprience:</td><td class=\"data\" id=\"gexp\">\n" + gexp + "</td></tr>\n" +
                "<tr class=\"adhartitle\"><td colspan=\"2\" align=\"center\" class=\"fontbio\" >Adhar Card</td></tr>\n" +
                "<tr><td align=\"right\"  class=\"pad\" ><img class=\"adharimg\" alt=\"AdharFront\" src=\"\n" + url.img2 + "\"/></td>" +
                "<td align=\"left\"  class=\"pad\" ><img class=\"adharimg\"  alt=\"AdharBack\" src=\"\n" + url.img3 + "\"/></td></tr>\n" +
                "</table>\n" +
                "</div>\n" +
                "</center>\n" +
                "</body>\n" +
                "</html>";
        view.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

}