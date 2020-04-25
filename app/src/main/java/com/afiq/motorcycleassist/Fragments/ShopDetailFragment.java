package com.afiq.motorcycleassist.Fragments;

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

import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.Service.PushNotificationHelper;
import com.afiq.motorcycleassist.ViewModel.ShopVM;

public class ShopDetailFragment extends Fragment {

    private TextView mId,mEmail,mName,mPhone,mShopId,mDays,mHours,mSuperbike;
    private Button approve,delete;
    private Shop thisShop;
    public ShopDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shop_detail, container, false);

        String shopId = getArguments().getString("shopId");

        mId = (TextView)rootView.findViewById(R.id.shopId);
        mEmail = (TextView)rootView.findViewById(R.id.email);
        mName = (TextView)rootView.findViewById(R.id.name);
        mPhone = (TextView)rootView.findViewById(R.id.phone);
        mShopId = (TextView)rootView.findViewById(R.id.shopId);
        mDays = (TextView)rootView.findViewById(R.id.workingDays);
        mHours = (TextView)rootView.findViewById(R.id.workingHours);
        mSuperbike = (TextView)rootView.findViewById(R.id.superbikeTowing);


        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);

        approve = (Button)rootView.findViewById(R.id.approve);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopVM.approveShop(shopId).observe(ShopDetailFragment.this, message -> {
                    if (message != null) {
                        Toast.makeText(getActivity(), "Shop has been approved!", Toast.LENGTH_SHORT).show();
                        PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();
                        pushNotificationHelper.sendNotification(thisShop.getToken(),"Your shop registration has been approved!", "You can now login");
                        approve.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                    }
                });
            }
        });


        delete = (Button)rootView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopVM.deleteShop(shopId).observe(ShopDetailFragment.this, message -> {
                    if (message != null) {
                        Toast.makeText(getActivity(), "Shop has been removed!", Toast.LENGTH_SHORT).show();
                        PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();
                        pushNotificationHelper.sendNotification(thisShop.getToken(),"Your shop registration has been rejected!", "");
                        Fragment fragment = new ShopListFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        });

        shopVM.getShop(shopId).observe(this, shop -> {
            if(shop == null)
                Toast.makeText(getActivity(), "No shop", Toast.LENGTH_SHORT).show();
            else{
                thisShop = shop;
                mId.setText(shop.getShopId());
                mEmail.setText(shop.getShopEmail());
                mName.setText(shop.getShopName());
                mPhone.setText(shop.getShopPhone());

                String displayDays = "";
                for(int x=0;x<shop.getWorkingDays().size();x++){
                    String day = shop.getWorkingDays().get(x).substring(0,3);
                    displayDays += day;
                    if(x != shop.getWorkingDays().size()-1){
                        displayDays +=", ";
                    }
                }
                mDays.setText(displayDays);
                mHours.setText(shop.getStartTime()+" - "+shop.getEndTime());
                mSuperbike.setText(shop.getSuperbikeTowing());
                mShopId.setText(shop.getShopId());

                if(shop.getShopStatus().equals("Pending")){
                    approve.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                }

            }
        });





        return rootView;
    }





}
