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

    // field 선언부
    Button renew; // 새로고침 버튼
    double latitude; // 위도
    double longitude; // 경도
    LocationListener locationListener; // 위치변화 감지를 위한 LocationListener
    LocationManager locationManager; // 위치서비스 시스템 관리를 위한 LocationManager
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private String locationAddress; // 주소
    private Location location; // location
    private StringBuilder sb = new StringBuilder(); // TM좌표, 주소, 측정소 정보를 임시로 저장하고 미세먼지 정보를 출력하기 위한 StringBuilder
    private String key =
            "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D"; // 공공데이터 API 인증키

    // fragment가 return될 때 실행되는 메소드
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // 초기 화면을 "air"으로 설정
        final View view = inflater.inflate(R.layout.air, container, false);

        // 버튼 클릭시 실행 여부를 다시 확인하는 AlertDialog 생성
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // 제목설정(생략가능)
        alertDialogBuilder.setTitle("Alert");

        // AlertDialog 설정
        alertDialogBuilder
                .setMessage("renew?") // 출력할 메시지(생략가능)
                .setCancelable(true) // true일 때 AlertDialog 창 밖을 터치하면 창이 꺼지도록 설정(생략시 default는 true)
                .setPositiveButton("renew", // PositiveButton 이름을 "renew"로 설정
                        new DialogInterface.OnClickListener() { // 리스너 생성
                            public void onClick(DialogInterface dialog, int id) { // AlertDialog에서 renew를 선택하면
                                // 위치서비스를 통해 WGS84 위경도 좌표 획득하여 location에 저장
                                location = getLocation();
                                // WGS84 위경도 좌표를 통해 주소(읍면동) 획득
                                new AddressAsyncTask().execute(view);
                                // 주소(읍면동)을 통해 TM좌표 획득
                                new TMAsyncTask().execute(view);
                                // TM좌표를 통해 인근측정소 정보 획득
                                new MeasuringStationAsyncTask().execute(view);
                                // 인근측정소 정보를 통해 대기오염정보 획득, TextView에 출력
                                new AirAsyncTask().execute(view);
                            }
                        }
                )
                .setNegativeButton("cancel", // NegativeButton의 이름을 "cancel"로 설정
                        new DialogInterface.OnClickListener() { // 리스너 생성
                            public void onClick( // AlertDialog에서 cancel을 선택하면
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소
                                dialog.cancel();
                                // "Update cancelled" 라는 Toast 메시지를 짧게 출력
                                Toast.makeText(getActivity(),
                                        "Update cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        // AlertDialog 생성
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // 새로고침 버튼 초기화
        renew = view.findViewById(R.id.button);

        // 버튼을 클릭하면 AlertDialog 출력
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        return view;
    }

    // 위치서비스를 통해 WGS84 위경도 좌표를 획득하는 메소드
    Location getLocation(){
        // 권한이 허용되어 있지 않으면
        if(ContextCompat.checkSelfPermission (Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission (getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            // 이전에 사용자가 "ACCESS_COARSE_LOCATION" 권한을 거부한 적이 있으면
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                // 권한이 필요한 이유를 설명
                Toast.makeText(getActivity(),
                        "정확한 위치정보를 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 권한 요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS);
            }
            // 이전에 사용자가 "ACCESS_FINE_LOCATION" 권한을 거부한 적이 있으면
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                // 권한이 필요한 이유를 설명
                Toast.makeText(getActivity(),
                        "정확한 위치정보를 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 권한 요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            // 권한 요청이 처음이라면
            else{
                // 권한 요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS);
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        // 권한이 허용되어 있으면
        else{
            // 위치정보 요청
            locationManager =
                    (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            // GPS 프로바이더 사용가능여부
            boolean isGPSEnabled =
                    Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 네트워크 프로바이더 사용가능여부
            boolean isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // GPS, 네트워크 프로바이더 사용가능여부 확인 Log
            Log.d(TAG, "isGPSEnabled="+ isGPSEnabled);
            Log.d(TAG, "isNetworkEnabled="+ isNetworkEnabled);

            // GPS, 네트워크 프로바이더 둘다 사용이 불가능한 경우
            if(!(isGPSEnabled | isNetworkEnabled)){
                Toast.makeText(getActivity(), "위치서비스를 활성화 해주세요", Toast.LENGTH_SHORT).show();
            }
            // 사용가능한 프로바이더가 있는 경우
            else {
                // "location'을 프로바이더에서 얻은 위치로 갱신
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // LocationListener 선언
                locationListener = new LocationListener() {

                    // 위치 변화가 감지되면 위경도 갱신
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

                // 위치정보를 얻기 위해 LocationListener를 LocationManager에 등록
                // 1초(1000ms)마다, 10m마다 갱신
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        1000, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, 10, locationListener);
            }
        }
        return location;
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 해당 메소드가 존재하는 버전일 경우
        if (Build.VERSION.SDK_INT >= 23) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS:
                    // 요청이 거절되면 length=0
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                        //resume tasks needing this permission
                        break;
                    }
                    else{
                        Toast.makeText(getActivity(),
                                "권한이 거부된 상태입니다.", Toast.LENGTH_SHORT).show();
                    }

                case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    // 요청이 거절되면 length=0
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                        //resume tasks needing this permission
                        break;
                    }
                    else{
                        Toast.makeText(getActivity(),
                                "권한이 거부된 상태입니다.", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    String getAddressFromLocation(Location location, Locale locale) {
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(getActivity(), locale);

        // 지오코더를 이용하여 주소 리스트를 구합니다.

        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        } catch (IOException e) {
            Toast.makeText(getActivity(),
                    "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }

        // 주소 리스트가 비어있는지 확인합니다. 비어 있으면, 주소 대신 그것이 없음을 알리는 문자열을 리턴합니다.

        if (1 > addressList.size()) {
            return "해당 위치에 주소 없음" ;
        }

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
    class AddressAsyncTask extends AsyncTask<View, String, String> {

        AddressAsyncTask(){

        }

        @Override
        protected String doInBackground(View... views) {
            locationAddress = getAddressFromLocation(location, Locale.KOREA);
            return locationAddress;
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

        @Override
        protected String doInBackground(View... views) {

            boolean bsggName, bumdName, btmX, btmY;
            bsggName = false; bumdName = false; btmX = false; btmY = false;

            String sggName, umdName, tmX  , tmY;
            sggName = null; umdName = null; tmX = null; tmY = null;

            connectURL();
            parse(bsggName, bumdName, btmX, btmY, sggName, umdName, tmX , tmY);

            return null;
        }

        private void connectURL() {

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

        private void parse(boolean bsggName, boolean bumdName, boolean btmX, boolean btmY,
                             String sggName, String umdName, String tmX , String tmY) {
            XmlPullParser parser;
            int parserEvent;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parserEvent = parser.getEventType();
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

        @Override
        protected String doInBackground(View... views) {

            boolean bstationName = false;
            String stationName = null;
            connectURL();
            sb.delete(0, sb.length());
            parse(bstationName,stationName);
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        private void connectURL() {

            urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"); //URL
            try {
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(key); //서비스 키
                if(sb.toString().split("").length >= 2) {
                    urlBuilder.append("&").append(URLEncoder.encode("tmX", "UTF-8")).append("=").append(URLEncoder.encode(sb.toString().split(" ")[0], "UTF-8")); //읍면동
                    urlBuilder.append("&").append(URLEncoder.encode("tmY", "UTF-8")).append("=").append(URLEncoder.encode(sb.toString().split(" ")[1], "UTF-8")); //페이지 수
                }
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

        private void parse(boolean bstationName, String stationName) {
            XmlPullParser parser;
            int parserEvent;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parserEvent = parser.getEventType();

                parser.setInput(url.openStream(), null);

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                            if (parser.getName().equals("stationName")) { //stationName(측정소 이름) 만나면 내용을 받을수 있게 하자
                                bstationName = true;
                            }
                            break;

                        case XmlPullParser.TEXT://parser가 내용에 접근했을때
                            if (bstationName) { //bsggName true일 때 태그의 내용을 저장.
                                stationName = parser.getText();
                                bstationName = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                sb.append(stationName).append(" ");
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
            if(sb.length()!=0)
            sb.deleteCharAt(sb.length()-1);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class AirAsyncTask extends AsyncTask<View, String, String>{

        AirAsyncTask(){

        }

        private StringBuilder urlBuilder;
        private URL url;
        private HttpURLConnection conn;
        private BufferedReader rd;
        TextView airView;
        boolean bdataTime = false; boolean bpm10Value = false; boolean bpm10Grade1h = false;
        boolean bpm25Value = false; boolean bpm25Grade1h = false;
        String dataTime = null; String pm10Value = null; String pm10Grade1h = null;
        String pm25Value = null; String pm25Grade1h = null;

        @Override
        protected String doInBackground(View... views) {

            airView = views[0].findViewById(R.id.airView);

            connectURL();
            return(parse());
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.length()!=0) {
                airView.setText(s);
                Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(), "해당 API 오류로 새로고침에 실패하였습니다.", Toast.LENGTH_LONG).show();
            }
        }

        private void connectURL() {

            urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); //URL
            try {
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(key); //서비스 키
                urlBuilder.append("&").append(URLEncoder.encode("stationName", "UTF-8")).append("=").append(URLEncoder.encode(sb.toString().split(" ")[0], "UTF-8")); //측정소명
                urlBuilder.append("&").append(URLEncoder.encode("dataTerm", "UTF-8")).append("=").append(URLEncoder.encode("DAILY", "UTF-8")); //데이터기간
                urlBuilder.append("&").append(URLEncoder.encode("ver", "UTF-8")).append("=").append(URLEncoder.encode("1.3", "UTF-8")); //버전
                urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8")); //받아올 데이터 개수
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

        private String parse() {
            XmlPullParser parser;
            int parserEvent;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parserEvent = parser.getEventType();

                parser.setInput(url.openStream(), null);

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                            if (parser.getName().equals("dataTime")) { //dataTime(측정시간) 만나면 내용을 받을수 있게 하자
                                bdataTime = true;
                            }
                            if (parser.getName().equals("pm10Value")) { //pm10Value(미세먼지 농도) 만나면 내용을 받을수 있게 하자
                                bpm10Value = true;
                            }
                            if (parser.getName().equals("pm10Grade1h")) { //pm10Grade1h(미세먼지 등급) 만나면 내용을 받을수 있게 하자
                                bpm10Grade1h = true;
                            }
                            if (parser.getName().equals("pm25Value")) { //pm25Value(초미세먼지 농도) 만나면 내용을 받을수 있게 하자
                                bpm25Value = true;
                            }
                            if (parser.getName().equals("pm25Grade1h")) { //pm25Grade1h(초미세먼지 등급) 만나면 내용을 받을수 있게 하자
                                bpm25Grade1h = true;
                            }
                            break;

                        case XmlPullParser.TEXT://parser가 내용에 접근했을때
                            if (bdataTime) { //bsggName true일 때 태그의 내용을 저장.
                                dataTime = parser.getText();
                                bdataTime = false;
                            }
                            if (bpm10Value) { //bsggName true일 때 태그의 내용을 저장.
                                pm10Value = parser.getText();
                                bpm10Value = false;
                            }
                            if (bpm10Grade1h) { //bsggName true일 때 태그의 내용을 저장.
                                pm10Grade1h = parser.getText();
                                bpm10Grade1h = false;
                            }
                            if (bpm25Value) { //bsggName true일 때 태그의 내용을 저장.
                                pm25Value = parser.getText();
                                bpm25Value = false;
                            }
                            if (bpm25Grade1h) { //bsggName true일 때 태그의 내용을 저장.
                                pm25Grade1h = parser.getText();
                                bpm25Grade1h = false;
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                switch(Integer.parseInt(pm10Grade1h)) {
                                    case 1: pm10Grade1h = "좋음"; break;
                                    case 2: pm10Grade1h = "보통"; break;
                                    case 3: pm10Grade1h = "나쁨"; break;
                                    case 4: pm10Grade1h = "매우나쁨"; break;
                                }
                                switch(Integer.parseInt(pm25Grade1h)) {
                                    case 1: pm25Grade1h = "좋음"; break;
                                    case 2: pm25Grade1h = "보통"; break;
                                    case 3: pm25Grade1h = "나쁨"; break;
                                    case 4: pm25Grade1h = "매우나쁨"; break;
                                }
                                    sb.append(dataTime).append(" 기준\n미세먼지농도: ").append(pm10Value).append("\n미세먼지등급: ").append(pm10Grade1h)
                                            .append("\n초미세먼지농도: ").append(pm25Value).append("\n초미세먼지등급: ").append(pm25Grade1h);
                            }
                            break;
                    }
                    try {
                        parserEvent = parser.next();
                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
    }

}
