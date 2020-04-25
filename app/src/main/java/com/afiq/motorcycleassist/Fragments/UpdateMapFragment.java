package com.afiq.motorcycleassist.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Models.LatLong;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.ShopVM;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;


public class UpdateMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{
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

    private Button set,wait;

    private FirebaseAuth mAuth;
    public UpdateMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_update_map, container, false);

        mAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        set = (Button)rootView.findViewById(R.id.setLocation);
        set.setOnClickListener(this);
        wait = (Button)rootView.findViewById(R.id.wait);
        return rootView;
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
                displayShop();
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }

    @Override
    public void onClick(View v) {

        if(v == set){
            LatLong L = new LatLong(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude);
            ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
            shopVM.setLocation(L).observe(this, message -> {
                if(message == null){
                    Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new ShopAccountFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

            });
        }
    }

    public void displayShop(){

        ShopVM shopVM = ViewModelProviders.of(this).get(ShopVM.class);
        shopVM.getShop(mAuth.getUid()).observe(this, shop -> {
            if(shop != null){
                LatLng latLng = new LatLng(shop.getShopLocation().getLatitude(),shop.getShopLocation().getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
                mMap.addMarker(markerOptions);
            }

        });





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

        markerOptions = new MarkerOptions();

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                set.setVisibility(View.GONE);
                wait.setVisibility(View.VISIBLE);
                wait.setEnabled(false);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                set.setVisibility(View.VISIBLE);
                wait.setVisibility(View.GONE);
            }
        });

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
    }
}
