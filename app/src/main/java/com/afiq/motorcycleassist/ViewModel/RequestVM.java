package com.afiq.motorcycleassist.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.Request;
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

public class RequestVM extends ViewModel {

    private MutableLiveData<Request> request;
    private MutableLiveData<List<Request>> requestList;
    private MutableLiveData<String> message;

    public LiveData<String> createRequest(Request request){
        message = new MutableLiveData<String>();
        message.postValue(null);
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        String requestKey = requestRef.push().getKey();
        requestRef = requestRef.child(requestKey);
        requestRef.setValue(request);
        message.postValue(requestKey);
        return message;
    }

    public LiveData<Request> getRequest(String requestId) {
        request = new MutableLiveData<Request>();
        DatabaseReference gsrRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestId);
        gsrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Request r = dataSnapshot.getValue(Request.class);
                r.setRequestId(dataSnapshot.getKey());
                request.postValue(r);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return request;
    }

    public void acceptRequest(String requestId){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestId);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        Date date = new Date();
        String startTime = dateFormat.format(date).toString();
        String Uid = FirebaseAuth.getInstance().getUid();
        requestRef.child("mechId").setValue(Uid);
        requestRef.child("startTime").setValue(startTime);
        requestRef.child("status").setValue("Request Accepted");
    }

    public void acceptRequestNegotiation(String requestId, double finalServiceCharge, double finalTotal){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestId);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        Date date = new Date();
        String startTime = dateFormat.format(date).toString();
        String Uid = FirebaseAuth.getInstance().getUid();
        requestRef.child("mechId").setValue(Uid);
        requestRef.child("startTime").setValue(startTime);
        requestRef.child("status").setValue("Negotiation Started");
        requestRef.child("finalServiceCharge").setValue(finalServiceCharge);
        requestRef.child("finalTotal").setValue(finalTotal);
    }

    public void updateStatus(String requestId, String status){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestId);
        requestRef.child("status").setValue(status);
        if(status.equals("Completed")){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
            Date date = new Date();
            String endTime = dateFormat.format(date).toString();
            requestRef.child("endTime").setValue(endTime);
        }
    }

    public LiveData<List<Request>> getRequestHistory(String requesterId) {
        requestList = new MutableLiveData<List<Request>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query historyQuery = requestRef.orderByChild("motorcyclistId").equalTo(requesterId);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Request> requestArrayList = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {
                    Request r = child.getValue(Request.class);
                    if(r.getStatus().equals("Completed")){
                        r.setRequestId(child.getKey());
                        requestArrayList.add(r);
                    }

                }
                Collections.reverse(requestArrayList);
                requestList.postValue(requestArrayList);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return requestList;
    }

    public LiveData<List<Request>> getMechAssistanceHistory(String mechId) {
        requestList = new MutableLiveData<List<Request>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query historyQuery = requestRef.orderByChild("mechId").equalTo(mechId);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Request> requestArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Request r = child.getValue(Request.class);
                        if(r.getStatus().equals("Completed")){
                            r.setRequestId(child.getKey());
                            requestArrayList.add(r);
                        }
                    }
                    Collections.reverse(requestArrayList);
                    requestList.postValue(requestArrayList);
                }
                else{
                    requestList.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return requestList;
    }

    public LiveData<List<Request>> getShopAssistanceHistory(String shopId) {
        requestList = new MutableLiveData<List<Request>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query historyQuery = requestRef.orderByChild("shopId").equalTo(shopId);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Request> requestArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Request r = child.getValue(Request.class);
                        if(r.getStatus().equals("Completed")){
                            r.setRequestId(child.getKey());
                            requestArrayList.add(r);
                        }


                    }
                    Collections.reverse(requestArrayList);
                    requestList.postValue(requestArrayList);
                }
                else{
                    requestList.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return requestList;
    }

    public LiveData<List<Request>> requesterListenToRequest(String requesterId) {
        requestList = new MutableLiveData<List<Request>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query requestQuery = requestRef.orderByChild("motorcyclistId").equalTo(requesterId);
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Request> requestArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Request r = child.getValue(Request.class);
                        r.setRequestId(child.getKey());
                        requestArrayList.add(r);

                    }
                    requestList.postValue(requestArrayList);
                }
                else{
                    requestList.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return requestList;
    }

    public LiveData<String> listenToNegotiation(String requestId) {
        message = new MutableLiveData<String>();
        message.postValue(null);
        DatabaseReference negotiationRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestId).child("status");
        negotiationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().toString().equalsIgnoreCase("Negotiation Declined")){
                        message.postValue("Negotiation Declined");
                        negotiationRef.removeEventListener(this);
                    }
                    if(dataSnapshot.getValue().toString().equalsIgnoreCase("Negotiation Accepted")){
                        negotiationRef.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return message;
    }

    public LiveData<String> requesterListenToCompletedRequest(String requestId) {
        message = new MutableLiveData<String>();
        message.postValue(null);
        DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestId).child("status");
        completedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().toString().equalsIgnoreCase("Completed")){
                        message.postValue("completed");
                        completedRef.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return message;
    }


    public LiveData<List<Request>> mechListenToRequest(String shopId) {
        requestList = new MutableLiveData<List<Request>>();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query requestQuery = requestRef.orderByChild("shopId").equalTo(shopId);
        requestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Request> requestArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Request r = child.getValue(Request.class);
                        r.setRequestId(child.getKey());
                        requestArrayList.add(r);

                    }
                    requestList.postValue(requestArrayList);
                }
                else{
                    requestList.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return requestList;
    }

}
