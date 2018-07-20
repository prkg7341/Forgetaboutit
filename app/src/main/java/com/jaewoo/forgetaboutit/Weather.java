package com.jaewoo.forgetaboutit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather extends Fragment {

    public Weather() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.weather, container, false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // 제목셋팅-생략가능
        alertDialogBuilder.setTitle("Title");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("renew?")//생략가능
                .setCancelable(true)
                .setPositiveButton("renew",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                new RunningAsyncTask().execute(view);

                            }
                        }
                )
                .setNegativeButton("cancle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                        Toast.makeText(getActivity(), "Update cancelled", Toast.LENGTH_SHORT).show();
                                    }
                        }
                );

        // 다이얼로그 생성
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button renew = (Button) view.findViewById(R.id.renewWeather);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 보여주기
                alertDialog.show();
            }
        });

        return view;
    }

    class RunningAsyncTask extends AsyncTask<View, String, String> {//동네예보

        RunningAsyncTask() {

        }

        private String key = "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D";
        private StringBuilder urlBuilder;
        private URL url;
        private HttpURLConnection conn;
        private BufferedReader rd;
        private StringBuilder sb = new StringBuilder();
        private String line;
        private String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        private String dateTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date());
        TextView dataView;
        TextView timeView;

        @Override
        protected String doInBackground(View... views) {

            dataView = (TextView) views[0].findViewById(R.id.weatherView);
            timeView = (TextView) views[0].findViewById(R.id.timeView);

            boolean bbaseDate, bbaseTime, bcategory, bfcstDate, bfcstTime, bfcstValue, bnx, bny;
            bbaseDate = false; bbaseTime = false; bcategory = false; bfcstDate = false;
            bfcstTime = false; bfcstValue = false; bnx = false; bny = false;
            String baseDate, baseTime, category, fcstDate, fcstTime, fcstValue, nx, ny;
            baseDate = null; baseTime = null; category = null; fcstDate = null;
            fcstTime = null; fcstValue = null; nx = null; ny = null;

            getWeatherInfo();

            XmlPullParser parser;
            int parserEvent = 0;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parserEvent = parser.getEventType();
                factory.setNamespaceAware(true);

                parser.setInput(url.openStream(), null);

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                            if (parser.getName().equals("baseDate")) { //baseDate(예측일자) 만나면 내용을 받을수 있게 하자
                                bbaseDate = true;
                            }
                            if (parser.getName().equals("baseTime")) { //baseTime(예측시간) 만나면 내용을 받을수 있게 하자
                                bbaseTime = true;
                            }
                            if (parser.getName().equals("category")) { //category(자료구분문자) 만나면 내용을 받을수 있게 하자
                                bcategory = true;
                            }
                            if (parser.getName().equals("fcstDate")) { //fcstDate(예보일자) 만나면 내용을 받을수 있게 하자
                                bfcstDate = true;
                            }
                            if (parser.getName().equals("fcstTime")) { //fcstTime(예보시간) 만나면 내용을 받을수 있게 하자
                                bfcstTime = true;
                            }
                            if (parser.getName().equals("fcstValue")) { //fcstValue(예보 값) 만나면 내용을 받을수 있게 하자
                                bfcstValue = true;
                            }
                            /*if (parser.getName().equals("message")) { //message 태그를 만나면 에러 출력
                                status1.setText(status1.getText() + "에러");
                                //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                            }*/
                            break;

                        case XmlPullParser.TEXT://parser가 내용에 접근했을때
                            if (bbaseDate) { //bbaseDate true일 때 태그의 내용을 저장.
                                baseDate = parser.getText();
                                bbaseDate = false;
                            }
                            if (bbaseTime) { //bbaseTime true일 때 태그의 내용을 저장.
                                baseTime = parser.getText();
                                bbaseTime = false;
                            }
                            if (bcategory) { //bcategory true일 때 태그의 내용을 저장.
                                category = parser.getText();
                                bcategory = false;
                            }
                            if (bfcstDate) { //bfcstDate true일 때 태그의 내용을 저장.
                                fcstDate = parser.getText();
                                bfcstDate = false;
                            }
                            if (bfcstTime) { //bfcstTime true일 때 태그의 내용을 저장.
                                fcstTime = parser.getText();
                                bfcstTime = false;
                            }
                            if (bfcstValue) { //bfcstValue true일 때 태그의 내용을 저장.
                                fcstValue = parser.getText();
                                bfcstValue = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                sb.append(baseDate).append(" ").append(baseTime).append(" ").append(category).append(" ").append(fcstValue).append(" ").append(fcstDate).append(" ").append(fcstTime).append("\n");
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

        private void getWeatherInfo() {

            urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); //URL
            try {
                urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key); //서비스 인증
                urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //오늘 발표
                urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); //05시 발표 * 기술문서 참조
                urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("58", "UTF-8")); //예보지점의 X 좌표값
                urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("125", "UTF-8")); //예보지점의 Y 좌표값
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); //한 페이지 결과 수
                //urlBuilder.append("&" + URLEncoder.encode("pageSize","UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); //페이지 사이즈
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지 번호
                //urlBuilder.append("&" + URLEncoder.encode("startPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //시작 페이지
                urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); //xml(기본값), json
                url = new URL(urlBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                /*while ((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }*/
                rd.close();
                conn.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dataView.setText(s);
            timeView.setText("Updated time is " + dateTime);
            Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}