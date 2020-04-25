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
import com.afiq.motorcycleassist.Models.Mechanic;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


public class UpdateMechanicFragment extends Fragment {

    private TextInputEditText mEmail,mName,mPhone;
    private TextView eEmail,eName,ePhone;
    private Button update;
    private FirebaseAuth mAuth;
    public UpdateMechanicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();

        View rootView = inflater.inflate(R.layout.fragment_update_mechanic, container, false);
        mEmail = (TextInputEditText)rootView.findViewById(R.id.email);
        mName = (TextInputEditText)rootView.findViewById(R.id.name);
        mPhone = (TextInputEditText)rootView.findViewById(R.id.phone);
        update = (Button) rootView.findViewById(R.id.update);

        eEmail = (TextView) rootView.findViewById(R.id.eEmail);
        eName = (TextView)rootView.findViewById(R.id.eName);
        ePhone = (TextView)rootView.findViewById(R.id.ePhone);

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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String name = mName.getText().toString();
                final String phone = mPhone.getText().toString();
                Mechanic m = new Mechanic(Uid,email,name,phone,null,null);

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
                    mechanicVM.updateMechanic(m).observe(UpdateMechanicFragment.this, message -> {
                        if(message != null){
                            if(message.equals("successful")){
                                Toast.makeText(getActivity(), "Update successful!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                eEmail.setText("Email already exists!");
                            }

                        }
                    });
                }


            }
        });

        return rootView;
    }

}
