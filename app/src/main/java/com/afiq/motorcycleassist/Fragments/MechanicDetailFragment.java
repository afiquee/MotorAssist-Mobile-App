package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.firebase.auth.FirebaseAuth;

public class MechanicDetailFragment extends Fragment {

    private TextView mEmail,mName,mPhone,mShop;
    private Button retire;
    private FirebaseAuth mAuth;
    public MechanicDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mechanic_detail, container, false);

        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();
        String mechId = getArguments().getString("mechId");

        mEmail = (TextView)rootView.findViewById(R.id.email);
        mName = (TextView)rootView.findViewById(R.id.name);
        mPhone = (TextView)rootView.findViewById(R.id.phone);
        mShop = (TextView)rootView.findViewById(R.id.shop);
        retire = (Button) rootView.findViewById(R.id.retire);

        MechanicVM mechanicVM = ViewModelProviders.of(this).get(MechanicVM.class);
        mechanicVM.getMechanic(mechId).observe(this, mechanic -> {
            if(mechanic == null)
                Toast.makeText(getActivity(), "No mechanic", Toast.LENGTH_SHORT).show();
            else{
                mEmail.setText(mechanic.getMechEmail());
                mName.setText(mechanic.getMechName());
                mPhone.setText(mechanic.getMechPhone());

                if(!mechanic.getMechStatus().equals("Retired")){
                    retire.setVisibility(View.VISIBLE);
                }
            }
        });

        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopVM.getMechShop(Uid).observe(this, shop -> {
            if(shop != null){
                mShop.setText(shop.getShopName());
            }

        });

        retire = (Button)rootView.findViewById(R.id.retire);
        retire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mechanicVM.retireMechanic(mechId).observe(MechanicDetailFragment.this, mechanic -> {
                    if(mechanic != null){
                        retire.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Update successful!", Toast.LENGTH_SHORT).show();
                    }



                });




            }
        });

        return rootView;
    }

}
