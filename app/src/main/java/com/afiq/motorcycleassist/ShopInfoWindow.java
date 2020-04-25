package com.afiq.motorcycleassist;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.afiq.motorcycleassist.Models.Shop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class ShopInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public ShopInfoWindow(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.shop_custom_infowindow, null);

        TextView name = view.findViewById(R.id.name);
        TextView phone = view.findViewById(R.id.phone);
        TextView days = view.findViewById(R.id.days);
        TextView hours = view.findViewById(R.id.hours);




        name.setText(marker.getTitle());

        Shop shop = (Shop) marker.getTag();

        phone.setText(shop.getShopPhone());
        String displayDays = "";
        for(String day : shop.getWorkingDays()){
            displayDays += day;
        }
        days.setText(displayDays);
        String displayHours = shop.getStartTime()+" - "+shop.getEndTime();
        hours.setText(displayHours);


        return view;
    }
}
