package com.afiq.motorcycleassist.ViewModel;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.LatLong;
import com.afiq.motorcycleassist.Models.Shop;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopVM extends ViewModel {

    private FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    private MutableLiveData<Shop> shop;
    private MutableLiveData<List<Shop>> shopList;
    private MutableLiveData<String> message = new MutableLiveData<String>();;
    private MutableLiveData<Map<String, Object>> liveKeyData = new MutableLiveData<Map<String, Object>>();
    boolean checked;

    public LiveData<String> auth(){
        message.postValue(null);
        checked =false;
        if(mAuth.getUid() != null){
            DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(mAuth.getUid());
            shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    checked  = true;
                    if(dataSnapshot.exists()){

                        String shopStatus = dataSnapshot.child("shopStatus").getValue().toString();
                        Log.d("TAG" , "masuk status : "+shopStatus);
                        if(shopStatus.equalsIgnoreCase("Pending")){
                            message.postValue("pending");
                            Log.d("TAG" , "masuk pending vm ");
                        }
                        if(shopStatus.equalsIgnoreCase("Approved")){
                            message.postValue("isShop");
                            Log.d("TAG" , "masuk approved vm ");
                        }
                    }
                    else{
                        message.postValue("removed");
                        Log.d("TAG" , "masuk removed vm ");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if(!checked){
            message.postValue(null);
        }

        return message;

    }

    public LiveData<String> registerShop(Shop shop){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getUid();
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(Uid);
        shopRef.child("shopEmail").setValue(shop.getShopEmail());
        shopRef.child("shopName").setValue(shop.getShopName());
        shopRef.child("shopPhone").setValue(shop.getShopPhone());
        shopRef.child("workingDays").setValue(shop.getWorkingDays());
        shopRef.child("startTime").setValue(shop.getStartTime());
        shopRef.child("endTime").setValue(shop.getEndTime());
        shopRef.child("superbikeTowing").setValue(shop.getSuperbikeTowing());
        shopRef.child("shopStatus").setValue(shop.getShopStatus());
        shopRef.child("token").setValue(shop.getToken());
        message.postValue("successful");
        return message;
    }

    public LiveData<String> updateShop(Shop shop){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().updateEmail(shop.getShopEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    message.postValue("successful");
                    DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(shop.getShopId());
                    shopRef.child("shopEmail").setValue(shop.getShopEmail());
                    shopRef.child("shopName").setValue(shop.getShopName());
                    shopRef.child("shopPhone").setValue(shop.getShopPhone());
                    shopRef.child("workingDays").setValue(shop.getWorkingDays());
                    shopRef.child("startTime").setValue(shop.getStartTime());
                    shopRef.child("endTime").setValue(shop.getEndTime());
                    shopRef.child("superbikeTowing").setValue(shop.getSuperbikeTowing());
                }
                else{
                    message.postValue("unsuccessful");
                }
            }
        });
        return message;

    }

    public LiveData<String> setLocation(LatLong L){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getUid();
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("ShopLocation");
        GeoFire geoFire = new GeoFire(shopRef);
        geoFire.setLocation(Uid, new GeoLocation(L.getLatitude(), L.getLongitude()),new
                GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        message.postValue("successful");
                    }
                });
        return message;
    }

    public LiveData<String> setToken(){
        message.postValue(null);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                String user_id = mAuth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(user_id).child("token").setValue(token);
                message.postValue("successful");

            }
        });
        return message;
    }

    public LiveData<Shop> getShop(String shopId) {
        shop = new MutableLiveData<Shop>();
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(shopId);
        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Shop s = dataSnapshot.getValue(Shop.class);
                    s.setShopId(dataSnapshot.getKey());
                    DatabaseReference locRef = FirebaseDatabase.getInstance().getReference().child("ShopLocation").child(shopId).child("l");
                    locRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double latitude = dataSnapshot.child("0").getValue(Double.class);
                            double longitude = dataSnapshot.child("1").getValue(Double.class);
                            LatLong latLong = new LatLong(latitude,longitude);
                            s.setShopLocation(latLong);
                            shop.postValue(s);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    shop.postValue(null);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return shop;
    }

    public LiveData<Shop> getMechShop(String mechId) {
        shop = new MutableLiveData<Shop>();
        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId);
        mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shopId = dataSnapshot.child("shopId").getValue().toString();

                    DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(shopId);
                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Shop s = dataSnapshot.getValue(Shop.class);
                            s.setShopId(dataSnapshot.getKey());
                            shop.postValue(s);

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

        return shop;
    }

    public LiveData<List<Shop>> getApprovedShops() {
        shopList = new MutableLiveData<List<Shop>>();
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop");
        Query shopQuery = shopRef.orderByChild("shopStatus").equalTo("Approved");
        shopQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Shop> shopArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Shop s = child.getValue(Shop.class);
                        s.setShopId(child.getKey());
                        shopArrayList.add(s);

                    }
                    shopList.postValue(shopArrayList);
                }
                else{
                    shopList.postValue(null);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return shopList;
    }


    public LiveData<Map<String, Object>> queryShopLocation(Location location) {
        liveKeyData.postValue(null);
        DatabaseReference shopLocationRef = FirebaseDatabase.getInstance().getReference().child("ShopLocation");
        GeoFire geoFire = new GeoFire(shopLocationRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 10);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("TAG", "key location : "+key);
                Map<String, Object> keyData = new HashMap<>();
                LatLng latLng = new LatLng(location.latitude,
                        location.longitude);
                keyData.put("type", "entered");
                keyData.put("key", key);
                keyData.put("location", latLng);
                liveKeyData.postValue(keyData);
            }

            @Override
            public void onKeyExited(String key) {
                Map<String, Object> keyData = new HashMap<>();
                keyData.put("type", "exited");
                keyData.put("key", key);
                liveKeyData.postValue(keyData);
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

    public LiveData<List<Shop>> getPendingShops() {
        shopList = new MutableLiveData<List<Shop>>();

        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop");
        Query shopQuery = shopRef.orderByChild("shopStatus").equalTo("Pending");
        shopQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Shop> shopArrayList = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {
                        Shop s = child.getValue(Shop.class);
                        s.setShopId(child.getKey());
                        shopArrayList.add(s);

                    }
                    shopList.postValue(shopArrayList);
                }
                else{
                    shopList.postValue(null);
                }




            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return shopList;
    }

    public LiveData<String> approveShop(String shopId){
        message = new MutableLiveData<String>();
        message.postValue(null);
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(shopId);
        shopRef.child("shopStatus").setValue("Approved");
        message.postValue("successful");
        return message;
    }

    public LiveData<String> deleteShop(String shopId){
        message = new MutableLiveData<String>();
        message.postValue(null);
        FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(shopId).removeValue();
        message.postValue("successful");
        return message;
    }


}
