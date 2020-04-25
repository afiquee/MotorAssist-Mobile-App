package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.Models.Service;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectBreakdownFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextInputLayout tyreLayout;
    private TextInputEditText one,two,three;
    private CardView towing,tube,plug,chain,hose;
    private TextView error;
    String tyreType;
    private ArrayList<Service> serviceList = new ArrayList<>();


    public SelectBreakdownFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_select_breakdown, container, false);

        towing = (CardView)rootView.findViewById(R.id.towing);
        towing.setOnClickListener(this);
        tube = (CardView)rootView.findViewById(R.id.tube);
        tube.setOnClickListener(this);

        plug = (CardView)rootView.findViewById(R.id.plug);
        plug.setOnClickListener(this);
        chain = (CardView)rootView.findViewById(R.id.chain);
        chain.setOnClickListener(this);
        hose = (CardView)rootView.findViewById(R.id.hose);
        hose.setOnClickListener(this);

        Session session = new Session(getActivity());
        Motorcycle motorcycle = session.getMotorcycle();
        //motor.setText(motorcycle.getMotorBrand()+" "+motorcycle.getmotorModel());


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Service");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    Service service = child.getValue(Service.class);
                    serviceList.add(service);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Session session = new Session(getActivity());
        session.clearSession();


        if(v == towing){
            for(int x=0;x<serviceList.size();x++){
                if(serviceList.get(x).getServiceName().equalsIgnoreCase("Towing")){

                    session.setService(serviceList.get(x));
                }
            }
        }

        if(v == tube){



            final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.tyre_form, null);

            final TextInputEditText one = (TextInputEditText)dialogView.findViewById(R.id.one);
            final TextInputEditText two = (TextInputEditText)dialogView.findViewById(R.id.two);
            final TextInputEditText three = (TextInputEditText)dialogView.findViewById(R.id.three);
            error = (TextView) dialogView.findViewById(R.id.error);
            Button next = (Button) dialogView.findViewById(R.id.next);
            tyreType = "Original Tyre Size";

            /*
            RadioGroup tyreGroup = (RadioGroup)dialogView.findViewById(R.id.tyreType);
            tyreGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton typeRadioButton = (RadioButton) dialogView.findViewById(checkedId);
                    tyreType = typeRadioButton.getText().toString();
                    if(typeRadioButton.getText().toString().equals("Original Tyre Size")){
                        tyreLayout.setVisibility(View.GONE);
                    }
                    else{
                        tyreLayout.setVisibility(View.VISIBLE);
                    }

                }
            });

             */

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Session session = new Session(getActivity());

                    String first = one.getText().toString();
                    String second = two.getText().toString();
                    String third = three.getText().toString();

                    String regex = "\\d+";

                    if(first.matches(regex) && second.matches(regex) && third.matches(regex)){
                        error.setText("");

                    }
                    else{
                        error.setText("Size must be in digit!");
                        return;
                    }

                    String size = first+"/"+second+"-"+third;

                    session.setTyre("Tyre Size",size.toString());

                    dialogBuilder.dismiss();

                    /*if(tyreType.equals("Original Tyre Size")){
                        session.setTyre("Original Tyre Size",null);
                    }
                    else{
                        session.setTyre("Modified Tyre Size",size.getText().toString());
                    }

                     */
                    for(int x=0;x<serviceList.size();x++) {

                        if (serviceList.get(x).getServiceName().equalsIgnoreCase("Change tyre tube")) {
                            session.setService(serviceList.get(x));
                            Fragment fragment = new MotorcyclistHomeFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }

                }
            });

            dialogBuilder.setView(dialogView);
            dialogBuilder.show();

            return;

            /*NDialog nDialog = new NDialog(getActivity(), ButtonType.TWO_BUTTON);
            ButtonClickListener buttonClickListener = new ButtonClickListener() {
                @Override
                public void onClick(int button) {
                    switch (button) {
                        case NDialog.BUTTON_POSITIVE:
                            nDialog.dismiss();
                            Session session = new Session(getActivity());
                            session.setTyre("Original Tyre Size",null);
                            Fragment fragment = new MotorcyclistHomeFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        case NDialog.BUTTON_NEGATIVE:
                            nDialog.dismiss();
                            NDialog nDialog = new NDialog(getActivity(), ButtonType.NO_BUTTON);
                            nDialog.setCustomView(R.layout.tyre_form);
                            nDialog.setCustomViewClickListener(new CustomViewClickListener() {
                                @Override
                                public void onClickView(View v) {
                                    switch (v.getId()) {
                                        case R.id.next:
                                            tyreSize = (TextInputEditText)rootView.findViewById(R.id.tyreSize);
                                            String size = tyreSize.getText().toString();
                                            Session session = new Session(getActivity());
                                            session.setTyre("Modified Tyre Size",size);
                                            Fragment fragment = new MotorcyclistHomeFragment();
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.main_container, fragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                            break;
                                    }
                                }
                            });
                            nDialog.show();
                            break;
                    }
                }
            };

            nDialog.setPositiveButtonText("Original Tyre Size");
            nDialog.setPositiveButtonTextColor(Color.GREEN);
            nDialog.setPositiveButtonOnClickDismiss(false); // default : true
            nDialog.setPositiveButtonClickListener(buttonClickListener);

// Negative Button
            nDialog.setNegativeButtonText("Modified Tyre Size");
            nDialog.setNegativeButtonTextColor(Color.parseColor("#FF0000"));
            nDialog.setNegativeButtonOnClickDismiss(true); // default : true
            nDialog.setNegativeButtonClickListener(buttonClickListener);
            nDialog.show();
            return;

             */

        }

        if(v == plug){
            for(int x=0;x<serviceList.size();x++) {
                if (serviceList.get(x).getServiceName().equalsIgnoreCase("Plug tubeless tyre")) {
                    session.setService(serviceList.get(x));
                    Fragment fragment = new MotorcyclistHomeFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        }

        if(v == chain){
            for(int x=0;x<serviceList.size();x++){
                if(serviceList.get(x).getServiceName().equalsIgnoreCase("Change chain")){
                    session.setService(serviceList.get(x));
                }
            }
        }

        if(v == hose){
            for(int x=0;x<serviceList.size();x++){
                if(serviceList.get(x).getServiceName().equalsIgnoreCase("Change fuel line hose")){
                    session.setService(serviceList.get(x));
                }
            }
        }


        Fragment fragment = new MotorcyclistHomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }
}
