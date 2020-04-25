package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afiq.motorcycleassist.Models.Mechanic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MechanicRegisterActivity extends AppCompatActivity {

    private Button mRegister;
    private TextInputEditText mEmail,mPassword,mCpassword,mName,mPhone,mShopId;
    private TextView eEmail,ePassword,eCpassword,eName,ePhone,eShopId;
    private String token;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(MechanicRegisterActivity.this, MechanicHomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }


        };

        mEmail = (TextInputEditText)findViewById(R.id.email);
        mPassword = (TextInputEditText)findViewById(R.id.password);
        mCpassword = (TextInputEditText)findViewById(R.id.cpassword);
        mName = (TextInputEditText)findViewById(R.id.name);
        mPhone = (TextInputEditText)findViewById(R.id.phone);
        mShopId = (TextInputEditText)findViewById(R.id.shopId);

        eEmail = (TextView) findViewById(R.id.eEmail);
        ePassword = (TextView)findViewById(R.id.ePassword);
        eCpassword = (TextView)findViewById(R.id.eConfirm);
        eName = (TextView)findViewById(R.id.eName);
        ePhone = (TextView)findViewById(R.id.ePhone);
        eShopId = (TextView)findViewById(R.id.eShopId);



        mRegister = (Button)findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String cpassword = mCpassword.getText().toString();
                final String name = mName.getText().toString();
                final String phone = mPhone.getText().toString();
                final String shopId = mShopId.getText().toString();

                boolean valid =true;
                EmailValidator emailValidator = new EmailValidator();

                if(email.isEmpty()){
                    eEmail.setText("Email must not be empty!");
                    valid =false;
                }
                else{
                    if(emailValidator.validateEmail(email)){
                        eEmail.setText("");
                    }
                    else{
                        eEmail.setText("Invalid email address!");
                        valid =false;
                    }
                }

                if(password.isEmpty()){
                    ePassword.setText("Password must not be empty!");
                    valid =false;
                }
                else{

                    if(password.length()<6){
                        ePassword.setText("Password must have at least 6 characters!");
                        valid =false;
                    }
                    else{
                        ePassword.setText("");
                    }

                }

                if(cpassword.isEmpty()){
                    eCpassword.setText("Re-enter password must not be empty!");
                    valid =false;
                }
                else{
                    if(!password.equals(cpassword)){
                        eCpassword.setText("Passwords do not match!");
                        valid =false;
                    }
                    else{
                        eCpassword.setText("");
                    }

                }

                if(name.isEmpty()){
                    eName.setText("Name must not be empty!");
                    valid =false;
                }
                else{
                    eName.setText("");
                }

                if(phone.isEmpty()){
                    ePhone.setText("Shop name must not be empty!");
                    valid =false;
                }
                else{
                    ePhone.setText("");
                }

                if(shopId.isEmpty()){
                    eShopId.setText("Shop Id must not be empty!");
                    valid =false;
                }
                else{
                    eShopId.setText("");
                }

                if(valid){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(shopId);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                eShopId.setText("");
                                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {

                                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MechanicRegisterActivity.this,new OnSuccessListener<InstanceIdResult>() {
                                                @Override
                                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                                    token = instanceIdResult.getToken();

                                                    String user_id = mAuth.getCurrentUser().getUid();
                                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(user_id);
                                                    current_user_db.setValue(true);

                                                    Mechanic mechanic = new Mechanic(user_id,email,name,phone,"Not Working", token);

                                                    current_user_db.child("mechEmail").setValue(mechanic.getMechEmail());
                                                    current_user_db.child("mechName").setValue(mechanic.getMechName());
                                                    current_user_db.child("mechPhone").setValue(mechanic.getMechPhone());
                                                    current_user_db.child("mechStatus").setValue(mechanic.getMechStatus());
                                                    current_user_db.child("token").setValue(mechanic.getToken());
                                                    current_user_db.child("shopId").setValue(shopId);



                                                    Intent intent = new Intent(MechanicRegisterActivity.this, MechanicActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });




                                        }
                                    }
                                });
                            }
                            else {
                                eShopId.setText("Invalid Shop Id!");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
