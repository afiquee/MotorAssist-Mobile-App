package com.afiq.motorcycleassist.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.afiq.motorcycleassist.MainActivity;
import com.afiq.motorcycleassist.R;
import com.google.firebase.auth.FirebaseAuth;


public class AdminAccountFragment extends Fragment {

    private TextView mEmail,mName,mPhone;
    private Button logout,update;

    private FirebaseAuth mAuth;

    public AdminAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (TextView)rootView.findViewById(R.id.email);
        mName = (TextView)rootView.findViewById(R.id.name);
        mPhone = (TextView)rootView.findViewById(R.id.phone);

        logout = (Button)rootView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });



        return rootView;
    }

}
