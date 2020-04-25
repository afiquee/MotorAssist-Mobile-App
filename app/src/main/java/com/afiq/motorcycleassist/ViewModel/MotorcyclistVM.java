package com.afiq.motorcycleassist.ViewModel;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.Motorcyclist;
import com.afiq.motorcycleassist.Service.PushNotificationHelper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class MotorcyclistVM extends ViewModel {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Motorcyclist> motorcyclist;
    private MutableLiveData<String> message = new MutableLiveData<String>();;
    private MutableLiveData<Map<String, Object>> liveKeyData = new MutableLiveData<Map<String, Object>>();

    public LiveData<String> auth(){
        message.postValue(null);
       if(mAuth.getUid() != null){
           DatabaseReference motorcyclistRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(mAuth.getUid());
           motorcyclistRef.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.exists()){
                       message.postValue("isMotorcyclist");
                   }
                   else{
                       message.postValue("notMotorcyclist");
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
                FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(user_id).child("token").setValue(token);
                message.postValue("successful");

            }
        });
        return message;
    }

    public LiveData<Motorcyclist> getMotorcyclist(String motorcyclistId) {
        motorcyclist = new MutableLiveData<Motorcyclist>();
            DatabaseReference dataMotor = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(motorcyclistId);
            dataMotor.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Motorcyclist m = dataSnapshot.getValue(Motorcyclist.class);
                        m.setMotoryclistId(dataSnapshot.getKey());
                        motorcyclist.postValue(m);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        return motorcyclist;
    }

    public LiveData<String> updateMotorcyclist(Motorcyclist motorcyclist){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().updateEmail(motorcyclist.getMotorcyclistEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    message.postValue("successful");
                    DatabaseReference motorcyclistRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(motorcyclist.getMotoryclistId());
                    motorcyclistRef.child("motorcyclistEmail").setValue(motorcyclist.getMotorcyclistEmail());
                    motorcyclistRef.child("motorcyclistName").setValue(motorcyclist.getMotorcyclistName());
                    motorcyclistRef.child("motorcyclistPhone").setValue(motorcyclist.getMotorcyclistPhone());
                }
                else{
                    message.postValue("unsuccessful");
                }
            }
        });
        return message;

    }

    public LiveData<String> queryGoodSamaritanLocation(Location location, String requestKey, String gsrName){
        message = new MutableLiveData<String>();
        message.postValue(null);
        DatabaseReference goodSamaritanRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan");
        GeoFire geoFire = new GeoFire(goodSamaritanRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 10);
        //geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(key);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(mAuth.getUid() != key){
                            String title = "Someone is requesting your help for "+gsrName+"!";
                            String body = "Click to view this request";
                            String data = requestKey;
                            Motorcyclist motorcyclist = dataSnapshot.getValue(Motorcyclist.class);
                            PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();
                            pushNotificationHelper.sendGoodSamaritansNotification(motorcyclist.getToken(), title,body,data);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
        return message;

    }

    public LiveData<String> getGoodSamaritanStatus(){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getUid();
        DatabaseReference goodsamaritanRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(Uid);

        goodsamaritanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    message.postValue("active");
                }
                else{
                    message.postValue("inactive");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return message;
    }

    public LiveData<Map<String, Object>> queryGoodSamaritanLocation(Location location) {
        String Uid = mAuth.getUid();
        liveKeyData.postValue(null);
        DatabaseReference gsRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan");
        GeoFire geoFire = new GeoFire(gsRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 10);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!key.equals(Uid)){
                    Log.d("TAG", "Uid : "+Uid+" key : "+key);
                    Map<String, Object> keyData = new HashMap<>();
                    LatLng latLng = new LatLng(location.latitude,
                            location.longitude);
                    keyData.put("type", "entered");
                    keyData.put("key", key);
                    keyData.put("location", latLng);
                    liveKeyData.postValue(keyData);
                }

            }

            @Override
            public void onKeyExited(String key) {
                if(!key.equals(Uid)){
                    Map<String, Object> keyData = new HashMap<>();
                    keyData.put("type", "exited");
                    keyData.put("key", key);
                    liveKeyData.postValue(keyData);
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        return liveKeyData;
    }

    public void updateGSLocation(Location location, String gsId){
        DatabaseReference gsRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan");
        GeoFire geoFire = new GeoFire(gsRef);
        geoFire.setLocation(gsId, new GeoLocation(location.getLatitude(), location.getLongitude()),new
                GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
    }
}