package com.afiq.motorcycleassist.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.afiq.motorcycleassist.GetNearbyPlacesData;
import com.afiq.motorcycleassist.Models.LatLong;
import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminHomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private String TAG = "MotorAssist";

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Marker marker;
    private Marker motorcyclistMarker;
    private Polyline motorcyclistPolyline, shopPolyline;
    private DirectionsResult directionsResult;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    GoogleApiClient mGoogleApiClient;

    final int LOCATION_REQUEST_CODE = 1;
    SupportMapFragment mapFragment;
    private LocationCallback locationCallback;
    private GeoApiContext mGeoApiContext = null;

    private String shopDuration,stationDuration;
    private double stationDistanceD;

    private Shop myShop = new Shop();
    private Shop selectedShop = new Shop();

    private double travelCharge = 0.0,minServiceCharge= 0.0, maxServiceCharge= 0.0, shopDistance= 0.0,motorcyclistDistance= 0.0,
            minTotal= 0.0,maxTotal,finalServiceCharge=0.0,finalTotal = 0.0;

    private String duration,status,serviceName,additionalInfo,motorcycleId,motorcyclistId;
    private LatLong pickupLocation;
    private String myShopId,requestedService,requestKey,requestStatus;

    private Marker stationMarker, shopMarker;

    private FirebaseAuth mAuth;

    private TextView stationName,stationDistance;
    private Button fuelDirection,shopDirection;

    private View bottomSheet,stationSheet;
    private BottomSheetBehavior mBottomSheetBehavior,stationSheetBehavior;

    private LinearLayout callShop;

    private TextView open, mName,mDistance,mPhone,mEmail,mDays,mHours,
          mShopDistance,mUserDistance;

    private String motorType,shopOpen;
    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_admin_home, container, false);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mName = (TextView) rootView.findViewById(R.id.name);
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mPhone = (TextView) rootView.findViewById(R.id.phone);
        mEmail = (TextView) rootView.findViewById(R.id.email);
        mDays = (TextView) rootView.findViewById(R.id.days);
        mHours = (TextView) rootView.findViewById(R.id.hours);
        open = (TextView) rootView.findViewById(R.id.open);

        callShop = rootView.findViewById(R.id.callShop);
        callShop.setOnClickListener(this);

        bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        stationSheet = rootView.findViewById(R.id.station_sheet);
        stationSheetBehavior = BottomSheetBehavior.from(stationSheet);
        stationSheetBehavior.setHideable(true);
        stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        stationName = (TextView)rootView.findViewById(R.id.stationName);
        stationDistance = (TextView)rootView.findViewById(R.id.stationDistance);
        fuelDirection = (Button)rootView.findViewById(R.id.fueldirection);
        fuelDirection.setOnClickListener(this);
        shopDirection = (Button)rootView.findViewById(R.id.shopdirection);
        shopDirection.setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.map));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        getDeviceLocation();

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }


        /*

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                getDeviceLocation();
                getMechanicInfo();

            }
        });
        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(getActivity(),51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });*/


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);

                }
                else{
                    Toast.makeText(getActivity(), "Map Error !", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation =location;


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 1, this.locationListener);
        mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLastLocation != null) {
            displayShops();
            getNearbyPlaces();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }

    private void displayShops(){

        DatabaseReference shopLocationRef = FirebaseDatabase.getInstance().getReference().child("ShopLocation");
        GeoFire geoFire = new GeoFire(shopLocationRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                LatLng latLng = new LatLng(location.latitude,
                        location.longitude);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(key);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Shop shop = dataSnapshot.getValue(Shop.class);
                        if(shop != null){
                            if(shop.getShopStatus().equals("Approved")){
                                shop.setShopId(dataSnapshot.getKey());
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(latLng).title(shop.getShopName());
                                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
                                Marker m = mMap.addMarker(marker);
                                m.setTag(shop);
                                m.hideInfoWindow();
                            }


                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    marker.hideInfoWindow();

                                    if(marker.getTag() instanceof Shop){
                                        Shop shop = (Shop)(marker.getTag());
                                        selectedShop = shop;
                                        shopMarker = marker;
                                        calculateShopDirection(marker);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                shopDirection.setVisibility(View.VISIBLE);
                                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                                stationSheetBehavior.setHideable(true);
                                                stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                            }
                                        });

                                    }

                                    if(marker.getTag().equals("Petrol Station")){

                                        fuelDirection.setVisibility(View.VISIBLE);
                                        mBottomSheetBehavior.setHideable(true);
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                        stationSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        stationSheetBehavior.setHideable(true);
                                        stationMarker = marker;
                                        calculateStationDirection();
                                    }

                                    return false;
                                }
                            });

                            mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                                @Override
                                public void onInfoWindowClose(Marker marker) {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                                }
                            });
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
    }


    @Override
    public void onClick(View v) {


        if(v == callShop) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", selectedShop.getShopPhone(), null)));
        }

        if(v == shopDirection){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + shopMarker.getPosition().latitude +"," + shopMarker.getPosition().longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        if(v == fuelDirection){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + stationMarker.getPosition().latitude +"," + stationMarker.getPosition().longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

    }

    public void calculateShopDirection(Marker marker){

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );

        directions.alternatives(false);
        directions.origin(origin);
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

                shopDuration = result.routes[0].legs[0].duration.toString();

                String dis = result.routes[0].legs[0].distance.toString();
                if(dis.contains("km")){
                    dis = dis.replaceAll(" km","");
                    shopDistance = Double.parseDouble(dis);
                }
                else {
                    dis = dis.replaceAll(" m","");
                    shopDistance = Double.parseDouble(dis);
                }

                setShopInfo(marker);


            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }
    public void setShopInfo(Marker marker){

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mName.setText(selectedShop.getShopName());
                mDistance.setText(shopDuration+" away " +" ("+shopDistance+" km)");

                try {

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    String today = dateFormat.format(date);




                    Date startTime=new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(today+" "+selectedShop.getStartTime());
                    Date endTime=new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(today+" "+selectedShop.getEndTime());

                    Log.d(TAG,"Start Time : "+startTime.toString());
                    Log.d(TAG,"End  Time : "+endTime.toString());
                    Log.d(TAG,"Current Time : "+date.toString());

                    if(startTime.before(date) && endTime.after(date)){
                        shopOpen = "Open Now";
                        open.setTextColor(Color.parseColor("#00ff44"));
                    }
                    else{
                        shopOpen = "Close Now";
                        open.setTextColor(Color.parseColor("#f7000c"));
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                open.setText(shopOpen);
                //mPhone.setText(selectedShop.getShopPhone());
                mDays.setText(selectedShop.getShopPhone());
                String displayDays = "";
                for(int x=0;x<selectedShop.getWorkingDays().size();x++){
                    String day = selectedShop.getWorkingDays().get(x).substring(0,3);
                    displayDays += day;
                    if(x != selectedShop.getWorkingDays().size()-1){
                        displayDays +=", ";
                    }
                }

                displayDays += "\n("+selectedShop.getStartTime()+" - "+selectedShop.getEndTime()+")";
                mDays.setText(displayDays);

            }
        });


    }

    public void calculateStationDirection(){

        com.google.maps.model.LatLng station = new com.google.maps.model.LatLng(
                stationMarker.getPosition().latitude,
                stationMarker.getPosition().longitude
        );

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);


        directions.origin(origin);
        directions.alternatives(false);

        directions.destination(station).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                stationDuration = result.routes[0].legs[0].duration.toString();
                String dis = result.routes[0].legs[0].distance.toString();
                if (dis.contains("km")) {
                    dis = dis.replaceAll(" km", "");
                    stationDistanceD = Double.parseDouble(dis);
                } else {
                    dis = dis.replaceAll(" m", "");
                    stationDistanceD = Double.parseDouble(dis);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        stationName.setText(stationMarker.getTitle());
                        if(stationDistanceD >= 100)
                            stationDistance.setText(stationDuration + " away ("+stationDistanceD+" m)");
                        else
                            stationDistance.setText(stationDuration + " away ("+stationDistanceD+" km)");

                        stationSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        stationSheetBehavior.setHideable(false);
                    }
                });

            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });



    }

    public void getNearbyPlaces(){
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        String hospital = "gas_station";
        String url = getUrl(mLastLocation.getLatitude(), mLastLocation.getLongitude(), hospital);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);
    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+10000);
        googlePlaceUrl.append("&type=gas_station");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBrpXdMXjG17XGj45NJ9HJhfGvdGjjt6eE");


        return googlePlaceUrl.toString();
    }
}
