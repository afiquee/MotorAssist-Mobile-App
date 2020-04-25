package com.afiq.motorcycleassist.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.firebase.auth.FirebaseAuth;

public class ShopAccountFragment extends Fragment {

    private TextView mEmail,mName,mPhone,mShopId,mDays,mHours,mSuperbike;
    private Button logout,update,updateLocation;
    private Button mCopy;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    private FirebaseAuth mAuth;
    public ShopAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shop_account, container, false);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextView)rootView.findViewById(R.id.email);
        mName = (TextView)rootView.findViewById(R.id.name);
        mPhone = (TextView)rootView.findViewById(R.id.phone);
        mShopId = (TextView)rootView.findViewById(R.id.shopId);
        mDays = (TextView)rootView.findViewById(R.id.workingDays);
        mHours = (TextView)rootView.findViewById(R.id.workingHours);
        mSuperbike = (TextView)rootView.findViewById(R.id.superbikeTowing);

        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopVM.getShop(mAuth.getUid()).observe(this, shop -> {
            if(shop == null)
                Toast.makeText(getActivity(), "No shop", Toast.LENGTH_SHORT).show();
            else{
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

            }
        });

        update = (Button)rootView.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UpdateShopFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });

        updateLocation = (Button)rootView.findViewById(R.id.updateLocation);
        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UpdateMapFragment();
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

        mCopy = (Button) rootView.findViewById(R.id.copy);
        myClipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mShopId.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getActivity(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}
