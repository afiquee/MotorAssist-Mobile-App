package com.afiq.motorcycleassist.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class InsertService {

    public static void main(String[] args){


        ArrayList<Service> serviceList = new ArrayList<>();
        Service s1 = new Service("Change tube",15,20);
        Service s2 = new Service("Change tyre",70,120);
        Service s3 = new Service("Change fuel line hose",15,20);
        Service s4 = new Service("Change chain",30,50);
        Service s5 = new Service("Plug tubeless tyre",4,5);
        Service s6 = new Service("Towing",3,3);
        Service s7 = new Service("Four Wheel Drive Towing",50,4);
        Service s8 = new Service("Traveling",2,2);
        serviceList.add(s1);
        serviceList.add(s2);
        serviceList.add(s3);
        serviceList.add(s4);
        serviceList.add(s5);
        serviceList.add(s6);
        serviceList.add(s7);
        serviceList.add(s8);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Service");
        for(int x=0;x<serviceList.size();x++){
            databaseReference.push().setValue(serviceList.get(x));
        }


    }
}
