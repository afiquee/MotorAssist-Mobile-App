package com.afiq.motorcycleassist.ViewModel;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
import com.afiq.motorcycleassist.Service.PushNotificationHelper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoodSamaritanRequestVM extends ViewModel {
    private MutableLiveData<List<GoodSamaritanRequest>> gsrList;
    private MutableLiveData<GoodSamaritanRequest> gsr;
    private MutableLiveData<String> message;
    PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();


    public LiveData<String> createGSR(GoodSamaritanRequest goodSamaritanRequest){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getUid();
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(goodSamaritanRequest.getGsrId());
        goodSamaritanRequest.setGsrId(null);
        gsrRef.setValue(goodSamaritanRequest);
        message.postValue("successful");
        return message;
    }

    public LiveData<List<GoodSamaritanRequest>> getAllGSR() {
        gsrList = new MutableLiveData<List<GoodSamaritanRequest>>();
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        gsrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<GoodSamaritanRequest> goodSamaritanRequestArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        GoodSamaritanRequest g = child.getValue(GoodSamaritanRequest.class);
                        if(g.getGsrStatus().equals("Completed")){
                            g.setGsrId(child.getKey());
                            goodSamaritanRequestArrayList.add(g);
                        }


                    }
                    gsrList.postValue(goodSamaritanRequestArrayList);
                }
                else{
                    gsrList.postValue(null);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return gsrList;
    }

    public LiveData<List<GoodSamaritanRequest>> getNearbyGSR() {
        gsrList = new MutableLiveData<List<GoodSamaritanRequest>>();
        gsrList.postValue(null);
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        Query gsrQuery = gsrRef.orderByChild("gsrStatus").equalTo("Requesting Help");
        gsrQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<GoodSamaritanRequest> goodSamaritanRequestArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        GoodSamaritanRequest g = child.getValue(GoodSamaritanRequest.class);
                        Log.d("TAG","ggsr "+g.getGsrName());
                        if(g.getGsrStatus().equals("Requesting Help")){
                            g.setGsrId(child.getKey());
                            goodSamaritanRequestArrayList.add(g);
                        }

                    }
                    gsrList.postValue(goodSamaritanRequestArrayList);
                }
                else{
                    Log.d("TAG","ggsr empty");
                    gsrList.postValue(null);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return gsrList;
    }

    public LiveData<GoodSamaritanRequest> getGSR(String gsrId) {
        gsr = new MutableLiveData<GoodSamaritanRequest>();
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(gsrId);
        gsrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GoodSamaritanRequest g = dataSnapshot.getValue(GoodSamaritanRequest.class);
                    g.setGsrId(dataSnapshot.getKey());
                    gsr.postValue(g);
                }
                else{
                    gsr.postValue(null);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return gsr;
    }

    public LiveData<List<GoodSamaritanRequest>> getGSRHistory(String requesterId) {
        gsrList = new MutableLiveData<List<GoodSamaritanRequest>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        Query historyQuery = requestRef.orderByChild("gsrRequesterId").equalTo(requesterId);
        historyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<GoodSamaritanRequest> glist = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        GoodSamaritanRequest g = child.getValue(GoodSamaritanRequest.class);
                        if(g.getGsrStatus().equals("Completed")){
                            g.setGsrId(child.getKey());
                            glist.add(g);
                        }

                    }
                    Collections.reverse(glist);
                    gsrList.postValue(glist);
                }
                else{
                    gsrList.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return gsrList;
    }

    public LiveData<List<GoodSamaritanRequest>> getGSRAssistanceHistory(String rescuerId) {
        gsrList = new MutableLiveData<List<GoodSamaritanRequest>>();
        DatabaseReference rescuerRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        Query historyQuery = rescuerRef.orderByChild("gsrRescuerId").equalTo(rescuerId);
        historyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<GoodSamaritanRequest> glist = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        GoodSamaritanRequest g = child.getValue(GoodSamaritanRequest.class);
                        if(g.getGsrStatus().equals("Completed")){
                            g.setGsrId(child.getKey());
                            glist.add(g);
                        }

                    }
                    Collections.reverse(glist);
                    gsrList.postValue(glist);
                }
                else{
                    gsrList.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return gsrList;
    }

    public void loadGSR(){
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        Query gsrQuery = gsrRef.orderByChild("gsrStatus").equalTo("Requesting Help");
        gsrQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gsrList = new MutableLiveData<List<GoodSamaritanRequest>>();


                List<GoodSamaritanRequest> goodSamaritanRequestList = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    GoodSamaritanRequest gsr = child.getValue(GoodSamaritanRequest.class);
                    Log.d("TAG"," gsr name : "+gsr.getGsrName().toString());
                    gsr.setGsrId(child.getKey());
                    goodSamaritanRequestList.add(gsr);
                }
                gsrList.postValue(goodSamaritanRequestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void acceptGSRTowing(String gsrId, String rescuerId, String requesterToken, Location location){

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        Date date = new Date();
        String startTime = dateFormat.format(date).toString();
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(gsrId);
        gsrRef.child("gsrRescuerId").setValue(rescuerId);
        gsrRef.child("gsrStartTime").setValue(startTime);
        gsrRef.child("gsrStatus").setValue("Request Accepted");

        FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(rescuerId).removeValue();
        DatabaseReference rescuerRef = FirebaseDatabase.getInstance().getReference().child("Rescuer");
       pushNotificationHelper.sendGoodSamaritansNotification(requesterToken,"Your request has been accepted!","Please wait while your rescuer is preparing");
        GeoFire geoFire = new GeoFire(rescuerRef);
        geoFire.setLocation(rescuerId, new GeoLocation(location.getLatitude(), location.getLongitude()),new
                GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                    }
                });

    }

    public void updateStatus(String gsrId, String requesterToken, String status){
        FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(gsrId).child("gsrStatus").setValue(status);
    }

    public void updateStatus(String gsrId, String status){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(gsrId);
        requestRef.child("gsrStatus").setValue(status);
        if(status.equals("Completed")){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
            Date date = new Date();
            String endTime = dateFormat.format(date).toString();
            requestRef.child("gsrEndTime").setValue(endTime);

            String Uid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Rescuer").child(Uid).removeValue();
            FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(Uid);
        }
    }

    public void cancelGSR(String requestId){
        FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(requestId).removeValue();

    }
}
