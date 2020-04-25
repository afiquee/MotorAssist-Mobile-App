package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.R;

public class SelectGoodSamaritansFragment extends Fragment implements View.OnClickListener {

    private CardView mTowing,mFuel;
    Motorcycle motorcycle;

    public SelectGoodSamaritansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_good_samaritans_request, container, false);

        mTowing = (CardView)rootView.findViewById(R.id.towing);
        mTowing.setOnClickListener(this);
        mFuel = (CardView)rootView.findViewById(R.id.fuel);
        mFuel.setOnClickListener(this);

        Session session = new Session(getActivity());
        motorcycle = session.getMotorcycle();

        return rootView;
    }


    @Override
    public void onClick(View v) {



        if(v == mTowing){
            Session session = new Session(getActivity());
            session.clearSession();
            session.setRequest("Towing");
            session.setMotorcycle(motorcycle);

            Fragment fragment = new MotorcyclistHomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(v == mFuel){
            Session session = new Session(getActivity());
            session.clearSession();
            session.setRequest("Fuel Request");
            session.setMotorcycle(motorcycle);

            Fragment fragment = new MotorcyclistHomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }
}
