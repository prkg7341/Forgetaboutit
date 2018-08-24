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

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.jaewoo.forgetaboutit.Setting.st1;
import static com.jaewoo.forgetaboutit.Setting.st2;
import static com.jaewoo.forgetaboutit.Setting.st3;

public class Air extends Fragment {

    // 생성자 선언
    public Air() {
        // 아무 기능도 없지만, 안드로이드에서 생성자 선언을 해주지 않을 때, 오류가 발생할 가능성이 있어 기능없이 선언해준다.
    }

    // field 선언부
    Button renew; // 새로고침 버튼
    TextView airView; // DB에 저장된 내용 출력할 TextView
    double latitude; // 위도
    double longitude; // 경도
    LocationListener locationListener; // 위치변화 감지를 위한 LocationListener
    LocationManager locationManager; // 위치서비스 시스템 관리를 위한 LocationManager
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS = 1; // 권한 구분
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2; // 권한 구분
    private String locationAddress; // 주소 저장
    public static Location location; // 위치정보 저장
    private StringBuilder sb = new StringBuilder(); // TM좌표, 주소, 측정소 정보를 임시로 저장하고 미세먼지 정보를 출력하기 위한 문자열 생성 객체
    boolean isGPSEnabled; // GPS로 위치서비스 사용가능여부 판별
    boolean isNetworkEnabled; // Network로 위치서비스 사용가능여부 판별
    private String key =
            "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D"; // 공공데이터 API 인증키
    DataBase dataBase; // DataBase 클래스 객체
    String address;
    // 필드에서 선언된 now값을 현재시간으로 초기화

    // fragment가 return될 때 실행되는 메소드
        public View onCreateView(@NonNull LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState) {

        // 초기 화면을 "air"으로 설정
        final View view = inflater.inflate(R.layout.air, container, false);

        // DB가 존재하지 않으면 생성
        dataBase = DataBase.openDB(getActivity());
        // DB가 처음 생성되어 데이터가 없으면, 새로고침을 눌러달라는 메시지 출력
        if(dataBase.count("Air")==0){
            dataBase.insert("Air", "입력된 ","데이터가 ","없습니다. ","새로고침 ","버튼을 ", "눌러주세요", "\n ");
        }

        // airView 초기화
        airView = view.findViewById(R.id.airView);

        String st = dataBase.select("Air");

        // 데이터가 존재하면 airView에 데이터 출력
        if(st!=null) {
            String temp = st.split("\n")[0];
            String str1 = temp.split("-")[0];
            String str2 = temp.split("-")[1];
            String str3 = temp.split("-")[2];
            String str4 = temp.split("-")[3];
            String str5 = temp.split("-")[4];
            String str6 = temp.split("-")[5];
            String result;
            if(str1.length()==10) {
                 result = str1.substring(2, 4) + "월 "
                        + str1.substring(4, 6) + "일 "
                        + str1.substring(6, 8) + "시 "
                        + str1.substring(8, 10) + "분 기준\n"
                        + "미세먼지 농도: " + str2 + "\n"
                        + "미세먼지 등급: " + str3 + "\n"
                        + "초미세먼지 농도: " + str4 + "\n"
                        + "초미세먼지 등급: " + str5 + "\n"
                        + str6.substring(2, 4) + "월 "
                        + str6.substring(4, 6) + "일 "
                        + str6.substring(6, 8) + "시 "
                        + str6.substring(8, 10) + "분에 업데이트되었습니다.";
            }
            else{
                result = st;
            }

            // TextView에 리턴받은 값을 출력한다.
            airView.setText(result);
        }

        /*if(Main.userLocation!=null) {
            Main.userLocation.setText(address);
        }
        else{
            Main.userLocation.setText("선택된 위치가 없습니다.");
        }*/

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
                                location=null;
                                locationAddress=null;
                                address = null;
                                getLocation(view);
                                if(location!=null){
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
    void getLocation(View view){
        // 권한이 허용되어 있지 않으면
        if(ContextCompat.checkSelfPermission (Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission (getActivity(),
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
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // GPS 프로바이더 사용가능여부
            isGPSEnabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 네트워크 프로바이더 사용가능여부
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // GPS, 네트워크 프로바이더 둘다 사용이 불가능한 경우
            if(!(isGPSEnabled | isNetworkEnabled)){
                location=null;
                locationAddress=null;
                address = null;
                // 주소(읍면동)을 통해 TM좌표 획득
                new TMAsyncTask().execute(view);
                // TM좌표를 통해 인근측정소 정보 획득
                new MeasuringStationAsyncTask().execute(view);
                // 인근측정소 정보를 통해 대기오염정보 획득, TextView에 출력
                new AirAsyncTask().execute(view);
            }
            // 사용가능한 프로바이더가 있는 경우
            else {
                // "location'을 프로바이더에서 얻은 위치로 갱신
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // LocationListener 생성
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
    }

    // 권한 유무 확인 및 권한 요청
    // 따로 호출하지 않아도 권한이 필요할 경우 프로그램이 자동으로 호출
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 해당 메소드가 존재하는 버전일 경우
        if (Build.VERSION.SDK_INT >= 23) {
            // 권한 구분
            switch (requestCode) {
                // requestCode가 REQUEST_ACCESS_COARSE_LOCATIONS 일때
                case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS:
                    // 요청이 거절되면 length=0
                    // "ACCESS_COARSE_LOCATIONS" 권한이 허용되어 있으면
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(),
                                "MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS 권한이 허용되어있습니다.", Toast.LENGTH_SHORT).show();
                        // resume tasks needing this permission
                        break;
                    }
                    // "ACCESS_COARSE_LOCATIONS" 권한이 거부되어 있으면
                    else{
                        Toast.makeText(getActivity(),
                                "MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATIONS 권한이 거부된 상태입니다.", Toast.LENGTH_LONG).show();
                    }
                    break;

                // requestCode가 REQUEST_ACCESS_FINE_LOCATION 일때
                case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    // 요청이 거절되면 length=0
                    // "MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION" 권한이 허용되어 있으면
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(),
                                "MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION 권한이 허용되어있습니다.", Toast.LENGTH_SHORT).show();
                        // resume tasks needing this permission
                        break;
                    }
                    // "MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION" 권한이 거부되어 있으면
                    else{
                        Toast.makeText(getActivity(),
                                "MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION 권한이 거부된 상태입니다.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    // getLocation 메소드에서 얻은 "location"을 인자로 주소를 얻는 메소드를 포함하는 클래스
    class AddressAsyncTask extends AsyncTask<View, String, String> {

        // 생성자 선언
        AddressAsyncTask(){
            // 아무 기능도 없지만, 안드로이드에서 생성자 선언을 해주지 않을 때, 오류가 발생할 가능성이 있어 기능없이 선언해준다.
        }

        // doInBackground는 상위 캘래스의 view를 인자로 받아 AsyncTask 클래스의 메인 메소드 역할을 하며
        // UI와 관련되지 않은 작업들을 포함한다. (UI 관련 작업은 onPostExecute 메소드에서 실행)
        @Override
        protected String doInBackground(View... views) {
            // getLocation 메소드에서 얻은 "location" 과 Locale 값인 "KOREA"를 인자로 주소를 얻어 "locationAddress"에 저장한다.
            // Locale은 어플리케이션이 실행되는 지역을 나타내며, 이 어플리케이션은 한국 내에서만 실행될 예정이므로
            // KOREA로 한정하여 메소드를 실행한다.
            locationAddress = getAddressFromLocation(location, Locale.KOREA);
            if(locationAddress!=null) {
                address = locationAddress;
            }
            return null; // onPostExecute 메소드를 사용하지 않으므로 return 값이 필요하지 않다.
        }

        // 위치를 이용해 주소를 구하는 메소드
        String getAddressFromLocation(Location location, Locale locale) {
            // 주소 리스트 생성
            List<Address> addressList;
            // 위경도를 이용해 주소를 구하기 위해 Geocoder 생성
            // (Geocoder는 주소로 위경도를 구하거나, 위경도로 주소를 구하는 기능을 가진 Library)
            // Geocoder 객체 생성 후, getFromLocation 메소드를 통해 위경도를 얻는다.
            // Geocoder 객체 생성
            Geocoder geocoder = new Geocoder(getActivity(), locale);

            // Geocoder 객체를 이용하여 주소 리스트 갱신
            try {
                // getFromLocation 메소드 실행
                addressList = geocoder.getFromLocation(
                        // 위도
                        location.getLatitude(),
                        // 경도
                        location.getLongitude(),
                        // 결과값의 개수
                        1
                );
            }
            // getFromLocation 메소드 실행 중 오류 처리
            catch (IOException e) {
                // 오류 발생 시 Toast 메시지 출력
                // Toast.makeText(getActivity(),
                //        "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
                // 에러 메세지의 발생 근원지를 찾아 단계별로 에러 출력 (가장 자세히)
                e.printStackTrace();
                // 에러 메시지 리턴
                return "주소 인식 불가" ;
            }

            // 주소 리스트가 비어있는지 확인 후, 비어 있으면 에러 메시지 리턴
            if (1 > addressList.size()) {
                return "해당 위치에 주소 없음" ;
            }

            // 주소를 담는 문자열을 생성하고 리턴
            // address에 주소 리스트에 저장되어있던 값을 저장
            Address address = addressList.get(0);
            // 주소를 저장할 StringBuilder 생성
            StringBuilder addressStringBuilder = new StringBuilder();

            // 주소 사이마다 "\n"(줄변환)을 입력하여 구분하여 StringBuilder에 저장
            for (int i=0;  i<=address.getMaxAddressLineIndex() ; i++) {
                addressStringBuilder.append(address.getAddressLine(i));
                if (i < address.getMaxAddressLineIndex())
                    addressStringBuilder.append("\n");
            }

            // StringBuilder에 저장되어있던 값을 String 형으로 변환하여 리턴
            return addressStringBuilder.toString();
        }
    }

    @SuppressLint("StaticFieldLeak")
    // AddressAsyncTask 클래스에서 얻은 주소를 인자로 TM좌표를 얻는 메소드를 포함하는 클래스
    class TMAsyncTask extends AsyncTask<View, String, String> {

        // 생성자 선언
        TMAsyncTask() {
            // 아무 기능도 없지만, 안드로이드에서 생성자 선언을 해주지 않을 때, 오류가 발생할 가능성이 있어 기능없이 선언해준다.
        }

        // 네트워크에 연결할 URL 선언
        URL url;

        // doInBackground는 상위 캘래스의 view를 인자로 받아 AsyncTask 클래스의 메인 메소드 역할을 하며
        // UI와 관련되지 않은 작업들을 포함한다. (UI 관련 작업은 onPostExecute 메소드에서 실행)
        @Override
        protected String doInBackground(View... views) {

            if(address==null){
                address = st1 + "-" + st2 + "-" + st3;
            }
            Log.d("address는 ", address);
            buildURL(); // URL 생성
            parse(); // 파싱
            return null;
        }

        // URL을 생성하는 메소드
        private void buildURL() {

            // 공공데이터 API를 사용할 때 필요한 형태로 URL을 생성하기 위해 StringBuilder 생성
            StringBuilder urlBuilder = new StringBuilder(
                    "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt"); //URL
            // encode 메소드를 실행할 때 필수적인 입출력 예외처리를 해주기 위해 try-catch 문 사용
            // "UTF-8"은 유니코드를 위한 가변 길이 문자 인코딩 방식 중 하나 (공공데이터 API 요구사항)
            try {
                // 각각 항목명에 대한 데이터를 입력해준다.
                // 서비스 키 입력
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8"))
                        .append("=").append(key);
                // 읍,면,동 입력 (split(" ")을 통해 index 순으로 국가[0] - 시,도[1] - 시,군,구[2] - 읍,면,동[3] 순의 배열 생성)
                if(locationAddress!=null && locationAddress.split(" ").length>=4) {
                    Log.d("여기","위치서비스 기반 ");
                    urlBuilder.append("&").append(URLEncoder.encode("umdName", "UTF-8"))
                            .append("=").append(URLEncoder.encode(locationAddress.split(" ")[3], "UTF-8"));
                }
                else{Log.d("여기","사용자 설정 지역 기반");
                    urlBuilder.append("&").append(URLEncoder.encode("umdName", "UTF-8"))
                            .append("=").append(URLEncoder.encode(st3, "UTF-8"));
                }
                // 페이지 수 입력 (데이터를 한 화면에 모두 나타내기 위함)
                urlBuilder.append("&").append(URLEncoder.encode("pageNum", "UTF-8"))
                        .append("=").append(URLEncoder.encode("1", "UTF-8"));
                // 줄 수 입력 (데이터를 한 화면에 모두 나타내기 위함)
                urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8"))
                        .append("=").append(URLEncoder.encode("999", "UTF-8"));
                // StringBuilder로 생성한 문자열을 URL 형태로 변환 및 저장
                url = new URL(urlBuilder.toString());
                if(locationAddress!=null) {
                    Log.d("location", locationAddress);
                }
                else{
                    Log.d("location", st1 + " " + st2 + " " + st3);
                }
            }
            // 입출력 예외처리
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 공공데이터 파싱을 위한 메소드
        private void parse() {

            // xml 형태의 데이터를 파싱하는 XmlPullParser 객체 생성
            XmlPullParser parser;
            // 파싱의 작업상태를 나타내는 변수 선언
            int parserEvent;

            // 파싱을 할 때 조건문에 필요한 boolean 형 데이터 선언 및 false로 초기화
            boolean bsggName, bumdName, btmX, btmY;
            bsggName = false; bumdName = false; btmX = false; btmY = false;

            // 파싱에 필요한 String 형 데이터 선언 및 null로 초기화
            String sggName, umdName, tmX  , tmY;
            sggName = null; umdName = null; tmX = null; tmY = null;

            try {
                // XmlPullParserFactory 객체 생성 (XmlPullParser 객체 초기화를 위함)
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // XmlPullParser 객체 초기화 (파싱 객체 선언의 약속된 형태)
                parser = factory.newPullParser();
                // 파싱의 작업상태 초기화
                parserEvent = parser.getEventType();
                // 파싱할 데이터를 가져올 URL 스트림 생성
                parser.setInput(url.openStream(), null);

                // 파싱 작업이 끝날때까지
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    // 파싱의 작업상태에 따라 구분
                    switch (parserEvent) {
                        // parser가 시작 태그를 만날 때 실행
                        case XmlPullParser.START_TAG:
                            // parser가 sggName(시군구이름)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("sggName")) {
                                bsggName = true;
                            }
                            // parser가 umdName(읍면동이름)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("umdName")) {
                                bumdName = true;
                            }
                            // parser가 tmX(TM X좌표)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("tmX")) {
                                btmX = true;
                            }
                            // parser가 tmY(TM Y좌표)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("tmY")) {
                                btmY = true;
                            }
                            break;

                        // parser가 내용에 접근할 때 실행
                        case XmlPullParser.TEXT:
                            // bsggName이 true일 때 태그의 내용을 저장.
                            if (bsggName) {
                                sggName = parser.getText();
                                bsggName = false;
                            }
                            // bumdName이 true일 때 태그의 내용을 저장.
                            if (bumdName) {
                                umdName = parser.getText();
                                bumdName = false;
                            }
                            // btmX가 true일 때 태그의 내용을 저장.
                            if (btmX) {
                                tmX = parser.getText();
                                btmX = false;
                            }
                            // btmY가 true일 때 태그의 내용을 저장.
                            if (btmY) {
                                tmY = parser.getText();
                                btmY = false;
                            }
                            break;

                        // parser가 끝 태그를 만날 때 실행
                        case XmlPullParser.END_TAG:
                            // parser가 "item" 태그를 만나고 시군구이름과 읍면동이름이 전에 구한 주소값과 같을 때 TM좌표 저장
                            if(locationAddress!=null && locationAddress.split(" ").length>=4) {
                                if(locationAddress.split(" ")[2]!=null) {
                                    if (parser.getName().equals("item") && sggName.equals(locationAddress.split(" ")[2])
                                            && umdName.equals(locationAddress.split(" ")[3])) {
                                        sb.append(tmX).append(" ").append(tmY);
                                    }
                                }
                                else{
                                    if (parser.getName().equals("item") && sggName.equals("세종시")
                                            && umdName.equals(locationAddress.split(" ")[3])) {
                                        sb.append(tmX).append(" ").append(tmY);
                                    }
                                }
                            }
                            else{
                                if (parser.getName().equals("item") && sggName.equals(st2)
                                        && umdName.equals(st3)) {
                                    sb.append(tmX).append(" ").append(tmY);
                                }
                            }
                            break;
                    }
                    // parser를 다음 데이터로 이동
                    try {
                        parserEvent = parser.next();
                    }
                    // xml 파싱과 입출력 관련 예외 처리
                    catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // xml 파싱과 입출력 관련 예외 처리
            catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    // TMAsyncTask 클래스에서 얻은 TM좌표를 인자로 측정소 정보를 얻는 메소드를 포함하는 클래스
    class MeasuringStationAsyncTask extends AsyncTask<View, String, String>{

        // 생성자 선언
        MeasuringStationAsyncTask(){
            // 아무 기능도 없지만, 안드로이드에서 생성자 선언을 해주지 않을 때, 오류가 발생할 가능성이 있어 기능없이 선언해준다.
        }

        // 네트워크에 연결할 URL 선언
        private URL url;

        // doInBackground는 상위 캘래스의 view를 인자로 받아 AsyncTask 클래스의 메인 메소드 역할을 하며
        // UI와 관련되지 않은 작업들을 포함한다. (UI 관련 작업은 onPostExecute 메소드에서 실행)
        @Override
        protected String doInBackground(View... views) {

            buildURL(); // URL 생성
            sb.delete(0, sb.length()); // parse 메소드에서 사용하기 위해 StringBuilder에서 TM 좌표 데이터 삭제
            parse(); // 파싱
            return null; // onPostExecute 메소드를 사용하지 않으므로 return 값이 필요하지 않다.
        }

        // URL을 생성하는 메소드
        private void buildURL() {

            // 공공데이터 API를 사용할 때 필요한 형태로 URL을 생성하기 위해 StringBuilder 생성
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"); //URL
            // encode 메소드를 실행할 때 필수적인 입출력 예외처리를 해주기 위해 try-catch 문 사용
            // "UTF-8"은 유니코드를 위한 가변 길이 문자 인코딩 방식 중 하나 (공공데이터 API 요구사항)
            try {
                // 각각 항목명에 대한 데이터를 입력해준다.
                // 서비스 키 입력
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8"))
                        .append("=").append(key);
                // TM 좌표 데이터가 StringBuilder에 입력되어있으면
                if(sb.toString().split(" ").length >= 2) {
                    // TM X좌표 입력
                    urlBuilder.append("&").append(URLEncoder.encode("tmX", "UTF-8"))
                            .append("=").append(URLEncoder.encode(sb.toString().split(" ")[0], "UTF-8")); //읍면동
                    // TM Y좌표 입력
                    urlBuilder.append("&").append(URLEncoder.encode("tmY", "UTF-8"))
                            .append("=").append(URLEncoder.encode(sb.toString().split(" ")[1], "UTF-8")); //페이지 수
                }
                // url 저장
                url = new URL(urlBuilder.toString());
            }
            // 입출력 예외처리
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 공공데이터 파싱을 위한 메소드
        private void parse() {

            // xml 형태의 데이터를 파싱하는 XmlPullParser 객체 생성
            XmlPullParser parser;
            // 파싱의 작업상태를 나타내는 변수 선언
            int parserEvent;

            // 파싱을 할 때 조건문에 필요한 데이터 선언 및 초기화
            boolean bstationName = false;
            String stationName = null;

            try {
                // XmlPullParserFactory 객체 생성 (XmlPullParser 객체 초기화를 위함)
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // XmlPullParser 객체 초기화 (파싱 객체 선언의 약속된 형태)
                parser = factory.newPullParser();
                // 파싱의 작업상태 초기화
                parserEvent = parser.getEventType();
                // 파싱할 데이터를 가져올 URL 스트림 생성
                parser.setInput(url.openStream(), null);

                // 파싱 작업이 끝날때까지, 한번만 실행되도록
                while (parserEvent != XmlPullParser.END_DOCUMENT && (sb.length()==0)) {
                    // 파싱의 작업상태에 따라 구분
                    switch (parserEvent) {
                        // parser가 시작 태그를 만날 때 실행
                        case XmlPullParser.START_TAG:
                            // parser가 stationName(측정소 이름)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("stationName")) {
                                bstationName = true;
                            }
                            break;

                        // parser가 내용에 접근할 때 실행
                        case XmlPullParser.TEXT:
                            // bstationName이 true일 때 태그의 내용을 저장.
                            if (bstationName) {
                                stationName = parser.getText();
                                bstationName = false;
                            }
                            break;

                        // parser가 끝 태그를 만날 때 실행
                        case XmlPullParser.END_TAG:
                            // parser가 "item" 태그를 만나면 측정소 이름 저장
                            if (parser.getName().equals("item")) {
                                sb.append(stationName);
                            }
                            break;
                    }
                    try {
                        // parser를 다음 데이터로 이동
                        parserEvent = parser.next();
                    }
                    // xml 파싱과 입출력 관련 예외 처리
                    catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // xml 파싱과 입출력 관련 예외 처리
            catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            Log.d("여기", sb.toString());
        }
    }

    // MeasuringStationAsyncTask 클래스에서 얻은  측정소 정보를 인자로 미세먼지 데이터를 얻는 클래스
    @SuppressLint("StaticFieldLeak")
    class AirAsyncTask extends AsyncTask<View, String, String>{

        // 생성자 선언
        AirAsyncTask(){
            // 아무 기능도 없지만, 안드로이드에서 생성자 선언을 해주지 않을 때, 오류가 발생할 가능성이 있어 기능없이 선언해준다.
        }

        // 네트워크에 연결할 URL 선언
        private URL url;
        // 현재 시간을 저장할 String 변수 선언

        // doInBackground는 상위 캘래스의 view를 인자로 받아 AsyncTask 클래스의 메인 메소드 역할을 하며
        // UI와 관련되지 않은 작업들을 포함한다. (UI 관련 작업은 onPostExecute 메소드에서 실행)
        @Override
        protected String doInBackground(View... views) {

            buildURL(); // URL 생성
            updateDB(parse()); // 파싱한 문자열을 이용해 데이터를 업데이트 한다.

            return(dataBase.select("Air")); // SQL SELECT 문을 통해 정보를 읽어온다.
        }

        // doInBackground 메소드에서 리턴한 값을 인자로 UI를 수정하는 메소드
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // 리턴받은 값이 올바른 형태로 저장되어 있을 때
            if(s.split("-")[5].split("\n")[0].compareTo(new SimpleDateFormat("yyMMddHHmm",Locale.KOREA).format(new Date()))==0){
                String temp = s.split("\n")[0];
                String str1 = temp.split("-")[0];
                String str2 = temp.split("-")[1];
                String str3 = temp.split("-")[2];
                String str4 = temp.split("-")[3];
                String str5 = temp.split("-")[4];
                String str6 = temp.split("-")[5];
                String result = str1.substring(2,4) + "월 "
                        + str1.substring(4,6) + "일 "
                        + str1.substring(6,8) + "시 "
                        + str1.substring(8,10) + "분 기준\n"
                        + "미세먼지 농도: " + str2 + "\n"
                        + "미세먼지 등급: " + str3 + "\n"
                        + "초미세먼지 농도: " + str4 + "\n"
                        + "초미세먼지 등급: " + str5 + "\n"
                        + str6.substring(2,4) + "월 "
                        + str6.substring(4,6) + "일 "
                        + str6.substring(6,8) + "시 "
                        + str6.substring(8,10) + "분에 업데이트되었습니다.";

                // TextView에 리턴받은 값을 출력한다.
                airView.setText(result);

                String string =  dataBase.select("Air").split("\n")[1];
                if(string.split(" ").length>2){
                    if(address.split(" ")[2].compareTo(string.split(" ")[2])==0){
                        // 리턴받은 값을 출력한 후, Updated successfully 메시지를 보여준다.
                        Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                if(string.split("-").length>2){
                    if(address.split("-")[2].compareTo(string.split("-")[2])==0){
                        // 리턴받은 값을 출력한 후, Updated successfully 메시지를 보여준다.
                        Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if(Main.all!=null) {
                if(address.split("-").length==3) {
                    String location = address.split("-")[0] + " " + address.split("-")[1] + " " + address.split("-")[2];
                    Main.all.setText("현재위치: " + location);
                }
                else{
                    Main.all.setText("현재위치: " + address);
                }
            }
            else{
                Main.all.setText("선택된 위치가 없습니다.");
            }
        }

        // URL을 생성하는 메소드
        private void buildURL() {

            // 공공데이터 API를 사용할 때 필요한 형태로 URL을 생성하기 위해 StringBuilder 생성
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); //URL
            // encode 메소드를 실행할 때 필수적인 입출력 예외처리를 해주기 위해 try-catch 문 사용
            // "UTF-8"은 유니코드를 위한 가변 길이 문자 인코딩 방식 중 하나 (공공데이터 API 요구사항)
            try {
                // 각각 항목명에 대한 데이터를 입력해준다.
                // 서비스 키 입력
                urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8"))
                        .append("=").append(key);
                // 측정소명 입력
                urlBuilder.append("&").append(URLEncoder.encode("stationName", "UTF-8"))
                        .append("=").append(URLEncoder.encode(sb.toString().split(" ")[0], "UTF-8"));
                // 데이터 기간 입력
                urlBuilder.append("&").append(URLEncoder.encode("dataTerm", "UTF-8"))
                        .append("=").append(URLEncoder.encode("DAILY", "UTF-8"));
                // 버전 입력
                urlBuilder.append("&").append(URLEncoder.encode("ver", "UTF-8"))
                        .append("=").append(URLEncoder.encode("1.3", "UTF-8"));
                // 받아올 데이터 개수 입력
                urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8"))
                        .append("=").append(URLEncoder.encode("1", "UTF-8"));
                // url 저장
                url = new URL(urlBuilder.toString());

            }
            // 입출력 예외처리
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 공공데이터 파싱을 위한 메소드
        private String parse() {

            // xml 형태의 데이터를 파싱하는 XmlPullParser 객체 생성
            XmlPullParser parser;
            // 파싱의 작업상태를 나타내는 변수 선언
            int parserEvent;

            // 파싱을 할 때 조건문에 필요한 데이터 선언 및 초기화
            boolean bdataTime = false; boolean bpm10Value = false; boolean bpm10Grade1h = false;
            boolean bpm25Value = false; boolean bpm25Grade1h = false;
            String dataTime = null; String pm10Value = null; String pm10Grade1h = null;
            String pm25Value = null; String pm25Grade1h = null;

            try {
                // XmlPullParserFactory 객체 생성 (XmlPullParser 객체 초기화를 위함)
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // XmlPullParser 객체 초기화 (파싱 객체 선언의 약속된 형태)
                parser = factory.newPullParser();
                // 파싱의 작업상태 초기화
                parserEvent = parser.getEventType();
                // 파싱할 데이터를 가져올 URL 스트림 생성
                parser.setInput(url.openStream(), null);

                // 파싱 작업이 끝날때까지
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    // 파싱의 작업상태에 따라 구분
                    switch (parserEvent) {
                        // parser가 시작 태그를 만날 때 실행
                        case XmlPullParser.START_TAG:
                            // parser가 dataTime(측정시간)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("dataTime")) {
                                bdataTime = true;
                            }
                            // parser가 pm10Value(미세먼지농도)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("pm10Value")) {
                                bpm10Value = true;
                            }
                            // parser가 pm10Grade1h(미세먼지등급)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("pm10Grade1h")) {
                                bpm10Grade1h = true;
                            }
                            // parser가 pm25Value(초미세먼지농도)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("pm25Value")) {
                                bpm25Value = true;
                            }
                            // parser가 pm25Grade1h(초미세먼지등급)을 만나면 내용 저장이 가능하도록
                            if (parser.getName().equals("pm25Grade1h")) {
                                bpm25Grade1h = true;
                            }
                            break;

                        // parser가 내용에 접근할 때 실행
                        case XmlPullParser.TEXT:
                            // bdataTime이 true일 때 태그의 내용을 저장.
                            if (bdataTime) {
                                dataTime = parser.getText();
                                bdataTime = false;
                            }
                            // bpm10Value가 true일 때 태그의 내용을 저장.
                            if (bpm10Value) {
                                pm10Value = parser.getText();
                                bpm10Value = false;
                            }
                            // bpm10Grade1h가 true일 때 태그의 내용을 저장.
                            if (bpm10Grade1h) {
                                pm10Grade1h = parser.getText();
                                bpm10Grade1h = false;
                            }
                            // bpm25Value가 true일 때 태그의 내용을 저장.
                            if (bpm25Value) {
                                pm25Value = parser.getText();
                                bpm25Value = false;
                            }
                            // bpm25Grade1h가 true일 때 태그의 내용을 저장.
                            if (bpm25Grade1h) {
                                pm25Grade1h = parser.getText();
                                bpm25Grade1h = false;
                            }
                            break;

                        // parser가 끝 태그를 만날 때 실행
                        case XmlPullParser.END_TAG:
                            try{
                                // parser가 "item" 태그를 만나면 측정소 이름 저장
                                if (parser.getName().equals("item")) {
                                    // 미세먼지등급에 따른 분류 (공공데이터 자체 기준)
                                    if(!(pm10Value.compareTo("-")==0 || pm10Value==null)) {
                                        switch (Integer.parseInt(pm10Grade1h)) {
                                            case 1:
                                                pm10Grade1h = "좋음";
                                                break;
                                            case 2:
                                                pm10Grade1h = "보통";
                                                break;
                                            case 3:
                                                pm10Grade1h = "나쁨";
                                                break;
                                            case 4:
                                                pm10Grade1h = "매우나쁨";
                                                break;
                                        }
                                    }
                                    else{
                                        pm10Grade1h = null;
                                        pm10Value = null;
                                    }
                                    // 초미세먼지등급에 따른 분류 (공공데이터 자체 기준)
                                    if(!(pm25Value.compareTo("-")==0 || pm25Value==null)) {
                                        switch (Integer.parseInt(pm25Grade1h)) {
                                            case 1:
                                                pm25Grade1h = "좋음";
                                                break;
                                            case 2:
                                                pm25Grade1h = "보통";
                                                break;
                                            case 3:
                                                pm25Grade1h = "나쁨";
                                                break;
                                            case 4:
                                                pm25Grade1h = "매우나쁨";
                                                break;
                                        }
                                    }
                                    else{
                                        pm25Grade1h = null;
                                        pm25Value = null;
                                    }
                                    // StringBuilder로 DB에 입력할 데이터 생성
                                    if(pm10Grade1h==null || pm25Grade1h==null || pm10Value==null || pm25Value==null){
                                        new MakeToast("미세먼지 정보 API 오류로 새로고침에 실패하였습니다.");
                                    }
                                    else {
                                        sb.append("\n").append(dataTime).append(" 기준\n미세먼지농도: ").append(pm10Value)
                                                .append("\n미세먼지등급: ").append(pm10Grade1h)
                                                .append("\n초미세먼지농도: ").append(pm25Value)
                                                .append("\n초미세먼지등급: ").append(pm25Grade1h);
                                    }
                                }
                            }
                            // NumberFormatException 예외 처리
                            catch(NumberFormatException e){
                                new MakeToast("미세먼지 정보 API 오류로 새로고침에 실패하였습니다.");
                            }
                            break;
                    }
                    try {
                        // parser를 다음 데이터로 이동
                        parserEvent = parser.next();
                    }
                    // xml 파싱과 입출력 관련 예외 처리
                    catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
            // xml 파싱과 입출력 관련 예외 처리
            catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            // StringBuidler로 만든 데이터를 String 형으로 리턴
            // updateData 메소드의 인자로 쓰인다.
            Log.d("여기", sb.toString());
            return sb.toString();
        }

        // parse 메소드에서 리턴받은 값을 인자로 DB를 업데이트하는 메소드
        private void updateDB(String parsedString){

            // 리턴받은 값에 데이터가 정확한 형태로 저장되어 있을 때
            if(parsedString.length()!=0 && parsedString.split(" ").length>2 && parsedString.split(" ")[1].compareTo("측정소")!=0) {
                // 선언부
                String st = parsedString.split("\n")[1].split(" 기준")[0];
                String dataTime = st.split("-")[0].split("")[3] + st.split("-")[0].split("")[4]
                        + st.split("-")[1] + st.split("-")[2].split(" ")[0]
                        + st.split("-")[2].split(" ")[1].split(":")[0]
                        + st.split("-")[2].split(" ")[1].split(":")[1];
                String pm10Value = parsedString.split("\n")[2].split(" ")[1];
                String pm10Grade1h = parsedString.split("\n")[3].split(" ")[1];
                String pm25Value = parsedString.split("\n")[4].split(" ")[1];
                String pm25Grade1h = parsedString.split("\n")[5].split(" ")[1];

                // DB에 입력된 데이터의 개수를 구한다.
                int count = dataBase.count("Air");
                // 데이터 개수에 따라
                // 데이터가 없을 때 (데이터가 제대로 입력되지 않았을 때)
                if(count == 0) {
                    // 데이터 입력
                    dataBase.insert("Air", dataTime, pm10Value, pm10Grade1h, pm25Value, pm25Grade1h,  new SimpleDateFormat("yyMMddHHmm",Locale.KOREA).format(new Date()), address);
                }
                // 데이터가 하나일때 (일반적인 경우)
                else if(count == 1){
                    // 데이터 수정
                    dataBase.update("Air", dataTime, pm10Value, pm10Grade1h, pm25Value, pm25Grade1h,  new SimpleDateFormat("yyMMddHHmm",Locale.KOREA).format(new Date()), address);
                }
                // 데이터가 두개 이상일때 (오류가 발생했을 경우)
                else{
                    // 데이터를 하나만 남기고 지운 뒤
                    while(dataBase.count("Air")!=1 ) {
                        dataBase.delete("Air");
                    }
                    // 데이터를 수정한다.
                    dataBase.update("Air", dataTime, pm10Value, pm10Grade1h, pm25Value, pm25Grade1h,  new SimpleDateFormat("yyMMddHHmm",Locale.KOREA).format(new Date()), address);
                }

                // StringBuilder에 저장되어있던 미세먼지 데이터를 삭제한다.
                sb.delete(0, sb.length());
            }
            // 위치서비스를 이용할 수 없을 경우
            /*else if(!(isGPSEnabled | isNetworkEnabled)){
                new MakeToast("위치서비스를 활성화 해주세요").execute();
            }*/
            // API 서버 오류로 데이터를 가져올수 없을 경우
            else{
                new MakeToast("현재 해당 지역의 미세먼지 정보가 제공되지 않습니다.").execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class MakeToast extends AsyncTask<View, String, String>{
            String st;
        MakeToast(String st) {
            this.st = st;
        }
        @Override
        protected String doInBackground(View... views) {
            return st;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
        }
    }
}
