package com.jaewoo.forgetaboutit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.constraint.Constraints.TAG;

public class Air extends Fragment {

    public Air() {

    }

    TextView textView;
    Button button;
    double latitude;
    double longitude;
    LocationListener locationListener;
    LocationManager locationManager;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.air, container, false);

        textView = (TextView) view.findViewById(R.id.airView);
        button = (Button) view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission (getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){//권한이 허용되어있으면
                    if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        Log.d(TAG, "권한 없음1");
                    }
                    else{
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        Log.d(TAG, "권한 없음2");
                    }
                }
                else{
                    //위치정보 요청 후
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    // GPS 프로바이더 사용가능여부
                    isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    // 네트워크 프로바이더 사용가능여부
                    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    Log.d(TAG, "isGPSEnabled="+ isGPSEnabled);
                    Log.d(TAG, "isNetworkEnabled="+ isNetworkEnabled);

                    if((isGPSEnabled|isNetworkEnabled)==false){
                        Toast.makeText(getActivity(), "위치서비스를 활성화 해주세요", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        locationListener = new LocationListener() {

                            public void onLocationChanged(Location location) {
                                longitude = location.getLongitude(); //경도
                                latitude = location.getLatitude(); //위도
                                textView.setText("위도: " + latitude + "\n경도: " + longitude);
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
                        };

                        // Register the listener with the Location Manager to receive location updates
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                        Log.d(TAG, "NP 됨");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Log.d(TAG, "GP 됨");

                        /*// 수동으로 위치 구하기
                        String locationProvider = LocationManager.GPS_PROVIDER;
                        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                        if (lastKnownLocation != null) {
                            double lng = lastKnownLocation.getLatitude();
                            double lat = lastKnownLocation.getLatitude();
                            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
                        }*/
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                //resume tasks needing this permission
            }
        }
    }
}
