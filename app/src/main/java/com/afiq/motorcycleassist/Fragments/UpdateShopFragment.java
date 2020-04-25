package com.afiq.motorcycleassist.Fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.EmailValidator;
import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class UpdateShopFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText mEmail,mName,mPhone,mStart,mEnd;
    private TextView eEmail,ePassword,eCpassword,eName,ePhone,eDays,eStart,eEnd;
    private CheckBox monday,tuesday,wednesday,thursday,friday,saturday,sunday,mSuperbike;
    private int  mHour, mMinute;
    private Button update;
    private FirebaseAuth mAuth;
    public UpdateShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_update_shop, container, false);
        mEmail = (TextInputEditText)rootView.findViewById(R.id.email);
        mName = (TextInputEditText)rootView.findViewById(R.id.name);
        mPhone = (TextInputEditText)rootView.findViewById(R.id.phone);
        mStart = (TextInputEditText) rootView.findViewById(R.id.start);
        mStart.setOnClickListener(this);
        mEnd = (TextInputEditText) rootView.findViewById(R.id.end);
        mEnd.setOnClickListener(this);

        eEmail = (TextView) rootView.findViewById(R.id.eEmail);
        ePassword = (TextView)rootView.findViewById(R.id.ePassword);
        eCpassword = (TextView)rootView.findViewById(R.id.eConfirm);
        eName = (TextView)rootView.findViewById(R.id.eName);
        ePhone = (TextView)rootView.findViewById(R.id.ePhone);
        eDays = (TextView)rootView.findViewById(R.id.eDays);
        eStart = (TextView)rootView.findViewById(R.id.eStart);
        eEnd = (TextView)rootView.findViewById(R.id.eEnd);

        update = (Button) rootView.findViewById(R.id.update);


        monday = (CheckBox) rootView.findViewById(R.id.monday);
        tuesday = (CheckBox) rootView.findViewById(R.id.tuesday);
        wednesday = (CheckBox) rootView.findViewById(R.id.wednesday);
        thursday = (CheckBox) rootView.findViewById(R.id.thursday);
        friday = (CheckBox) rootView.findViewById(R.id.friday);
        saturday = (CheckBox) rootView.findViewById(R.id.saturday);
        sunday = (CheckBox) rootView.findViewById(R.id.sunday);
        mSuperbike = (CheckBox)rootView.findViewById(R.id.superbikeTowing);

        mAuth = FirebaseAuth.getInstance();

        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopVM.getShop(mAuth.getUid()).observe(this, shop -> {
            if(shop == null)
                Toast.makeText(getActivity(), "No shop", Toast.LENGTH_SHORT).show();
            else{
                mEmail.setText(shop.getShopEmail());
                mName.setText(shop.getShopName());
                mPhone.setText(shop.getShopPhone());

                for(int x=0;x<shop.getWorkingDays().size();x++){
                    String day = shop.getWorkingDays().get(x);
                    if(day.equals("Monday")){
                        monday.setChecked(true);
                    }
                    if(day.equals("Tuesday")){
                        tuesday.setChecked(true);
                    }
                    if(day.equals("Wednesday")){
                        wednesday.setChecked(true);
                    }
                    if(day.equals("Thursday")){
                        thursday.setChecked(true);
                    }
                    if(day.equals("Friday")){
                        friday.setChecked(true);
                    }
                    if(day.equals("Saturday")){
                        saturday.setChecked(true);
                    }
                    if(day.equals("Sunday")){
                        sunday.setChecked(true);
                    }
                }

                mStart.setText(shop.getStartTime());
                mEnd.setText(shop.getEndTime());


            }
        });



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> workingDays = new ArrayList<>();
                final String email = mEmail.getText().toString();
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
                    Shop shop = new Shop(mAuth.getUid(), email,name,phone,workingDays,start,end, superbikeTowing, null, null,null);
                    shopVM.updateShop(shop).observe(UpdateShopFragment.this, message -> {
                        if(message != null){
                            if(message.equals("successful")){
                                Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
                                Fragment fragment = new ShopAccountFragment();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.main_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                            else{
                                eEmail.setText("Email already exists!");
                            }

                        }
                    });
                }

            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {

        if(v == mStart) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
    }
}
