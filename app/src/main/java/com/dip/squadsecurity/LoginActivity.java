package com.dip.squadsecurity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {
Button login,Go;
EditText Number,OTPText;
String phno,cphno;
String code,VerificationId,otp;
TextView resend;
LinearLayout NumLayout,OTPLayout;
int flag;
ProgressBar pbotp;
FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCE", Context.MODE_PRIVATE);

        Go=(Button)findViewById(R.id.btn_go);
        NumLayout=(LinearLayout)findViewById(R.id.LinearLayoutNum);
        OTPLayout=(LinearLayout)findViewById(R.id.LinearLayoutOTP);
        OTPText=(EditText)findViewById(R.id.editTextotp);
        resend=(TextView)findViewById(R.id.textViewresend);
        login = (Button) findViewById(R.id.Btn_Login);
        Number = (EditText) findViewById(R.id.editText);
        pbotp=(ProgressBar)findViewById(R.id.progressBarOTP);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phno = Number.getText().toString();
                if (phno.isEmpty() || Number.length()<10) {
                    //Toast.makeText(getApplicationContext(), "Plese Enter Number", Toast.LENGTH_SHORT).show();
                    Number.requestFocus();
                    Number.setError("invalid Input");
                } else {
                    cphno = ("+91" + phno);
                    //Toast.makeText(getApplicationContext(), cphno, Toast.LENGTH_SHORT).show();
                    sendVerificationCode(cphno);
                    NumLayout.setVisibility(View.INVISIBLE);
                    OTPLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = OTPText.getText().toString();
                if (!otp.isEmpty() && !(otp.length()<6)){
                    try {
                        if (code.equals(otp)) {
                            verifycode(otp);
                            SharedPreferences.Editor edito = sharedPreferences.edit();
                            edito.putString("login", "Login");
                            edito.putString("num", phno);
                            edito.apply();
                            String prof = sharedPreferences.getString("profile", "");
                            if (prof.equals("Done")) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(LoginActivity.this, UserProfile.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                                finish();
                            }
                        }
                        else{
                            OTPText.requestFocus();
                            OTPText.setError("Invalid Input");
                        }
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Check SIM inserted In this Devices",Toast.LENGTH_LONG).show();
                    }

                } else {
                    //Toast.makeText(getApplicationContext(),"Please Enter OTP First!",Toast.LENGTH_LONG).show();
                    OTPText.requestFocus();
                    OTPText.setError("Invalid Input");
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(cphno);
            }
        });
    }

    public void verifycode(String code){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
            signInWithCredential(credential);

    }


    public void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Verification Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
        public void sendVerificationCode(String ph) {

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    ph,60,
                    TimeUnit.SECONDS,
                    TaskExecutors.MAIN_THREAD,
                    mCallbacks);
        }


    //the callback to detect the verification status
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerificationId=s;
        }

        @Override
        public void  onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            code=phoneAuthCredential.getSmsCode();
            if(code!=null){
                OTPText.setText(code);
                pbotp.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),code,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification Failed" +e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    };


}
