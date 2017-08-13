package com.crm.sharepointevo.bootcamplocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    private Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Looper looper;
    private Criteria criteria;
    private EditText zipcode;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    InputMethodManager inputMethodManager;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container,false);
        mapView = (MapView) view.findViewById(R.id.mapview);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView = (RecyclerView) view.findViewById(R.id.rvBottom);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LocationAdapter(getNearBootCampLocations());
        recyclerView.setAdapter(adapter);

        RelativeLayout bottomsheet = (RelativeLayout) view.findViewById(R.id.bottomsheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        zipcode = (EditText) view.findViewById(R.id.etZipCode);
        zipcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) &&
                event.getAction() == KeyEvent.ACTION_DOWN)){
                    hideKeyboard(v);
                    ShowNearBootCampLocations();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        getCurrentLocation();
        return view;
    }

    private void hideKeyboard(View view) {
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.setMinZoomPreference(17);
    }

    private void setMap() {
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    public void getCurrentLocation() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                setMap();
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

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 1);
            return;
        }
        else {
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
        }
        looper = null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);
                }
                break;
        }
    }

    public void ShowNearBootCampLocations(){

        ArrayList<BootCamp> bootCamps = getNearBootCampLocations();

        for (BootCamp b: bootCamps) {
            LatLng location = new LatLng(b.getLatitude(), b.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(b.getTitle())
                    .snippet(b.getSubtitle())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    public ArrayList<BootCamp> getNearBootCampLocations(){

        ArrayList<BootCamp> bootCamps = new ArrayList<>();

        bootCamps.add(new BootCamp(123.886609,10.302441,
                "Queensland", "V Rama Ave, Cebu City"));
        bootCamps.add(new BootCamp(123.886563, 10.302314,
                "Frobel iSchool", "V. Rama Ave, Lucio Lopez, Cebu City"));
        bootCamps.add(new BootCamp(123.887778, 10.300299,
                "USC South Campus", "J. Alcantara St. Lungsod, Cebu City"));

        return bootCamps;
    }
}
