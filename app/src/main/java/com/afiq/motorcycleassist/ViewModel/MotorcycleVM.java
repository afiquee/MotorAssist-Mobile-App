package com.afiq.motorcycleassist.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MotorcycleVM extends ViewModel {
    private MutableLiveData<Motorcycle> motorcycle;
    private MutableLiveData<List<Motorcycle>> motorcycles;
    private MutableLiveData<String> message;


    public LiveData<String> registerMotorcycle(Motorcycle motorcycle){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(Uid).child("Motorcycle").child(motorcycle.getMotorId());
        motorcycle.setMotorId(null);
        databaseRef.setValue(motorcycle);
        message.postValue("successful");
        return message;

    }

    public LiveData<Motorcycle> getMotorcycle(String motorcycleId, String Uid) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        motorcycle = new MutableLiveData<Motorcycle>();
        DatabaseReference dataMotor = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(Uid).child("Motorcycle").child(motorcycleId);
        dataMotor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Motorcycle m = dataSnapshot.getValue(Motorcycle.class);
                m.setMotorId(dataSnapshot.getKey());
                motorcycle.postValue(m);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return motorcycle;
    }
    public LiveData<List<Motorcycle>> getMotorcycles(String motorcyclistId) {
        if (motorcycles == null) {
            motorcycles = new MutableLiveData<List<Motorcycle>>();
            loadMotorcycle(motorcyclistId);
        }
        return motorcycles;
    }

    public LiveData<String> updateMotorcycle(Motorcycle motorcycle){
        message = new MutableLiveData<String>();
        message.postValue(null);
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference motorRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(Uid).child("Motorcycle").child(motorcycle.getMotorId());
        motorRef.child("motorType").setValue(motorcycle.getMotorType());
        motorRef.child("motorBrand").setValue(motorcycle.getMotorBrand());
        motorRef.child("motorModel").setValue(motorcycle.getmotorModel());
        message.postValue("successful");
        return message;

    }

    public LiveData<String> deleteMotorcycle(String motorcycleId){
        message = new MutableLiveData<String>();
        message.postValue(null);
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference motorRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(Uid)
                .child("Motorcycle").child(motorcycleId);
        motorRef.removeValue();
        message.postValue("successful");

        /*
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query requestQuery = requestRef.orderByChild("motorcycleId").equalTo(motorcycleId);
        requestQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    message.postValue("unsuccessful");
                }
                else{
                    DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
                    Query gsrQuery = requestRef.orderByChild("motorcycleId").equalTo(motorcycleId);
                    gsrQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                message.postValue("unsuccessful");
                            }
                            else{
                                message.postValue("successful");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */

        return message;

    }

    private void loadMotorcycle(String motorcyclistId) {

        DatabaseReference dataMotor = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(motorcyclistId).child("Motorcycle");

        dataMotor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                List<Motorcycle> motorcycleList = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    Motorcycle motor = child.getValue(Motorcycle.class);
                    motor.setMotorId(child.getKey());
                    motorcycleList.add(motor);
                }
                motorcycles.postValue(motorcycleList);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
