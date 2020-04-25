package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afiq.motorcycleassist.R;

public class SelectRequestTypeFragment extends Fragment implements View.OnClickListener {

    private CardView mShop,mGood;

    public SelectRequestTypeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_request_type, container, false);

        mShop = (CardView)rootView.findViewById(R.id.shop);
        mShop.setOnClickListener(this);
        mGood = (CardView)rootView.findViewById(R.id.good);
        mGood.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        if(v == mShop){
            Fragment fragment = new SelectBreakdownFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(v == mGood){
            Fragment fragment = new SelectGoodSamaritansFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }
}
