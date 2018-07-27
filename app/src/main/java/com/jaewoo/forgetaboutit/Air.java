package com.jaewoo.forgetaboutit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
                                textView.setText("위도: " + latitude + "\n경도: " + longitude+"\n"
                                        +getAddressFromLocation(location, Locale.KOREA));
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
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

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

    String getAddressFromLocation(Location location, Locale locale) {
        List<Address> addressList = null ;
        Geocoder geocoder = new Geocoder( getActivity(), locale);

        //------------------------------------------------------------------
        // 지오코더를 이용하여 주소 리스트를 구합니다.

        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        } catch (IOException e) {
            Toast. makeText( getActivity(), "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }

        //------------------------------------------------------------------
        // 주소 리스트가 비어있는지 확인합니다. 비어 있으면, 주소 대신 그것이 없음을 알리는 문자열을 리턴합니다.

        if (1 > addressList.size()) {
            return "해당 위치에 주소 없음" ;
        }

        //------------------------------------------------------------------
        // 주소를 담는 문자열을 생성하고 리턴합니다.

        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }
}
