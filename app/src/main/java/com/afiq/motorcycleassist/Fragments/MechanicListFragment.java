package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Adapters.MechanicListAdapter;
import com.afiq.motorcycleassist.Models.Mechanic;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MechanicListFragment extends Fragment implements MechanicListAdapter.OnListListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;

    private List<Mechanic> mechanicList = new ArrayList();
    public MechanicListFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mechanic_list, container, false);


        TextView msg = (TextView) rootView.findViewById(R.id.msg);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();

        MechanicVM mechanicVM = ViewModelProviders.of(this).get(MechanicVM.class);
        mechanicVM.getShopMechanic(Uid).observe(this, mechanics -> {
            if(mechanics == null){
                msg.setVisibility(View.VISIBLE);
            }

            else{
                mechanicList = mechanics;
                adapter = new MechanicListAdapter(mechanicList, this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });


        return rootView;
    }

    @Override
    public void onListClick(int position) {

        Fragment fragment = new MechanicDetailFragment();
        Bundle args = new Bundle();
        args.putString("mechId", mechanicList.get(position).getMechId());
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}
