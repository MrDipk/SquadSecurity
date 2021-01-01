package com.dip.squadsecurity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ImageView profile;
    TextView name,phone;
    String Uname,phno,AdminMO;
    FirebaseStorage storage;
    StorageReference storageReference;
    ViewGroup vg;
    String text;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    List<SearchGuardList> personList;
    ProgressBar PBProfile;
    FirebaseDatabase df = FirebaseDatabase.getInstance();
    DatabaseReference tbl_admin;

    private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckAdmin();
        SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCE", MODE_PRIVATE);
        //Nav_Header********************************
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        profile=(ImageView)hView.findViewById(R.id.imageViewprof);
        PBProfile=(ProgressBar)hView.findViewById(R.id.progressBarprofile);
        name=(TextView)hView.findViewById(R.id.textviewname);
        phone=(TextView)hView.findViewById(R.id.textviewphone);
        Uname= sharedPreferences.getString("name","");
        phno=sharedPreferences.getString("num","");
        phone.setText(phno);
        name.setText(Uname);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (phone.getText().toString().equals(AdminMO)) {
                    Intent i = new Intent(MainActivity.this, AddGuard.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Only Admin Can Access",Toast.LENGTH_LONG).show();

                }
            }


        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        vg = (ViewGroup)findViewById(R.id.viewGroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        personList = new ArrayList<>();



        //*******************Retrive Image from firebase**************//
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("image/"+phno);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri) {
               Glide.with(getApplicationContext())
                        .load(uri)
                       .apply(RequestOptions.circleCropTransform())
                        .into(profile);
               PBProfile.setVisibility(View.GONE);
               profile.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(),exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    public void CheckAdmin(){

                    tbl_admin = df.getReference("Admin");
                    tbl_admin.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                AdminAccess adminAccess = dataSnapshot.getValue(AdminAccess.class);
                                if (phno.equals(adminAccess.admphno)) {
                                    AdminMO = adminAccess.admphno;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =(SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Enter Guard Name of Phone no");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
                FirebaseDatabase df = FirebaseDatabase.getInstance();
                final DatabaseReference tbl_guard;
                text =searchView.getQuery().toString();

                if (!query.isEmpty()) {
                    vg.setVisibility(View.VISIBLE);
                    tbl_guard = df.getReference("GuardDetails");
                    tbl_guard.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            vg.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                final GuardInfo gf = dataSnapshot.getValue(GuardInfo.class);
                                if (gf.GuardName.equals(query) || gf.GuardPhno.equals(query)) {
                                    // Toast.makeText(getContext(),gf.GuardName + gf.GuardPhno,Toast.LENGTH_LONG).show();
                                    SearchGuardList myperson = new SearchGuardList(gf.GuardName.toLowerCase(), gf.GuardPhno, gf.Gid);
                                    personList.add(myperson);
                                }
                            }
                            if(personList.isEmpty()){
                                showAlertDialog("No data Found","ok");
                            }
                            recyclerViewadapter = new MyRecyclerViewAdapter(personList, getApplicationContext());
                            recyclerView.setAdapter(recyclerViewadapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_LONG).show();
                }

                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    personList.clear();
                    recyclerView.setVisibility(View.GONE);
                    vg.setVisibility(View.GONE);
                    return false;
                }
                personList.clear();
                recyclerView.setVisibility(View.GONE);
                vg.setVisibility(View.GONE);
                return true;
            }


        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item:
                SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("login", "");
                editor.putString("profile","") ;
                editor.apply();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Show a modular alertDialog with options in arguments
    private void showAlertDialog(final String message, String positive) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

