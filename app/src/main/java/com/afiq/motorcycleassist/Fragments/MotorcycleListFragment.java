package com.afiq.motorcycleassist.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.MotorcycleListRecyclerView.MotorcycleListAdapter;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MotorcycleVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MotorcycleListFragment extends Fragment implements MotorcycleListAdapter.OnListListener {

    private Button mCreate;
    private ImageView edit,delete;

    private RecyclerView mMotorcycleListReyclerView;
    private RecyclerView.Adapter mMotorcycleListAdapter;
    private RecyclerView.LayoutManager mMotorcycleListLayoutManager;

    private FirebaseAuth mAuth;

    private List<Motorcycle> motorcycleList = new ArrayList();

    public MotorcycleListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_motorcycle_list, container, false);

        mMotorcycleListReyclerView = (RecyclerView) rootView.findViewById(R.id.MotorcycleListRecyclerView);
        mMotorcycleListReyclerView.setNestedScrollingEnabled(false);

        mMotorcycleListLayoutManager = new LinearLayoutManager(getActivity());
        mMotorcycleListReyclerView.setLayoutManager(mMotorcycleListLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();


        MotorcycleVM motorcycleVM = ViewModelProviders.of(this).get(MotorcycleVM.class);
        motorcycleVM.getMotorcycles(user_id).observe(this, motorcycles -> {
            if(motorcycles == null)
            Toast.makeText(getActivity(), "No motorcycles", Toast.LENGTH_SHORT).show();
            else{
                motorcycleList = motorcycles;
                mMotorcycleListAdapter = new MotorcycleListAdapter(motorcycleList, this);
                mMotorcycleListReyclerView.setAdapter(mMotorcycleListAdapter);
                mMotorcycleListAdapter.notifyDataSetChanged();
            }
        });



        mCreate = rootView.findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CreateMotorcycleFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });

        return rootView;
    }

    @Override
    public void onListClick(int position) {
        Session session = new Session(getActivity());
        session.setMotorcycle(motorcycleList.get(position));
        Fragment fragment = new SelectRequestTypeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onEditClick(int position) {
        Fragment fragment = new UpdateMotorcycleFragment();
        Bundle args = new Bundle();
        args.putString("motorcycleId", motorcycleList.get(position).getMotorId());
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDeleteClick(int position) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Delete motorcycle")
                .setMessage("Are you sure you want to delete this motorcycle?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MotorcycleVM motorcycleVM = ViewModelProviders.of(getActivity()).get(MotorcycleVM.class);
                        motorcycleVM.deleteMotorcycle(motorcycleList.get(position).getMotorId()).observe(getActivity(), message -> {
                            if(message != null){
                                Toast.makeText(getActivity(), "Motorcycle successfully deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();



    }

}
