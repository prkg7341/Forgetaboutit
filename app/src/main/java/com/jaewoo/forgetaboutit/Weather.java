package com.jaewoo.forgetaboutit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        Button renew = (Button) view.findViewById(R.id.renewWeather);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //weatherView.setText("Success");
                new RunningAsyncTask().execute(view);
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

            getWeatherInfo();

            dataView = (TextView) views[0].findViewById(R.id.weatherView);
            timeView = (TextView) views[0].findViewById(R.id.timeView);

            return sb.toString();
        }

        private void getWeatherInfo() {

            urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); //URL
            try {
                urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key); //서비스 인증
                urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //오늘 발표
                urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); //05시 발표 * 기술문서 참조
                urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("60", "UTF-8")); //예보지점의 X 좌표값
                urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); //예보지점의 Y 좌표값
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); //한 페이지 결과 수
                //urlBuilder.append("&" + URLEncoder.encode("pageSize","UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); //페이지 사이즈
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지 번호
                //urlBuilder.append("&" + URLEncoder.encode("startPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //시작 페이지
                urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); //xml(기본값), json
                url = new URL(urlBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                while ((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
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
        }
    }
}