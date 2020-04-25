package com.afiq.motorcycleassist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
import com.afiq.motorcycleassist.Models.Mechanic;
import com.afiq.motorcycleassist.Models.Motorcyclist;
import com.afiq.motorcycleassist.Models.Request;
import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.GoodSamaritanRequestVM;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.afiq.motorcycleassist.ViewModel.MotorcyclistVM;
import com.afiq.motorcycleassist.ViewModel.RequestVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MotorcyclistHistoryDetailFragment extends Fragment {

    public MotorcyclistHistoryDetailFragment() {
        // Required empty public constructor
    }

    private Request request;
    private Shop shop;
    private Mechanic mechanic;

    private TextView shopName,requesterName,mechName,serviceName,serviceCharge,travelingCharge,total,date,time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        String requestId = getArguments().getString("requestId");
        String requestType = getArguments().getString("requestType");

        if(requestType.equals("Good Samaritan")){
            rootView = inflater.inflate(R.layout.fragment_motorcyclist_gsrhistory_detail, container, false);

            TextView rescuerName = rootView.findViewById(R.id.rescuerName);
            TextView requesterName = rootView.findViewById(R.id.requesterName);
            TextView requestName = rootView.findViewById(R.id.requestName);
            TextView requestDestination = rootView.findViewById(R.id.requestDestination);
            TextView requestDistance = rootView.findViewById(R.id.requestDistance);
            date = rootView.findViewById(R.id.date);
            time = rootView.findViewById(R.id.time);

            GoodSamaritanRequestVM goodSamaritanRequestVM = ViewModelProviders.of(this).get(GoodSamaritanRequestVM.class);
            goodSamaritanRequestVM.getGSR(requestId).observe(this, goodSamaritanRequest -> {
                if(goodSamaritanRequest != null){

                    GoodSamaritanRequest gsr = (GoodSamaritanRequest) goodSamaritanRequest;
                    requestName.setText(gsr.getGsrName());
                    requestDistance.setText(gsr.getGsrDistance()+" km");

                    try {
                        Date startTime=new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(gsr.getGsrStartTime());
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = dateFormat.format(startTime);
                        date.setText(strDate);

                        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
                        String startHour = timeFormat.format(startTime);

                        Date endTime=new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(gsr.getGsrEndTime());
                        String endHour = timeFormat.format(endTime);
                        time.setText(startHour+ " - "+endHour);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    MotorcyclistVM motorcyclistVM = ViewModelProviders.of(this).get(MotorcyclistVM.class);
                    motorcyclistVM.getMotorcyclist(gsr.getGsrRescuerId()).observe(this, rescuer -> {
                        if(rescuer != null){
                            Motorcyclist motorcyclist = (Motorcyclist) rescuer;
                            rescuerName.setText(motorcyclist.getMotorcyclistName());

                            motorcyclistVM.getMotorcyclist(gsr.getGsrRequesterId()).observe(this, requester -> {
                                if(requester != null){
                                    Motorcyclist motorcyclist2 = (Motorcyclist) requester;
                                    requesterName.setText(motorcyclist2.getMotorcyclistName());
                                }
                            });
                        }
                    });





                    if(gsr.getGsrName().equals("Towing")){

                        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
                        shopVM.getShop(gsr.getGsrDestination()).observe(this, destination -> {
                            if(destination != null){
                                Shop shop = (Shop) destination;
                                requestDestination.setText(shop.getShopName());
                            }

                        });
                    }

                    else{
                        requestDestination.setText(gsr.getGsrDestination());
                    }

                }

            });


        }

        else {
            rootView = inflater.inflate(R.layout.fragment_motorcyclist_history_detail, container, false);
            shopName = rootView.findViewById(R.id.shopName);
            mechName = rootView.findViewById(R.id.mechName);
            requesterName = rootView.findViewById(R.id.requesterName);
            serviceName = rootView.findViewById(R.id.serviceName);
            serviceCharge = rootView.findViewById(R.id.serviceCharge);
            travelingCharge = rootView.findViewById(R.id.travelingCharge);
            total = rootView.findViewById(R.id.total);
            date = rootView.findViewById(R.id.date);
            time = rootView.findViewById(R.id.time);

            RequestVM requestVM = ViewModelProviders.of(this).get(RequestVM.class);
            requestVM.getRequest(requestId).observe(this, req -> {
                if(req != null){

                    request = req;
                    serviceName.setText(request.getServiceName());
                    serviceCharge.setText("RM " + request.getFinalServiceCharge());
                    travelingCharge.setText("RM " + request.getTravelingCharge());
                    total.setText("RM " + request.getFinalTotal());

                    try {
                        Date startTime=new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(request.getStartTime());
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = dateFormat.format(startTime);
                        date.setText(strDate);

                        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
                        String startHour = timeFormat.format(startTime);

                        Date endTime=new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(request.getEndTime());
                        String endHour = timeFormat.format(endTime);
                        time.setText(startHour+ " - "+endHour);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
                    shopVM.getShop(request.getShopId()).observe(this, S -> {
                        if(S != null){
                            Shop shop = S;
                            shopName.setText(shop.getShopName());
                        }

                    });

                    MechanicVM mechanicVM = ViewModelProviders.of(this).get(MechanicVM.class);
                    mechanicVM.getMechanic(request.getMechId()).observe(this, mech -> {
                        if(req != null){
                            Mechanic mechanic = mech;
                            mechName.setText(mechanic.getMechName());
                        }
                    });

                    MotorcyclistVM motorcyclistVM = ViewModelProviders.of(this).get(MotorcyclistVM.class);
                    motorcyclistVM.getMotorcyclist(request.getMotorcyclistId()).observe(this, requester -> {
                        if(requester != null){
                            Motorcyclist motorcyclist = requester;
                            requesterName.setText(motorcyclist.getMotorcyclistName());
                        }
                    });
                }
            });



        }

        return rootView;
    }
}
