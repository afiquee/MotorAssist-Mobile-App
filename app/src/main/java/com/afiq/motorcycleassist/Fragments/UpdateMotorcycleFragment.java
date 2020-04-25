package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.MotorBrand;
import com.afiq.motorcycleassist.Models.MotorType;
import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MotorBrandVM;
import com.afiq.motorcycleassist.ViewModel.MotorTypeVM;
import com.afiq.motorcycleassist.ViewModel.MotorcycleVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UpdateMotorcycleFragment extends Fragment implements View.OnClickListener {

    View rootView;

    private Spinner spinnerType,spinnerBrand;
    private List<MotorBrand> brandList;
    private List<MotorType> typeList;
    private Button mCreate;
    private EditText mModel, mPlate;

    private TextView eType,eBrand,eModel,ePlate;

    String selectType,selectBrand,selectFront,selectRear;

    ArrayAdapter<MotorBrand> adapter;
    ArrayAdapter<MotorType> adapterType;
    String motorcycleId;

    private FirebaseAuth mAuth;
    public UpdateMotorcycleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_update_motorcycle, container, false);

        eType = (TextView) rootView.findViewById(R.id.eType);
        eBrand = (TextView)rootView.findViewById(R.id.eBrand);
        eModel = (TextView)rootView.findViewById(R.id.eModel);
        ePlate = (TextView)rootView.findViewById(R.id.ePlate);

        if(getArguments()!= null){
            motorcycleId = getArguments().getString("motorcycleId");

        }

        spinnerType = (Spinner)rootView.findViewById(R.id.spinnerType);
        typeList = new ArrayList<>();

        MotorTypeVM motorTypeVM = ViewModelProviders.of(this).get(MotorTypeVM.class);
        motorTypeVM.getMotorTypes().observe(this, motorTypes -> {
            if(motorTypes != null){
                typeList = motorTypes;

                adapterType = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,typeList);
                adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerType.setAdapter(adapterType);

                populateSpinner();



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
        });

        brandList = new ArrayList<>();
        spinnerBrand = (Spinner)rootView.findViewById(R.id.spinnerBrand);

        MotorBrandVM motorBrandVM = ViewModelProviders.of(this).get(MotorBrandVM.class);
        motorBrandVM.getMotorBrands().observe(this, motorBrands -> {
            if(motorBrands != null){
                brandList = motorBrands;

                adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,brandList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerBrand.setAdapter(adapter);

                populateSpinner();



                spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        selectBrand = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        mModel = (EditText)rootView.findViewById(R.id.model);
        mPlate = (EditText)rootView.findViewById(R.id.plate);

        mCreate = rootView.findViewById(R.id.update);
        mCreate.setOnClickListener(this);



        return rootView;
    }

    public void populateSpinner(){


        mAuth = FirebaseAuth.getInstance();
        MotorcycleVM motorcycleVM = ViewModelProviders.of(this).get(MotorcycleVM.class);
        motorcycleVM.getMotorcycle(motorcycleId,mAuth.getUid()).observe(this, motorcycle -> {
            mModel.setText(motorcycle.getmotorModel());
            mPlate.setText(motorcycle.getMotorId());

            for (int i=0;i<spinnerType.getCount();i++){
                if (spinnerType.getItemAtPosition(i).toString().equals(motorcycle.getMotorType())){
                    spinnerType.setSelection(i);
                }
            }

            for (int i=0;i<spinnerBrand.getCount();i++){
                if (spinnerBrand.getItemAtPosition(i).toString().equals(motorcycle.getMotorBrand())){
                    spinnerBrand.setSelection(i);
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        boolean valid = true;

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        String model = mModel.getText().toString();
        String plate = mPlate.getText().toString();

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
            motorcycleVM.updateMotorcycle(motor).observe(this, message -> {
                if(message != null)
                    Toast.makeText(getActivity(), "Motorcycle successfully updated!", Toast.LENGTH_SHORT).show();

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
