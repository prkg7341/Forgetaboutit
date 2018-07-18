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

public class Main extends Fragment {

    public Main() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.main, container, false);
        Button renew = (Button) view.findViewById(R.id.renew);
        final TextView weather = (TextView) view.findViewById(R.id.weather);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weather.setText("The button works well!");
            }
        });
        return view;
    }
}