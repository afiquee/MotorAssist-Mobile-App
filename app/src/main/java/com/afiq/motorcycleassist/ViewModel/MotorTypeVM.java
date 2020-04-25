package com.afiq.motorcycleassist.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.MotorType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MotorTypeVM extends ViewModel {

    private MutableLiveData<List<MotorType>> motorType;
    private MutableLiveData<String> message;
    public LiveData<List<MotorType>> getMotorTypes() {
        motorType = new MutableLiveData<List<MotorType>>();
        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference().child("MotorType");
        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<MotorType> typeList = new ArrayList<MotorType>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                MotorType def1 = new MotorType("0","Select Motorcycle Type");
                typeList.add(def1);
                for(DataSnapshot child : children) {
                    MotorType typeData = child.getValue(MotorType.class);
                    typeList.add(typeData);
                }
                motorType.postValue(typeList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return motorType;
    }
}
