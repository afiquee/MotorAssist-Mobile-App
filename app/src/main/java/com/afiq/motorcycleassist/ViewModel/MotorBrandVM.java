package com.afiq.motorcycleassist.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.MotorBrand;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MotorBrandVM extends ViewModel {

    private MutableLiveData<List<MotorBrand>> brandList;
    private MutableLiveData<String> message;
    public LiveData<List<MotorBrand>> getMotorBrands() {
        brandList = new MutableLiveData<List<MotorBrand>>();
        DatabaseReference brandRef = FirebaseDatabase.getInstance().getReference().child("MotorBrand");
        brandRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<MotorBrand> brandArrayList = new ArrayList<MotorBrand>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                MotorBrand def1 = new MotorBrand("0","Select Motorcycle Brand");
                brandArrayList.add(def1);
                for(DataSnapshot child : children) {
                    MotorBrand brand = child.getValue(MotorBrand.class);
                    brandArrayList.add(brand);
                }
                brandList.postValue(brandArrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return brandList;
    }
}
