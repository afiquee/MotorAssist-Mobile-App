package com.afiq.motorcycleassist.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceVM extends ViewModel {

    private MutableLiveData<List<Service>> serviceList;
    private MutableLiveData<String> message;
    public LiveData<List<Service>> getServices() {
        serviceList = new MutableLiveData<List<Service>>();
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Service");
        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Service> serviceArrayList = new ArrayList<Service>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    Service service = child.getValue(Service.class);
                    serviceArrayList.add(service);
                }
                serviceList.postValue(serviceArrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return serviceList;
    }
}
