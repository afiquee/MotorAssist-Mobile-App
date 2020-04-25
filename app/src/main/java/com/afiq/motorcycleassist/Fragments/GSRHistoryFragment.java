package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.util.Log;
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

import com.afiq.motorcycleassist.Adapters.GSRHistoryAdapter;
import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.GoodSamaritanRequestVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GSRHistoryFragment extends Fragment implements GSRHistoryAdapter.OnListListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;

    private List<GoodSamaritanRequest> gsrList = new ArrayList();
    public GSRHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gsr_history, container, false);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        TextView msg = (TextView) rootView.findViewById(R.id.msg);



        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();

        Session session = new Session(getActivity());
        String role = session.getRole();

        if(role.equals("Motorcyclist")){
            GoodSamaritanRequestVM goodSamaritanRequestVM = ViewModelProviders.of(this).get(GoodSamaritanRequestVM.class);
            goodSamaritanRequestVM.getGSRHistory(Uid).observe(this, gsr -> {
                if(gsr == null){
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("No Good Samaritan Request");
                }
                else{
                    gsrList = gsr;
                    adapter = new GSRHistoryAdapter(gsrList, this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        if(role.equals("Admin")){
            Log.d("TAG", "role : "+role);
            GoodSamaritanRequestVM goodSamaritanRequestVM = ViewModelProviders.of(this).get(GoodSamaritanRequestVM.class);
            goodSamaritanRequestVM.getAllGSR().observe(this, gsr -> {
                if(gsr == null){
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("No Request");
                }
                else{
                    gsrList = gsr;
                    adapter = new GSRHistoryAdapter(gsrList, this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }





        return rootView;
    }

    @Override
    public void onListClick(int position) {

        Fragment fragment = new MotorcyclistHistoryDetailFragment();
        Bundle args = new Bundle();
        args.putString("requestId", gsrList.get(position).getGsrId());
        args.putString("requestType", "Good Samaritan");
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}
