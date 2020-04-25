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

import com.afiq.motorcycleassist.Adapters.MechanicHistoryAdapter;
import com.afiq.motorcycleassist.Models.Request;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.RequestVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MechanicHistoryFragment extends Fragment implements MechanicHistoryAdapter.OnListListener {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;
    private List<Request> requestList = new ArrayList();

    public MechanicHistoryFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mechanic_history, container, false);

        TextView msg = (TextView) rootView.findViewById(R.id.msg);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();
        RequestVM requestVM = ViewModelProviders.of(this).get(RequestVM.class);
        requestVM.getMechAssistanceHistory(Uid).observe(this, req -> {
            if(req == null){
                msg.setVisibility(View.VISIBLE);
            }

            else{
                requestList = req;
                adapter = new MechanicHistoryAdapter(requestList, this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onListClick(int position) {

        Fragment fragment = new MotorcyclistHistoryDetailFragment();
        Bundle args = new Bundle();
        args.putString("requestId", requestList.get(position).getRequestId());
        args.putString("requestType", "Motorcyclist Shop Request");
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}
