package com.ssudio.julofeature;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.ssudio.julofeature.address.model.Residence;
import com.ssudio.julofeature.permission.PermissionUtility;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAddressActivity extends FragmentActivity
        implements android.location.LocationListener, OnMapReadyCallback {

    public static final int GET_ADDRESS_FROM_POSITION = 1;

    private GoogleMap googleMap;
    private boolean gmapLoaded = true;
    private Address selectedAddress;

    @BindView(R.id.mainContainer)
    protected View mainContainer;
    @BindView(R.id.txtAddress)
    protected TextView txtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        setupMap();

        ButterKnife.bind(this);
    }

    private void setupMap() {
        try {
            if (googleMap == null) {
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.userMap);

                mapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            Snackbar
                    .make(mainContainer, "Unable to setup map, closing activity", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (gmapLoaded) {
            setCenter(location.getLatitude(), location.getLongitude());

            gmapLoaded = false;
        }
    }

    private void setCenter(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        this.googleMap = gmap;

        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (googleMap != null) {
                    googleMap.clear();
                }

                googleMap.addMarker(new MarkerOptions()
                        .position(googleMap.getCameraPosition().target));
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                selectedAddress = geocodeAddress(googleMap.getCameraPosition().target);

                if (selectedAddress != null) {
                    txtAddress.setText(selectedAddress.getAddressLine(0));
                }
            }
        });

        initializeCurrentLocation();
    }

    private void initializeCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PermissionUtility.JULO_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {
            onLocationChanged(location);
        } else {
            Snackbar.make(mainContainer, "Cannot determine current location", Snackbar.LENGTH_SHORT).show();
        }

        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
    }

    private Address geocodeAddress(LatLng pos) {
        Geocoder geoCoder = new Geocoder(this);

        List<Address> matches;
        Address bestMatch;

        try {
            matches = geoCoder.getFromLocation(pos.latitude, pos.longitude, 1);

            bestMatch = (matches.isEmpty() ? null : matches.get(0));
        } catch (IOException e) {
            bestMatch = null;
        }

        return bestMatch;
    }

    @OnClick(R.id.btnConfirm)
    protected void btnConfirmClicked() {
        Residence result = new Residence();

        if (selectedAddress != null) {
            result.setCountry(selectedAddress.getCountryName());
            result.setCity(selectedAddress.getAdminArea());
            result.setDistrict(selectedAddress.getSubAdminArea());
            result.setSubDistrict(selectedAddress.getLocality());
            result.setStreetName(selectedAddress.getAddressLine(0));
            result.setPostalCode(selectedAddress.getPostalCode());
        }

        Intent returnIntent = new Intent();

        returnIntent.putExtra("userResidence", new Gson().toJson(result));

        setResult(Activity.RESULT_OK,returnIntent);

        finish();
    }
}
