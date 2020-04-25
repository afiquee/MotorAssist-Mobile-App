package com.afiq.motorcycleassist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SelectRoleActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView bMotorcyclist, bShop, bMechanic;
    final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);


        /*
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword("Admin@gmail.com","Admin123").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Admin").child(user_id);

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SelectRoleActivity.this,new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String token = instanceIdResult.getToken();
                            databaseReference.child("adminEmail").setValue("Admin");
                            databaseReference.child("adminName").setValue("Admin");
                            databaseReference.child("token").setValue(token);
                            mAuth.signOut();

                        }
                    });
                }


            }
        });

         */



        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).
                edit().clear().apply();

        bMotorcyclist = (CardView)findViewById(R.id.motorcyclist);
        bShop = (CardView)findViewById(R.id.shop);
        bMechanic = (CardView)findViewById(R.id.mechanic);

        bMotorcyclist.setOnClickListener(this);
        bShop.setOnClickListener(this);
        bMechanic.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(SelectRoleActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SelectRoleActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(SelectRoleActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

    }


    @Override
    public void onClick(View v) {

        if(v==bMotorcyclist){
            Intent intent = new Intent(SelectRoleActivity.this, MotorcyclistRegisterActivity.class);
            startActivity(intent);
            finish();
        }

        if(v == bShop) {
            Intent intent = new Intent(SelectRoleActivity.this, ShopRegisterActivity.class);
            startActivity(intent);
        }

        if(v == bMechanic) {
            Intent intent = new Intent(SelectRoleActivity.this, MechanicRegisterActivity.class);
            startActivity(intent);
        }


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
}
