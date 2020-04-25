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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.GetNearbyPlacesData;
import com.afiq.motorcycleassist.GpsUtils;
import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
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
import com.afiq.motorcycleassist.ViewModel.ServiceVM;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MotorcyclistHomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private MarkerOptions marker;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    LocationListener locationListener;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int locationRequestCode = 1000;
    private boolean isContinue = false;
    private boolean isGPS = false;
    static final float COORDINATE_OFFSET = 0.00002f;

    SupportMapFragment mapFragment;
    private LocationCallback locationCallback;
    private LatLong pickupLocation;
    private GeoApiContext mGeoApiContext = null;
    private FirebaseAuth mAuth;
    private String TAG = "MotorAssist";


    //Shop Request
    private LinearLayout callShop,callMechanic,mcallRequester,callRequester,callRescuer;
    private Button fuelDirection,request,breakdown,accept,decline,close,gsrClose,gsClose,declineClose;
    private CardView shopInfo,requestInfo,rServiceInfo,requesting,gsAcceptDecline;
    private LinearLayout negotiationInfo,serviceInfo;
    private View shopRequesterSheet;
    private BottomSheetBehavior shopRequesterBehavior;
    private CoordinatorLayout completedButton,declinedButton;
    private TextView noSuperbike,open,stationName,stationDistance, instruction, mName,mDistance,mPhone,mEmail,mDays,mHours,
            myMotor,myBreakdown,mService,mAdditional,mTravelCost,mServiceCost,mShopDistance,mUserDistance,mTotal,
            rname,rdistance,mechname,mechphone,mechemail,rservice,radditional,rtravelCharge,rserviceCharge,rtotal,status;
    private double travelCharge = 0.0,minServiceCharge= 0.0,maxServiceCharge = 0.0, finalServiceCharge = 0.0,
            shopDistance= 0.0,  userDistance= 0.0,minTotal= 0.0,maxTotal, finalTotal =0.0;
    private String userDuration,motorType,shopOpen,shopDuration,requestKey,requestedService,additionalInfo,requestStatus,motorcycleId,motorcyclistId;
    private Marker mechMarker;
    private Mechanic requestedMechanic = new Mechanic();
    private Shop selectedShop = new Shop();
    private Shop requestedShop = new Shop();
    private ArrayList<Shop> shopList = new ArrayList<>();
    private ArrayList<Service> serviceList = new ArrayList<>();

    //Good Samaritan
    private CardView selectingStation;
    private TextView gsrName,gsrStatus,gsrDestination,gsrDistance,gsrRequesterDistance,gsrRequesterName,gsrRequesterPhone,gsrMotorType,gsrMotorPlate,gsrMotorModel,
            gsName,gsStatus,gsDestination,gsDistance,gsRescuerDistance,gsRescuerName,gsRescuerPhone,gsMotorType,gsMotorPlate,gsMotorModel,
            rfName,rfDestination,rfDistance,rfRequesterDistance,rfRequesterName,rfMotorType,rfMotorPlate,rfMotorModel;
    private Button towHere,cancel,gsAccept,gsDecline,gsHeadOut,gsAcquired, gsFuelDirection,gsArrived,gsDirection,gsStartTowing,gsDestDirection,gsCompleted,rfSelect,rfDecline;
    private Switch goodsamaritanSwitch;
    private LinearLayout gsAcceptDeclineButton,headOutButton,fuelAcquiredDirectionButton,arrivedDirectionButton,startTowingButton,directionCompletedButton,gsrCloseButton,gsCloseButton;
    private View bottomSheet,gsResquerSheet,gsRequesterSheet,gsFuelRequestSheet,stationSheet;
    private BottomSheetBehavior mBottomSheetBehavior,gsRequesterBehavior,gsResquerBehavior,gsFuelRequestBehavior, stationSheetBehavior;
    private String gsRequestStatus, gsKeyFromNoti,gsRequestKey,requesterDistance,rescuerDistance,rescuerDuration,requesterDuration,
            selectedStation,stationDuration, stationUserDuration,destinationName,
            selectedRequest;
    private double stationDistanceD, requesterDistanceD;
    private GoodSamaritanRequest goodSamaritanRequest = new GoodSamaritanRequest();
    private Motorcyclist gsrRequester = new Motorcyclist();
    private Motorcyclist gsrRescuer = new Motorcyclist();
    private Motorcycle gsrRequesterMotor = new Motorcycle();
    private Marker rescuerMarker, requesterMarker, destinationMarker,stationMarker, selectedStationMarker;
    private DirectionsResult requesterDR,destinationDR,stationDR;
    private Polyline requesterPolyline, shopPolyline, stationPolyline;
    private Shop shopDestination = new Shop();
    private LatLng destinationLatLng = null;

    PushNotificationHelper pushNotificationHelper = new PushNotificationHelper();

    GoodSamaritanRequestVM goodSamaritanRequestVM;
    RequestVM requestVM;
    ShopVM shopVM;
    MotorcyclistVM motorcyclistVM;
    MotorcycleVM motorcycleVM;
    MechanicVM mechanicVM;
    ServiceVM serviceVM;

    String Uid;
    String goodSamaritanStatus;
    HashMap<String,Marker> goodSamaritanMarkers = new HashMap<>();
    HashMap<String,Marker> shopMarkers = new HashMap<>();
    Session session;
    boolean inRequest;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        goodSamaritanRequestVM = ViewModelProviders.of(this).get(GoodSamaritanRequestVM.class);
        requestVM = ViewModelProviders.of(this).get(RequestVM.class);
        shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        motorcyclistVM = ViewModelProviders.of(this).get(MotorcyclistVM.class);
        motorcycleVM = ViewModelProviders.of(this).get(MotorcycleVM.class);
        mechanicVM = ViewModelProviders.of(this).get(MechanicVM.class);
        serviceVM = ViewModelProviders.of(this).get(ServiceVM.class);
    }


    public MotorcyclistHomeFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motorcyclist_home, container, false);

        inRequest =false;

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        if(getActivity().getIntent().getExtras()!= null){
            gsKeyFromNoti = getActivity().getIntent().getExtras().getString("gsRequestKey");
            getActivity().getIntent().removeExtra("gsRequestKey");
        }

        else if(getArguments()!= null){
            gsKeyFromNoti = getArguments().getString("requestId");
        }

        else{
            gsKeyFromNoti = null;
        }

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mAuth = FirebaseAuth.getInstance();

        mName = (TextView) rootView.findViewById(R.id.name);
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mPhone = (TextView) rootView.findViewById(R.id.phone);
        mEmail = (TextView) rootView.findViewById(R.id.email);
        mDays = (TextView) rootView.findViewById(R.id.days);
        mHours = (TextView) rootView.findViewById(R.id.hours);
        open = (TextView) rootView.findViewById(R.id.open);

        callShop = rootView.findViewById(R.id.callShop);
        callShop.setOnClickListener(this);
        callMechanic =  rootView.findViewById(R.id.callMechanic);
        callMechanic.setOnClickListener(this);
        callRequester =rootView.findViewById(R.id.callRequester);
        callRequester.setOnClickListener(this);
        callRescuer = rootView.findViewById(R.id.callRescuer);
        callRescuer.setOnClickListener(this);

        mService = (TextView) rootView.findViewById(R.id.service);
        mAdditional = (TextView) rootView.findViewById(R.id.additional);
        mTravelCost = (TextView) rootView.findViewById(R.id.travelCost);
        mServiceCost = (TextView) rootView.findViewById(R.id.serviceCost);
        mTotal = (TextView) rootView.findViewById(R.id.total);

        rname = (TextView) rootView.findViewById(R.id.rname);
        rdistance = (TextView) rootView.findViewById(R.id.rdistance);
        mechname = (TextView) rootView.findViewById(R.id.mechname);

        rservice = (TextView) rootView.findViewById(R.id.rservice);
        radditional = (TextView) rootView.findViewById(R.id.radditional);
        rtravelCharge = (TextView) rootView.findViewById(R.id.rtravelCharge);
        rserviceCharge = (TextView) rootView.findViewById(R.id.rserviceCharge);
        rtotal = (TextView) rootView.findViewById(R.id.rtotal);
        status = (TextView) rootView.findViewById(R.id.status);


        //Good Samaritans
        gsrName = (TextView) rootView.findViewById(R.id.gsrName);
        gsrStatus = (TextView) rootView.findViewById(R.id.gsrStatus);
        gsrDestination = (TextView) rootView.findViewById(R.id.gsrDestination);
        gsrDistance = (TextView) rootView.findViewById(R.id.gsrDistance);
        gsrRequesterDistance = (TextView) rootView.findViewById(R.id.gsrRequesterDistance);
        gsrRequesterName = (TextView) rootView.findViewById(R.id.gsrRequesterName);
        gsrRequesterPhone = (TextView) rootView.findViewById(R.id.gsrRequesterPhone);
        gsrMotorType = (TextView) rootView.findViewById(R.id.gsrMotorType);
        gsrMotorPlate = (TextView) rootView.findViewById(R.id.gsrMotorPlate);
        gsrMotorModel = (TextView) rootView.findViewById(R.id.gsrMotorModel);

        gsName = (TextView) rootView.findViewById(R.id.gsName);
        gsStatus = (TextView) rootView.findViewById(R.id.gsStatus);
        gsDestination = (TextView) rootView.findViewById(R.id.gsDestination);
        gsDistance = (TextView) rootView.findViewById(R.id.gsDistance);
        gsRescuerDistance = (TextView) rootView.findViewById(R.id.gsRescuerDistance);
        gsRescuerName = (TextView) rootView.findViewById(R.id.gsRescuerName);
        gsRescuerPhone = (TextView) rootView.findViewById(R.id.gsRescuerPhone);
        gsMotorType = (TextView) rootView.findViewById(R.id.gsMotorType);
        gsMotorPlate = (TextView) rootView.findViewById(R.id.gsMotorPlate);
        gsMotorModel = (TextView) rootView.findViewById(R.id.gsMotorModel);

        rfName = (TextView) rootView.findViewById(R.id.rfName);
        rfDestination = (TextView) rootView.findViewById(R.id.rfDestination);
        rfDistance = (TextView) rootView.findViewById(R.id.rfDistance);
        rfRequesterDistance = (TextView) rootView.findViewById(R.id.rfRequesterDistance);
        rfRequesterName = (TextView) rootView.findViewById(R.id.rfRequesterName);
        rfMotorType = (TextView) rootView.findViewById(R.id.rfMotorType);
        rfMotorPlate = (TextView) rootView.findViewById(R.id.rfMotorPlate);
        rfMotorModel = (TextView) rootView.findViewById(R.id.rfMotorModel);

        selectingStation = (CardView) rootView.findViewById(R.id.selectingStation);
        gsAcceptDeclineButton = (LinearLayout)rootView.findViewById(R.id.gsAcceptDeclineButton);
        headOutButton = (LinearLayout)rootView.findViewById(R.id.headOutButton);
        arrivedDirectionButton = (LinearLayout)rootView.findViewById(R.id.arrivedDirectionButton);
        fuelAcquiredDirectionButton = (LinearLayout)rootView.findViewById(R.id.fuelAcquiredDirectionButton);
        startTowingButton = (LinearLayout)rootView.findViewById(R.id.startTowingButton);
        directionCompletedButton = (LinearLayout)rootView.findViewById(R.id.directionCompletedButton);
        gsrCloseButton = (LinearLayout)rootView.findViewById(R.id.gsrCloseButton);
        gsCloseButton = (LinearLayout)rootView.findViewById(R.id.gsCloseButton);

        rfDecline = (Button)rootView.findViewById(R.id.rfDecline);
        rfDecline.setOnClickListener(this);
        rfSelect = (Button)rootView.findViewById(R.id.rfSelect);
        rfSelect.setOnClickListener(this);
        gsAccept = (Button)rootView.findViewById(R.id.gsAccept);
        gsAccept.setOnClickListener(this);
        gsDecline = (Button)rootView.findViewById(R.id.gsDecline);
        gsDecline.setOnClickListener(this);
        gsHeadOut = (Button)rootView.findViewById(R.id.gsHeadOut);
        gsHeadOut.setOnClickListener(this);
        gsAcquired = (Button)rootView.findViewById(R.id.gsAcquired);
        gsAcquired.setOnClickListener(this);
        gsFuelDirection = (Button)rootView.findViewById(R.id.gsFuelDirection);
        gsFuelDirection.setOnClickListener(this);
        gsArrived = (Button)rootView.findViewById(R.id.gsArrived);
        gsArrived.setOnClickListener(this);
        gsDirection = (Button)rootView.findViewById(R.id.gsDirection);
        gsDirection.setOnClickListener(this);
        gsStartTowing = (Button)rootView.findViewById(R.id.gsStartTowing);
        gsStartTowing.setOnClickListener(this);
        gsDestDirection = (Button)rootView.findViewById(R.id.gsDestDirection);
        gsDestDirection.setOnClickListener(this);
        gsCompleted = (Button)rootView.findViewById(R.id.gsCompleted);
        gsCompleted.setOnClickListener(this);
        gsClose = (Button)rootView.findViewById(R.id.gsClose);
        gsClose.setOnClickListener(this);
        gsrClose = (Button)rootView.findViewById(R.id.gsrClose);
        gsrClose.setOnClickListener(this);


        gsResquerSheet = rootView.findViewById(R.id.gsRescuerSheet);
        gsResquerBehavior = BottomSheetBehavior.from(gsResquerSheet);
        gsResquerBehavior.setHideable(true);
        gsResquerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        gsRequesterSheet = rootView.findViewById(R.id.gsRequesterSheet);
        gsRequesterBehavior = BottomSheetBehavior.from(gsRequesterSheet);
        gsRequesterBehavior.setHideable(true);
        gsRequesterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        gsFuelRequestSheet = rootView.findViewById(R.id.gsFuelRequestSheet);
        gsFuelRequestBehavior = BottomSheetBehavior.from(gsFuelRequestSheet);
        gsFuelRequestBehavior.setHideable(true);
        gsFuelRequestBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        stationSheet = rootView.findViewById(R.id.station_sheet);
        stationSheetBehavior = BottomSheetBehavior.from(stationSheet);
        stationSheetBehavior.setHideable(true);
        stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        towHere = (Button)rootView.findViewById(R.id.towHere);
        towHere.setOnClickListener(this);
        goodsamaritanSwitch = (Switch)rootView.findViewById(R.id.goodSamaritanSwitch);
        goodsamaritanSwitch.setOnClickListener(this);
        //Good Samaritans


        myMotor = (TextView) rootView.findViewById(R.id.mymotor);
        myBreakdown = (TextView) rootView.findViewById(R.id.mybreakdown);
        Session session = new Session(getActivity());

        String role = session.getRole();

        if(session.getService()!=null){
            //inRequest = true;
            String motor = session.getMotorcycle().getMotorId();
            motorType = session.getMotorcycle().getMotorType();
            String breakdown = session.getService().getServiceName();

            myMotor.setText(motor);
            myBreakdown.setText(breakdown);

            instruction = (TextView) rootView.findViewById(R.id.instruction);
            instruction.setText("Click on any shop marker and click 'REQUEST' button");
            instruction.setVisibility(View.VISIBLE);
        }
        else{
            instruction = (TextView) rootView.findViewById(R.id.instruction);
            instruction.setText("Click on 'REQUEST' button on top left to start requesting help");
            instruction.setVisibility(View.VISIBLE);
        }

        if(session.getRequest()!=null){
            String motor = session.getMotorcycle().getMotorId();
            selectedRequest = session.getRequest();
            myMotor.setText(motor);
            myBreakdown.setText(selectedRequest);

            if(selectedRequest.equalsIgnoreCase("Towing")){
                instruction = (TextView) rootView.findViewById(R.id.instruction);
                instruction.setText("Click on any shop marker and click 'TOW HERE' button");
                instruction.setVisibility(View.VISIBLE);
            }

        }
        //Shop Request
        cancel = (Button)rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        close = (Button)rootView.findViewById(R.id.close);
        close.setOnClickListener(this);
        declineClose = (Button)rootView.findViewById(R.id.declineClose);
        declineClose.setOnClickListener(this);
        accept = (Button)rootView.findViewById(R.id.accept);
        accept.setOnClickListener(this);
        decline = (Button)rootView.findViewById(R.id.decline);
        decline.setOnClickListener(this);
        request = (Button)rootView.findViewById(R.id.request);
        request.setOnClickListener(this);
        breakdown = (Button)rootView.findViewById(R.id.breakdown);
        breakdown.setOnClickListener(this);




        request = (Button)rootView.findViewById(R.id.request);
        noSuperbike = (TextView)rootView.findViewById(R.id.noSuperbike);
        shopInfo = (CardView)rootView.findViewById(R.id.shopInfo);
        serviceInfo = (LinearLayout) rootView.findViewById(R.id.serviceInfo);
        requesting = (CardView)rootView.findViewById(R.id.requesting);
        negotiationInfo = (LinearLayout)rootView.findViewById(R.id.negotiationInfo);
        completedButton = (CoordinatorLayout) rootView.findViewById(R.id.completedButton);
        declinedButton = (CoordinatorLayout) rootView.findViewById(R.id.declinedButton);
        bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        shopRequesterSheet = rootView.findViewById(R.id.shopRequesterSheet);
        shopRequesterBehavior = BottomSheetBehavior.from(shopRequesterSheet);
        shopRequesterBehavior.setHideable(true);
        shopRequesterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        //fuel station
        stationName = (TextView)rootView.findViewById(R.id.stationName);
        stationDistance = (TextView)rootView.findViewById(R.id.stationDistance);
        fuelDirection = (Button)rootView.findViewById(R.id.fueldirection);






        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markerClick();

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }else {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    mLastLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    //displayShops();
                    Session session = new Session(getActivity());
                    if(session.getRequest()!=null){
                        Log.d("TAG", "selected request: "+selectedRequest);
                        if(selectedRequest.equals("Fuel Request")){
                            getNearbyPlaces();
                        }
                        else{
                            displayShops();
                        }
                    }
                    if(!inRequest){
                        if(session.getService()!=null){
                            displayShops();
                        }
                        if(session.getService() == null && session.getRequest() == null){
                            getNearbyPlaces();
                            displayShops();
                        }
                    }


                }
            });
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLastLocation = location;

                        if (!isContinue) {

                        } else {

                        }
                        if (!isContinue && fusedLocationProviderClient != null) {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        getDeviceLocation();


        /*
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
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
        });
        */


        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }

        getGoodSamaritanStatus();
        getGoodSamaritanRequest();
        goodSamaritansRescuerRequest();
        getShopRequest();
        //getShopRequest();


    }

    public void getGoodSamaritanStatus(){

        motorcyclistVM.getGoodSamaritanStatus().observe(this, status -> {
            if(status != null){
                if(status.equals("active")){
                    goodsamaritanSwitch.setChecked(true);
                    goodSamaritanStatus = "active";
                }
                else{
                    goodsamaritanSwitch.setChecked(false);
                    goodSamaritanStatus = "inactive";
                }

            }
        });
    }


    private void displayShops(){
        /*
        shopVM.queryShopLocation(mLastLocation).observe(this, keyData -> {
            if(keyData != null){
                Map<String, Object> shopData = keyData;
                String type = shopData.get("type").toString();
                String key = shopData.get("key").toString();
                Log.d("TAG", "shop key : "+key);
                if(type.equals("entered")){
                    LatLng shopLatLng = (LatLng)shopData.get("location");

                    shopVM.getShop(key).observe(this, shop -> {
                        if(shop != null){

                            if(shop.getShopStatus().equalsIgnoreCase("Approved")){
                                marker = new MarkerOptions();
                                marker.position(shopLatLng).title(shop.getShopName());
                                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
                                Marker m = mMap.addMarker(marker);
                                m.setTag(shop);
                                m.hideInfoWindow();
                            }
                        }
                    });
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();

                if(marker.getTag() instanceof Shop){
                    Shop shop = (Shop)(marker.getTag());
                    selectedShop = shop;
                    //shopInfo.setVisibility(View.VISIBLE);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stationSheetBehavior.setHideable(true);
                            stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                    Session session = new Session(getActivity());
                    if(session.getService()!=null){
                        serviceInfo.setVisibility(View.VISIBLE);
                        calculateDirections(marker);

                    }
                    else if(session.getRequest()!= null){
                        towHere.setVisibility(View.VISIBLE);
                        calculateShopDirection(marker);
                    }
                    else {
                        calculateShopDirection(marker);
                    }
                }

                if(marker.getTag().equals("Petrol Station")){

                    if(goodSamaritanRequest != null){
                        if(goodSamaritanRequest.getGsrName()!= null){
                            if(goodSamaritanRequest.getGsrName().equals("Fuel Request") && !goodSamaritanRequest.getGsrRequesterId().equals(Uid)){
                                gsFuelRequestBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                gsFuelRequestBehavior.setHideable(true);
                                stationMarker = marker;
                                calculateStationDirection();
                            }
                        }
                        else{
                            mBottomSheetBehavior.setHideable(true);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            stationSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            fuelDirection.setVisibility(View.VISIBLE);
                            stationMarker = marker;
                            calculateNearbyStationDirection();
                        }
                    }

                }


                return false;
            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                gsFuelRequestBehavior.setHideable(true);
                gsFuelRequestBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                serviceInfo.setVisibility(View.GONE);

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
                                    marker = new MarkerOptions();
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



    }

    public void markerClick(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();

                Log.d("TAG", "marker tag: "+marker.getTag());

                if(marker.getTag() instanceof Shop){
                    Shop shop = (Shop)(marker.getTag());
                    selectedShop = shop;
                    //shopInfo.setVisibility(View.VISIBLE);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stationSheetBehavior.setHideable(true);
                            stationSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                    Session session = new Session(getActivity());
                    if(session.getService()!=null){
                        serviceInfo.setVisibility(View.VISIBLE);
                        calculateDirections(marker);





                    }
                    else if(session.getRequest()!= null){
                        towHere.setVisibility(View.VISIBLE);



                        calculateShopDirection(marker);
                    }
                    else {
                        calculateShopDirection(marker);
                    }
                }

                Log.d("TAG", "marker tag: "+marker.getTag());
                if(marker.getTag().equals("Petrol Station")){
                    if(gsKeyFromNoti != null){
                        Log.d("TAG", "masuk 1 : ");
                        if(goodSamaritanRequest.getGsrName()!= null){
                            if(goodSamaritanRequest.getGsrName().equals("Fuel Request")){
                                gsFuelRequestBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                gsFuelRequestBehavior.setHideable(true);
                                stationMarker = marker;
                                calculateStationDirection();
                            }
                        }
                        else{
                            Log.d("TAG", "masuk 2 : ");
                            mBottomSheetBehavior.setHideable(true);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            stationSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            fuelDirection.setVisibility(View.VISIBLE);
                            stationMarker = marker;
                            calculateNearbyStationDirection();
                        }
                    }

                }


                return false;
            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                gsFuelRequestBehavior.setHideable(true);
                gsFuelRequestBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                serviceInfo.setVisibility(View.GONE);

            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1000:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (isContinue) {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                            if (location != null) {
                                mLastLocation = location;
                            } else {
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Map Error !", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void getNearbyGoodSamaritan(){

        if(!inRequest){
            motorcyclistVM.queryGoodSamaritanLocation(mLastLocation).observe(this, keyData -> {
                if(keyData != null){
                    Map<String, Object> shopData = keyData;
                    String type = shopData.get("type").toString();
                    String key = shopData.get("key").toString();
                    if(type.equals("entered")){
                        LatLng gsLatLng = (LatLng)shopData.get("location");

                        motorcyclistVM.getMotorcyclist(key).observe(this, motorcyclist -> {
                            if(motorcyclist != null){

                                Iterator<Map.Entry<String, Marker> >
                                        iterator = goodSamaritanMarkers.entrySet().iterator();

                                boolean isKeyPresent = false;
                                while (iterator.hasNext()) {
                                    // Get the entry at this iteration
                                    Map.Entry<String, Marker>
                                            entry
                                            = iterator.next();

                                    // Check if this key is the required key
                                    if (key.equals(entry.getKey())) {
                                        isKeyPresent = true;
                                    }
                                }
                                if(!isKeyPresent){
                                    marker = new MarkerOptions();
                                    marker.position(gsLatLng).title(motorcyclist.getMotorcyclistName());
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.helmet_icon));
                                    Marker m = mMap.addMarker(marker);
                                    goodSamaritanMarkers.put(key , m);
                                    m.setTag(motorcyclist);
                                    m.hideInfoWindow();
                                }

                            }
                        });
                    }
                    else{
                        Marker marker = goodSamaritanMarkers.get(key);
                        if(marker != null){
                            marker.remove();
                            goodSamaritanMarkers.remove(key);
                        }

                    }
                }
            });
        }


    }


    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation =location;
                /*
                if(mAuth.getUid() != null){
                    if(goodSamaritanStatus != null){
                        if(goodSamaritanStatus.equals("active")){
                            Toast.makeText(getActivity(), "status : "+goodSamaritanStatus,Toast.LENGTH_SHORT).show();
                            motorcyclistVM.updateGSLocation(mLastLocation,Uid);
                        }
                    }
                }


                 */



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

            if(mAuth.getUid() != null){
                if(goodSamaritanStatus != null){
                    if(goodSamaritanStatus.equals("active")){
                        motorcyclistVM.updateGSLocation(mLastLocation,Uid);
                    }
                }
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            getNearbyGoodSamaritan();

            Session session = new Session(getActivity());
            if(session.getRequest()!=null){
                if(session.getRequest().equals("Fuel Request")){

                    String user_id = mAuth.getUid();
                    DatabaseReference gsRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(user_id);
                    gsRef.removeValue();
                    goodsamaritanSwitch.setChecked(false);

                    String requestName = session.getRequest();
                    session.setRequest(null);
                    Motorcycle motorcycle = session.getMotorcycle();


                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
                    Date date = new Date();
                    String gsrStartTime = dateFormat.format(date).toString();

                    Map<String, Object> requestData = new HashMap<>();
                    requestData.put("gsrRequesterId", user_id);
                    requestData.put("gsrMotorcycleId", motorcycle.getMotorId());
                    requestData.put("gsrDestination", selectedShop.getShopId());
                    requestData.put("gsrDistance", shopDistance);
                    requestData.put("gsrName", requestName);
                    requestData.put("gsrStartTime", gsrStartTime);
                    requestData.put("gsrStatus", "Requesting Help");
                    LatLong gsrLocation = new LatLong(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    requestData.put("gsrLocation",gsrLocation);
                    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
                    String requestKey = requestRef.push().getKey();
                    requestRef.child(requestKey).setValue(requestData);


                    DatabaseReference goodSamaritanRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan");
                    GeoFire geoFire = new GeoFire(goodSamaritanRef);
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10);
                    //geoQuery.removeAllListeners();

                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {

                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(key);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Log.d(TAG,"key : "+key);
                                    if(mAuth.getUid() != key){
                                        String title = "Someone is requesting your for "+requestName+"!";
                                        String body = "Tap to view this request";
                                        String data = requestKey;
                                        Motorcyclist motorcyclist = dataSnapshot.getValue(Motorcyclist.class);
                                        pushNotificationHelper.sendNotificationWithData(motorcyclist.getToken(), title,body,data);
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


            }
        }
    }



        /*
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastLocation = task.getResult();
                            if (mLastLocation != null) {
                                displayShops();
                                getNearbyPlaces();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));


                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



                            }
                        } else {
                            Toast.makeText(getActivity(), "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                */


    public void calculateNearbyStationDirection(){

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

    public void calculateStationDirection(){

        com.google.maps.model.LatLng station = new com.google.maps.model.LatLng(
                stationMarker.getPosition().latitude,
                stationMarker.getPosition().longitude
        );

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );


        com.google.maps.model.LatLng userDestination = new com.google.maps.model.LatLng(
                goodSamaritanRequest.getGsrLocation().getLatitude(),
                goodSamaritanRequest.getGsrLocation().getLongitude()
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);


                directions.origin(origin);
                directions.alternatives(false);

                directions.destination(station).setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        stationDR = result;
                        stationDuration = result.routes[0].legs[0].duration.toString();
                        String dis = result.routes[0].legs[0].distance.toString();
                        if (dis.contains("km")) {
                            dis = dis.replaceAll(" km", "");
                            stationDistanceD = Double.parseDouble(dis);
                        } else {
                            dis = dis.replaceAll(" m", "");
                            stationDistanceD = Double.parseDouble(dis);
                        }



                DirectionsApiRequest stationUserDirection = new DirectionsApiRequest(mGeoApiContext);
                stationUserDirection.origin(station);
                stationUserDirection.destination(userDestination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result2) {
                        requesterDR = result2;

                        requesterDuration = result2.routes[0].legs[0].duration.toString();
                        String duration1 ="",duration2 = "";
                        duration1 = stationDuration.replaceAll("[^\\d.]", "");

                        duration2 = requesterDuration.replaceAll("[^\\d.]", "");

                        int totalDuration = Integer.parseInt(duration1) +Integer.parseInt(duration2);
                        if(totalDuration < 60){
                            stationUserDuration += totalDuration +" min";
                        }
                        else{
                            stationUserDuration += totalDuration +" hour";
                        }

                        String dis = result.routes[0].legs[0].distance.toString();
                        if (dis.contains("km")) {
                            dis = dis.replaceAll(" km", "");
                            requesterDistanceD = Double.parseDouble(dis);
                        } else {
                            dis = dis.replaceAll(" m", "");
                            requesterDistanceD = Double.parseDouble(dis);
                        }

                        addPolylinesToMap("Station",stationDR);
                        addPolylinesToMap("Requester",requesterDR);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                rfName.setText(goodSamaritanRequest.getGsrName());
                                rfDestination.setText(stationMarker.getTitle());
                                if(stationDistanceD >= 100)
                                    rfDistance.setText("Station : " +stationDuration + " away ("+stationDistanceD+" m)");
                                else
                                    rfDistance.setText("Station : " +stationDuration + " away ("+stationDistanceD+" km)");
                                rfRequesterName.setText(gsrRequester.getMotorcyclistName());
                                double totalDistance = stationDistanceD +requesterDistanceD;
                                if(totalDistance >= 100)
                                    rfRequesterDistance.setText("Requester : "+totalDuration+" mins away("+totalDistance+ " m )");
                                else
                                    rfRequesterDistance.setText("Requester : "+totalDuration+" mins away("+totalDistance+ " km )");
                                rfMotorType.setText(gsrRequesterMotor.getMotorType());
                                rfMotorPlate.setText(gsrRequesterMotor.getMotorId());
                                rfMotorModel.setText(gsrRequesterMotor.getMotorBrand()+" "+gsrRequesterMotor.getmotorModel());
                                gsFuelRequestBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                gsFuelRequestBehavior.setHideable(false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });



    }


    private void calculateDirections(Marker marker){

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

                DirectionsApiRequest userDirection = new DirectionsApiRequest(mGeoApiContext);
                userDirection.origin(destination);
                userDirection.destination(origin).setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {



                        String dis = result.routes[0].legs[0].distance.toString();
                        if(dis.contains("km")){
                            dis = dis.replaceAll(" km","");
                            userDistance = Double.parseDouble(dis);
                        }
                        else {
                            dis = dis.replaceAll(" m","");
                            userDistance = Double.parseDouble(dis);
                        }


                        setShopInfo(marker);
                        setServiceInfo();






                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });


            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

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

    public void setServiceInfo(){
        Session session = new Session(getActivity());
        Service myService = session.getService();
        /*
        Toast.makeText(getActivity(), "masuk", Toast.LENGTH_SHORT).show();
        serviceVM.getServices().observe(this, services -> {
            if(services != null){

                Toast.makeText(getActivity(), "service not null", Toast.LENGTH_SHORT).show();

                if(myService.getServiceName().equals("Change tyre tube")){
                    mAdditional.setText("Tyre size : "+session.getTyreSize());

                }
                else{
                    mAdditional.setText("NA");
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                List<Service> serviceList = (List)services;
                Toast.makeText(getActivity(), "s"+serviceList.size(), Toast.LENGTH_SHORT).show();
                Iterator<Service> serviceIterator = serviceList.iterator();
                while(serviceIterator.hasNext()){

                    Service service = serviceIterator.next();
                    mService.setText(myService.getServiceName());
                    Toast.makeText(getActivity(), "s"+service.getMaxPrice(), Toast.LENGTH_SHORT).show();
                    if(service.getServiceName().equalsIgnoreCase("Traveling")){


                        travelCharge = userDistance * service.getMaxPrice();
                        travelCharge = Math.round(travelCharge*100)/100;
                        String ud ="";
                        ud += userDistance;
                        if(userDistance<1000){
                            ud += "km";
                        }
                        else{
                            ud += "m";
                        }
                        mTravelCost.setText("RM"+travelCharge+" ("+ud+")");
                    }

                    if(myService.getServiceName().equalsIgnoreCase("Towing")){

                        String sd ="";
                        sd += shopDistance;
                        if(shopDistance<1000){
                            sd += "km";
                        }
                        else{
                            sd += "m";
                        }
                        minServiceCharge = myService.getMaxPrice() * shopDistance;
                        minServiceCharge = Math.round(minServiceCharge*100)/100;
                        maxServiceCharge = minServiceCharge;
                        finalServiceCharge = maxServiceCharge;

                        mServiceCost.setText("RM"+minServiceCharge+" ("+sd+")");
                        minTotal = minServiceCharge+travelCharge;
                        maxTotal = maxServiceCharge+travelCharge;
                        finalTotal = maxTotal;
                        mTotal.setText("RM"+maxTotal);
                    }

                    else if(myService.getMinPrice() == myService.getMaxPrice()){
                        mServiceCost.setText("RM"+myService.getMaxPrice());
                        minServiceCharge = myService.getMinPrice();
                        maxServiceCharge = myService.getMaxPrice();
                        minTotal = minServiceCharge+travelCharge;
                        maxTotal = maxServiceCharge+travelCharge;
                        mTotal.setText("RM"+maxTotal);
                    }
                    else {
                        mServiceCost.setText("RM"+myService.getMinPrice()+" - RM"+myService.getMaxPrice());
                        minServiceCharge = myService.getMinPrice();
                        maxServiceCharge = myService.getMaxPrice();
                        minTotal = myService.getMinPrice()+travelCharge;
                        maxTotal = myService.getMaxPrice()+travelCharge;
                        mTotal.setText("RM"+minTotal+" - RM"+maxTotal);
                    }
                }
            }
        });

            }
        });

         */


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Service");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(myService.getServiceName().equals("Towing") && motorType.equals("Superbike") && selectedShop.getSuperbikeTowing().equals("No")){
                            noSuperbike.setVisibility(View.VISIBLE);
                            request.setVisibility(View.GONE);
                        }
                        else{
                            request.setVisibility(View.VISIBLE);
                            noSuperbike.setVisibility(View.GONE);
                        }

                        if(myService.getServiceName().equals("Change tyre tube")){
                            mAdditional.setText("Tyre size : "+session.getTyreSize());

                        }
                        else{
                            mAdditional.setText("NA");
                        }


                for(DataSnapshot child : children) {

                    Service service = child.getValue(Service.class);
                    mService.setText(myService.getServiceName());


                    if(service.getServiceName().equalsIgnoreCase("Traveling")){
                        travelCharge = userDistance * service.getMaxPrice();
                        travelCharge = Math.round(travelCharge*100)/100;
                        String ud ="";
                        ud += userDistance;
                        if(userDistance<1000){
                            ud += "km";
                        }
                        else{
                            ud += "m";
                        }
                        mTravelCost.setText("RM"+travelCharge+" ("+ud+")");
                    }

                    if(myService.getServiceName().equalsIgnoreCase("Towing")){

                        String sd ="";
                        sd += shopDistance;
                        if(shopDistance<1000){
                            sd += "km";
                        }
                        else{
                            sd += "m";
                        }
                        minServiceCharge = myService.getMaxPrice() * shopDistance;
                        minServiceCharge = Math.round(minServiceCharge*100)/100;
                        maxServiceCharge = minServiceCharge;
                        finalServiceCharge = maxServiceCharge;

                        mServiceCost.setText("RM"+minServiceCharge+" ("+sd+")");
                        minTotal = minServiceCharge+travelCharge;
                        maxTotal = maxServiceCharge+travelCharge;
                        finalTotal = maxTotal;
                        mTotal.setText("RM"+maxTotal);
                    }

                    else if(myService.getMinPrice() == myService.getMaxPrice()){
                        mServiceCost.setText("RM"+myService.getMaxPrice());
                        minServiceCharge = myService.getMinPrice();
                        maxServiceCharge = myService.getMaxPrice();
                        minTotal = minServiceCharge+travelCharge;
                        maxTotal = maxServiceCharge+travelCharge;
                        mTotal.setText("RM"+maxTotal);
                    }
                    else {
                        mServiceCost.setText("RM"+myService.getMinPrice()+" - RM"+myService.getMaxPrice());
                        minServiceCharge = myService.getMinPrice();
                        maxServiceCharge = myService.getMaxPrice();
                        minTotal = myService.getMinPrice()+travelCharge;
                        maxTotal = myService.getMaxPrice()+travelCharge;
                        mTotal.setText("RM"+minTotal+" - RM"+maxTotal);
                    }


                }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void setRequestedServiceInfo(){

        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Service");
        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(DataSnapshot child : children) {
                            Service service = child.getValue(Service.class);
                            rservice.setText(requestedService);
                            if(requestedService.equals("Change tyre tube")){
                                radditional.setText(additionalInfo);
                            }
                            else{
                                radditional.setText("NA");
                            }

                            if(service.getServiceName().equalsIgnoreCase("Traveling")){
                                travelCharge = userDistance * service.getMaxPrice();
                                travelCharge = Math.round(travelCharge*100)/100;
                                String ud ="";
                                ud += userDistance;
                                if(userDistance<1000){
                                    ud += "km";
                                }
                                else{
                                    ud += "m";
                                }
                                rtravelCharge.setText("RM"+travelCharge+" ("+ud+")");
                            }

                            if(requestedService.equalsIgnoreCase("Towing")){

                                String sd ="";
                                sd += shopDistance;
                                if(shopDistance<1000){
                                    sd += "km";
                                }
                                else{
                                    sd += "m";
                                }
                                finalServiceCharge = service.getMaxPrice() * shopDistance;
                                finalServiceCharge = Math.round(minServiceCharge*100)/100;

                                rserviceCharge.setText("RM"+finalServiceCharge+" ("+sd+")");
                                finalTotal = finalServiceCharge+travelCharge;
                                rtotal.setText("RM"+finalTotal);
                            }
                            else {
                                rserviceCharge.setText("RM"+finalServiceCharge);
                                finalTotal = finalServiceCharge + travelCharge;
                                rtotal.setText("RM"+finalTotal);
                            }
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addPolylinesToMap(String type, DirectionsResult directionsResult) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (DirectionsRoute route : directionsResult.routes) {
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    if(type.equalsIgnoreCase("Requester")){
                        if(requesterPolyline != null)
                            requesterPolyline.remove();
                        requesterPolyline = null;
                        requesterPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        requesterPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    }
                    else if(type.equalsIgnoreCase("Station")){
                        if(stationPolyline != null)
                            stationPolyline.remove();
                        stationPolyline = null;
                        stationPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        stationPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    }
                    else if(type.equalsIgnoreCase("Shop")){
                        shopPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        shopPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    }


                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v == callShop) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", selectedShop.getShopPhone(), null)));
        }

        if(v == callMechanic) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", requestedMechanic.getMechPhone(), null)));
        }

        if(v == callRescuer) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", gsrRescuer.getMotorcyclistPhone(), null)));
        }

        if(v == callRequester) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", gsrRequester.getMotorcyclistPhone(), null)));
        }


        if(v == goodsamaritanSwitch) {
            if(goodsamaritanSwitch.isChecked()) {
                goodSamaritanStatus = "active";
                String user_id = mAuth.getUid();
                DatabaseReference goodSamaritanRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan");
                GeoFire geoFire = new GeoFire(goodSamaritanRef);
                geoFire.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),new
                        GeoFire.CompletionListener(){
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Do some stuff if you want to
                            }
                        });

            }
            else {
                goodSamaritanStatus = "inactive";
                String user_id = mAuth.getUid();
                DatabaseReference goodSamaritanRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(user_id);
                goodSamaritanRef.removeValue();
            }

        }

        if (v == breakdown){
            Fragment fragment = new MotorcycleListFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(v == accept){
            String user_id = mAuth.getUid();
            requestVM.updateStatus(requestKey,"Negotiation Accepted");
        }
        if(v == decline){
            String user_id = mAuth.getUid();
            requestVM.updateStatus(requestKey,"Negotiation Declined");
            status.setText("Negotiation Declined");
            negotiationInfo.setVisibility(View.GONE);
            declinedButton.setVisibility(View.VISIBLE);
            mMap.clear();
            inRequest = false;
            getNearbyGoodSamaritan();
            displayShops();
            getNearbyPlaces();
            getNearbyGoodSamaritan();
        }

        if(v == declineClose){
            shopRequesterBehavior.setHideable(true);
            shopRequesterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            declinedButton.setVisibility(View.GONE);
            mMap.clear();
            inRequest = false;
            getNearbyGoodSamaritan();
            displayShops();
            getNearbyPlaces();
            getNearbyGoodSamaritan();

        }
        if(v == close){
            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestKey);
            requestRef.child("status").setValue("Completed");
            shopRequesterBehavior.setHideable(true);
            shopRequesterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            completedButton.setVisibility(View.GONE);
            mMap.clear();
            inRequest = false;
            getNearbyGoodSamaritan();
            displayShops();
            getNearbyPlaces();
            getNearbyGoodSamaritan();

        }

        if(v == rfSelect){
            goodsamaritanSwitch.setChecked(false);
            goodSamaritanStatus = "inactive";
            selectingStation.setVisibility(View.GONE);
            selectedStationMarker = stationMarker;
            selectedStation = selectedStationMarker.getTitle();
            String user_id = mAuth.getUid();
            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(gsRequestKey);
            requestRef.child("gsrRescuerId").setValue(user_id);
            requestRef.child("gsrDestination").setValue(selectedStation);
            requestRef.child("gsrDestinationLocation").setValue(selectedStationMarker.getPosition());
            requestRef.child("gsrDistance").setValue(stationDistanceD+requesterDistanceD);
            FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(mAuth.getUid()).removeValue();
            DatabaseReference rescuerRef = FirebaseDatabase.getInstance().getReference().child("Rescuer");
            pushNotificationHelper.sendNotification(gsrRequester.getToken(),"Your request has been accepted!","Please wait while your rescuer is preparing");
            GeoFire geoFire = new GeoFire(rescuerRef);
            geoFire.setLocation(mAuth.getUid(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),new
                    GeoFire.CompletionListener(){
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                        }
                    });
            requestRef.child("gsrStatus").setValue("Request Accepted");

        }

        if(v == rfDecline){
            gsKeyFromNoti = null;
            selectingStation.setVisibility(View.GONE);
        }

        if(v == towHere){
            requestedShop = selectedShop;
            String user_id = mAuth.getUid();
            Session session = new Session(getActivity());
            Motorcycle motorcycle = session.getMotorcycle();
            String requestName = session.getRequest();

            LatLong gsrLocation = new LatLong(mLastLocation.getLatitude(),mLastLocation.getLongitude());

            Map<String, Object> requestData = new HashMap<>();

            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
            String requestKey = requestRef.push().getKey();
            GoodSamaritanRequest gsr = new GoodSamaritanRequest(requestKey,requestName,user_id,motorcycle.getMotorId(),null,shopDistance,
                    selectedShop.getShopId(),"Requesting Help",null,null,gsrLocation,null);

            goodSamaritanRequestVM.createGSR(gsr).observe(this, message -> {
                if(message != null){
                    motorcyclistVM.queryGoodSamaritanLocation(mLastLocation,requestKey,gsr.getGsrName()).observe(this, message2 -> {
                        if(message2 != null){

                        }
                    });
                }
            });

        }

        if(v == gsAccept){
            gsKeyFromNoti = null;
            goodsamaritanSwitch.setChecked(false);
            goodSamaritanStatus = "inactive";
            gsAcceptDeclineButton.setVisibility(View.GONE);
            goodSamaritanRequestVM.acceptGSRTowing(gsRequestKey,mAuth.getUid(),gsrRequester.getToken(),mLastLocation);
            FirebaseDatabase.getInstance().getReference().child("GoodSamaritan").child(mAuth.getUid()).removeValue();

        }
        if(v == gsDecline){
            mMap.clear();
            inRequest = false;
            getNearbyGoodSamaritan();
            displayShops();
            getNearbyPlaces();
            gsAcceptDeclineButton.setVisibility(View.GONE);
            gsKeyFromNoti = null;
            gsResquerBehavior.setHideable(true);
            gsResquerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        if(v == gsHeadOut){
            if(goodSamaritanRequest.getGsrName().equals("Fuel Request")){
                goodSamaritanRequestVM.updateStatus(gsRequestKey, gsrRequester.getToken(),"Rescuer Fetching Fuel");
                pushNotificationHelper.sendNotification(gsrRequester.getToken(),"Your rescuer is heading out!","Sit back until his arrival is informed");
            }
            else{
                goodSamaritanRequestVM.updateStatus(gsRequestKey, gsrRequester.getToken(),"Rescuer On The Way");
                pushNotificationHelper.sendGoodSamaritansNotification(gsrRequester.getToken(),"Your rescuer is heading out!","Sit back until his arrival is informed");
            }
        }

        if(v == gsAcquired){
            FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(goodSamaritanRequest.getGsrId()).child("gsrStatus").setValue("Rescuer On The Way");
            pushNotificationHelper.sendNotification(gsrRequester.getToken(),"Your rescuer is heading out!","Sit back until his arrival is informed");
        }
        if(v == gsFuelDirection){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + selectedStationMarker.getPosition().latitude + "," + selectedStationMarker.getPosition().longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        if(v == gsArrived){
            FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(goodSamaritanRequest.getGsrId()).child("gsrStatus").setValue("Rescuer Arrived");
            pushNotificationHelper.sendNotification(gsrRequester.getToken(),"Your rescuer has arrived!","Look around you");
        }
        if(v == gsDirection){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + goodSamaritanRequest.getGsrLocation().getLatitude() +"," + goodSamaritanRequest.getGsrLocation().getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        if(v == gsDestDirection){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + destinationLatLng.latitude +"," + destinationLatLng.longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        if(v == gsCompleted){
            mMap.clear();
            inRequest = false;
            getNearbyGoodSamaritan();
            displayShops();
            getNearbyPlaces();
            goodSamaritanRequestVM.updateStatus(gsRequestKey,"Completed");
            directionCompletedButton.setVisibility(View.GONE);
            gsrCloseButton.setVisibility(View.VISIBLE);

            /*
            DatabaseReference gsRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritan");
            GeoFire geoFire = new GeoFire(gsRef);
            geoFire.setLocation(mAuth.getUid(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),new
                    GeoFire.CompletionListener(){
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //Do some stuff if you want to
                        }
                    });

             */
        }

        if(v == gsrClose){
            mMap.clear();
            inRequest = false;
            getNearbyGoodSamaritan();
            displayShops();
            getNearbyPlaces();
          gsResquerBehavior.setHideable(true);
          gsResquerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        if(v == gsClose){
            inRequest = false;
            getNearbyGoodSamaritan();
            mMap.clear();
            displayShops();
            getNearbyPlaces();
            gsRequesterBehavior.setHideable(true);
            gsRequesterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        if(v == cancel){
            FirebaseDatabase.getInstance().getReference().child("Request").child(requestKey).removeValue();
            requesting.setVisibility(View.GONE);
        }

        if(v == request) {
            inRequest = true;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    requesting.setVisibility(View.VISIBLE);


                }
            });

            requestedShop = selectedShop;

            String user_id = mAuth.getUid();
            Session session = new Session(getActivity());
            Motorcycle motorcycle = session.getMotorcycle();
            Service service = session.getService();

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("motorcyclistId", user_id);
            requestData.put("motorcycleId", motorcycle.getMotorId());
            requestData.put("shopId", selectedShop.getShopId());
            LatLong latLong = new LatLong(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            requestData.put("LatLong", latLong);
            requestData.put("travelingCharge", travelCharge);
            requestData.put("motorcyclistDistance", userDistance);
            requestData.put("shopDistance", shopDistance);
            String additional = "NA";
            if(service.getServiceName().equals("Change tyre tube")){
                String size = session.getTyreSize();
                additional = "Tyre Size : "+size;
                requestData.put("additionalInfo","Tyre Size: "+size);
            }
            requestData.put("serviceName", service.getServiceName());
            requestData.put("minServiceCharge", minServiceCharge);
            requestData.put("maxServiceCharge", maxServiceCharge);
            requestData.put("finalServiceCharge", finalServiceCharge);
            requestData.put("minTotal", minTotal);
            requestData.put("maxTotal", maxTotal);
            requestData.put("finalTotal", finalTotal);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
            Date date = new Date();
            String startTime = dateFormat.format(date).toString();

            Request request = new Request(null,service.getServiceName(),additional,userDistance,shopDistance,null,null,travelCharge,minServiceCharge,maxServiceCharge,finalServiceCharge,
                    minTotal,maxTotal,finalTotal,"Requesting Help",user_id,motorcycle.getMotorId(),null,selectedShop.getShopId(),latLong);

            requestVM.createRequest(request).observe(this, key -> {
                if(key != null){
                    requestKey = key;
                }
            });

            DatabaseReference dataMechanic = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic");
            Query requestQuery = dataMechanic.orderByChild("shopId").equalTo(selectedShop.getShopId());

            requestQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot child : children) {

                        Mechanic mechanic = child.getValue(Mechanic.class);
                        if(mechanic.getMechStatus().equalsIgnoreCase("Working")){
                            sendMechanicNotification(mechanic);
                        }
                    }

                    }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });







        }
    }

    public void sendMechanicNotification(Mechanic mechanic){


        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", mechanic.getToken());
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title","Someone is requesting your help!");
            notification.put("body", "Click to view this request");
            message.put("notification", notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendGoodSamaritansNotification(String token, String title, String body, String data){


        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title","Someone is requesting your help for "+goodSamaritanRequest.getGsrName());
            notification.put("body", "Tap to view this request");
            JSONObject jsonData = new JSONObject();
            jsonData.put("gsRequestKey",data);
            message.put("notification", notification);
            message.put("data",jsonData);

            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendGoodSamaritansNotification(String token, String title, String body){
        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body", body);
            message.put("notification", notification);

            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // requester listen to request
    public void getShopRequest(){

        String user_id = mAuth.getUid ();

        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        Query requestQuery = requestRef.orderByChild("motorcyclistId").equalTo(user_id);
        requestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {

                    requestStatus = child.child("status").getValue().toString();
                    if(request.equals("Requesting Help") || requestStatus.equals("Negotiation Started") || requestStatus.equals("Request Accepted") || requestStatus.equals("Mechanic Arrived")
                            || requestStatus.equals("Negotiation Accepted")){
                        requestKey = child.getKey();
                        motorcyclistId = child.child("motorcyclistId").getValue().toString();
                        motorcycleId = child.child("motorcycleId").getValue().toString();
                        requestedService = child.child("serviceName").getValue().toString();
                        additionalInfo = null;
                        if(child.child("additionalInfo").exists()){
                            additionalInfo = child.child("additionalInfo").getValue().toString();
                        }
                        travelCharge = Double.valueOf(child.child("travelingCharge").getValue().toString());
                        finalServiceCharge = Double.valueOf(child.child("finalServiceCharge").getValue().toString());
                        userDistance = Double.valueOf(child.child("motorcyclistDistance").getValue().toString());
                        shopDistance = Double.valueOf(child.child("shopDistance").getValue().toString());
                        finalTotal = Double.valueOf(child.child("finalTotal").getValue().toString());
                        pickupLocation = child.child("LatLong").getValue(LatLong.class);
                    }

                    if(requestStatus.equals("Request Accepted")){
                        mMap.clear();
                        String mechId = child.child("mechId").getValue().toString();

                        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId);
                        mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                requestedMechanic = dataSnapshot.getValue(Mechanic.class);
                                requestedMechanic.setMechId(dataSnapshot.getKey());


                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        requesting.setVisibility(View.GONE);
                                        shopRequesterBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        shopRequesterBehavior.setHideable(false);
                                        rname.setText(requestedShop.getShopName());
                                        rdistance.setText(shopDistance+" km");
                                        mechname.setText(requestedMechanic.getMechName());

                                    }
                                });


                                status.setText("Request Accepted");
                                setRequestedServiceInfo();
                                displayMechanicLocation();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    if(requestStatus.equalsIgnoreCase("Negotiation Started")){
                        mMap.clear();

                        String mechId = child.child("mechId").getValue().toString();

                        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId);
                        mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                requestedMechanic = dataSnapshot.getValue(Mechanic.class);
                                requestedMechanic.setMechId(dataSnapshot.getKey());



                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        negotiationInfo.setVisibility(View.VISIBLE);
                                        requesting.setVisibility(View.GONE);
                                        shopRequesterBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        shopRequesterBehavior.setHideable(false);
                                        rname.setText(requestedShop.getShopName());
                                        rdistance.setText(shopDistance+" km");
                                        mechname.setText(requestedMechanic.getMechName());
                                        status.setText("Negotiation Started");
                                        setRequestedServiceInfo();
                                        displayMechanicLocation();

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    if(requestStatus.equalsIgnoreCase("Negotiation Accepted")){

                        String mechId = child.child("mechId").getValue().toString();
                        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId);
                        mechRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                requestedMechanic = dataSnapshot.getValue(Mechanic.class);
                                requestedMechanic.setMechId(dataSnapshot.getKey());

                                negotiationInfo.setVisibility(View.GONE);
                                displayMechanicLocation();
                                shopRequesterBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                shopRequesterBehavior.setHideable(false);
                                setRequestedServiceInfo();
                                status.setText("Negotiation Accepted");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }

                    if(requestStatus.equals("Mechanic On The Way")){
                        mMap.clear();
                        String mechId = child.child("mechId").getValue().toString();

                        mechanicVM.getMechanic(mechId).observe(getActivity(), mechanic -> {
                            requestedMechanic = mechanic;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    requesting.setVisibility(View.GONE);
                                    shopRequesterBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    shopRequesterBehavior.setHideable(false);
                                    rname.setText(requestedShop.getShopName());
                                    rdistance.setText(userDistance+" km");
                                    mechname.setText(requestedMechanic.getMechName());
                                    status.setText("Mechanic On The Way");
                                    setRequestedServiceInfo();
                                    displayMechanicLocation();

                                }
                            });

                        });

                    }

                    if(requestStatus.equalsIgnoreCase("Mechanic Arrived")){

                        shopRequesterBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        shopRequesterBehavior.setHideable(false);

                        if(requestedService.equalsIgnoreCase("Towing")){

                            DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requestKey).child("status");
                            completedRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue().toString().equalsIgnoreCase("Completed")){
                                        status.setText("Status : Completed");
                                        completedButton.setVisibility(View.VISIBLE);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                        else{
                            completedButton.setVisibility(View.VISIBLE);
                        }

                        if(mechMarker != null){
                            mechMarker.remove();
                            mechMarker = null;
                        }

                        status.setText("Status : Mechanic Arrived");

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //rescuer listen to request
    public void goodSamaritansRescuerRequest(){

        if(gsKeyFromNoti != null){
            inRequest = true;
            gsRequestKey = gsKeyFromNoti;
            mMap.clear();

            goodSamaritanRequestVM.getGSR(gsKeyFromNoti).observe(this, gsr-> {
                Toast.makeText(getActivity(), "cik", Toast.LENGTH_SHORT).show();

                if(gsr != null){

                    goodSamaritanRequest = gsr;
                    gsrStatus.setText(goodSamaritanRequest.getGsrStatus());

                    Toast.makeText(getActivity(), "g " +goodSamaritanRequest.getGsrName(), Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    if(goodSamaritanRequest.getGsrName().equals("Towing")){
                        gsResquerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        gsrCloseButton.setVisibility(View.GONE);
                        gsAcceptDeclineButton.setVisibility(View.VISIBLE);
                        shopVM.getShop(goodSamaritanRequest.getGsrDestination()).observe(this, shop -> {
                            requestedShop = shop;
                            shopDestination = shop;
                            destinationLatLng = new LatLng(requestedShop.getShopLocation().getLatitude(),requestedShop.getShopLocation().getLongitude());
                            addMarker("Destination");
                        });

                    }
                    else{
                        mMap.clear();
                        getNearbyPlaces();
                        addMarker("Requester");
                        selectingStation.setVisibility(View.VISIBLE);
                    }
                    Log.d("TAG", "rqstr id :"+goodSamaritanRequest.getGsrRequesterId());
                    motorcyclistVM.getMotorcyclist(goodSamaritanRequest.getGsrRequesterId()).observe(this, requester-> {
                        gsrRequester = requester;
                        Log.d("TAG", "rqstr id :"+goodSamaritanRequest.getGsrRequesterId());
                        Toast.makeText(getActivity(), "rs "+gsrRequester.getMotorcyclistName(), Toast.LENGTH_SHORT).show();
                        motorcycleVM.getMotorcycle(goodSamaritanRequest.getGsrMotorcycleId(), goodSamaritanRequest.getGsrRequesterId()).observe(this, motorcycle -> {
                            gsrRequesterMotor = motorcycle;
                            gsAcceptDeclineButton.setVisibility(View.VISIBLE);

                            if(goodSamaritanRequest.getGsrName().equals("Towing")){
                                addMarker("Requester");
                                displayRequesterInfo(true);


                            }


                        });


                    });
                }
                else{
                    Toast.makeText(getActivity(), "The request no longer exists", Toast.LENGTH_SHORT).show();
                }



            });


        }

        // rescuer listen to request
        String user_id = mAuth.getUid();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        Query rescuerQuery = requestRef.orderByChild("gsrRescuerId").equalTo(user_id);
        rescuerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child : children) {
                    gsRequestStatus = child.child("gsrStatus").getValue().toString();
                    if(gsRequestStatus.equals("Request Accepted") || gsRequestStatus.equals("Rescuer Fetching Fuel")
                            || gsRequestStatus.equals("Rescuer On The Way") || gsRequestStatus.equals("Rescuer Arrived")){

                        goodSamaritanRequest = child.getValue(GoodSamaritanRequest.class);
                        goodSamaritanRequest.setGsrId(child.getKey());
                        gsRequestKey = goodSamaritanRequest.getGsrId();

                        DatabaseReference motorRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist")
                                .child(goodSamaritanRequest.getGsrRequesterId()).child("Motorcycle").child(goodSamaritanRequest.getGsrMotorcycleId());
                        motorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                gsrRequesterMotor = dataSnapshot1.getValue(Motorcycle.class);
                                gsrRequesterMotor.setMotorId(dataSnapshot1.getKey());
                                DatabaseReference requesterRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(goodSamaritanRequest.getGsrRequesterId());
                                requesterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                        if(goodSamaritanRequest.getGsrName().equalsIgnoreCase("Towing")){
                                            gsrRequester = dataSnapshot2.getValue(Motorcyclist.class);
                                            gsrRequester.setMotoryclistId(dataSnapshot2.getKey());

                                            DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(goodSamaritanRequest.getGsrDestination());
                                            shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                    shopDestination = dataSnapshot3.getValue(Shop.class);
                                                    shopDestination.setShopId(dataSnapshot3.getKey());
                                                    goodSamaritanRequest.setGsrDestination(shopDestination.getShopName());


                                                    DatabaseReference shopLocationRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("ShopLocation").child(shopDestination.getShopId()).child("l");
                                                    shopLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {
                                                            double locLat = (double)dataSnapshot4.child("0").getValue();
                                                            double locLong = (double)dataSnapshot4.child("1").getValue();
                                                            LatLong latLong = new LatLong(locLat,locLong);
                                                            shopDestination.setShopLocation(latLong);
                                                            destinationLatLng = new LatLng(locLat,locLong);

                                                            if(gsRequestStatus.equals("Request Accepted")){
                                                                headOutButton.setVisibility(View.VISIBLE);
                                                                displayRequesterInfo(true);
                                                                displayDestinationInfo(false);
                                                                mMap.clear();
                                                                addMarker("Requester");
                                                                addMarker("Destination");
                                                            }

                                                            if(gsRequestStatus.equals("Rescuer On The Way")){
                                                                gsAcceptDeclineButton.setVisibility(View.GONE);
                                                                headOutButton.setVisibility(View.GONE);
                                                                arrivedDirectionButton.setVisibility(View.VISIBLE);
                                                                displayDestinationInfo(false);
                                                                displayRequesterInfo(true);
                                                                mMap.clear();
                                                                addMarker("Requester");
                                                                addMarker("Destination");
                                                            }
                                                            if(gsRequestStatus.equals("Rescuer Arrived")){
                                                                mMap.clear();
                                                                addMarker("Destination");
                                                                displayRequesterInfo(false);
                                                                displayDestinationInfo(true);
                                                                arrivedDirectionButton.setVisibility(View.GONE);
                                                                directionCompletedButton.setVisibility(View.VISIBLE);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });






                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        else{
                                            gsrRequester = dataSnapshot2.getValue(Motorcyclist.class);
                                            gsrRequester.setMotoryclistId(dataSnapshot2.getKey());
                                            destinationLatLng = new LatLng(goodSamaritanRequest.getGsrDestinationLocation().getLatitude(), goodSamaritanRequest.getGsrDestinationLocation().getLongitude());
                                            if(gsRequestStatus.equals("Request Accepted")){
                                                gsAcceptDeclineButton.setVisibility(View.GONE);
                                                headOutButton.setVisibility(View.VISIBLE);
                                                displayRequesterInfo(false);
                                                displayDestinationInfo(true);
                                                mMap.clear();
                                                addMarker("Requester");
                                                addMarker("Station");
                                            }
                                            if(gsRequestStatus.equals("Rescuer Fetching Fuel")){
                                                headOutButton.setVisibility(View.GONE);
                                                fuelAcquiredDirectionButton.setVisibility(View.VISIBLE);
                                                displayRequesterInfo(false);
                                                displayDestinationInfo(true);
                                                mMap.clear();
                                                addMarker("Requester");
                                                addMarker("Station");
                                            }
                                            if(gsRequestStatus.equals("Rescuer On The Way")){
                                                fuelAcquiredDirectionButton.setVisibility(View.GONE);
                                                arrivedDirectionButton.setVisibility(View.VISIBLE);
                                                displayRequesterInfo(true);
                                                mMap.clear();
                                                addMarker("Requester");
                                                addMarker("Station");
                                            }
                                            if(gsRequestStatus.equals("Rescuer Arrived")){
                                                completedButton.setVisibility(View.VISIBLE);
                                                gsCloseButton.setVisibility(View.VISIBLE);
                                                mMap.clear();
                                                addMarker("Station");
                                                displayRequesterInfo(false);
                                                arrivedDirectionButton.setVisibility(View.GONE);
                                                directionCompletedButton.setVisibility(View.VISIBLE);
                                            }
                                        }




                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getGoodSamaritanRequest(){

        String user_id = mAuth.getUid();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest");
        Query rescuerQuery = requestRef.orderByChild("gsrRequesterId").equalTo(user_id);
        rescuerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child : children) {
                    gsRequestStatus = child.child("gsrStatus").getValue().toString();
                    if(gsRequestStatus.equals("Requesting Help") || gsRequestStatus.equals("Request Accepted") || gsRequestStatus.equals("Rescuer Fetching Fuel")
                            || gsRequestStatus.equals("Rescuer On The Way") || gsRequestStatus.equals("Rescuer Arrived")){
                        mMap.clear();
                        gsCloseButton.setVisibility(View.GONE);
                        requesting.setVisibility(View.GONE);
                        goodSamaritanRequest = child.getValue(GoodSamaritanRequest.class);
                        if(!gsRequestStatus.equals("Requesting Help")){
                            if(goodSamaritanRequest.getGsrName().equals("Fuel Request")){
                                destinationLatLng = new LatLng(goodSamaritanRequest.getGsrDestinationLocation().getLatitude(), goodSamaritanRequest.getGsrDestinationLocation().getLongitude());
                            }
                        }


                        goodSamaritanRequest.setGsrId(child.getKey());

                        DatabaseReference motorRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist")
                                .child(goodSamaritanRequest.getGsrRequesterId()).child("Motorcycle").child(goodSamaritanRequest.getGsrMotorcycleId());
                        motorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                gsrRequesterMotor = dataSnapshot1.getValue(Motorcycle.class);
                                gsrRequesterMotor.setMotorId(dataSnapshot1.getKey());

                                if(!goodSamaritanRequest.getGsrStatus().equals("Requesting Help")){
                                    instruction.setVisibility(View.GONE);
                                    DatabaseReference rescuerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(goodSamaritanRequest.getGsrRescuerId());
                                    rescuerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                            if(goodSamaritanRequest.getGsrName().equalsIgnoreCase("Towing")){
                                                gsrRescuer = dataSnapshot2.getValue(Motorcyclist.class);
                                                gsrRescuer.setMotoryclistId(dataSnapshot2.getKey());

                                                DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("User").child("Shop").child(goodSamaritanRequest.getGsrDestination());
                                                shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                        goodSamaritanRequest.setGsrDestination(dataSnapshot3.child("shopName").getValue().toString());

                                                        if(gsRequestStatus.equals("Request Accepted")){
                                                            addMarker("Rescuer");
                                                            displayRescuerInfo();
                                                        }
                                                        if(gsRequestStatus.equals("Arrived")){
                                                            addMarker("Rescuer");
                                                            displayRescuerInfo();
                                                        }
                                                        if(gsRequestStatus.equals("Rescuer On The Way")){
                                                            addMarker("Rescuer");
                                                            displayRescuerInfo();
                                                        }
                                                        if(gsRequestStatus.equals("Rescuer Arrived")){
                                                            addMarker("Rescuer");
                                                            displayRescuerInfo();

                                                            DatabaseReference completedRef= FirebaseDatabase.getInstance().getReference().child("GoodSamaritanRequest").child(goodSamaritanRequest.getGsrId());
                                                            completedRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if(dataSnapshot.child("gsrStatus").getValue().toString().equals("Completed")){
                                                                        gsCloseButton.setVisibility(View.VISIBLE);
                                                                        goodSamaritanRequest.setGsrStatus(dataSnapshot.child("gsrStatus").getValue().toString());
                                                                        displayRescuerInfo();
                                                                        mMap.clear();
                                                                        displayShops();
                                                                        completedRef.removeEventListener(this);
                                                                    }
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
                                            }
                                            else{
                                                mMap.clear();
                                                gsrRescuer = dataSnapshot2.getValue(Motorcyclist.class);
                                                gsrRescuer.setMotoryclistId(dataSnapshot2.getKey());
                                                if(gsRequestStatus.equals("Request Accepted")){
                                                    mMap.clear();
                                                    addMarker("Rescuer");
                                                    addMarker("Station");
                                                    displayRescuerInfo();

                                                }
                                                if(gsRequestStatus.equals("Arrived")){
                                                    mMap.clear();
                                                    addMarker("Rescuer");
                                                    addMarker("Station");
                                                    displayRescuerInfo();
                                                }
                                                if(gsRequestStatus.equals("Rescuer Fetching Fuel")){
                                                    mMap.clear();
                                                    addMarker("Rescuer");
                                                    addMarker("Station");
                                                    displayRescuerInfo();
                                                }
                                                if(gsRequestStatus.equals("Rescuer On The Way")){
                                                    mMap.clear();
                                                    addMarker("Rescuer");
                                                    addMarker("Station");
                                                    displayRescuerInfo();
                                                }
                                                if(gsRequestStatus.equals("Rescuer Arrived")){
                                                    mMap.clear();
                                                    addMarker("Rescuer");
                                                    addMarker("Station");
                                                    displayRescuerInfo();
                                                    gsCloseButton.setVisibility(View.VISIBLE);
                                                    mMap.clear();
                                                    displayShops();
                                                }
                                            }




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





                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Query requestQuery = requestRef.orderByChild("gsrRequesterId").equalTo(user_id);
        requestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children) {


                    gsRequestStatus = child.child("gsrStatus").getValue().toString();

                    if(gsRequestStatus.equalsIgnoreCase("Requesting Help")){
                        gsRequestKey = child.getKey();
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        requesting.setVisibility(View.VISIBLE);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requesting.setVisibility(View.GONE);
                                GoodSamaritanRequestVM goodSamaritanRequestVM = ViewModelProviders.of(getActivity()).get(GoodSamaritanRequestVM.class);
                                goodSamaritanRequestVM.cancelGSR(gsRequestKey);
                            }
                        });



                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void displayRescuerInfo(){

        DatabaseReference rescuerLocationRef = FirebaseDatabase.getInstance().getReference().child("Rescuer").child(gsrRescuer.getMotoryclistId()).child("l");
        rescuerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                double locLat = (double)dataSnapshot.child("0").getValue();
                double locLong = (double)dataSnapshot.child("1").getValue();
                LatLng latLng = new LatLng(locLat,locLong);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Rescuer").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helmet_icon));

                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                        latLng.latitude,
                        latLng.longitude
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
                        requesterDistance = result.routes[0].legs[0].distance.toString();
                        requesterDuration = result.routes[0].legs[0].duration.toString();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(rescuerMarker == null){
                                    rescuerMarker = mMap.addMarker(markerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rescuerMarker.getPosition(), 12));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                }
                                else{
                                    rescuerMarker.setPosition(latLng);
                                }

                                String dis = "";
                                if(goodSamaritanRequest.getGsrDistance() > 100){
                                    dis = goodSamaritanRequest.getGsrDistance()+" m";
                                }
                                else{
                                    dis = goodSamaritanRequest.getGsrDistance()+" km";
                                }
                                gsDistance.setText(dis);
                                gsName.setText(goodSamaritanRequest.getGsrName());
                                gsStatus.setText(goodSamaritanRequest.getGsrStatus());
                                gsDestination.setText(goodSamaritanRequest.getGsrDestination());
                                gsRescuerName.setText("Rescuer : "+gsrRescuer.getMotorcyclistName());
                                gsRescuerPhone.setText(gsrRescuer.getMotorcyclistPhone());
                                gsRescuerDistance.setText(requesterDistance+ " ("+requesterDuration+" away)");
                                gsMotorType.setText(gsrRequesterMotor.getMotorType());
                                gsMotorPlate.setText(gsrRequesterMotor.getMotorId());
                                gsMotorModel.setText(gsrRequesterMotor.getMotorBrand()+" "+gsrRequesterMotor.getmotorModel());
                                gsRequesterBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                gsRequesterBehavior.setHideable(false);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public void displayRequesterInfo(boolean addPolyline){

        LatLng latLng = new LatLng(goodSamaritanRequest.getGsrLocation().getLatitude(), goodSamaritanRequest.getGsrLocation().getLongitude());

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                latLng.latitude,
                latLng.longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );

        directions.alternatives(false);
        directions.origin(origin);
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                requesterDR = result;
                requesterDistance = result.routes[0].legs[0].distance.toString();
                requesterDuration = result.routes[0].legs[0].duration.toString();


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(addPolyline){
                            addPolylinesToMap("Requester",requesterDR);
                        }
                        gsrName.setText(goodSamaritanRequest.getGsrName());
                        gsrStatus.setText(goodSamaritanRequest.getGsrStatus());
                        String dis = "";
                        if(goodSamaritanRequest.getGsrDistance() > 100){
                            dis = goodSamaritanRequest.getGsrDistance()+" m";
                        }
                        else{
                            dis = goodSamaritanRequest.getGsrDistance()+" km";
                        }
                        gsrDistance.setText(dis);
                        gsrRequesterName.setText("Requester : "+gsrRequester.getMotorcyclistName());
                        gsrRequesterPhone.setText(gsrRequester.getMotorcyclistPhone());
                        gsrRequesterDistance.setText(requesterDistance+ " ("+requesterDuration+" away)");
                        if(goodSamaritanRequest.getGsrName().equalsIgnoreCase("Towing")){
                            gsrDestination.setText(shopDestination.getShopName());
                        }
                        else{
                            gsrDestination.setText(goodSamaritanRequest.getGsrDestination());
                        }

                        //gsrDestination.setText(shopDestination.getShopName());
                        gsRescuerName.setText("Rescuer : "+gsrRescuer.getMotorcyclistName());
                        gsrMotorType.setText(gsrRequesterMotor.getMotorType());
                        gsrMotorPlate.setText(gsrRequesterMotor.getMotorId());
                        gsrMotorModel.setText(gsrRequesterMotor.getMotorBrand()+" "+gsrRequesterMotor.getmotorModel());
                        gsResquerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        gsResquerBehavior.setHideable(false);
                    }
                });



            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    public void displayDestinationInfo(boolean addPolyline){
        LatLng latLng = new LatLng(destinationLatLng.latitude,destinationLatLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 12));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude()
        );
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                latLng.latitude,
                latLng.longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);



        directions.alternatives(false);
        directions.origin(origin);
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                destinationDR = result;
                requesterDistance = result.routes[0].legs[0].distance.toString();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(addPolyline){
                            Log.d("TAG" , "masuk addpolyline");

                            if(goodSamaritanRequest.getGsrName().equals("Fuel Request")){
                                addPolylinesToMap("Station",destinationDR);
                            }
                            else{
                                Log.d("TAG" , "masuk fuel request add polyline");
                                addPolylinesToMap("Shop",destinationDR);
                            }

                        }


                        gsrName.setText(goodSamaritanRequest.getGsrName());
                        gsrStatus.setText(goodSamaritanRequest.getGsrStatus());
                        String dis = "";
                        if(goodSamaritanRequest.getGsrDistance() > 1000){
                            dis = goodSamaritanRequest.getGsrDistance()+" m";
                        }
                        else{
                            dis = goodSamaritanRequest.getGsrDistance()+" km";
                        }
                        gsrDistance.setText(dis);
                        gsrRequesterName.setText("Requester : "+gsrRequester.getMotorcyclistName());
                        gsrRequesterDistance.setText(requesterDistance+ " ("+requesterDuration+" away)");
                        gsrMotorType.setText(gsrRequesterMotor.getMotorType());
                        gsrMotorPlate.setText(gsrRequesterMotor.getMotorId());
                        gsrMotorModel.setText(gsrRequesterMotor.getMotorBrand()+" "+gsrRequesterMotor.getmotorModel());
                        gsResquerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        gsResquerBehavior.setHideable(false);


                    }
                });



            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    public void addMarker(String option){

        if(option.equals("Requester")){
                LatLng latLng = new LatLng(goodSamaritanRequest.getGsrLocation().getLatitude(),
                        goodSamaritanRequest.getGsrLocation().getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Requester").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helmet_icon));
                requesterMarker = mMap.addMarker(markerOptions);
                requesterMarker.setTag("Requester");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        if(option.equals("Rescuer")){
            Log.d("TAG", "rsc location : "+goodSamaritanRequest.getGsrLocation().getLongitude()+ " / "+goodSamaritanRequest.getGsrLocation().getLongitude());
            LatLng latLng = new LatLng(goodSamaritanRequest.getGsrLocation().getLatitude(),
                    goodSamaritanRequest.getGsrLocation().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Rescuer").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helmet_icon));
            rescuerMarker = mMap.addMarker(markerOptions);
            rescuerMarker.setTag("Rescuer");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        if(option.equals("Destination")){
                MarkerOptions markerOptions = new MarkerOptions().position(destinationLatLng).title("Your Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
                destinationMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 12));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        if(option.equals("Station")){
            MarkerOptions markerOptions = new MarkerOptions().position(destinationLatLng).title("Petrol Station").icon(BitmapDescriptorFactory.fromResource(R.mipmap.gas_icon));
            selectedStationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }


    }

    public void displayMechanicLocation(){

        Log.d("TAG", "masuk 1");
        String mechId = requestedMechanic.getMechId();
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference().child("User").child("Mechanic").child(mechId).child("mechLocation").child("l");
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double locLat = (double)dataSnapshot.child("0").getValue();
                double locLong = (double)dataSnapshot.child("1").getValue();
                LatLng latLng = new LatLng(locLat,locLong);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your mechanic").icon(BitmapDescriptorFactory.fromResource(R.mipmap.mechanic_icon));


                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                        locLat,
                        locLong
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

                        String dis = result.routes[0].legs[0].distance.toString();
                        userDuration = result.routes[0].legs[0].duration.toString();
                        if(dis.contains("km")){
                            dis = dis.replaceAll(" km","");
                            shopDistance = Double.parseDouble(dis);
                        }
                        else {
                            dis = dis.replaceAll(" m","");
                            shopDistance = Double.parseDouble(dis);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                markerOptions.snippet(result.routes[0].legs[0].distance +" ("+result.routes[0].legs[0].duration+")");
                                mechMarker = mMap.addMarker(markerOptions);
                                /*if(mechMarker == null){

                                    Log.d("TAG", "masuk 2");
                                    mechMarker = mMap.addMarker(markerOptions);
                                }
                                else{
                                    mechMarker.setPosition(latLng);
                                }

                                 */

                            }
                        });





                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        session = new Session(getActivity());
        session.clearSession();
        super.onDetach();

    }


}

