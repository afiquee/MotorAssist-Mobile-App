package com.afiq.motorcycleassist.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.afiq.motorcycleassist.Models.Admin;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class AdminVM extends ViewModel {


    private FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    private MutableLiveData<Admin> admin;
    private MutableLiveData<List<Admin>> adminList;
    private MutableLiveData<String> message = new MutableLiveData<String>();;

    public LiveData<String> auth(){
        message.postValue(null);
        if(mAuth.getUid() != null){
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("User").child("Admin").child(mAuth.getUid());
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        message.postValue("isAdmin");
                    }
                    else{
                        message.postValue("notAdmin");
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
                FirebaseDatabase.getInstance().getReference().child("User").child("Admin").child(user_id).child("token").setValue(token);
                message.postValue("successful");

            }
        });
        return message;
    }

    public LiveData<Admin> getAdmin(String adminId) {

        DatabaseReference dataMechanic = FirebaseDatabase.getInstance().getReference().child("User").child("Admin").child(adminId);
        dataMechanic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    admin = new MutableLiveData<Admin>();
                    Admin a = dataSnapshot.getValue(Admin.class);
                    a.setAdminId(dataSnapshot.getKey());
                    admin.postValue(a);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return admin;
    }

    public LiveData<List<Admin>> getAllAdmin() {
        adminList = new MutableLiveData<List<Admin>>();
        adminList.postValue(null);
        DatabaseReference dataMechanic = FirebaseDatabase.getInstance().getReference().child("User").child("Admin");
        dataMechanic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                List<Admin> adminArrayList = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for(DataSnapshot child : children) {
                        Admin a = child.getValue(Admin.class);
                        a.setAdminId(child.getKey());
                        adminArrayList.add(a);

                    }
                    adminList.postValue(adminArrayList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return adminList;
    }
}
