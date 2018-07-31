package com.jaewoo.forgetaboutit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class Air extends Fragment {

    public Air() {

    }

    Button button;
    double latitude;
    double longitude;
    LocationListener locationListener;
    LocationManager locationManager;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private String locationAddress;
    Location location;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.air, container, false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // 제목셋팅-생략가능
        alertDialogBuilder.setTitle("Alert");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("renew?")//생략가능
                .setCancelable(true) //생략하면 default true
                .setPositiveButton("renew",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                getLocation();
                                new LocationAsyncTask().execute(view); //주소(읍면동)
                                new TMAsyncTask().execute(view); //TM좌표
                                new MeasuringStationAsyncTask().execute(view); //측정소
                            }
                        }
                )
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                                Toast.makeText(getActivity(), "Update cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        final AlertDialog alertDialog = alertDialogBuilder.create();

        button = view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        return view;
    }

    void getLocation(){
        if(ContextCompat.checkSelfPermission (Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
            boolean isGPSEnabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 네트워크 프로바이더 사용가능여부
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(TAG, "isGPSEnabled="+ isGPSEnabled);
            Log.d(TAG, "isNetworkEnabled="+ isNetworkEnabled);

            if(!(isGPSEnabled | isNetworkEnabled)){
                Toast.makeText(getActivity(), "위치서비스를 활성화 해주세요", Toast.LENGTH_SHORT).show();
            }
            else {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                locationListener = new LocationListener() {

                    public void onLocationChanged(Location location) {
                        longitude = location.getLongitude(); //경도
                        latitude = location.getLatitude(); //위도
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
            }
        }
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
        List<Address> addressList;
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
            Toast.makeText(getActivity(), "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
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

    @SuppressLint("StaticFieldLeak")
    class LocationAsyncTask extends AsyncTask<View, String, String> {

        LocationAsyncTask(){

        }

        @Override
        protected String doInBackground(View... views) {
            locationAddress = getAddressFromLocation(location, Locale.KOREA);
            return "위도: " + latitude + "\n경도: " + longitude+"\n"
                    +locationAddress;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class TMAsyncTask extends AsyncTask<View, String, String> {//미세먼지

        TMAsyncTask() {

        }

        private StringBuilder urlBuilder;
        private URL url;
        private HttpURLConnection conn;
        private BufferedReader rd;
        private StringBuilder sb = new StringBuilder();

        @Override
        protected String doInBackground(View... views) {

            boolean bsggName, bumdName, btmX, btmY;
            bsggName = false; bumdName = false; btmX = false; btmY = false;

            String sggName, umdName, tmX  , tmY;
            sggName = null; umdName = null; tmX = null; tmY = null;

            getInfo();

            return(parse(bsggName, bumdName, btmX, btmY, sggName, umdName, tmX , tmY));
        }

        private void getInfo() {
            String key = "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D";

            urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt"); //URL
            try {
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(key); //서비스 키
                urlBuilder.append("&").append(URLEncoder.encode("umdName", "UTF-8")).append("=").append(URLEncoder.encode(locationAddress.split(" ")[3], "UTF-8")); //읍면동
                urlBuilder.append("&").append(URLEncoder.encode("pageNum", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8")); //페이지 수
                urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("999", "UTF-8")); //줄 수
                url = new URL(urlBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                rd.close();
                conn.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String parse(boolean bsggName, boolean bumdName, boolean btmX, boolean btmY,
                             String sggName, String umdName, String tmX , String tmY) {
            XmlPullParser parser;
            int parserEvent;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parserEvent = parser.getEventType();
                //factory.setNamespaceAware(true); //되면 테스트해보기 (없어도 되는지)

                parser.setInput(url.openStream(), null);

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                            if (parser.getName().equals("sggName")) { //sggName(시군구이름) 만나면 내용을 받을수 있게 하자
                                bsggName = true;
                            }
                            if (parser.getName().equals("umdName")) { //umdName(읍면동이름) 만나면 내용을 받을수 있게 하자
                                bumdName = true;
                            }
                            if (parser.getName().equals("tmX")) { //tmX(TM X좌표) 만나면 내용을 받을수 있게 하자
                                btmX = true;
                            }
                            if (parser.getName().equals("tmY")) { //tmY(TM Y좌표) 만나면 내용을 받을수 있게 하자
                                btmY = true;
                            }
                            break;

                        case XmlPullParser.TEXT://parser가 내용에 접근했을때
                            if (bsggName) { //bsggName true일 때 태그의 내용을 저장.
                                sggName = parser.getText();
                                bsggName = false;
                            }
                            if (bumdName) { //bumdName true일 때 태그의 내용을 저장.
                                umdName = parser.getText();
                                bumdName = false;
                            }
                            if (btmX) { //btmX true일 때 태그의 내용을 저장.
                                tmX = parser.getText();
                                btmX = false;
                            }
                            if (btmY) { //btmY true일 때 태그의 내용을 저장.
                                tmY = parser.getText();
                                btmY = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item") && sggName.equals(locationAddress.split(" ")[2]) && umdName.equals(locationAddress.split(" ")[3])){
                                sb.append(tmX).append(" ").append(tmY);
                            }
                            break;
                    }
                    try {
                        parserEvent = parser.next();
                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class MeasuringStationAsyncTask extends AsyncTask<View, String, String>{

        MeasuringStationAsyncTask(){

        }

        private StringBuilder urlBuilder;
        private URL url;
        private HttpURLConnection conn;
        private BufferedReader rd;
        private StringBuilder sb = new StringBuilder();
        TextView airView;

        @Override
        protected String doInBackground(View... views) {

            airView = views[0].findViewById(R.id.airView);

            boolean bsidoName, bsggName, bumdName, btmX, btmY;
            bsidoName = false; bsggName = false; bumdName = false; btmX = false;
            btmY = false;

            String sidoName, sggName, umdName, tmX  , tmY;
            sidoName = null; sggName = null; umdName = null; tmX = null;
            tmY = null;

            getInfo();

            return(parse(bsidoName,bsggName, bumdName, btmX, btmY,sidoName, sggName, umdName, tmX , tmY));
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            airView.setText(s); Log.d(TAG, url.toString());
            Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
        }

        private void getInfo() {
            String key = "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D";

            urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt"); //URL
            try {
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(key); //서비스 키
                urlBuilder.append("&").append(URLEncoder.encode("umdName", "UTF-8")).append("=").append(URLEncoder.encode(locationAddress.split(" ")[3], "UTF-8")); //읍면동
                urlBuilder.append("&").append(URLEncoder.encode("pageNum", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8")); //페이지 수
                urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("999", "UTF-8")); //줄 수
                url = new URL(urlBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                rd.close();
                conn.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String parse(boolean bsidoName,boolean bsggName, boolean bumdName, boolean btmX, boolean btmY,
                             String sidoName, String sggName, String umdName, String tmX , String tmY) {
            XmlPullParser parser;
            int parserEvent;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parserEvent = parser.getEventType();
                //factory.setNamespaceAware(true); //되면 테스트해보기 (없어도 되는지)

                parser.setInput(url.openStream(), null);

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                            if (parser.getName().equals("sidoName")) { //sidoName(시도이름) 만나면 내용을 받을수 있게 하자
                                bsidoName = true;
                            }
                            if (parser.getName().equals("sggName")) { //sggName(시군구이름) 만나면 내용을 받을수 있게 하자
                                bsggName = true;
                            }
                            if (parser.getName().equals("umdName")) { //umdName(읍면동이름) 만나면 내용을 받을수 있게 하자
                                bumdName = true;
                            }
                            if (parser.getName().equals("tmX")) { //tmX(TM X좌표) 만나면 내용을 받을수 있게 하자
                                btmX = true;
                            }
                            if (parser.getName().equals("tmY")) { //tmY(TM Y좌표) 만나면 내용을 받을수 있게 하자
                                btmY = true;
                            }
                            break;

                        case XmlPullParser.TEXT://parser가 내용에 접근했을때
                            if (bsidoName) { //bsidoName true일 때 태그의 내용을 저장.
                                sidoName = parser.getText();
                            }
                            if (bsggName) { //bsggName true일 때 태그의 내용을 저장.
                                sggName = parser.getText();
                                bsggName = false;
                            }
                            if (bumdName) { //bumdName true일 때 태그의 내용을 저장.
                                umdName = parser.getText();
                                bumdName = false;
                            }
                            if (btmX) { //btmX true일 때 태그의 내용을 저장.
                                tmX = parser.getText();
                                btmX = false;
                            }
                            if (btmY) { //btmY true일 때 태그의 내용을 저장.
                                tmY = parser.getText();
                                btmY = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item") && sggName.equals(locationAddress.split(" ")[2]) && umdName.equals(locationAddress.split(" ")[3])) {
                                sb.append(tmX).append(" ").append(tmY);
                            }
                            break;
                    }
                    try {
                        parserEvent = parser.next();
                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }
    }

}
