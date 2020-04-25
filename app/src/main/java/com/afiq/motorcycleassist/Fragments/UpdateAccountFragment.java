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

import com.afiq.motorcycleassist.EmailValidator;
import com.afiq.motorcycleassist.Models.Motorcyclist;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MotorcyclistVM;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


public class UpdateAccountFragment extends Fragment {

    private TextInputEditText mEmail,mName,mPhone;
    private TextView eEmail,eName,ePhone;
    private Button update;
    private FirebaseAuth mAuth;
    Motorcyclist currentUser = null;
    public UpdateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Session session = new Session(getActivity());
        String role = session.getRole();
        mAuth = FirebaseAuth.getInstance();
        View rootView;

        if(role.equals("Motorcyclist")){
            rootView =  inflater.inflate(R.layout.fragment_update_motorcyclist_account, container, false);
            mEmail = (TextInputEditText)rootView.findViewById(R.id.email);
            mName = (TextInputEditText)rootView.findViewById(R.id.name);
            mPhone = (TextInputEditText)rootView.findViewById(R.id.phone);
            update = (Button) rootView.findViewById(R.id.update);

            eEmail = (TextView) rootView.findViewById(R.id.eEmail);
            eName = (TextView)rootView.findViewById(R.id.eName);
            ePhone = (TextView)rootView.findViewById(R.id.ePhone);



            MotorcyclistVM motorcyclistVM = ViewModelProviders.of(this).get(MotorcyclistVM.class);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String email = mEmail.getText().toString();
                    final String name = mName.getText().toString();
                    final String phone = mPhone.getText().toString();

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

                    if(valid){
                        Motorcyclist m = new Motorcyclist(mAuth.getUid(),email,name,phone,currentUser.getToken());
                        motorcyclistVM.updateMotorcyclist(m).observe(UpdateAccountFragment.this, message -> {
                            if(message != null){
                                if(message.equals("successful")){
                                    Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    eEmail.setText("Email already exists!");
                                }

                            }
                        });
                    }

                }
            });
            motorcyclistVM.getMotorcyclist(mAuth.getUid()).observe(this, motorcyclist -> {
                if(motorcyclist == null)
                    Toast.makeText(getActivity(), "No motorcycles", Toast.LENGTH_SHORT).show();
                else{
                    currentUser = motorcyclist;
                    mEmail.setText(motorcyclist.getMotorcyclistEmail());
                    mName.setText(motorcyclist.getMotorcyclistName());
                    mPhone.setText(motorcyclist.getMotorcyclistPhone());


                }
            });
        }
        else if(role.equals("Mechanic")){
            rootView =  inflater.inflate(R.layout.fragment_update_motorcyclist_account, container, false);
        }
        else{
            rootView =  inflater.inflate(R.layout.fragment_update_motorcyclist_account, container, false);
        }
        return rootView;
    }

}
