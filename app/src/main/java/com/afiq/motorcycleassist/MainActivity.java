package com.afiq.motorcycleassist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.ViewModel.AdminVM;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.afiq.motorcycleassist.ViewModel.MotorcyclistVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity{

    final int LOCATION_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private MotorcyclistVM motorcyclistVM;
    private AdminVM adminVM;
    private ShopVM shopVM;
    private MechanicVM mechanicVM;

    int check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        motorcyclistVM= ViewModelProviders.of(MainActivity.this).get(MotorcyclistVM.class);
        adminVM =  ViewModelProviders.of(MainActivity.this).get(AdminVM.class);
        shopVM =  ViewModelProviders.of(MainActivity.this).get(ShopVM.class);
        mechanicVM =  ViewModelProviders.of(MainActivity.this).get(MechanicVM.class);
        Session session = new Session(MainActivity.this);
        session.clearSession();

        String gsRequestKey = getIntent().getStringExtra("gsRequestKey");


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if(FirebaseAuth.getInstance().getCurrentUser()== null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                check =0;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    motorcyclistVM.auth().observe(MainActivity.this, message -> {
                        if(message!= null){
                            if(message.equals("isMotorcyclist")){
                                motorcyclistVM.setToken().observe(MainActivity.this, message2 -> {
                                    if(message2 != null){
                                        if(message2.equals("successful")){
                                            session.setRole("Motorcyclist");
                                            Intent intent = new Intent(MainActivity.this, MotorcyclistActivity.class);
                                            Bundle mBundle = new Bundle();
                                            mBundle.putString("gsRequestKey", gsRequestKey);
                                            intent.putExtras(mBundle);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else if(message.equals("notMotorcyclist")){
                                check++;
                                if(check == 4){
                                    Intent intent = new Intent(MainActivity.this, RestrictedActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                    });


                    mechanicVM.auth().observe(MainActivity.this, message -> {
                        if(message!= null){
                            if(message.equals("isMechanic")){
                                mechanicVM.setToken().observe(MainActivity.this, message2 -> {
                                    if(message2 != null){
                                        if(message2.equals("successful")){
                                            session.setRole("Mechanic");
                                            Intent intent = new Intent(MainActivity.this, MechanicActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else if(message.equals("notMechanic")){
                                check++;
                                if(check == 4){
                                    Intent intent = new Intent(MainActivity.this, RestrictedActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                    });




                    shopVM.auth().observe(MainActivity.this, message -> {
                        if(message!= null){
                            if(message.equals("isShop")){
                                shopVM.setToken().observe(MainActivity.this, message2 -> {
                                    if(message2 != null){
                                        if(message2.equals("successful")){
                                            session.setRole("Shop");
                                            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else if(message.equals("pending")){
                                shopVM.setToken().observe(MainActivity.this, message2 -> {
                                    if(message2 != null){
                                        if(message2.equals("successful")){
                                            Intent intent = new Intent(MainActivity.this, RestrictedActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else if(message.equals("removed")){
                                check++;
                                if(check == 4){

                                    Intent intent = new Intent(MainActivity.this, RestrictedActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                    });

                    adminVM.auth().observe(MainActivity.this, message -> {
                        if(message!= null){
                            if(message.equals("isAdmin")){
                                adminVM.setToken().observe(MainActivity.this, message2 -> {
                                    if(message2 != null){
                                        if(message2.equals("successful")){
                                            session.setRole("Admin");
                                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else if(message.equals("notAdmin")){
                                check++;
                                if(check == 4){
                                    Intent intent = new Intent(MainActivity.this, RestrictedActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                    });







                }
            }
        };


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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
