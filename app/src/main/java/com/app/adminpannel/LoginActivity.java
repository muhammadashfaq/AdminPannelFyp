package com.app.adminpannel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.adminpannel.Constants.Constants;
import com.app.adminpannel.Session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText edtTxtEamil,edtTxtPassword;
    String email,password;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    Button btnLogin;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);

        initizalizations();

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loggin in");
        progressDialog.setMessage("Please wait a while");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtTxtEamil.getText().toString().trim();
                password = edtTxtPassword.getText().toString().trim();


                if(Constants.isConnectedtoInternet(LoginActivity.this)){
                    if(email.isEmpty() && password.isEmpty()){
                        edtTxtEamil.setError("Please enter Email");
                        edtTxtPassword.setError("Please enter Password");
                        edtTxtEamil.requestFocus();
                    }else{
                        performSignUp();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Seems you're not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void performSignUp() {
        progressDialog.show();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Admin");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    String emailDb = dataSnapshot.child("email").getValue().toString();
                    String passDb = dataSnapshot.child("password").getValue().toString();

                    if(email.equalsIgnoreCase(emailDb) && password.equalsIgnoreCase(passDb)){
                        progressDialog.dismiss();
                        sessionManager.logTheUserIn(true,emailDb,passDb);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Please Enter Valid Email and Password" , Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initizalizations() {
        edtTxtEamil=findViewById(R.id.edt_txt_email);
        edtTxtPassword=findViewById(R.id.edt_txt_password);
        btnLogin = findViewById(R.id.btn_login);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(sessionManager.checkUserLogin()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }
}
