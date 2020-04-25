package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.ViewModel.AdminVM;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.afiq.motorcycleassist.ViewModel.MotorcyclistVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class  LoginActivity extends AppCompatActivity {

private EditText mEmail, mPassword;
    private TextView eEmail,ePassword,eLogin;
private Button mLogin;
private TextView mRegister;
private CardView loading;

private FirebaseAuth mAuth;
private AuthStateListener firebaseAuthListener;
private MotorcyclistVM motorcyclistVM;
private AdminVM adminVM;
private ShopVM shopVM;
private MechanicVM mechanicVM;
private boolean valid;

    int check;
    boolean rejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcyclist_login);
        motorcyclistVM= ViewModelProviders.of(LoginActivity.this).get(MotorcyclistVM.class);
        adminVM =  ViewModelProviders.of(LoginActivity.this).get(AdminVM.class);
        shopVM =  ViewModelProviders.of(LoginActivity.this).get(ShopVM.class);
        mechanicVM =  ViewModelProviders.of(LoginActivity.this).get(MechanicVM.class);
        Session session = new Session(LoginActivity.this);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        eEmail = (TextView) findViewById(R.id.eEmail);
        ePassword = (TextView)findViewById(R.id.ePassword);
        eLogin = (TextView)findViewById(R.id.eLogin);

        loading = findViewById(R.id.loading);



        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                check =0;
                rejected = false;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Log.d("TAG","masuk user "+user.toString()+ user.getEmail());
                    motorcyclistVM.auth().observe(LoginActivity.this, message -> {
                        if(message!= null){
                            if(message.equals("isMotorcyclist")){
                                motorcyclistVM.setToken().observe(LoginActivity.this, message2 -> {
                                    if(message2 != null){
                                        if(message2.equals("successful")){
                                            session.setRole("Motorcyclist");
                                            Intent intent = new Intent(LoginActivity.this, MotorcyclistActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else{
                                mechanicVM.auth().observe(LoginActivity.this, message3 -> {
                                    if(message3!= null){
                                        if(message3.equals("isMechanic")){
                                            mechanicVM.setToken().observe(LoginActivity.this, message4 -> {
                                                if(message4 != null){
                                                    if(message4.equals("successful")){
                                                        session.setRole("Mechanic");
                                                        Intent intent = new Intent(LoginActivity.this, MechanicActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                        else if(message3.equals("notMechanic")){
                                            adminVM.auth().observe(LoginActivity.this, message5 -> {
                                                if(message5!= null){
                                                    if(message5.equals("isAdmin")){
                                                        adminVM.setToken().observe(LoginActivity.this, message6 -> {
                                                            if(message6 != null){
                                                                if(message6.equals("successful")){
                                                                    session.setRole("Admin");
                                                                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else if(message5.equals("notAdmin")){
                                                        shopVM.auth().observe(LoginActivity.this, message7 -> {
                                                            if(message7!= null){
                                                                if(message7.equals("isShop")){
                                                                    shopVM.setToken().observe(LoginActivity.this, message8 -> {
                                                                        if(message8 != null){
                                                                            if(message8.equals("successful")){
                                                                                session.setRole("Shop");
                                                                                Intent intent = new Intent(LoginActivity.this, ShopActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else if(message7.equals("pending")){
                                                                    Log.d("TAG","masuk pending");
                                                                    loading.setVisibility(View.GONE);
                                                                    Intent intent = new Intent(LoginActivity.this, RestrictedActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else if(message7.equals("removed")){
                                                                    Log.d("TAG","masuk removed");
                                                                    rejected =true;
                                                                    loading.setVisibility(View.GONE);
                                                                    Intent intent = new Intent(LoginActivity.this, RestrictedActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }

                                                        });
                                                    }
                                                }

                                            });
                                        }
                                    }

                                });
                            }
                        }

                    });



                }
            }
        };

        mRegister = (TextView)findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SelectRoleActivity.class);
                startActivity(intent);
            }
        });

        mLogin = (Button)findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                valid =true;
                eLogin.setText("");
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(email.isEmpty()){
                    eEmail.setText("Email must not be empty!");
                    valid =false;
                }
                else{
                    eEmail.setText("");
                }

                if(password.isEmpty()){
                    ePassword.setText("Password must not be empty!");
                    valid =false;
                }
                else{
                    ePassword.setText("");
                }

                if(valid){
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                loading.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    loading.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
