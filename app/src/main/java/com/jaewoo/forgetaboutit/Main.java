package com.jaewoo.forgetaboutit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Fragment {

    public Main() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View view = inflater.inflate(R.layout.main, container, false);
        Button renew = (Button) view.findViewById(R.id.renew);
        final TextView test = (TextView) view.findViewById(R.id.weather);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.setText("The button works well!");
            }
        });
        return view;
    }
}