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

    static TextView all;
    DataBase dataBase;

    // fragment가 return될 때 실행되는 메소드
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 초기 화면을 "main"으로 설정
        final View view = inflater.inflate(R.layout.main, container, false);

        dataBase = DataBase.openDB(getActivity());

        // 버튼 클릭시 내용을 출력할 TextView 생성 - 사용자의 위치를 출력하도록 수정예정
        TextView userLocation;
        userLocation = (TextView) view.findViewById(R.id.userLocation);
        all = userLocation;

        if(dataBase.count("Air")!=0){
            String loc = dataBase.select("Air").split("\n")[1];
            if(loc.split("-").length==3) {
                String location = loc.split("-")[0] + " " + loc.split("-")[1] + " " + loc.split("-")[2];
                userLocation.setText("현재위치: " + location);
            }
            else {
                userLocation.setText("현재위치: " + loc);
            }
        }

        return view;
    }
}