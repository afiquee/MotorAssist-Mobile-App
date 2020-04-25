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

import com.afiq.motorcycleassist.Adapters.ShopListAdapter;
import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ApprovedShopFragment extends Fragment implements ShopListAdapter.OnListListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;

    private List<Shop> shopList = new ArrayList();
    public ApprovedShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gsr_history, container, false);


        TextView msg = (TextView) rootView.findViewById(R.id.msg);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Log.d("TAG", "masuk 1");
        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopVM.getApprovedShops().observe(this, shops -> {
            if(shops == null){
                Log.d("TAG", "masuk 2");
                msg.setVisibility(View.VISIBLE);
                msg.setText("No Approved Shop");
            }
            else{
                Log.d("TAG", "masuk 3");
                shopList = shops;
                adapter = new ShopListAdapter(shopList, this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });



        return rootView;
    }

    @Override
    public void onListClick(int position) {

        Fragment fragment = new ShopDetailFragment();
        Bundle args = new Bundle();
        args.putString("shopId", shopList.get(position).getShopId());
        args.putString("requestType", "Good Samaritan");
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}
