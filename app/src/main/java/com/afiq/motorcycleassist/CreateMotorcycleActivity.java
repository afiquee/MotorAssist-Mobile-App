package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afiq.motorcycleassist.Models.MotorBrand;
import com.afiq.motorcycleassist.Models.MotorType;
import com.afiq.motorcycleassist.Models.Motorcycle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateMotorcycleActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerType,spinnerBrand;
    private RadioGroup gFront,gRear;
    private RadioButton bFront,bRear;
    private List<MotorBrand> brandList;
    private List<MotorType> typeList;
    private Button mCreate;
    private EditText mModel, mPlate;

    String selectType,selectBrand,selectFront,selectRear;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_motorcycle);

        spinnerType = findViewById(R.id.spinnerType);
        typeList = new ArrayList<>();

        gFront = (RadioGroup)findViewById(R.id.front);
        gRear = (RadioGroup)findViewById(R.id.rear);

        MotorType def1 = new MotorType("0","Select Motorcycle Type");


        DatabaseReference dataType = FirebaseDatabase.getInstance().getReference().child("MotorType");


        typeList.add(def1);
        dataType.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    MotorType typeData = child.getValue(MotorType.class);
                    typeList.add(typeData);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ArrayAdapter<MotorType> adapterType = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,typeList);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBrand = findViewById(R.id.spinnerBrand);
        brandList = new ArrayList<>();
        MotorBrand def = new MotorBrand("0","Select Motorcycle Brand");


        DatabaseReference dataBrand = FirebaseDatabase.getInstance().getReference().child("MotorBrand");

        brandList.add(def);
        dataBrand.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    MotorBrand brandData = child.getValue(MotorBrand.class);
                    brandList.add(brandData);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ArrayAdapter<MotorBrand> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,brandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapter);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectBrand = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mModel = (EditText)findViewById(R.id.model);
        mPlate = (EditText)findViewById(R.id.plate);



        mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        /*if (selectType.equals("Select Motorcycle Type")) {
            Toast.makeText(CreateMotorcycleActivity.this, "Please select motorcycle type !", Toast.LENGTH_SHORT).show();
        }
        else if (selectBrand.equals("Select Motorcycle Brand")) {
            Toast.makeText(CreateMotorcycleActivity.this, "Please select motorcycle brand !", Toast.LENGTH_SHORT).show();
        }*/

            mAuth = FirebaseAuth.getInstance();
            String user_id = mAuth.getCurrentUser().getUid();

            String model = mModel.getText().toString();
            String plate = mPlate.getText().toString();

            Motorcycle motor = new Motorcycle (plate,selectType,selectBrand,model);
            DatabaseReference dataMotorcycle = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(user_id).child("Motorcycle").child(plate);
            dataMotorcycle.setValue(true);
            dataMotorcycle.child("motorType").setValue(motor.getMotorType());
            dataMotorcycle.child("motorBrand").setValue(motor.getMotorBrand());
            dataMotorcycle.child("motorModel").setValue(motor.getmotorModel());

            Intent intent = new Intent(CreateMotorcycleActivity.this, MotorcycleListActivity.class);
            startActivity(intent);
            finish();

    }
}
