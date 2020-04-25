package com.afiq.motorcycleassist.ViewModel;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.Mechanic;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class MechanicVM extends ViewModel {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Mechanic> mechanic = new MutableLiveData<Mechanic>();
    private MutableLiveData<List<Mechanic>> mechanicList;
    private MutableLiveData<String> message = new MutableLiveData<String>();

    DatabaseReference mechLocationRef;

    public LiveData<String> auth(){
        message.postValue(null);
        if(mAuth.getUid() != null){
            DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mAuth.getUid());
            mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        message.postValue("isMechanic");
                    }
                    else{
                        message.postValue("notMechanic");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return message;
    }

    public LiveData<String> setToken(){
        message.postValue(null);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                String user_id = mAuth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(user_id).child("token").setValue(token);
                message.postValue("successful");

            }
        });
        return message;
    }

    public LiveData<Mechanic> getMechanic(String mechanicId) {

        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechanicId);
        mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Mechanic m = dataSnapshot.getValue(Mechanic.class);
                    m.setMechId(dataSnapshot.getKey());
                    mechanic.postValue(m);
                }
                else{
                    mechanic.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return mechanic;
    }

    public LiveData<List<Mechanic>> getShopMechanic(String shopId) {
        mechanicList = new MutableLiveData<List<Mechanic>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic");
        Query historyQuery = requestRef.orderByChild("shopId").equalTo(shopId);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Mechanic> mechanicArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Mechanic m = child.getValue(Mechanic.class);
                        m.setMechId(child.getKey());
                        mechanicArrayList.add(m);

                    }
                    mechanicList.postValue(mechanicArrayList);
                }
                else{
                    mechanicList.postValue(null);
                }




            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return mechanicList;
    }

    public LiveData<String> updateMechanic(Mechanic mechanic){
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.d("TAG", "email : "+mechanic.getMechEmail());
        mAuth.getCurrentUser().updateEmail(mechanic.getMechEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    message.postValue("successful");
                    DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechanic.getMechId());
                    mechRef.child("mechEmail").setValue(mechanic.getMechEmail());
                    mechRef.child("mechName").setValue(mechanic.getMechName());
                    mechRef.child("mechPhone").setValue(mechanic.getMechPhone());
                }
                else{
                    message.postValue("unsuccessful");
                }
            }
        });
        return message;

    }

    public LiveData<String> getShopId(String mechId){
        message.postValue(null);
        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId);
        mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String shopId = dataSnapshot.child("shopId").getValue().toString();
                    message.postValue(shopId);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return message;

    }

    public LiveData<String> retireMechanic(String mechId){
        message.postValue(null);
        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId).child("mechStatus");
        mechRef.setValue("Retired");
        message.postValue("successfull");

        return message;

    }

    public void toggleMechanicStatus(String mechId, String status){
        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId).child("mechStatus");
        mechRef.setValue(status);
    }

    public void updateMechanicLocation(Location location, String mechId){
        mechLocationRef= FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId);
        GeoFire geoFire = new GeoFire(mechLocationRef);
        geoFire.setLocation("mechLocation", new GeoLocation(location.getLatitude(), location.getLongitude()),new
                GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
    }

    public void removeLocationUpdate(){


    }




}