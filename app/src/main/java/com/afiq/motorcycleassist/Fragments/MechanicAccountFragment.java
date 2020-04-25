package com.afiq.motorcycleassist.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.MainActivity;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.google.firebase.auth.FirebaseAuth;

public class MechanicAccountFragment extends Fragment {

    private TextView mEmail,mName,mPhone;
    private Button logout,update;
    private Button mCopy;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    private FirebaseAuth mAuth;
    public MechanicAccountFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mechanic_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();

        mEmail = (TextView)rootView.findViewById(R.id.email);
        mName = (TextView)rootView.findViewById(R.id.name);
        mPhone = (TextView)rootView.findViewById(R.id.phone);

        MechanicVM mechanicVM = ViewModelProviders.of(this).get(MechanicVM.class);
        mechanicVM.getMechanic(Uid).observe(this, mechanic -> {
            if(mechanic == null)
                Toast.makeText(getActivity(), "No mechanic", Toast.LENGTH_SHORT).show();
            else{
                mEmail.setText(mechanic.getMechEmail());
                mName.setText(mechanic.getMechName());
                mPhone.setText(mechanic.getMechPhone());
            }
        });

        update = (Button)rootView.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UpdateMechanicFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });


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
