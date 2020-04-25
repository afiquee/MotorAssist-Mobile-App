package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.MotorBrand;
import com.afiq.motorcycleassist.Models.MotorType;
import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MotorcycleVM;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateMotorcycleFragment extends Fragment implements View.OnClickListener {

    View rootView;

    private Spinner spinnerType,spinnerBrand;
    private RadioGroup gFront,gRear;
    private RadioButton bFront,bRear;
    private List<MotorBrand> brandList;
    private List<MotorType> typeList;
    private Button mCreate;
    private EditText mModel, mPlate;

    private TextView eType,eBrand,eModel,ePlate;

    String selectType,selectBrand,selectFront,selectRear;

    ArrayAdapter<MotorBrand> adapter;
    ArrayAdapter<MotorType> adapterType;

    private FirebaseAuth mAuth;



    public CreateMotorcycleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_create_motorcycle, container, false);

        spinnerType = (Spinner)rootView.findViewById(R.id.spinnerType);
        typeList = new ArrayList<>();

        gFront = (RadioGroup)rootView.findViewById(R.id.front);
        gRear = (RadioGroup)rootView.findViewById(R.id.rear);

        eType = (TextView) rootView.findViewById(R.id.eType);
        eBrand = (TextView)rootView.findViewById(R.id.eBrand);
        eModel = (TextView)rootView.findViewById(R.id.eModel);
        ePlate = (TextView)rootView.findViewById(R.id.ePlate);

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





        spinnerBrand = (Spinner)rootView.findViewById(R.id.spinnerBrand);
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



        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,brandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        adapterType = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,typeList);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mModel = (EditText)rootView.findViewById(R.id.model);
        mPlate = (EditText)rootView.findViewById(R.id.plate);



        mCreate = rootView.findViewById(R.id.create);
        mCreate.setOnClickListener(this);


        populateSpinner();
        return rootView;
    }

    public void populateSpinner(){



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
    }

    @Override
    public void onClick(View v) {

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        String model = mModel.getText().toString();
        String plate = mPlate.getText().toString();

        boolean valid = true;
        if (selectType.equals("Select Motorcycle Type")) {
            eType.setText("Please select motorcycle type !");
            valid = false;
        }
        else{
            eType.setText("");
        }
        if (selectBrand.equals("Select Motorcycle Brand")) {
            eBrand.setText("Please select motorcycle brand !");
        }
        else{
            eBrand.setText("");
        }

        if(plate.isEmpty()){
            ePlate.setText("Plate must not be empty!");
            valid =false;
        }
        else{
            ePlate.setText("");
        }

        if(model.isEmpty()){
            eModel.setText("Model must not be empty!");
            valid =false;
        }
        else{
            eModel.setText("");
        }


        if(valid){
            Motorcycle motor = new Motorcycle (plate,selectType,selectBrand,model);
            MotorcycleVM motorcycleVM = ViewModelProviders.of(this).get(MotorcycleVM.class);
            motorcycleVM.registerMotorcycle(motor).observe(this, message -> {
                if(message != null){
                    if(message.equals("successful")){
                        Toast.makeText(getActivity(), "Motorcycle Successfuly Registered", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            Fragment fragment = new MotorcycleListFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

}
