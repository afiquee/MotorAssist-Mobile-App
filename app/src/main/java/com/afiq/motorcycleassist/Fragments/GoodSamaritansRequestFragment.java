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

import com.afiq.motorcycleassist.Adapters.GSRequestListAdapter;
import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.GoodSamaritanRequestVM;

import java.util.ArrayList;
import java.util.List;

public class GoodSamaritansRequestFragment extends Fragment implements GSRequestListAdapter.OnListListener {

    private RecyclerView listRecyclerView;
    private RecyclerView.Adapter listAdadpter;
    private RecyclerView.LayoutManager layoutManager;
    List<GoodSamaritanRequest> goodSamaritanRequestList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_good_samaritans_request, container, false);

        TextView msg = (TextView) rootView.findViewById(R.id.msg);

        listRecyclerView = (RecyclerView) rootView.findViewById(R.id.GSRequestListRecyclerView);
        listRecyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity());
        listRecyclerView.setLayoutManager(layoutManager);


        listAdadpter = new GSRequestListAdapter(goodSamaritanRequestList, this);

        GoodSamaritanRequestVM goodSamaritanRequestVM = ViewModelProviders.of(this).get(GoodSamaritanRequestVM.class);
        goodSamaritanRequestVM.getNearbyGSR().observe(this, goodSamaritansRequests -> {
            if(goodSamaritansRequests == null){
                msg.setVisibility(View.VISIBLE);
            }
            else{
                msg.setVisibility(View.GONE);
                goodSamaritanRequestList = goodSamaritansRequests;
                Log.d("TAG","ggsr frg "+goodSamaritanRequestList.size());
                listAdadpter = new GSRequestListAdapter(goodSamaritanRequestList, this);
                listRecyclerView.setAdapter(listAdadpter);
                listAdadpter.notifyDataSetChanged();
            }

        });





        return rootView;
    }

    @Override
    public void onListClick(int position) {

        Fragment fragment = new MotorcyclistHomeFragment();
        Bundle args = new Bundle();
        args.putString("requestId", goodSamaritanRequestList.get(position).getGsrId());
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event

}
