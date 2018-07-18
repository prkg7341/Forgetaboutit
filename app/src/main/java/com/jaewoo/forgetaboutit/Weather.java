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
        final TextView weatherView = (TextView) view.findViewById(R.id.weatherView);

        RunningAsyncTask rat = new RunningAsyncTask(view);
        rat.execute();

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherView.setText("Success");
            }
        });


        return view;
    }

    class RunningAsyncTask extends AsyncTask<String, String, String> {//동네예보

        RunningAsyncTask(View view) {

        }

        private String key = "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D";
        private StringBuilder urlBuilder;
        private URL url;
        private HttpURLConnection conn;
        private BufferedReader rd;
        private StringBuilder sb = new StringBuilder();
        private String line;
        private String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        @Override
        protected String doInBackground(String... strings) {

            getWeatherInfo();

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

        private void getWaterInfo() {

            urlBuilder = new StringBuilder("http://apis.data.go.kr/B500001/rwis/waterQuality/list"); //URL
            try {
                Date dt = new Date();
                urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key); //Service Key
                urlBuilder.append("&" + URLEncoder.encode("stDt", "UTF-8") + "=" + URLEncoder.encode(dt.toString(), "UTF-8")); //조회시작일자
                urlBuilder.append("&" + URLEncoder.encode("stTm", "UTF-8") + "=" + URLEncoder.encode("08", "UTF-8")); //조회시작시간
                urlBuilder.append("&" + URLEncoder.encode("edDt", "UTF-8") + "=" + URLEncoder.encode("2017-07-04", "UTF-8")); //조회종료일자
                urlBuilder.append("&" + URLEncoder.encode("edTm", "UTF-8") + "=" + URLEncoder.encode("09", "UTF-8")); //조회종료시간
                urlBuilder.append("&" + URLEncoder.encode("fcltyMngNo", "UTF-8") + "=" + URLEncoder.encode("4824012333", "UTF-8")); //시설관리번호
                urlBuilder.append("&" + URLEncoder.encode("sujCode", "UTF-8") + "=" + URLEncoder.encode("333", "UTF-8")); //사업장코드
                urlBuilder.append("&" + URLEncoder.encode("liIndDiv", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //생활:1, 공업:2
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //줄수
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지번호
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

        protected void onPostExecute(String s, TextView textview) {
            super.onPostExecute(s);
            textview.setText(s);
        }
    }
}