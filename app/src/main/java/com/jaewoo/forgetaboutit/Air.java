package com.jaewoo.forgetaboutit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Air extends Fragment{

    public Air(){

    }

    Location location = null;
    TextView textView;
    double latitude;
    double longitude;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.air, container, false);

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates( locationListener );    // Stop the update if it is in progress.


        if(permissionCheck == PackageManager.PERMISSION_DENIED && permissionCheck2==PackageManager.PERMISSION_DENIED){
            // 권한 없음
             return view;
        }else {

            textView = (TextView) view.findViewById(R.id.airView);
            locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER , 0, 0, locationListener );
            textView.setText(latitude + " " + longitude);
        }

        return view;
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionCheck2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

            if(permissionCheck == PackageManager.PERMISSION_DENIED && permissionCheck2==PackageManager.PERMISSION_DENIED){
                // 권한 없음
                return;
            }else {
                // 권한 있음
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else {
                    return;
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

}
