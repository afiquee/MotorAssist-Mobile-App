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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.GetNearbyPlacesData;
import com.afiq.motorcycleassist.Models.LatLong;
import com.afiq.motorcycleassist.Models.Mechanic;
import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.Models.Motorcyclist;
import com.afiq.motorcycleassist.Models.Request;
import com.afiq.motorcycleassist.Models.Service;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.Service.PushNotificationHelper;
import com.afiq.motorcycleassist.ViewModel.GoodSamaritanRequestVM;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.afiq.motorcycleassist.ViewModel.MotorcycleVM;
import com.afiq.motorcycleassist.ViewModel.MotorcyclistVM;
import com.afiq.motorcycleassist.ViewModel.RequestVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MechanicHomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private TextView open, mName,mDistance,mPhone,mEmail,mDays,mHours,
            mShopName,mShopDistance,mUserDistance;

    private String motorType,shopOpen;

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Marker shopMarker;
    private Marker motorcyclistMarker;
    private Polyline motorcyclistPolyline, shopPolyline;
    private DirectionsResult directionsResult;

    private LinearLayout callShop,callRequester;

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

    TextInputEditText setServiceCharge;
    private Switch statusSwitch;
    private TextView mType,mPlate,mMotor,mStatus,
            myMotor,myBreakdown,mService,mAdditional,mTravelCharge,mServiceCharge,mTotal,mNewTotal;
    private Button headOut,accept,decline,go,arrived,goShop,completed,close,acceptNegotiate,declineNegotiate;
    private LinearLayout completedButton,headOutButton,requestButton,negotiateInfo,negotiateButton,inAssist,towingInfo;
    private CardView motorcyclistInfo,serviceInfo;
    static final float COORDINATE_OFFSET = 0.00002f;

    private View bottomSheet,shopMechanicSheet;
    private BottomSheetBehavior mBottomSheetBehavior,shopMechanicBehavior,stationSheetBehavior;

    PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();



    private double travelCharge = 0.0,minServiceCharge= 0.0, maxServiceCharge= 0.0, shopDistance= 0.0,motorcyclistDistance= 0.0,
            minTotal= 0.0,maxTotal,finalServiceCharge=0.0,finalTotal = 0.0;

    private String duration,status,serviceName,additionalInfo,motorcycleId,motorcyclistId;
    private LatLong pickupLocation;
    private String myShopId,requestedService,requestKey,requestStatus;

    private FirebaseAuth mAuth;


    private String shopDuration,stationDuration;
    private double stationDistanceD;

    private Shop selectedShop = new Shop();
    private Marker stationMarker, selectedStationMarker;
    private TextView stationName,stationDistance;
    private Button fuelDirection,shopDirection;
    private View stationSheet;



    private String TAG = "MotorAssist";


    private Motorcyclist myMotorcyclist;
    private Shop myShop = new Shop();
    private ArrayList<Shop> shopList = new ArrayList<>();
    private ArrayList<Service> serviceList = new ArrayList<>();

    private String Uid;

    GoodSamaritanRequestVM goodSamaritanRequestVM;
    RequestVM requestVM;
    ShopVM shopVM;
    MotorcyclistVM motorcyclistVM;
    MotorcycleVM motorcycleVM;
    MechanicVM mechanicVM;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        goodSamaritanRequestVM = ViewModelProviders.of(this).get(GoodSamaritanRequestVM.class);
        requestVM = ViewModelProviders.of(this).get(RequestVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        motorcyclistVM = ViewModelProviders.of(this).get(MotorcyclistVM.class);
        motorcycleVM = ViewModelProviders.of(this).get(MotorcycleVM.class);
        mechanicVM = ViewModelProviders.of(this).get(MechanicVM.class);
    }

    public MechanicHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_mechanic_home, container, false);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Session session = new Session(getActivity());

        open = (TextView) rootView.findViewById(R.id.open);

        mShopName = (TextView) rootView.findViewById(R.id.name);
        mShopDistance = (TextView) rootView.findViewById(R.id.distance);
        mPhone = (TextView) rootView.findViewById(R.id.phone);
        mEmail = (TextView) rootView.findViewById(R.id.email);
        mDays = (TextView) rootView.findViewById(R.id.days);
        mHours = (TextView) rootView.findViewById(R.id.hours);
        open = (TextView) rootView.findViewById(R.id.open);

        callShop = rootView.findViewById(R.id.callShop);
        callShop.setOnClickListener(this);
        stationName = (TextView)rootView.findViewById(R.id.stationName);
        stationDistance = (TextView)rootView.findViewById(R.id.stationDistance);
        fuelDirection = (Button)rootView.findViewById(R.id.fueldirection);
        fuelDirection.setOnClickListener(this);
        shopDirection = (Button)rootView.findViewById(R.id.shopdirection);
        shopDirection.setOnClickListener(this);

        stationSheet = rootView.findViewById(R.id.station_sheet);
        stationSheetBehavior = BottomSheetBehavior.from(stationSheet);
        stationSheetBehavior.setHideable(true);
        stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }

        stationName = (TextView)rootView.findViewById(R.id.stationName);
        stationDistance = (TextView)rootView.findViewById(R.id.stationDistance);
        fuelDirection = (Button)rootView.findViewById(R.id.fueldirection);
        shopDirection = (Button)rootView.findViewById(R.id.shopdirection);



        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mNewTotal = (TextView) rootView.findViewById(R.id.newTotal);
        setServiceCharge = (TextInputEditText) rootView.findViewById(R.id.setServiceCharge);
        setServiceCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = setServiceCharge.getText().toString();
                if(!string.equalsIgnoreCase(" ") || string != null){
                    try{
                        finalServiceCharge = Double.valueOf(setServiceCharge.getText().toString());
                        finalTotal = finalServiceCharge + travelCharge;
                        mNewTotal.setText("NEW TOTAL : RM"+finalTotal);
                    }
                    catch (NumberFormatException e){
                        Toast.makeText(getActivity(), "Service charge must be in digit!", Toast.LENGTH_SHORT).show();

                    }

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        shopMechanicSheet = rootView.findViewById(R.id.shopMechanicSheet);
        shopMechanicBehavior = BottomSheetBehavior.from(shopMechanicSheet);
        shopMechanicBehavior.setHideable(true);
        shopMechanicBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        headOutButton = (LinearLayout)rootView.findViewById(R.id.headOutButton);
        requestButton = (LinearLayout) rootView.findViewById(R.id.requestButton);
        completedButton = (LinearLayout) rootView.findViewById(R.id.completedButton);
        negotiateInfo = (LinearLayout) rootView.findViewById(R.id.negotiateInfo);
        negotiateButton = (LinearLayout) rootView.findViewById(R.id.negotiateButton);
        inAssist = (LinearLayout) rootView.findViewById(R.id.inAssist);
        towingInfo = (LinearLayout) rootView.findViewById(R.id.towingInfo);


        headOut = (Button)rootView.findViewById(R.id.headOut);
        headOut.setOnClickListener(this);
        accept = (Button) rootView.findViewById(R.id.accept);
        accept.setOnClickListener(this);
        decline = (Button) rootView.findViewById(R.id.decline);
        decline.setOnClickListener(this);
        acceptNegotiate = (Button) rootView.findViewById(R.id.acceptNegotiate);
        acceptNegotiate.setOnClickListener(this);
        declineNegotiate = (Button) rootView.findViewById(R.id.declineNegotiate);
        declineNegotiate.setOnClickListener(this);
        go = (Button) rootView.findViewById(R.id.go);
        go.setOnClickListener(this);
        arrived = (Button) rootView.findViewById(R.id.arrived);
        arrived.setOnClickListener(this);
        completed = (Button) rootView.findViewById(R.id.completed);
        completed.setOnClickListener(this);
        close = (Button) rootView.findViewById(R.id.close);
        close.setOnClickListener(this);
        goShop = (Button) rootView.findViewById(R.id.goShop);
        goShop.setOnClickListener(this);


        mStatus = (TextView) rootView.findViewById(R.id.requestStatus);
        statusSwitch = (Switch) rootView.findViewById(R.id.statusSwitch);
        statusSwitch.setOnClickListener(this);
        mName = (TextView) rootView.findViewById(R.id.rname);
        mDistance = (TextView) rootView.findViewById(R.id.rdistance);
        mType = (TextView) rootView.findViewById(R.id.motorType);
        mPlate = (TextView) rootView.findViewById(R.id.plate);
        mMotor = (TextView) rootView.findViewById(R.id.motor);

        mService = (TextView) rootView.findViewById(R.id.service);
        mAdditional = (TextView)rootView.findViewById(R.id.additional);
        mTravelCharge = (TextView) rootView.findViewById(R.id.travelCharge);
        mServiceCharge = (TextView) rootView.findViewById(R.id.serviceCharge);
        mTotal = (TextView) rootView.findViewById(R.id.total);

        callRequester =  rootView.findViewById(R.id.mcallRequester);
        callRequester.setOnClickListener(this);
        callShop = rootView.findViewById(R.id.callShop);
        callShop.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();

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

                if(mLastLocation != null && mAuth.getUid() != null){
                    mechanicVM.updateMechanicLocation(mLastLocation,Uid);
                }


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
            getMechanicInfo();
            displayShops();
            getNearbyPlaces();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }

    public void getMechanicInfo(){

        mechanicVM.getMechanic(Uid).observe(this, mechanic -> {
            if(mechanic != null){
                Mechanic M = mechanic;
                mechanicVM.getShopId(Uid).observe(this, shopId -> {
                    Log.d("TAG" ,"masuk sini");
                    if(shopId != null){
                        myShopId = shopId;


                        shopVM.getShop(myShopId).observe(this, shop -> {
                            if(shop != null){
                                myShop = shop;

                            }
                        });
                        if(M.getMechStatus().equals("Working")){
                            mStatus.setText("Working");
                            statusSwitch.setChecked(true);
                            getRequest();
                        }
                    }
                });
            }
        });
    }

    public void getRequest(){
        requestVM.mechListenToRequest(myShopId).observe(this, request -> {
            if(request != null){

                List<Request> requestList = request;
                Iterator<Request> listIterable = requestList.iterator();
                while (listIterable.hasNext()) {

                    Request R = listIterable.next();
                    status = R.getStatus();
                    if(status.equals("Requesting Help") || status.equals("Request Accepted") || status.equals("Negotiation Started")
                            || status.equals("Negotiation Accepted") || status.equals("Mechanic On The Way")  || status.equals("Mechanic Arrived")){

                        mMap.clear();
                        negotiateInfo.setVisibility(View.GONE);
                        negotiateButton.setVisibility(View.GONE);
                        requestButton.setVisibility(View.GONE);
                        completedButton.setVisibility(View.GONE);
                        headOutButton.setVisibility(View.GONE);

                        requestKey = R.getRequestId();
                        motorcyclistId = R.getMotorcyclistId();
                        motorcycleId = R.getMotorcycleId();
                        requestedService = R.getServiceName();
                        additionalInfo = null;
                        if(R.getAdditionalInfo()!= null){
                            additionalInfo = R.getAdditionalInfo();
                        }
                        travelCharge = R.getTravelingCharge();
                        minServiceCharge = R.getMinServiceCharge();
                        maxServiceCharge = R.getMaxServiceCharge();
                        finalServiceCharge = R.getFinalServiceCharge();
                        motorcyclistDistance = R.getMotorcyclistDistance();
                        shopDistance = R.getShopDistance();
                        minTotal = R.getMinTotal();
                        maxTotal = R.getMaxTotal();
                        finalTotal = R.getFinalTotal();
                        pickupLocation = R.getRequestLocation();


                        if(status.equalsIgnoreCase("Requesting Help")){
                            requestStatus = "Requesting Help";


                            if(minServiceCharge != maxServiceCharge){
                                shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopMechanicBehavior.setHideable(false);
                                negotiateInfo.setVisibility(View.VISIBLE);
                                negotiateButton.setVisibility(View.VISIBLE);
                                displayMotorcyclist();
                                setInfo();
                            }
                            else{
                                shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopMechanicBehavior.setHideable(false);
                                requestButton.setVisibility(View.VISIBLE);
                                displayMotorcyclist();
                                setInfo();
                            }


                        }

                        if(status.equalsIgnoreCase("Request Accepted")){
                            requestStatus = "Request Accepted";
                            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            shopMechanicBehavior.setHideable(false);
                            requestButton.setVisibility(View.GONE);
                            headOutButton.setVisibility(View.VISIBLE);
                            displayMotorcyclist();
                            setInfo();
                        }
                        if(status.equalsIgnoreCase("Negotiation Started")){
                            negotiateInfo.setVisibility(View.GONE);
                            requestStatus = "Negotiation Started";
                            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            shopMechanicBehavior.setHideable(false);
                            displayMotorcyclist();
                            setInfo();

                            requestVM.listenToNegotiation(requestKey).observe(this, message-> {
                                if(message!= null){
                                    requestStatus = "Negotiation Declined";
                                    shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    shopMechanicBehavior.setHideable(false);
                                    completedButton.setVisibility(View.VISIBLE);
                                    setInfo();
                                    mMap.clear();
                                    displayShops();
                                    getNearbyPlaces();

                                }
                            });

                        }
                        if(status.equalsIgnoreCase("Negotiation Accepted")){
                            requestStatus = "Negotiation Accepted";
                            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            shopMechanicBehavior.setHideable(false);
                            headOutButton.setVisibility(View.VISIBLE);
                            displayMotorcyclist();
                            setInfo();
                        }

                        if(status.equalsIgnoreCase("Mechanic On The Way")){
                            requestStatus = "Mechanic On The Way";
                            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            shopMechanicBehavior.setHideable(false);
                            headOutButton.setVisibility(View.GONE);
                            inAssist.setVisibility(View.VISIBLE);
                            displayMotorcyclist();
                            setInfo();

                        }
                        if(status.equalsIgnoreCase("Mechanic Arrived")){
                            requestStatus = "Mechanic Arrived";
                            addMarker();
                            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            shopMechanicBehavior.setHideable(false);
                            inAssist.setVisibility(View.GONE);

                            if(requestedService.equalsIgnoreCase("Towing")){
                                towingInfo.setVisibility(View.VISIBLE);
                                calculateShopDirection();
                                displayShops();
                                setInfo();
                            }
                            else{
                                mMap.clear();
                                displayShops();
                                getNearbyPlaces();
                                requestStatus = "Completed";
                                setInfo();
                                completedButton.setVisibility(View.VISIBLE);
                                requestVM.updateStatus(requestKey,"Completed");
                            }
                        }
                        if(status.equalsIgnoreCase("Completed")){
                            mMap.clear();
                            displayShops();
                            getNearbyPlaces();
                            requestStatus = "Completed";
                            towingInfo.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });


        /*
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        requestRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(myShopId);
                shopRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        myShop = dataSnapshot.getValue(Shop.class);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {

                    String shopId = child.child("shopId").getValue().toString();
                    String status = child.child("status").getValue().toString();

                    if(shopId.equals(myShopId)){
                        if(status.equals("Requesting Help") || status.equals("Request Accepted") || status.equals("Negotiation Started")
                                || status.equals("Negotiation Accepted") || status.equals("Mechanic Arrived")){

                            negotiateInfo.setVisibility(View.GONE);
                            negotiateButton.setVisibility(View.GONE);
                            requestButton.setVisibility(View.GONE);

                            if(requestKey == null){
                                requestKey = child.getKey();
                                motorcyclistId = child.child("motorcyclistId").getValue().toString();
                                motorcycleId = child.child("motorcycleId").getValue().toString();
                                requestedService = child.child("serviceName").getValue().toString();
                                additionalInfo = null;
                                if(child.child("additionalInfo").exists()){
                                    additionalInfo = child.child("additionalInfo").getValue().toString();
                                }
                                travelCharge = Double.valueOf(child.child("travelingCharge").getValue().toString());
                                minServiceCharge = Double.valueOf(child.child("minServiceCharge").getValue().toString());
                                maxServiceCharge = Double.valueOf(child.child("maxServiceCharge").getValue().toString());
                                finalServiceCharge = Double.valueOf(child.child("finalServiceCharge").getValue().toString());
                                motorcyclistDistance = Double.valueOf(child.child("motorcyclistDistance").getValue().toString());
                                shopDistance = Double.valueOf(child.child("shopDistance").getValue().toString());
                                minTotal = Double.valueOf(child.child("minTotal").getValue().toString());
                                maxTotal = Double.valueOf(child.child("maxTotal").getValue().toString());
                                finalTotal = Double.valueOf(child.child("finalTotal").getValue().toString());
                                pickupLocation = child.child("requestLocation").getValue(LatLong.class);
                            }

                            if(status.equalsIgnoreCase("Requesting Help")){
                                requestStatus = "Requesting Help";


                                if(minServiceCharge != maxServiceCharge){
                                    shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    shopMechanicBehavior.setHideable(false);
                                    negotiateInfo.setVisibility(View.VISIBLE);
                                    negotiateButton.setVisibility(View.VISIBLE);
                                    displayMotorcyclist();
                                    setInfo();
                                }
                                else{
                                    shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    shopMechanicBehavior.setHideable(false);
                                    requestButton.setVisibility(View.VISIBLE);
                                    displayMotorcyclist();
                                    setInfo();
                                }


                            }

                            if(status.equalsIgnoreCase("Request Accepted")){
                                requestStatus = "Request Accepted";
                                shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopMechanicBehavior.setHideable(false);
                                requestButton.setVisibility(View.GONE);
                                inAssist.setVisibility(View.VISIBLE);
                                displayMotorcyclist();
                                setInfo();
                            }
                            if(status.equalsIgnoreCase("Negotiation Started")){
                                negotiateInfo.setVisibility(View.GONE);
                                requestStatus = "Negotiation Started";
                                shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopMechanicBehavior.setHideable(false);
                                displayMotorcyclist();
                                setInfo();
                            }
                            if(status.equalsIgnoreCase("Negotiation Accepted")){
                                requestStatus = "Negotiation Accepted";
                                shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopMechanicBehavior.setHideable(false);
                                inAssist.setVisibility(View.VISIBLE);
                                displayMotorcyclist();
                                setInfo();
                            }
                            if(status.equalsIgnoreCase("Mechanic Arrived")){
                                requestStatus = "Mechanic Arrived";
                                shopMechanicBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopMechanicBehavior.setHideable(false);
                                inAssist.setVisibility(View.GONE);

                                if(requestedService.equalsIgnoreCase("Towing")){
                                    towingInfo.setVisibility(View.VISIBLE);
                                    displayShops();
                                    setInfo();
                                }
                                else{
                                    requestStatus = "Completed";
                                    setInfo();
                                    completedButton.setVisibility(View.VISIBLE);
                                    DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestKey);
                                    completedRef.child("status").setValue("Completed");
                                }
                            }
                            if(shopId.equals(myShopId) && status.equalsIgnoreCase("Completed")){
                                requestStatus = "Completed";
                                towingInfo.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */

    }

    private void displayShops(){

        /*
        shopVM.queryShopLocation(mLastLocation).observe(this, keyData -> {
            if(keyData != null){

                Map<String, Object> shopData = keyData;
                String type = shopData.get("type").toString();
                String key = shopData.get("key").toString();
                Toast.makeText(getActivity(), "key : "+key, Toast.LENGTH_SHORT).show();
                if(type.equals("entered")){
                    LatLng shopLatLng = (LatLng)shopData.get("location");

                    shopVM.getShop(key).observe(this, shop -> {
                        if(shop != null){

                            Toast.makeText(getActivity(), "shop : "+shop.getShopName(), Toast.LENGTH_SHORT).show();

                            markerOptions = new MarkerOptions();
                            markerOptions.position(shopLatLng).title(shop.getShopName());
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(shop);
                            m.hideInfoWindow();
                        }
                    });
                }
            }
        });

         */

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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();

                if(marker.getTag() instanceof Shop){
                    Shop shop = (Shop)(marker.getTag());
                    selectedShop = shop;
                    shopMarker = marker;
                    calculateShopDirection(marker);
                    //shopInfo.setVisibility(View.VISIBLE);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shopDirection.setVisibility(View.VISIBLE);
                            stationSheetBehavior.setHideable(true);
                            stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                }

                if(marker.getTag().equals("Petrol Station")){
                            mBottomSheetBehavior.setHideable(true);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            stationSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            fuelDirection.setVisibility(View.VISIBLE);
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

    public void displayMotorcyclist(){

        LatLng latLng = new LatLng(pickupLocation.getLatitude(),
                pickupLocation.getLongitude());
        motorcyclistMarker = mMap.addMarker(
                new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.helmet_icon)));
        motorcyclistMarker.setTag(myMotorcyclist);

        calculateDirections();
        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

         */

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

        if(v == headOut){
            requestVM.updateStatus(requestKey,"Mechanic On The Way");
            pushNotificationHelper.sendNotification(myMotorcyclist.getToken(),"Your mechanic is heading out!","Sit back until his arrival is informed");

        }

        if(v == callRequester) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", myMotorcyclist.getMotorcyclistPhone(), null)));
        }

        if(v == statusSwitch) {
            if(statusSwitch.isChecked()) {
                mechanicVM.toggleMechanicStatus(Uid,"Working");
                getRequest();
            }
            else {
                mechanicVM.toggleMechanicStatus(Uid,"Not Working");
            }

        }

        if(v == accept){
            requestVM.acceptRequest(requestKey);
            pushNotificationHelper.sendNotification(myMotorcyclist.getToken(),"Your request has been accepted!","Please wait while your mechanic is preparing");
        }
        if(v == acceptNegotiate){

            if(finalServiceCharge < minServiceCharge || finalServiceCharge > maxServiceCharge){
                Toast.makeText(getActivity(), "Service charge must be between minimum and maximum service charge", Toast.LENGTH_SHORT).show();
                return;
            }
            requestVM.acceptRequestNegotiation(requestKey,finalServiceCharge,finalTotal);
            pushNotificationHelper.sendNotification(myMotorcyclist.getToken(),"Negotiation has started","Please accept or decline the nogotiation");

        }

        if(v == go){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + pickupLocation.getLatitude() +"," + pickupLocation.getLongitude());//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        if(v == arrived){
            requestVM.updateStatus(requestKey,"Mechanic Arrived");
            pushNotificationHelper.sendNotification(myMotorcyclist.getToken(),"Your rescuer has arrived!","Look around you");
            mMap.clear();
            negotiateInfo.setVisibility(View.GONE);
        }

        if(v == completed){
            requestVM.updateStatus(requestKey,"Completed");
            mMap.clear();
            requestStatus = "Completed";
            setInfo();
            towingInfo.setVisibility(View.GONE);
            completedButton.setVisibility(View.VISIBLE);
        }

        if(v == close){
            completedButton.setVisibility(View.GONE);
            shopMechanicBehavior.setHideable(true);
            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        if(v == decline){
            mMap.clear();
            displayShops();
            getNearbyPlaces();
            shopMechanicBehavior.setHideable(true);
            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        if(v == declineNegotiate){
            mMap.clear();
            displayShops();
            getNearbyPlaces();
            shopMechanicBehavior.setHideable(true);
            shopMechanicBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }


    }

    public void setInfo(){

        DatabaseReference motorcyclistRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(motorcyclistId);

        motorcyclistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                myMotorcyclist = dataSnapshot.getValue(Motorcyclist.class);
                myMotorcyclist.setMotoryclistId(dataSnapshot.getKey());
                Motorcycle motorcycle = dataSnapshot.child("Motorcycle").child(motorcycleId).getValue(Motorcycle.class);
                motorcycle.setMotorId(motorcycleId);

                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mStatus.setText(requestStatus);
                        mName.setText(myMotorcyclist.getMotorcyclistName());
                        mPlate.setText(motorcycle.getMotorId());
                        mType.setText(motorcycle.getMotorType());
                        mMotor.setText(motorcycle.getMotorBrand()+" "+motorcycle.getmotorModel()+" ("+motorcycle.getMotorId()+")");


                        String sd ="";
                        sd += shopDistance;
                        if(shopDistance<1000){
                            sd += "km";
                        }
                        else{
                            sd += "m";
                        }

                        String md ="";
                        md += motorcyclistDistance;
                        if(motorcyclistDistance<1000){
                            md += "km";
                        }
                        else{
                            md += "m";
                        }

                        if(requestStatus.equalsIgnoreCase("Requesting Help")){
                            mDistance.setText(md);
                        }

                        mService.setText(requestedService);
                        if(additionalInfo!= null){
                            mAdditional.setText(additionalInfo);
                        }
                        mTravelCharge.setText("RM "+travelCharge+" ("+md+")");

                        if(requestedService.equalsIgnoreCase("Towing")){
                            mServiceCharge.setText("RM "+minServiceCharge+" ("+sd+")");
                            mTotal.setText("RM "+minTotal);
                        }
                        else if(finalServiceCharge != 0){
                            mServiceCharge.setText("RM "+finalServiceCharge);
                            mTotal.setText("RM "+finalTotal);
                        }
                        else if(minServiceCharge != maxServiceCharge){
                            mServiceCharge.setText("RM "+minServiceCharge+" - RM "+maxServiceCharge);
                            mTotal.setText("RM "+minTotal+" - RM "+maxTotal);
                        }
                        else{
                            mServiceCharge.setText("RM "+minServiceCharge);
                            mTotal.setText("RM "+minTotal);
                        }

                        mMotor.setText(motorcycle.getMotorBrand()+" "+motorcycle.getmotorModel()+" ("+motorcycle.getMotorId()+")");



                    }

                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                mShopName.setText(selectedShop.getShopName());
                mShopDistance.setText(shopDuration+" away " +" ("+shopDistance+" km)");

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


    private void calculateDirections(){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                pickupLocation.getLatitude(),
                pickupLocation.getLongitude()
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );

        directions.alternatives(false);
        directions.origin(origin);
        Log.d(TAG, "calculateDirections: origin: " + origin.toString());
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                directionsResult = result;
                Log.d(TAG, "calculateDirections: result: " + result.toString());
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                String dis = result.routes[0].legs[0].distance.toString();
                if(dis.contains("km")){
                    dis = dis.replaceAll(" km","");
                    motorcyclistDistance= Double.parseDouble(dis);
                }
                else {
                    dis = dis.replaceAll(" m","");
                    motorcyclistDistance = Double.parseDouble(dis);
                }


                addPolylinesToMap("Motorcyclist");
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });


    }

    public void calculateShopDirection(){

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                myShop.getShopLocation().getLatitude(),
                myShop.getShopLocation().getLongitude()
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );

        DirectionsApiRequest userDirection = new DirectionsApiRequest(mGeoApiContext);
        userDirection.origin(origin);
        Log.d(TAG, "calculateDirections: destination2: " + destination.toString());
        userDirection.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                directionsResult = result;



                String dis = result.routes[0].legs[0].distance.toString();
                if(dis.contains("km")){
                    dis = dis.replaceAll(" km","");
                    shopDistance = Double.parseDouble(dis);
                }
                else {
                    dis = dis.replaceAll(" m","");
                    shopDistance = Double.parseDouble(dis);
                }

                Log.d(TAG, "calculateDirections: shopdistance: " + motorcyclistDistance);


                addPolylinesToMap("Shop");

            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    public void addMarker(){

        LatLng latLng = new LatLng(myShop.getShopLocation().getLatitude(),
                myShop.getShopLocation().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Shop").icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
        shopMarker = mMap.addMarker(markerOptions);
        shopMarker.setTag(myShop);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    private void addPolylinesToMap(String type) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + directionsResult.routes.length);

                for (DirectionsRoute route : directionsResult.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    if(type.equalsIgnoreCase("Motorcyclist")){
                        motorcyclistPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        motorcyclistPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                        motorcyclistPolyline.setClickable(true);
                    }
                    else{
                        shopPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        shopPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                        shopPolyline.setClickable(true);
                    }


                }
            }
        });
    }


    public void calculateStationDirection() {

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
                        if (stationDistanceD >= 100)
                            stationDistance.setText(stationDuration + " away (" + stationDistanceD + " m)");
                        else
                            stationDistance.setText(stationDuration + " away (" + stationDistanceD + " km)");

                        stationSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        stationSheetBehavior.setHideable(false);
                    }
                });

            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

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



    @Override
    public void onDetach() {
        super.onDetach();
    }



}




