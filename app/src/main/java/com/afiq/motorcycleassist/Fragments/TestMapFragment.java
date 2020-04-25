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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.gohn.nativedialog.ButtonType;
import com.gohn.nativedialog.CanceledListener;
import com.gohn.nativedialog.CustomViewClickListener;
import com.gohn.nativedialog.NDialog;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;


public class TestMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions marker;
    Location mLastLocation;
    LocationManager locationManager;
    LocationListener locationListener;
    LocationRequest mLocationRequest;
    final int LOCATION_REQUEST_CODE = 1;
    SupportMapFragment mapFragment;
    private GeoApiContext mGeoApiContext = null;
    String TAG ="Test";

    private BottomSheetBehavior mBottomSheetBehavior;

    public TestMapFragment() {
        // Required empty public constructor
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_test_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        Button buttonExpand = rootView.findViewById(R.id.button_expand);
        Button buttonCollapse = rootView.findViewById(R.id.button_collapse);

        buttonExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NDialog nDialog = new NDialog(getActivity(), ButtonType.NO_BUTTON);
                nDialog.setTitle("Requesting help");
                nDialog.setMessage("Sit back and wait for your rescue");
                nDialog.setCustomView(R.layout.cancel_floating_sheet);
                nDialog.setCustomViewClickListener(new CustomViewClickListener() {
                    @Override
                    public void onClickView(View v) {
                        switch (v.getId()) {
                            case R.id.cancel:
                                Log.d(TAG, "Custom View Text Clicked");
                                nDialog.dismiss();
                                break;
                        }

                    }
                });
                nDialog.isCancelable(false); // default : true
                nDialog.setCanceledListener(new CanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "Dialog Canceled");
                    }


                });
                nDialog.show();
            }
        });




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

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }

        getDeviceLocation();


    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {



        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation =location;
                DatabaseReference shopLocationRef = FirebaseDatabase.getInstance().getReference().child("ShopLocation");
                GeoFire geoFire = new GeoFire(shopLocationRef);
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10);
                geoQuery.removeAllListeners();

                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Toast.makeText(getActivity(),"key",Toast.LENGTH_LONG).show();
                        displayShop();
                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {
                        Toast.makeText(getActivity(),"ready",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });

                /*
                DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(mAuth.getUid());
                GeoFire geoFire = new GeoFire(mechRef);
                geoFire.setLocation("motorcyclistLocation", new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),new
                        GeoFire.CompletionListener(){
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Do some stuff if you want to
                            }
                        });*/

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

    public void displayShop(){

        DatabaseReference shopLocationRef = FirebaseDatabase.getInstance().getReference().child("ShopLocation");
        GeoFire geoFire = new GeoFire(shopLocationRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                LatLng latLng = new LatLng(location.latitude,
                        location.longitude);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child("Shop");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                        for(DataSnapshot child : children) {
                            Shop shop = child.getValue(Shop.class);
                            if(shop != null){
                                shop.setShopId(child.getKey());

                                marker = new MarkerOptions();
                                marker.position(latLng).title(shop.getShopName());
                                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.shop_icon));
                                Marker m = mMap.addMarker(marker);
                                m.setTag(shop);
                                m.hideInfoWindow();
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        if(marker.getTag() instanceof Shop){
                                            Shop shop = (Shop)(marker.getTag());
                                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
