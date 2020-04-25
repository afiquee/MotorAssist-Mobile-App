package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afiq.motorcycleassist.Models.Motorcyclist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MotorcyclistRegisterActivity extends AppCompatActivity {

    private Button mRegister;

    private TextInputEditText mEmail,mPassword,mCpassword,mName,mPhone;
    private TextView eEmail,ePassword,eCpassword,eName,ePhone;

    private FirebaseAuth mAuth;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcyclist_register);

        mAuth = FirebaseAuth.getInstance();
        mRegister = (Button)findViewById(R.id.register);
        mEmail = (TextInputEditText)findViewById(R.id.email);
        mPassword = (TextInputEditText)findViewById(R.id.password);
        mCpassword = (TextInputEditText)findViewById(R.id.cpassword);
        mName = (TextInputEditText)findViewById(R.id.name);
        mPhone = (TextInputEditText)findViewById(R.id.phone);

        eEmail = (TextView) findViewById(R.id.eEmail);
        ePassword = (TextView)findViewById(R.id.ePassword);
        eCpassword = (TextView)findViewById(R.id.eConfirm);
        eName = (TextView)findViewById(R.id.eName);
        ePhone = (TextView)findViewById(R.id.ePhone);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String cpassword = mCpassword.getText().toString();
                final String name = mName.getText().toString();
                final String phone = mPhone.getText().toString();


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

                if(valid){
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MotorcyclistRegisterActivity.this,new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        token = instanceIdResult.getToken();
                                        String user_id = mAuth.getCurrentUser().getUid();
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(user_id);
                                        current_user_db.setValue(true);
                                        Motorcyclist motorcyclist = new Motorcyclist(user_id,email,name,phone,token);
                                        current_user_db.child("motorcyclistEmail").setValue(motorcyclist.getMotorcyclistEmail());
                                        current_user_db.child("motorcyclistName").setValue(motorcyclist.getMotorcyclistName());
                                        current_user_db.child("motorcyclistPhone").setValue(motorcyclist.getMotorcyclistPhone());
                                        current_user_db.child("token").setValue(motorcyclist.getToken());
                                        Intent intent = new Intent(MotorcyclistRegisterActivity.this, MotorcyclistActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            }
                            else{
                                Toast.makeText(MotorcyclistRegisterActivity.this, "Registration Error !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }




            }
        });

    }
}
