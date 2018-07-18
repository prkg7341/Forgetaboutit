package com.jaewoo.forgetaboutit;

import android.os.AsyncTask;
import android.os.Bundle;
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

public class Water {

    public Water() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*final View view = inflater.inflate(R.layout.water, container, false);
        Button renew = (Button) view.findViewById(R.id.renewWater);
        final TextView weatherView = (TextView) view.findViewById(R.id.waterView);

        final Water.RunningAsyncTask rat = new Water.RunningAsyncTask(view);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //weatherView.setText("Success");
                rat.execute();
            }
        });

        return view;*/
        return null;
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

            getWaterInfo();

            return sb.toString();
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
