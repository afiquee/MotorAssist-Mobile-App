package com.afiq.motorcycleassist;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.LatLong;
import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.Service.PushNotificationHelper;
import com.afiq.motorcycleassist.ViewModel.AdminVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Calendar;

public class ShopRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail,mPassword,mCpassword,mName,mPhone,mStart,mEnd;
    private TextView eEmail,ePassword,eCpassword,eName,ePhone,eDays,eStart,eEnd;
    private CheckBox monday,tuesday,wednesday,thursday,friday,saturday,sunday,mSuperbike;
    private Button next;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Shop shop;
    private String token;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register);



        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);
        mCpassword = (EditText)findViewById(R.id.cpassword);
        mName = (EditText)findViewById(R.id.name);
        mPhone = (EditText)findViewById(R.id.phone);

        eEmail = (TextView) findViewById(R.id.eEmail);
        ePassword = (TextView)findViewById(R.id.ePassword);
        eCpassword = (TextView)findViewById(R.id.eConfirm);
        eName = (TextView)findViewById(R.id.eName);
        ePhone = (TextView)findViewById(R.id.ePhone);
        eDays = (TextView)findViewById(R.id.eDays);
        eStart = (TextView)findViewById(R.id.eStart);
        eEnd = (TextView)findViewById(R.id.eEnd);

        monday = (CheckBox) findViewById(R.id.monday);
        tuesday = (CheckBox) findViewById(R.id.tuesday);
        wednesday = (CheckBox) findViewById(R.id.wednesday);
        thursday = (CheckBox) findViewById(R.id.thursday);
        friday = (CheckBox) findViewById(R.id.friday);
        saturday = (CheckBox) findViewById(R.id.saturday);
        sunday = (CheckBox) findViewById(R.id.sunday);

        mStart = (EditText)findViewById(R.id.start);
        mStart.setOnClickListener(this);
        mStart.setInputType(InputType.TYPE_NULL);

        mEnd = (EditText)findViewById(R.id.end);
        mEnd.setOnClickListener(this);

        mSuperbike = (CheckBox)findViewById(R.id.superbikeTowing);

        next = (Button)findViewById(R.id.register);
        next.setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {

        if(v == mStart) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if(minute < 10){
                                mStart.setText(hourOfDay + ":" + minute+"0");
                            }
                            else{
                                mStart.setText(hourOfDay + ":" + minute);
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if(v == mEnd) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if(minute < 10){
                                mEnd.setText(hourOfDay + ":" + minute+"0");
                            }
                            else{
                                mEnd.setText(hourOfDay + ":" + minute);
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if(v == next) {

            final ArrayList<String> workingDays = new ArrayList<>();
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            final String cpassword = mCpassword.getText().toString();
            final String name = mName.getText().toString();
            final String phone = mPhone.getText().toString();
            String towing = "";
            if(mSuperbike.isChecked()){
                  towing = "Yes";
            }
            else{
                towing = "No";
            }

            final String superbikeTowing = towing;


            if(monday.isChecked())
                workingDays.add("Monday");
            if(tuesday.isChecked())
                workingDays.add("Tuesday");
            if(wednesday.isChecked())
                workingDays.add("Wednesday");
            if(thursday.isChecked())
                workingDays.add("Thursday");
            if(friday.isChecked())
                workingDays.add("Friday");
            if(saturday.isChecked())
                workingDays.add("Saturday");
            if(sunday.isChecked())
                workingDays.add("Sunday");

            final String start = mStart.getText().toString();
            final String end = mEnd.getText().toString();

            final LatLong latLong = new LatLong(0.0,0.0);

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
                eName.setText("Shop name must not be empty!");
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

            if(!monday.isChecked() && !tuesday.isChecked() && !wednesday.isChecked() && !thursday.isChecked() && !friday.isChecked()
               && !saturday.isChecked() && !sunday.isChecked()){
                eDays.setText("Please select at least one working days!");
                valid =false;
            }
            else{
                eDays.setText("");
            }
            if(start.isEmpty()){
                eStart.setText("Start time must not be empty!");
                valid =false;
            }
            else{
                eStart.setText("");
            }

            if(end.isEmpty()){
                eEnd.setText("End time must not be empty!");
                valid =false;
            }
            else{
                eEnd.setText("");
            }


            if(valid){
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(ShopRegisterActivity.this,new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    token = instanceIdResult.getToken();
                                    String status = "Pending";
                                    shop = new Shop("", email,name,phone,workingDays,start,end, superbikeTowing, latLong, status,token);
                                    ShopVM shopVM = ViewModelProviders.of(ShopRegisterActivity.this).get(ShopVM.class);
                                    shopVM.registerShop(shop).observe(ShopRegisterActivity.this, message -> {
                                        if(message != null){

                                            AdminVM adminVM  = ViewModelProviders.of(ShopRegisterActivity.this).get(AdminVM.class);
                                            adminVM.getAllAdmin().observe(ShopRegisterActivity.this, admins->{
                                                if(admins != null){
                                                    Log.d("TAG", "admin  : "+admins.size());
                                                    for(int x=0; x<admins.size();x++){
                                                        Log.d("TAG", "token  : "+admins.get(x).getToken());
                                                        pushNotificationHelper.sendNotification(admins.get(x).getToken(),"New Shop has registered!",name + " is waiting for your approval");
                                                    }
                                                    /*
                                                    Iterator<Admin> listIterable = admins.iterator();
                                                    while (listIterable.hasNext()) {
                                                        Log.d("TAG", "TOKEN : "+listIterable.next().getToken());
                                                        pushNotificationHelper.sendNotification(listIterable.next().getToken(),"New Shop has registered!",name + " is waiting for your approval");
                                                    }

                                                     */
                                                    Intent intent = new Intent(ShopRegisterActivity.this, ShopRegisterMapActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

                                        }

                                    });

                                }
                            });

                        }
                        else {
                            eEmail.setText("Email already exists!");
                        }
                    }
                });
            }



        }

    }
}
